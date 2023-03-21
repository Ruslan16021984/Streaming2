package com.natife.streaming.ui.live

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.WindowManager
import androidx.activity.addCallback
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.setFragmentResultListener
import androidx.transition.AutoTransition
import androidx.transition.Transition
import androidx.transition.TransitionManager
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.util.Util
import com.natife.streaming.R
import com.natife.streaming.base.BaseFragment
import kotlinx.android.synthetic.main.fragment_live.*
import org.koin.core.parameter.ParametersDefinition
import org.koin.core.parameter.parametersOf
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.trackselection.MappingTrackSelector
import com.google.android.exoplayer2.trackselection.TrackSelectionUtil
import com.google.android.exoplayer2.util.Assertions
import com.google.android.exoplayer2.video.VideoSize
import com.natife.streaming.VIDEO_AUTO
import com.natife.streaming.ext.dp
import com.natife.streaming.ext.subscribe
import com.natife.streaming.ext.toDisplayTime
import com.natife.streaming.ui.player.PlayerFragment
import com.natife.streaming.ui.player.menu.quality.VideoQualityDialog
import kotlinx.android.synthetic.main.custom_playback_control.*
import kotlinx.android.synthetic.main.fragment_live.live_video_view
import kotlinx.android.synthetic.main.fragment_live.progress_buffering_live
import timber.log.Timber


class LiveFragment : BaseFragment<LiveViewModel>() {
    override fun getLayoutRes() = R.layout.fragment_live

    override fun getParameters(): ParametersDefinition = {
        parametersOf(LiveFragmentArgs.fromBundle(requireArguments()))
    }

    override fun onResume() {
        activity?.window?.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        super.onResume()
    }

