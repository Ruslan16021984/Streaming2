package com.natife.streaming.ui.popupmatch.video.watch

import android.annotation.SuppressLint
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.leanback.widget.BaseGridView
import androidx.leanback.widget.VerticalGridView
import androidx.navigation.navGraphViewModels
import com.google.android.material.card.MaterialCardView
import com.natife.streaming.R
import com.natife.streaming.base.BaseFragment
import com.natife.streaming.base.EmptyViewModel
import com.natife.streaming.ext.subscribe
import com.natife.streaming.ext.toDisplayTime
import com.natife.streaming.ui.popupmatch.PopupSharedViewModel
import kotlinx.android.synthetic.main.fragment_tab_watch.*


class TabWatchFragment : BaseFragment<EmptyViewModel>() {
    override fun getLayoutRes(): Int = R.layout.fragment_tab_watch
    private val popupSharedViewModel: PopupSharedViewModel by navGraphViewModels(R.id.popupVideo)
    private val watchList = mutableListOf<WatchFill>()
    private val adapter: TabWatchAdapter by lazy { TabWatchAdapter(popupSharedViewModel) }

    @SuppressLint("RestrictedApi")
    override fun onStart(){
        super.onStart()
        watchList.clear()
        tab_watch_recycler.windowAlignment = VerticalGridView.WINDOW_ALIGN_BOTH_EDGE
        tab_watch_recycler.isFocusable = false
        tab_watch_recycler.setNumColumns(1)
        tab_watch_recycler.focusScrollStrategy = BaseGridView.FOCUS_SCROLL_ITEM
        tab_watch_recycler.adapter = adapter
        adapter.onClick = {
            requireActivity().findViewById<ConstraintLayout>(R.id.popap_layout).visibility = View.INVISIBLE
            when (it) {
                is WatchFill.FullGame -> popupSharedViewModel.fullMatch()
                is WatchFill.BallInPlay -> popupSharedViewModel.ballInPlay()
                is WatchFill.Highlights -> popupSharedViewModel.review()
                is WatchFill.FieldGoals -> popupSharedViewModel.goals()
            }
        }

        subscribe(popupSharedViewModel.matchInfo) {
            if (watchList.isEmpty()) watchList.add(
                0,
                WatchFill.FullGame(it.translates.fullGameTranslate, "")
            )
            else {
                (watchList[0] as WatchFill.FullGame).time.apply {
                    watchList[0] = WatchFill.FullGame(it.translates.fullGameTranslate, this)
                }
            }

            if (watchList.getOrNull(1) == null) {
                watchList.add(
                    1, WatchFill.BallInPlay(
                        it.translates.ballInPlayTranslate,
                        it.ballInPlayDuration.toDisplayTime()
                    )
                )
            } else {
                watchList[1] = WatchFill.BallInPlay(
                    it.translates.ballInPlayTranslate,
                    it.ballInPlayDuration.toDisplayTime()
                )
            }
            if (watchList.getOrNull(2) == null) {
                watchList.add(
                    2,
                    WatchFill.Highlights(
                        it.translates.highlightsTranslate,
                        it.highlightsDuration.toDisplayTime()
                    )
                )
            } else {
                watchList[2] = WatchFill.Highlights(
                    it.translates.highlightsTranslate,
                    it.highlightsDuration.toDisplayTime()
                )
            }
            if (watchList.getOrNull(3) == null) {
                watchList.add(
                    3,
                    WatchFill.FieldGoals(
                        it.translates.goalsTranslate,
                        it.goalsDuration.toDisplayTime()
                    )
                )
            } else {
                watchList[3] = WatchFill.FieldGoals(
                    it.translates.goalsTranslate,
                    it.goalsDuration.toDisplayTime()
                )
            }
            adapter.submitList(watchList)
            adapter.notifyDataSetChanged()
        }

        subscribe(popupSharedViewModel.fullVideoDuration) {
            if (watchList.isEmpty()) watchList.add(0, WatchFill.FullGame("", it.toDisplayTime()))
            else {
                (watchList[0] as WatchFill.FullGame).name.apply {
                    watchList[0] = WatchFill.FullGame(this, it.toDisplayTime())
                }
            }
            adapter.submitList(watchList)
            adapter.notifyDataSetChanged()
        }
    }

    override fun onResume() {
        super.onResume()
        tab_watch_recycler.scrollToPosition(0)
        if (tab_watch_recycler.findViewHolderForAdapterPosition(0)?.itemView != null) {
            (tab_watch_recycler.findViewHolderForAdapterPosition(0)?.itemView as MaterialCardView).requestFocus()
        }
    }

    override fun onPause() {
        super.onPause()
    }
}

sealed class WatchFill() {
    data class FullGame(val name: String, val time: String = "") : WatchFill()
    data class BallInPlay(val name: String, val time: String = "") : WatchFill()
    data class Highlights(val name: String, val time: String = "") : WatchFill()
    data class FieldGoals(val name: String, val time: String = "") : WatchFill()
}
