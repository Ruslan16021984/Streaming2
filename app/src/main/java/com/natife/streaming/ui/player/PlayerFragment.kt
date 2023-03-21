package com.natife.streaming.ui.player

import android.annotation.SuppressLint
import android.app.Activity
import android.graphics.Insets
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.DisplayMetrics
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.SeekBar
import androidx.activity.addCallback
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.setFragmentResultListener
import androidx.leanback.widget.BrowseFrameLayout
import androidx.transition.AutoTransition
import androidx.transition.Transition
import androidx.transition.TransitionManager
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.Player.TIMELINE_CHANGE_REASON_SOURCE_UPDATE
import com.google.android.exoplayer2.source.ClippingMediaSource
import com.google.android.exoplayer2.source.ConcatenatingMediaSource
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.MimeTypes
import com.google.android.exoplayer2.util.Util
import com.google.android.material.button.MaterialButton
import com.natife.streaming.base.BaseFragment
import com.natife.streaming.ext.*
import com.natife.streaming.ui.player.menu.quality.VideoQualityDialog
import com.natife.streaming.utils.dpToPx
import kotlinx.android.synthetic.main.custom_playback_control.*
import kotlinx.android.synthetic.main.fragment_player.*
import kotlinx.android.synthetic.main.view_player_bottom_bar.*
import org.koin.core.parameter.ParametersDefinition
import org.koin.core.parameter.parametersOf
import timber.log.Timber
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.trackselection.MappingTrackSelector
import com.google.android.exoplayer2.trackselection.TrackSelectionUtil
import com.google.android.exoplayer2.util.Assertions
import com.google.android.exoplayer2.video.VideoSize
import com.natife.streaming.*
import com.natife.streaming.data.matchprofile.Episode
import com.natife.streaming.data.player.PlayerBottomBarSetup
import com.natife.streaming.data.player.WatchType
import com.natife.streaming.data.player.toInitBottomData


class PlayerFragment : BaseFragment<PlayerViewModel>() {
    override fun getLayoutRes(): Int = R.layout.fragment_player

    private var simpleExoPlayer: SimpleExoPlayer? = null
    private lateinit var trackSelector: DefaultTrackSelector
    private var currentVideoQuality = VIDEO_AUTO
    private var playWhenReady = false
    private var playbackPosition: Long = 0
    private var durationSet = false
    private val handler = Handler(Looper.getMainLooper())
    private var seekBarState: SeekBarType = SeekBarType.BIG
    private var watchType: WatchType? = null

    override fun onResume() {
        activity?.window?.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        super.onResume()
    }

