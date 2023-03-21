package com.natife.streaming.ui.home

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.leanback.widget.BaseGridView
import androidx.leanback.widget.BrowseFrameLayout
import androidx.leanback.widget.VerticalGridView
import com.natife.streaming.R
import com.natife.streaming.base.BaseFragment
import com.natife.streaming.ext.subscribe
import com.natife.streaming.ext.subscribeEvent
import com.natife.streaming.ui.main.MainActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_account.*
import kotlinx.android.synthetic.main.fragment_home.*
import timber.log.Timber
import com.natife.streaming.ui.home.TournamentItem.RegularTournamentItem
import com.natife.streaming.utils.findLexicText
import kotlinx.android.synthetic.main.fragment_favorites_new.*

class HomeFragment : BaseFragment<HomeViewModel>() {
    override fun getLayoutRes() = R.layout.fragment_home

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        subscribe(viewModel.strings) {
            textView_match_not_find.text = it.findLexicText(context, R.integer.matches_not_found)
        }
    }

    @SuppressLint("RestrictedApi")
    override fun onStart() {
        super.onStart()
        val adapter = TournamentAdapter(viewModel)
        homeRecycler.windowAlignment = VerticalGridView.WINDOW_ALIGN_BOTH_EDGE;
        homeRecycler.apply {
            this.requestFocus()
            isSelected = true
        }
        homeRecycler.isFocusable = false
        homeRecycler.adapter = adapter
        homeRecycler.setNumColumns(1)
        homeRecycler.focusScrollStrategy = BaseGridView.FOCUS_SCROLL_ITEM

        subscribe(viewModel.listTournament) {
            var list = emptyList<TournamentItem>()
            it.forEach { tournamentItem ->
                val matches = tournamentItem.match
                if (matches.isNotEmpty()) list = list.plus(
                    if (tournamentItem is RegularTournamentItem) tournamentItem.copy(match = matches)
                    else tournamentItem
                )
            }
            homeRecycler.scrollToPosition(0)
            adapter.submitList(list)
            textView_match_not_find.isVisible = (it.isEmpty()) && (!viewModel.load)
        }
        subscribeEvent(viewModel.isLoadData) {
            progress_icon.visibility = if (it) View.GONE else View.VISIBLE
        }
        home_recycler_layout.onFocusSearchListener =
            BrowseFrameLayout.OnFocusSearchListener { focused, direction ->
                if (homeRecycler.hasFocus() && direction == 33) {
                    Timber.tag("TAG").d("$focused, $direction")
                    return@OnFocusSearchListener (activity as MainActivity).day_of_weektext_text
                        ?: null
                } else return@OnFocusSearchListener null
            }
    }
}