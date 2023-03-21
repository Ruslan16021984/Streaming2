package com.natife.streaming.ui.player.menu.quality

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import com.natife.streaming.R
import com.natife.streaming.base.BaseDialog
import com.natife.streaming.base.EmptyViewModel
import com.natife.streaming.base.LexisViewModel
import com.natife.streaming.ext.subscribe
import com.natife.streaming.ui.player.PlayerFragment.Companion.CURRENT_VIDEO_QUALITY
import com.natife.streaming.ui.player.PlayerFragment.Companion.VIDEO_QUALITY
import com.natife.streaming.utils.findLexicText
import kotlinx.android.synthetic.main.dialog_video_quality.*

class VideoQualityDialog(private val lexisViewModel: LexisViewModel) : BaseDialog<EmptyViewModel>() {
    override fun getLayoutRes(): Int = R.layout.dialog_video_quality

    private val videoQualityAdapter by lazy {
        VideoQualityAdapter {
            onQualityClicked(it)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.Theme_App_Dialog_FullScreen)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val params: VideoQualityParams = arguments?.let {
            it.get(VIDEO_QUALITY)
        } as VideoQualityParams
        val currentQuality: String = arguments?.let {
            it.get(CURRENT_VIDEO_QUALITY)
        } as String

        params.videoQualityList.forEachIndexed { index, quality ->
            if (quality == currentQuality) {
                videoQualityAdapter.setCurrentPosition(index)
                return@forEachIndexed
            }
        }

        rvQuality.adapter = videoQualityAdapter
        videoQualityAdapter.submitList(params.videoQualityList)

        subscribe(lexisViewModel.strings) {
            tvTitleQuality.text = it.findLexicText(context, R.integer.choose_quality)
        }
    }


    private fun onQualityClicked(quality: String) {
        setFragmentResult(KEY_QUALITY, bundleOf(KEY_QUALITY to quality))
        dismiss()
    }

    companion object {
        const val KEY_QUALITY = "key_quality"
    }
}