    private fun initializePlayer(list: List<Pair<String, Long>>?, mediaSource: MediaSource?) {
        if (simpleExoPlayer != null) {
            simpleExoPlayer?.release()
            simpleExoPlayer = null
            durationSet = false
        }
        trackSelector = DefaultTrackSelector(requireContext())
        // When player is initialized it'll be played with a quality of MaxVideoSize to prevent loading in 1080p from the start
        trackSelector.setParameters(
            trackSelector.buildUponParameters().setMaxVideoBitrate(Int.MAX_VALUE)
        )
        simpleExoPlayer =
            SimpleExoPlayer.Builder(requireContext()).setTrackSelector(trackSelector).build()

        when {
            mediaSource != null -> {
                simpleExoPlayer?.setMediaSource(mediaSource)
                watchType = WatchType.FULL_GAME
            }
            list != null -> {
                watchType = null
                //hack for full playlist duration
                val concatenatedSource = ConcatenatingMediaSource()
                list.forEach {
                    concatenatedSource.addMediaSource(
                        ClippingMediaSource(
                            ProgressiveMediaSource.Factory(
                                DefaultDataSourceFactory(
                                    requireContext()
                                )
                            ).createMediaSource(
                                MediaItem.Builder()
                                    .setUri(it.first)
                                    .setMimeType(MimeTypes.APPLICATION_MPD)
                                    .build()
                            ), it.second * 1000
                        )
                    )
                }
                simpleExoPlayer?.setMediaSource(concatenatedSource)
            }
        }

        playerView.player = simpleExoPlayer
        simpleExoPlayer!!.addListener(object : Player.Listener {
            override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
                if (playbackState == ExoPlayer.STATE_READY && !durationSet) {
                    val realDurationMillis: Long = simpleExoPlayer?.duration ?: 0
                    Timber.tag("PLAYER***").d("realDurationMillis=$realDurationMillis time=${realDurationMillis.div(1000).toDisplayTime()}")
                    durationSet = true
                    initVideoQuality()
                    if (watchType == WatchType.FULL_GAME) {
                        initPlayerBottomBar(realDurationMillis)
                        viewModel.setFullVideoDuration(realDurationMillis.div(1000))
                    }
                }
                super.onPlayerStateChanged(playWhenReady, playbackState)
            }

            override fun onPlaybackStateChanged(playbackState: Int) {
                if (playbackState == ExoPlayer.STATE_BUFFERING) {
                    progress_buffering.visibility = View.VISIBLE
                    exo_play.isEnabled = false
                    exo_preview.isEnabled = false
                    exo_interval_rewind_30.isEnabled = false
                    exo_interval_rewind_5.isEnabled = false
                    exo_interval_forward_5.isEnabled = false
                    exo_interval_forward_30.isEnabled = false
                    exo_next_episode.isEnabled = false
                    menuPlayer.isEnabled = false
                } else {
                    progress_buffering.visibility = View.INVISIBLE
                    exo_play.isEnabled = true
                    exo_preview.isEnabled = true
                    exo_interval_rewind_30.isEnabled = true
                    exo_interval_rewind_5.isEnabled = true
                    exo_interval_forward_5.isEnabled = true
                    exo_interval_forward_30.isEnabled = true
                    exo_next_episode.isEnabled = true
                    menuPlayer.isEnabled = true
                }
            }

            override fun onVideoSizeChanged(videoSize: VideoSize) {
                Timber.tag("TAG").d("onVideoSizeChanged videoSize-${videoSize.height}")
                super.onVideoSizeChanged(videoSize)
            }

            override fun onPlayWhenReadyChanged(playWhenReady: Boolean, reason: Int) {
                if (playWhenReady) {
                    handler.removeCallbacks(timerRunnable)
                    handler.postDelayed(timerRunnable, 500)
                }
                super.onPlayWhenReadyChanged(playWhenReady, reason)
            }
            override fun onPlayerError(error: PlaybackException) {
                Timber.tag("TAG").e("onPlayerError ${error.errorCodeName}")
                viewModel.onBackClicked(error.localizedMessage)
            }
        })
        playerView.controllerAutoShow = false
        playerView.setControllerVisibilityListener { visibility ->
            if (visibility == View.VISIBLE) {

            } else if (visibility == View.GONE) {
                TransformCustomControlLayoutToBigState()
            }
        }

        simpleExoPlayer?.videoScalingMode = Renderer.VIDEO_SCALING_MODE_SCALE_TO_FIT
        simpleExoPlayer?.seekTo(viewModel.currentWindow, playbackPosition)
        simpleExoPlayer?.playWhenReady = playWhenReady

        playerView.setShowMultiWindowTimeBar(true)
        simpleExoPlayer?.prepare()
        playerView.hideController()
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