    private lateinit var trackSelector: DefaultTrackSelector
    private var player: SimpleExoPlayer? = null
    private var playWhenReady = false
    private var currentWindow = 0
    private var durationSet = false
    private var watchType: WatchType? = null
    private var playbackPosition: Long = 0
    private var mediaSource: MediaSource? = null
    private val handler = Handler(Looper.getMainLooper())
    private var currentVideoQuality = VIDEO_AUTO

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        subscribeViewModels()

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            viewModel.onBackClicked()
        }

        arguments?.getString("title")?.let {
            bigGameTitle.text = it
        }

        arguments?.getString("watchType")?.let {
            watchType = WatchType.valueOf(it)
        }

        exo_interval_rewind_30.setOnClickListener {
            player?.currentPosition?.minus(30000)
                ?.let { it1 -> player?.seekTo(it1) }
        }
        exo_interval_rewind_5.setOnClickListener {
            player?.currentPosition?.minus(5000)
                ?.let { it1 -> player?.seekTo(it1) }
        }
        exo_interval_forward_30.setOnClickListener {
            player?.currentPosition?.plus(30000)
                ?.let { it1 -> player?.seekTo(it1) }
        }
        exo_interval_forward_5.setOnClickListener {
            player?.currentPosition?.plus(5000)
                ?.let { it1 -> player?.seekTo(it1) }
        }

        exo_preview.isEnabled = false
        exo_next_episode.isEnabled = false

        menuPlayer.setOnClickListener {
            viewModel.openVideoQualityMenu()
        }

        sight_of_bottom.setOnClickListener {
            //трансформируем
            val set = ConstraintSet()
            set.clone(custom_control_layout)
            changeToSmollCustomControlLayout(set)
            set.applyTo(custom_control_layout)
            val autoTransition = AutoTransition()
            autoTransition.duration = 100
            autoTransition.addListener(object : Transition.TransitionListener {
                override fun onTransitionStart(transition: Transition) {
                    //уменьшаем кнопки
                    exo_play.apply {
                        iconSize = 27.dp
                    }
                    exo_pause.apply {
                        iconSize = 27.dp
                    }
                    exo_preview.apply {
                        iconSize = 27.dp
                    }
                    exo_interval_rewind_30.apply {
                        iconSize = 27.dp
                    }
                    exo_interval_rewind_5.apply {
                        iconSize = 27.dp
                    }
                    exo_interval_forward_5.apply {
                        iconSize = 27.dp
                    }
                    exo_interval_forward_30.apply {
                        iconSize = 27.dp
                    }
                    exo_next_episode.apply {
                        iconSize = 27.dp
                    }
                }

                override fun onTransitionEnd(transition: Transition) {
                    bottom_button_line_layout.visibility = View.VISIBLE
                    bigGameTitle.visibility = View.VISIBLE
                    sight_of_bottom.visibility = View.GONE
                    sight_of_bottom.visibility = View.GONE
                    menuPlayer.visibility = View.VISIBLE
                    if (exo_play.isVisible) exo_play.requestFocus() else exo_pause.requestFocus()
                }

                override fun onTransitionCancel(transition: Transition) {}
                override fun onTransitionPause(transition: Transition) {}
                override fun onTransitionResume(transition: Transition) {}
            })
            TransitionManager.beginDelayedTransition(
                custom_control_layout,
                autoTransition
            )
        }
    }

    private fun subscribeViewModels() {

        setFragmentResultListener(VideoQualityDialog.KEY_QUALITY) { _, bundle ->
            bundle.getString(VideoQualityDialog.KEY_QUALITY)?.let { videoQuality ->
                currentVideoQuality = videoQuality
                updateVideoQuality(videoQuality)
            }
        }

        subscribe(viewModel.videoQualityParams) {
            val d = VideoQualityDialog(viewModel).apply {
                arguments =
                    bundleOf(PlayerFragment.VIDEO_QUALITY to it, PlayerFragment.CURRENT_VIDEO_QUALITY to currentVideoQuality)
            }
            d.show(parentFragmentManager, PlayerFragment.DIALOG_QUALITY)
        }

        subscribe(viewModel.mediaSourceLiveData) {
            mediaSource = it
            initializePlayer()
        }

        bottom_button_line_layout.initButtonLive(viewModel)

        subscribe(viewModel.returnToLiveEvent) {
            if (it) {
                playbackPosition = player?.duration ?: 0
                player?.seekTo(currentWindow, playbackPosition)
                playWhenReady = true
                player?.playWhenReady = playWhenReady
            }
        }
    }

    private fun updateVideoQuality(videoQuality: String) {
        val qualityList = viewModel.videoQualityParams.value?.videoQualityList
        var track = -1 // -1 - AUTO quality
        qualityList?.let { list ->
            for (trackId in list.indices) {
                if (videoQuality == list[trackId]) {
                    track = trackId - 1
                    return@let
                }
            }
        }

        var rendererIndex = 0
        val mappedTrackInfo = Assertions.checkNotNull(trackSelector.currentMappedTrackInfo)

        for (i in 0 until mappedTrackInfo.rendererCount) {
            if (isVideoRenderer(mappedTrackInfo, i)) {
                rendererIndex = i
            }
        }

        val rendererTrackGroups = mappedTrackInfo.getTrackGroups(rendererIndex)
        val selectionParameters = trackSelector.parameters
        val trackId = if (track >= 0) track else 0
        val selectionOverride = DefaultTrackSelector.SelectionOverride(rendererIndex, trackId)
        val isDisabled = selectionParameters.getRendererDisabled(rendererIndex)

        trackSelector.parameters = TrackSelectionUtil.updateParametersWithOverride(
            selectionParameters,
            rendererIndex,
            rendererTrackGroups,
            isDisabled,
            selectionOverride
        )
        // set AUTO
        if (track == -1) {
            trackSelector.setParameters(
                trackSelector.buildUponParameters().setMaxVideoBitrate(Int.MAX_VALUE)
            )
        }
    }

    private fun TransformCustomControlLayoutToBigState() {
        //трансформируем
        val set = ConstraintSet()
        set.clone(custom_control_layout)
        changeToBigCustomControlLayout(set)
        set.applyTo(custom_control_layout)
        val autoTransition = AutoTransition()
        autoTransition.duration = 100
        autoTransition.addListener(object : Transition.TransitionListener {
            override fun onTransitionStart(transition: Transition) {
                //уменьшаем кнопки
                exo_play.apply {
                    iconSize = 62.dp
                }
                exo_pause.apply {
                    iconSize = 62.dp
                }
                exo_preview.apply {
                    iconSize = 62.dp
                }
                exo_interval_rewind_30.apply {
                    iconSize = 62.dp
                }
                exo_interval_rewind_5.apply {
                    iconSize = 62.dp
                }
                exo_interval_forward_5.apply {
                    iconSize = 62.dp
                }
                exo_interval_forward_30.apply {
                    iconSize = 62.dp
                }
                exo_next_episode.apply {
                    iconSize = 62.dp
                }
            }

            override fun onTransitionEnd(transition: Transition) {
                bottom_button_line_layout.visibility = View.GONE
                bigGameTitle.visibility = View.GONE
                sight_of_bottom.visibility = View.VISIBLE
                menuPlayer.visibility = View.GONE
                if (exo_play.isVisible) exo_play.requestFocus() else exo_pause.requestFocus()
            }

            override fun onTransitionCancel(transition: Transition) {}
            override fun onTransitionPause(transition: Transition) {}
            override fun onTransitionResume(transition: Transition) {}
        })
        TransitionManager.beginDelayedTransition(
            custom_control_layout,
            autoTransition
        )
    }

    private fun changeToBigCustomControlLayout(set: ConstraintSet) {
        // увеличиваем меню
        set.clear(R.id.player_bottom_bar, ConstraintSet.BOTTOM)
        set.connect(
            R.id.player_bottom_bar,
            ConstraintSet.BOTTOM,
            ConstraintSet.PARENT_ID,
            ConstraintSet.BOTTOM,
            48.dp
        )
        //переносим кнопки
        set.clear(R.id.exo_play, ConstraintSet.TOP)
        set.clear(R.id.exo_play, ConstraintSet.BOTTOM)
        set.connect(
            R.id.exo_play,
            ConstraintSet.TOP,
            R.id.center_max_control_panel_guideline,
            ConstraintSet.TOP,
            0.dp
        )
        set.connect(
            R.id.exo_play,
            ConstraintSet.BOTTOM,
            R.id.center_max_control_panel_guideline,
            ConstraintSet.BOTTOM,
            0.dp
        )
        set.clear(R.id.exo_pause, ConstraintSet.TOP)
        set.clear(R.id.exo_pause, ConstraintSet.BOTTOM)
        set.connect(
            R.id.exo_pause,
            ConstraintSet.TOP,
            R.id.center_max_control_panel_guideline,
            ConstraintSet.TOP,
            0.dp
        )
        set.connect(
            R.id.exo_pause,
            ConstraintSet.BOTTOM,
            R.id.center_max_control_panel_guideline,
            ConstraintSet.BOTTOM,
            0.dp
        )
        // переносим боковые привязки
        set.clear(R.id.exo_preview, ConstraintSet.START)
        set.connect(
            R.id.exo_preview,
            ConstraintSet.START,
            ConstraintSet.PARENT_ID,
            ConstraintSet.START,
            0.dp
        )
        set.clear(R.id.exo_next_episode, ConstraintSet.END)
        set.connect(
            R.id.exo_next_episode,
            ConstraintSet.END,
            ConstraintSet.PARENT_ID,
            ConstraintSet.END,
            0.dp
        )
    }


    private fun changeToSmollCustomControlLayout(set: ConstraintSet) {
        // увеличиваем меню
        set.clear(R.id.player_bottom_bar, ConstraintSet.BOTTOM)
        set.connect(
            R.id.player_bottom_bar,
            ConstraintSet.BOTTOM,
            ConstraintSet.PARENT_ID,
            ConstraintSet.BOTTOM,
            200.dp
        )
        //переносим кнопки
        set.clear(R.id.exo_play, ConstraintSet.TOP)
        set.clear(R.id.exo_play, ConstraintSet.BOTTOM)
        set.connect(
            R.id.exo_play,
            ConstraintSet.TOP,
            R.id.top_mini_control_panel_guideline,
            ConstraintSet.TOP,
            0.dp
        )
        set.connect(
            R.id.exo_play,
            ConstraintSet.BOTTOM,
            R.id.top_mini_control_panel_guideline,
            ConstraintSet.BOTTOM,
            0.dp
        )
        set.clear(R.id.exo_pause, ConstraintSet.TOP)
        set.clear(R.id.exo_pause, ConstraintSet.BOTTOM)
        set.connect(
            R.id.exo_pause,
            ConstraintSet.TOP,
            R.id.top_mini_control_panel_guideline,
            ConstraintSet.TOP,
            0.dp
        )
        set.connect(
            R.id.exo_pause,
            ConstraintSet.BOTTOM,
            R.id.top_mini_control_panel_guideline,
            ConstraintSet.BOTTOM,
            0.dp
        )
        // переносим боковые привязки
        set.clear(R.id.exo_preview, ConstraintSet.START)
        set.connect(
            R.id.exo_preview,
            ConstraintSet.START,
            R.id.left_mini_control_panel_guideline,
            ConstraintSet.START,
            0.dp
        )
        set.clear(R.id.exo_next_episode, ConstraintSet.END)
        set.connect(
            R.id.exo_next_episode,
            ConstraintSet.END,
            R.id.right_mini_control_panel_guideline,
            ConstraintSet.END,
            0.dp
        )
    }

    private fun releasePlayer() {
        if (player != null) {
            playWhenReady = player!!.playWhenReady
            playbackPosition = player!!.currentPosition
            currentWindow = player!!.currentWindowIndex
            player!!.release()
            player = null
        }
    }

    override fun onPause() {
        super.onPause()
        player?.pause()
        activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        if (Util.SDK_INT <= 23) {
            releasePlayer()
        }
    }

    override fun onStop() {
        super.onStop()
        player?.stop()
        handler.removeCallbacks(timerRunnable)
        if (Util.SDK_INT > 23) {
            releasePlayer()
        }
    }

    private fun initializePlayer() {
        if (player != null) {
            player?.release()
            player = null
        }
        trackSelector = DefaultTrackSelector(requireContext())
        // When player is initialized it'll be played with a quality of MaxVideoSize to prevent loading in 1080p from the start
        trackSelector.setParameters(
            trackSelector.buildUponParameters().setMaxVideoBitrate(Int.MAX_VALUE)
        )
        val loadControl = DefaultLoadControl.Builder().setBufferDurationsMs(
            DefaultLoadControl.DEFAULT_MIN_BUFFER_MS,
            DefaultLoadControl.DEFAULT_MAX_BUFFER_MS,
            3000,
            DefaultLoadControl.DEFAULT_BUFFER_FOR_PLAYBACK_AFTER_REBUFFER_MS
        )
            .build()
        player = SimpleExoPlayer.Builder(requireContext())
            .setLoadControl(loadControl)
            .setTrackSelector(trackSelector).build()
        mediaSource?.let { player?.setMediaSource(it) }

        player?.addListener(object : Player.Listener {
            override fun onPlayWhenReadyChanged(playWhenReady: Boolean, reason: Int) {
                if (playWhenReady) {
                    handler.removeCallbacks(timerRunnable)
                    handler.postDelayed(timerRunnable, 500)
                }
                super.onPlayWhenReadyChanged(playWhenReady, reason)
            }

            override fun onPlayerError(error: PlaybackException) {
                if (error.errorCode == ExoPlaybackException.TYPE_SOURCE) {
                    val cause = error.cause
                    Timber.tag("TAG")
                        .e("onPlayerError ---ExoPlaybackException.TYPE_SOURCE ---${cause}")
                    viewModel.onFinishClicked()
                }
            }
        })

        live_video_view.player = player
        live_video_view.controllerAutoShow = false
        live_video_view.setControllerVisibilityListener { visibility ->
            if (visibility == View.VISIBLE) {
                TransformCustomControlLayoutToBigState()
            }
        }
        player?.addListener(object : Player.Listener {
            override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
                if (playbackState == ExoPlayer.STATE_READY && !durationSet && watchType != null) {
                    val realDurationMillis: Long = player?.duration ?: 0
                    durationSet = true
                    initVideoQuality()
                    setPlayerPosition(realDurationMillis)
                }
                super.onPlayerStateChanged(playWhenReady, playbackState)
            }
            override fun onPlaybackStateChanged(playbackState: Int) {
                if (playbackState == ExoPlayer.STATE_BUFFERING && live_video_view.isVisible) {
                    progress_buffering_live.visibility = View.VISIBLE
                } else {
                    progress_buffering_live.visibility = View.INVISIBLE
                }
            }
            override fun onVideoSizeChanged(videoSize: VideoSize) {
                Timber.tag("TAG").d("onVideoSizeChanged videoSize-${videoSize.height}")
                super.onVideoSizeChanged(videoSize)
            }
        })

        player?.videoScalingMode = Renderer.VIDEO_SCALING_MODE_SCALE_TO_FIT
        player?.seekTo(currentWindow, playbackPosition)
        player?.playWhenReady = playWhenReady

        live_video_view.setShowMultiWindowTimeBar(true)
        player?.prepare()
        live_video_view.hideController()
    }

    private fun initVideoQuality() {
        var rendererIndex = 0
        val mappedTrackInfo = Assertions.checkNotNull(trackSelector.currentMappedTrackInfo)

        for (i in 0 until mappedTrackInfo.rendererCount) {
            if (isVideoRenderer(mappedTrackInfo, i)) {
                rendererIndex = i
            }
        }

        val rendererTrackGroups = mappedTrackInfo.getTrackGroups(rendererIndex)
        val listQuality = mutableSetOf<String>()
        for (i in 0 until rendererTrackGroups.length) {
            val item = rendererTrackGroups[i]
            for (format in 0 until item.length) {
                listQuality.add(item.getFormat(format).height.toString())
            }
        }
        viewModel.setVideoQualityList(listQuality.toList())
    }

    private fun setPlayerPosition(duration: Long) {
        playbackPosition = when (watchType) {
            WatchType.WATCH_LIVE -> duration
            WatchType.WATCH_FROM_START -> 0
            WatchType.CONTINUE_WATCH -> {
                ((viewModel.currentPositionLiveData.value ?: 0) * 1000).toLong()
            }
            else -> 0
        }
        player?.seekTo(currentWindow, playbackPosition)
        player?.playWhenReady = true
    }

    // Timer for saving current position
    private val timerRunnable: Runnable = object : Runnable {
        override fun run() {
            player?.currentPosition?.let { position ->
                viewModel.saveCurrentPosition(position.div(1000))
                exo_position_video.text = position.div(1000).toDisplayTime()
            }
            exo_duration_video.text = player?.duration?.div(1000)?.toDisplayTime() ?: "00:00"
            handler.postDelayed(this, 500)
        }
    }

    private fun isVideoRenderer(mappedTrackInfo: MappingTrackSelector.MappedTrackInfo, rendererIndex: Int): Boolean {
        val trackGroupArray = mappedTrackInfo.getTrackGroups(rendererIndex)
        if (trackGroupArray.length == 0) {
            return false
        }
        val trackType = mappedTrackInfo.getRendererType(rendererIndex)
        return C.TRACK_TYPE_VIDEO == trackType
    }
}