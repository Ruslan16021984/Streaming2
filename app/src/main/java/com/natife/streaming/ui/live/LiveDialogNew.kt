package com.natife.streaming.ui.live

import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.activity.addCallback
import com.natife.streaming.base.BaseDialog
import com.natife.streaming.ext.subscribe
import com.natife.streaming.ext.toDisplayTime
import com.natife.streaming.utils.findLexicText
import kotlinx.android.synthetic.main.dialog_live_new.*
import org.koin.core.parameter.ParametersDefinition
import org.koin.core.parameter.parametersOf
import com.natife.streaming.*

class LiveDialogNew : BaseDialog<LiveViewModel>() {
    override fun getLayoutRes(): Int = R.layout.dialog_live_new

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.Theme_App_Dialog_FullScreen)
    }

    override fun getParameters(): ParametersDefinition = {
        parametersOf(LiveDialogNewArgs.fromBundle(requireArguments()))
    }

    override fun onResume() {
        activity?.window?.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        super.onResume()
    }

    private var continueWatchText = ""

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        subscribeViewModels()

        tvWatchLive.setOnClickListener {
//            viewModel.play(WatchType.WATCH_LIVE)
        }
        tvWatchFromStart.setOnClickListener {
//            viewModel.play(WatchType.WATCH_FROM_START)
        }
        tvContinueWatch.setOnClickListener {
//            viewModel.play(WatchType.CONTINUE_WATCH)
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            viewModel.onBackClicked()
        }

        arguments?.getString("title")?.let {
            tvTitle.text = it
        }
    }

    private fun subscribeViewModels() {

        subscribe(viewModel.strings) {
            tvWatchLive.text = it.findLexicText(context, R.integer.watch_live_stream)
            tvWatchFromStart.text = it.findLexicText(context, R.integer.watch_from_the_start)
            continueWatchText = it.findLexicText(context, R.integer.continue_with)
                ?: resources.getString(R.string.continue_with)
        }

        subscribe(viewModel.currentPositionLiveData) { seconds ->
            continueWatchText = continueWatchText
                .plus(" ")
                .plus(seconds.toLong().toDisplayTime())
            tvContinueWatch.text = continueWatchText
        }
    }
}