    private fun initPlayerBottomBar(realDurationMillis: Long) {
        val playerBottomBarSetup = PlayerBottomBarSetup(
            playlist = listOf(Episode(
                title = simpleExoPlayer?.mediaMetadata?.title.toString(),
                startMs = 0,
                endMs = realDurationMillis,
                half = 0,
                image = "",
                placeholder = ""
            )),
            additionallyPlaylist = null
        )

        val marginHorizontal = 88 // плавающий отступ в макете для компенсации округления при выборе длинны
        player_bottom_bar.initVideoUrl(
            playerBottomBarSetup,
            getScreenWidth(requireActivity()) - dpToPx(marginHorizontal),
            simpleExoPlayer,
            viewModel
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        subscribeViewModels()

        val menuPlayer = view.findViewById<MaterialButton>(R.id.menuPlayer)
        menuPlayer.setOnClickListener {
            viewModel.openVideoQualityMenu()
        }

        sight_of_bottom.setOnClickListener {
            //трансформируем
            seekBarState = SeekBarType.SMALL
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

        sliders_place_layout.onFocusSearchListener =
            BrowseFrameLayout.OnFocusSearchListener { focused, direction ->
                when (direction) {
                    33 -> {//top
                        seekBarState = SeekBarType.BIG
                        TransformCustomControlLayoutToBigState()
                        return@OnFocusSearchListener null
                    }
                    else -> return@OnFocusSearchListener null
                }
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

    @SuppressLint("RestrictedApi")
    private fun subscribeViewModels() {

        setFragmentResultListener(VideoQualityDialog.KEY_QUALITY) { _, bundle ->
            bundle.getString(VideoQualityDialog.KEY_QUALITY)?.let { videoQuality ->
                val saveCurrentPosition = simpleExoPlayer?.contentPosition!!
                val saveCurrentHalf = simpleExoPlayer?.currentWindowIndex ?: viewModel.currentWindow
                if (watchType == WatchType.FULL_GAME) {
                    updateVideoQuality(videoQuality)
                } else {
                    viewModel.changeVideoQuality(videoQuality, saveCurrentPosition, saveCurrentHalf)
                }
                currentVideoQuality = videoQuality
            }
        }
        subscribe(viewModel.videoQualityParams) {
            val d = VideoQualityDialog(viewModel).apply {
                arguments =
                    bundleOf(VIDEO_QUALITY to it, CURRENT_VIDEO_QUALITY to currentVideoQuality)
            }
            d.show(parentFragmentManager, DIALOG_QUALITY)
        }

        subscribe(viewModel.mediaSourceLiveData) {
            initializePlayer(null, mediaSource = it)
        }

        subscribeEvent(viewModel.videoLiveData) { videoUrl ->
            initializePlayer(list = videoUrl, null)
            subscribe(viewModel.initBottomBarData) {
                if (it != null) {
                    val marginHorizontal = 88 // плавающий отступ в макете для компенсации округления при выборе длинны
                    player_bottom_bar.initVideoUrl(
                        it,
                        getScreenWidth(requireActivity()) - dpToPx(marginHorizontal),
                        simpleExoPlayer,
                        viewModel
                    )
                }
            }

        }
        // bottom buttons
        subscribe(viewModel.sourceLiveData) { source ->
            bottom_button_line_layout.initButtons(source, viewModel)
        }

        subscribe(viewModel.fullVideoDuration) { duration ->
            exo_duration_video.text = duration.toDisplayTime()
        }

        subscribe(viewModel.currentVideoDuration) { duration ->
            exo_position_video.text = duration.toDisplayTime()
        }

        subscribe(viewModel.matchInfoLiveData) { info ->
            bigGameTitle.text = info
        }

        subscribe(viewModel.isEnableNextEpisode) { isEnable ->
            exo_next_episode.isEnabled = isEnable
            exo_next_episode.isFocusable = isEnable
            if (isEnable) {
                exo_interval_forward_30?.nextFocusRightId = exo_next_episode.id
            } else {
                exo_interval_forward_30?.nextFocusRightId = exo_interval_forward_30.id
            }
        }

        subscribe(viewModel.isEnablePreviewEpisode) { isEnable ->
            exo_preview.isEnabled = isEnable
            exo_preview.isFocusable = isEnable
            if (isEnable) {
                exo_interval_rewind_30?.nextFocusLeftId = exo_preview.id
            } else {
                exo_interval_rewind_30?.nextFocusLeftId = menuPlayer.id
            }
        }

        subscribe(viewModel.updatePosition) { data ->
            // update player
            if (data.isUpdatePlayer) {
                simpleExoPlayer?.seekTo(data.currentHalf, data.positionPlayer)
                simpleExoPlayer?.playWhenReady = data.playWhenReady
            }
            // update seekbar
            player_bottom_bar.updateData(
                currentSliderId = data.currentEpisodeId,
                progress = data.progressSeekBar
            )
            // update UI
            val id = data.currentEpisodeId
            sight_of_bottom?.let { imageView ->
                imageView.nextFocusDownId = id
                imageView.nextFocusLeftId = id
                imageView.nextFocusRightId = id
            }

            if (seekBarState == SeekBarType.BIG) {
                view?.findViewById<SeekBar>(id)?.let { seekBar ->
                    seekBar.nextFocusDownId = sight_of_bottom.id
                    seekBar.nextFocusUpId = sight_of_bottom.id
                }

                exo_play?.let { button ->
                    button.nextFocusDownId = sight_of_bottom.id
                    button.nextFocusUpId = sight_of_bottom.id
                }
                exo_pause?.let { button ->
                    button.nextFocusDownId = sight_of_bottom.id
                    button.nextFocusUpId = sight_of_bottom.id
                }
                exo_preview?.let { button ->
                    button.nextFocusDownId = sight_of_bottom.id
                    button.nextFocusUpId = sight_of_bottom.id
                    button.nextFocusLeftId = button.id
                }
                exo_interval_rewind_30?.let { button ->
                    button.nextFocusDownId = sight_of_bottom.id
                    button.nextFocusUpId = sight_of_bottom.id
                }
                exo_interval_rewind_5?.let { button ->
                    button.nextFocusDownId = sight_of_bottom.id
                    button.nextFocusUpId = sight_of_bottom.id
                }
                exo_interval_forward_5?.let { button ->
                    button.nextFocusDownId = sight_of_bottom.id
                    button.nextFocusUpId = sight_of_bottom.id
                }
                exo_interval_forward_30?.let { button ->
                    button.nextFocusDownId = sight_of_bottom.id
                    button.nextFocusUpId = sight_of_bottom.id
                }
                exo_next_episode?.let { button ->
                    button.nextFocusDownId = sight_of_bottom.id
                    button.nextFocusUpId = sight_of_bottom.id
                    button.nextFocusRightId = button.id
                }
            } else {
                view?.findViewById<SeekBar>(id)?.let { seekBar ->
                    seekBar.nextFocusDownId = if (exo_play.isVisible) exo_play.id else exo_pause.id
                    seekBar.nextFocusUpId = if (exo_play.isVisible) exo_play.id else exo_pause.id
                }

                menuPlayer?.let { imageView ->
                    imageView.nextFocusDownId = View.NO_ID
                    imageView.nextFocusUpId = id
                }

                exo_play?.let { button ->
                    button.nextFocusDownId = View.NO_ID
                    button.nextFocusUpId = id
                }
                exo_pause?.let { button ->
                    button.nextFocusDownId = View.NO_ID
                    button.nextFocusUpId = id
                }
                exo_preview?.let { button ->
                    button.nextFocusDownId = View.NO_ID
                    button.nextFocusUpId = id
                    button.nextFocusLeftId = View.NO_ID
                }
                exo_interval_rewind_30?.let { button ->
                    button.nextFocusDownId = View.NO_ID
                    button.nextFocusUpId = id
                }
                exo_interval_rewind_5?.let { button ->
                    button.nextFocusDownId = View.NO_ID
                    button.nextFocusUpId = id
                }
                exo_interval_forward_5?.let { button ->
                    button.nextFocusDownId = View.NO_ID
                    button.nextFocusUpId = id
                }
                exo_interval_forward_30?.let { button ->
                    button.nextFocusDownId = View.NO_ID
                    button.nextFocusUpId = id
                }
                exo_next_episode?.let { button ->
                    button.nextFocusDownId = View.NO_ID
                    button.nextFocusUpId = id
                    button.nextFocusRightId = id
                }
            }
        }

        exo_interval_rewind_30.setOnClickListener {
            viewModel.intervalRewind(-30000, simpleExoPlayer?.contentPosition!!,
                simpleExoPlayer?.currentWindowIndex ?: viewModel.currentWindow)
        }
        exo_interval_rewind_5.setOnClickListener {
            viewModel.intervalRewind(-5000, simpleExoPlayer?.contentPosition!!,
                simpleExoPlayer?.currentWindowIndex ?: viewModel.currentWindow)
        }
        exo_interval_forward_30.setOnClickListener {
            viewModel.intervalRewind(30000, simpleExoPlayer?.contentPosition!!,
                simpleExoPlayer?.currentWindowIndex ?: viewModel.currentWindow)
        }
        exo_interval_forward_5.setOnClickListener {
            viewModel.intervalRewind(5000, simpleExoPlayer?.contentPosition!!,
                simpleExoPlayer?.currentWindowIndex ?: viewModel.currentWindow)
        }
        exo_next_episode.setOnClickListener {
            viewModel.nextEpisode(0)
        }
        exo_preview.setOnClickListener {
            viewModel.previewEpisode(isStart = true)
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            if (playerView.isControllerVisible) playerView.hideController() else {
                viewModel.onBackClicked()
            }
        }
    }

    // Timer for episode seekbar
    private val timerRunnable: Runnable = object : Runnable {
        override fun run() {
            if (simpleExoPlayer?.playWhenReady == true) {
                viewModel.updatePosition(
                    simpleExoPlayer?.contentPosition!!,
                    simpleExoPlayer?.currentWindowIndex ?: viewModel.currentWindow,
                    false
                )
                handler.postDelayed(this, 500)
            }
        }
    }

    override fun onPause() {
        super.onPause()
        simpleExoPlayer?.pause()
        activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        if (Util.SDK_INT < 24) {
            releasePlayer()
        }
    }

    override fun onStop() {
        super.onStop()
        handler.removeCallbacks(timerRunnable)
        simpleExoPlayer?.stop()
        if (Util.SDK_INT >= 24) {
            releasePlayer()
        }
    }

    private fun releasePlayer() {
        if (simpleExoPlayer != null) {
            playWhenReady = simpleExoPlayer?.playWhenReady!!
            playbackPosition = simpleExoPlayer?.currentPosition!!
            viewModel.currentWindow = simpleExoPlayer?.currentWindowIndex!!
            simpleExoPlayer?.release()
            simpleExoPlayer = null
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

    private fun isVideoRenderer(mappedTrackInfo: MappingTrackSelector.MappedTrackInfo, rendererIndex: Int): Boolean {
        val trackGroupArray = mappedTrackInfo.getTrackGroups(rendererIndex)
        if (trackGroupArray.length == 0) {
            return false
        }
        val trackType = mappedTrackInfo.getRendererType(rendererIndex)
        return C.TRACK_TYPE_VIDEO == trackType
    }

    override fun getParameters(): ParametersDefinition = {
        parametersOf(PlayerFragmentArgs.fromBundle(requireArguments()))
    }

    private fun getScreenWidth(activity: Activity): Int {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val windowMetrics = activity.windowManager.currentWindowMetrics
            val insets: Insets = windowMetrics.windowInsets
                .getInsetsIgnoringVisibility(WindowInsets.Type.systemBars())
            windowMetrics.bounds.width() - insets.left - insets.right
        } else {
            val displayMetrics = DisplayMetrics()
            activity.windowManager.defaultDisplay.getMetrics(displayMetrics)
            displayMetrics.widthPixels
        }
    }

    companion object {
        enum class SeekBarType {
            BIG,
            SMALL,
        }

        const val VIDEO_QUALITY = "videoQualityParams"
        const val DIALOG_QUALITY = "dialogQuality"
        const val CURRENT_VIDEO_QUALITY = "currentVideoQualityParam"
    }
}
