package com.natife.streaming.ui.search

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.Group
import androidx.core.view.isVisible
import androidx.leanback.widget.BaseGridView
import androidx.leanback.widget.BrowseFrameLayout
import androidx.leanback.widget.VerticalGridView
import androidx.navigation.navGraphViewModels
import com.natife.streaming.R
import com.natife.streaming.base.BaseFragment
import com.natife.streaming.base.EmptyViewModel
import com.natife.streaming.data.search.SearchResult
import com.natife.streaming.ext.subscribe
import com.natife.streaming.utils.findLexicText
import kotlinx.android.synthetic.main.fragment_mypreferences_new.*
import kotlinx.android.synthetic.main.fragment_search_new.*
import kotlinx.android.synthetic.main.fragment_search_result.*
import kotlinx.android.synthetic.main.view_side_menu_new.*
import timber.log.Timber

class SearchResultFragment(private val searchViewModel: SearchViewModel) : BaseFragment<EmptyViewModel>() {
    override fun getLayoutRes(): Int = R.layout.fragment_search_result
    private val searchResultViewModel: SearchResultViewModel by navGraphViewModels(R.id.nav_main)

    //    private val searchResultViewModel: SearchResultViewModel by activityViewModels()
    private var typeOfSearch: SearchResult.Type? = null
    private var playersNotFoundText = ""
    private var teamsNotFoundText = ""
    private var tournamentsNotFoundText = ""

    @SuppressLint("RestrictedApi")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.let {
            typeOfSearch = when (it.getInt(TYPE_OF_SEARCH)) {
                0 -> SearchResult.Type.PLAYER
                1 -> SearchResult.Type.TEAM
                2 -> SearchResult.Type.TOURNAMENT
                else -> null
            }
        }
        val adapter: SearchAdapter by lazy { SearchAdapter(searchResultViewModel, typeOfSearch) }
        tab_search_recycler.windowAlignment = VerticalGridView.WINDOW_ALIGN_BOTH_EDGE
        tab_search_recycler.isFocusable = false
        tab_search_recycler.setNumColumns(3)
        tab_search_recycler.focusScrollStrategy = BaseGridView.FOCUS_SCROLL_ITEM
        tab_search_recycler.adapter = adapter
        adapter.onClick = {
            searchResultViewModel.searchResultClicked(it)
        }

        subscribe(searchViewModel.strings) {
            // todo: add lexics not_find_players & not_find_teams
            playersNotFoundText = getString(R.string.text_not_find_players)
            teamsNotFoundText = getString(R.string.text_not_find_teams)
            tournamentsNotFoundText = it.findLexicText(context, R.integer.tournaments_not_found)
                ?: getString(R.string.text_not_find_tour)
        }

        subscribe(searchResultViewModel.resultsPlayer) {
            if (typeOfSearch == SearchResult.Type.PLAYER) {
                adapter.submitList(it)
                textView_match_not_find_search.text = playersNotFoundText
                textView_match_not_find_search.isVisible = it.isEmpty()
            }
        }

        subscribe(searchResultViewModel.resultsTeam) {
            if (typeOfSearch == SearchResult.Type.TEAM) {
                adapter.submitList(it)
                textView_match_not_find_search.text = teamsNotFoundText
                textView_match_not_find_search.isVisible = it.isEmpty()
            }
        }

        subscribe(searchResultViewModel.resultsTournament) {
            if (typeOfSearch == SearchResult.Type.TOURNAMENT) {
                adapter.submitList(it)
                textView_match_not_find_search.text = tournamentsNotFoundText
                textView_match_not_find_search.isVisible = it.isEmpty()
            }
        }

        tab_search_recycler_layout.onFocusSearchListener =
            BrowseFrameLayout.OnFocusSearchListener { focused, direction ->
                Timber.tag("TAG").d("$focused, $direction")
                if (tab_search_recycler.hasFocus() && direction == 17) {
                    return@OnFocusSearchListener requireActivity().findViewById<Group>(R.id.menuHome) //((activity as MainActivity).menuHome as LinearLayout).view
                } else
                    return@OnFocusSearchListener null
            }
        textView_match_not_find_search.isVisible = false
    }


    @SuppressLint("RestrictedApi")
    override fun onResume() {
        super.onResume()
        tab_search_recycler.scrollToPosition(0)
        if (tab_search_recycler.findViewHolderForAdapterPosition(0)?.itemView != null) {
            (tab_search_recycler.findViewHolderForAdapterPosition(0)?.itemView as ConstraintLayout).requestFocus()
        }
    }

    override fun onPause() {
        super.onPause()
    }

    companion object {
        private const val TYPE_OF_SEARCH = "TYPE_OF_SEARCH"

        @JvmStatic
        fun newInstance(type: Int, searchViewModel: SearchViewModel) = SearchResultFragment(searchViewModel).apply {
            arguments = Bundle().apply {
                putInt(TYPE_OF_SEARCH, type)
            }
        }
    }

}