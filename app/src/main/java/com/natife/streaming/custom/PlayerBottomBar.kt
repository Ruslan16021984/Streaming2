package com.natife.streaming.custom


import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.SeekBar
import com.google.android.exoplayer2.IllegalSeekPositionException
import com.google.android.exoplayer2.SimpleExoPlayer
import com.natife.streaming.R
import com.natife.streaming.data.matchprofile.Episode
import com.natife.streaming.data.player.PlayerBottomBarSetup
import com.natife.streaming.databinding.ViewPlayerBottomBarBinding
import com.natife.streaming.ext.dp
import com.natife.streaming.ui.player.PlayerViewModel
import kotlinx.android.synthetic.main.view_player_bottom_bar.view.*
import timber.log.Timber
import kotlin.math.ceil


class PlayerBottomBar @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {
    private var _binding: ViewPlayerBottomBarBinding? = null
    private val sliderIds = mutableMapOf<Int, Episode>()
    private lateinit var viewModel: PlayerViewModel

    init {
        inflate(context, R.layout.view_player_bottom_bar, this)
        _binding = ViewPlayerBottomBarBinding.inflate(LayoutInflater.from(context))
    }

    fun initVideoUrl(
        video: PlayerBottomBarSetup,
        w: Int,
        simpleExoPlayer: SimpleExoPlayer?,
        viewModel: PlayerViewModel,
    ) {
        sliders_place.removeAllViews()
        //paddings
        val padding = when (video.playlist.size) {
            in 0..3 -> 26
            in 4..10 -> 8
            in 10..20 -> 4
            else -> 2
        }
        val widthSeekBar = (w.toDouble() / video.playlist.size)
        val addPadding = ceil((widthSeekBar - widthSeekBar.toInt()) * video.playlist.size).toInt()
        video.playlist.forEachIndexed { index, episode ->
            sliderIds[index] = episode.copy(
                endMs = episode.endMs,
                startMs = episode.startMs
            )
            val lp = when (index) {
                0 -> {
                    LayoutParams(
                        widthSeekBar.toInt() + addPadding / 2 - padding / 2,
                        4.dp
                    ).apply {
                        setMargins(0, 0, padding / 2, 0)
                        gravity = Gravity.CENTER
                    }
                }
                video.playlist.size - 1 -> {
                    LayoutParams(
                        widthSeekBar.toInt() + addPadding / 2  - padding / 2,
                        4.dp
                    ).apply {
                        setMargins(padding / 2, 0, 0, 0)
                        gravity = Gravity.CENTER
                    }
                }
                else -> {
                    LayoutParams(
                        widthSeekBar.toInt() - padding,
                        4.dp
                    ).apply {
                        setMargins(padding / 2, 0, padding / 2 , 0)
                        gravity = Gravity.CENTER
                    }
                }
            }
            sliders_place.addView(
                initSliderBars(
                    index,
                    episode,
                    video.playlist,
                    simpleExoPlayer,
                    viewModel,
                    video.playlist.size
                ), lp
            )
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun initSliderBars(
        index: Int,
        episode: Episode,
        playList: List<Episode>,
        simpleExoPlayer: SimpleExoPlayer?,
        viewModel: PlayerViewModel,
        size: Int
    ): SeekBar {
        this.viewModel = viewModel
        if (index == 0) viewModel.play(episode, playList)
        return SeekBar(context).apply {
            setPadding(0, 0, 0, 0)
            contentDescription = ""
            id = index
            thumb = if (size > 10) resources.getDrawable(
                R.drawable.player_seekbar_thumb_small_new,
                null
            ) else resources.getDrawable(R.drawable.player_seekbar_thumb_new, null)
            progressDrawable = resources.getDrawable(R.drawable.player_progress, null)
            progress = 0
            max = (episode.endMs - episode.startMs).toInt()
            splitTrack = false

            setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(
                    seekBar: SeekBar?,
                    progressMs: Int,
                    fromUser: Boolean
                ) {
                    if (fromUser) {
                        try {
                            viewModel.updatePosition(episode.startMs + progressMs, episode.half, true)
                        } catch (e: IllegalSeekPositionException) {
//                            Timber.tag("TAG").d("PlayerBottomBar error!!!!!!!!!! --- ${(episode.half)} -----------${episode.startMs} + $progressMs")
                            simpleExoPlayer?.release()
                            viewModel.onBackClicked()
                        }
                        viewModel.currentWindow = episode.half
                    }
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {
                    if (simpleExoPlayer?.isPlaying != false) simpleExoPlayer?.pause()
                }

                override fun onStopTrackingTouch(seekBar: SeekBar?) {
                    if (simpleExoPlayer?.isPlaying != true) simpleExoPlayer?.play()
                }
            })
        }
    }

    fun updateData(currentSliderId: Int, progress: Int) {
//        Timber.tag("PLAYER***").d("updateData bottom bar - currentSliderId=$currentSliderId progress=$progress")
        //убираем все подвисшие tumb
        sliderIds.keys.forEach { id ->
            findViewById<SeekBar>(id)?.let { seekBar ->
                seekBar.thumb.alpha = 0
                seekBar.progress = if (id < currentSliderId) seekBar.max else 0
                invalidate()
            }
        }
        // подсвечиваем текущий
        findViewById<SeekBar>(currentSliderId)?.let { currentSeekBar ->
            currentSeekBar.thumb.alpha = 255
            currentSeekBar.progress = progress
        }
    }
}