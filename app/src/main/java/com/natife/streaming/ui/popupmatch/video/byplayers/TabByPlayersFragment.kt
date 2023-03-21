package com.natife.streaming.ui.popupmatch.video.byplayers

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.leanback.widget.BaseGridView
import androidx.navigation.navGraphViewModels
import com.google.android.material.card.MaterialCardView
import com.natife.streaming.R
import com.natife.streaming.base.BaseFragment
import com.natife.streaming.base.EmptyViewModel
import com.natife.streaming.ext.subscribe
import com.natife.streaming.ui.popupmatch.PopupSharedViewModel
import kotlinx.android.synthetic.main.fragment_tab_by_players.*


class TabByPlayersFragment : BaseFragment<EmptyViewModel>() {
    override fun getLayoutRes(): Int = R.layout.fragment_tab_by_players
    private val popupSharedViewModel: PopupSharedViewModel by navGraphViewModels(R.id.popupVideo)
    private val teamAdapter1: PlayerAdapter by lazy { PlayerAdapter(popupSharedViewModel) }
    private val teamAdapter2: PlayerAdapter by lazy { PlayerAdapter() }

    @SuppressLint("RestrictedApi")
    override fun onStart(){
        super.onStart()
        firstTeamRecycler.windowAlignment = BaseGridView.WINDOW_ALIGN_BOTH_EDGE
        firstTeamRecycler.isFocusable = false
        firstTeamRecycler.setNumColumns(1)
        firstTeamRecycler.focusScrollStrategy = BaseGridView.FOCUS_SCROLL_ITEM
        firstTeamRecycler.adapter = teamAdapter1
        firstTeamRecycler.scrollToPosition(0)

        secondTeamRecycler.windowAlignment = BaseGridView.WINDOW_ALIGN_BOTH_EDGE
        secondTeamRecycler.isFocusable = false
        secondTeamRecycler.setNumColumns(1)
        secondTeamRecycler.focusScrollStrategy = BaseGridView.FOCUS_SCROLL_ITEM
        secondTeamRecycler.adapter = teamAdapter2
        firstTeamRecycler.scrollToPosition(0)

        subscribe(popupSharedViewModel.team1, teamAdapter1::submitList)
        subscribe(popupSharedViewModel.team2, teamAdapter2::submitList)

        teamAdapter1.onClick = {
            popupSharedViewModel.player(it)
        }
        teamAdapter2.onClick = {
            popupSharedViewModel.player(it)
        }
    }

    override fun onResume() {
        super.onResume()
        firstTeamRecycler.scrollToPosition(0)
        if (firstTeamRecycler.findViewHolderForAdapterPosition(0)?.itemView != null) {
            (firstTeamRecycler.findViewHolderForAdapterPosition(0)?.itemView as MaterialCardView).requestFocus()
        }
    }

    override fun onPause() {
        super.onPause()

    }
}