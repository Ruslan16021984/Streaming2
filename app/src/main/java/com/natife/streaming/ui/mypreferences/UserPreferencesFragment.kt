package com.natife.streaming.ui.mypreferences

import android.annotation.SuppressLint
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.addCallback
import androidx.core.view.isVisible
import androidx.leanback.widget.BaseGridView
import androidx.leanback.widget.BrowseFrameLayout
import androidx.lifecycle.lifecycleScope
import com.natife.streaming.PreferencesArgs
import com.natife.streaming.R
import com.natife.streaming.base.BaseFragment
import com.natife.streaming.data.dto.tournament.TournamentTranslateDTO
import com.natife.streaming.db.entity.toTournamentTranslateDTO
import com.natife.streaming.ext.hideKeyboard
import com.natife.streaming.ext.predominantColorToGradient
import com.natife.streaming.ext.subscribe
import com.natife.streaming.ui.billing.BillingFragmentArgs
import com.natife.streaming.utils.findLexicText
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.custom_playback_control.view.*
import kotlinx.android.synthetic.main.fragment_mypreferences_new.*
import kotlinx.android.synthetic.main.fragment_search_new.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.koin.core.parameter.ParametersDefinition
import org.koin.core.parameter.parametersOf


class UserPreferencesFragment : BaseFragment<UserPreferencesViewModel>() {
    override fun getLayoutRes() = R.layout.fragment_mypreferences_new
    private val sportAdapter by lazy {
        SportsAdapter(viewModel::kindOfSportClicked, viewModel::kindOfSportSelected)
    }
    private val tournamentAdapter by lazy {
        TournamentAdapter(viewModel::listOfTournamentsClicked)
    }
    private var temporalList: List<TournamentTranslateDTO>? = null

    //    var isSearchMode = false
    private var searchJob: Job? = null
    private var searchText: String = ""
    private var positionTournament = 0

    @SuppressLint("RestrictedApi")
    override fun onStart() {
        super.onStart()
        //Heading in the predominant team color
        topConstraintLayout.predominantColorToGradient("#CB312A")
        kindsOfSportsRecyclerView.isFocusable = false
        kindsOfSportsRecyclerView.adapter = sportAdapter
        kindsOfSportsRecyclerView.setNumColumns(1)
        kindsOfSportsRecyclerView.focusScrollStrategy = BaseGridView.FOCUS_SCROLL_ITEM

        listOfTournamentsRecyclerView.isFocusable = false
        listOfTournamentsRecyclerView.adapter = tournamentAdapter
        listOfTournamentsRecyclerView.setNumColumns(2)
        listOfTournamentsRecyclerView.focusScrollStrategy = BaseGridView.FOCUS_SCROLL_ITEM
//        listOfTournamentsRecyclerView.onRequestFocusInDescendants(View.FOCUS_LEFT, null)
        loadTournament("")

        search_pref_text_field?.editText?.setOnEditorActionListener(
            TextView.OnEditorActionListener { v, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    searchText = search_pref_text_field?.editText?.text.toString()
                    loadTournament(searchText)
                    v.hideKeyboard()
                    return@OnEditorActionListener true
                }
                false
            })

        subscribe(viewModel.strings) {
            tvMyPreferences.text = it.findLexicText(context, R.integer.my_preference)
            applay_button.text = it.findLexicText(context, R.integer.apply)
        }

        subscribe(viewModel.allUserPreferencesInSport) {
            viewModel.getTranslateLexic(it, resources.getString(R.string.lang))
        }

        subscribe(viewModel.sportsList) {
            sportAdapter.submitList(it)
        }
        subscribe(viewModel.positionPref){
            listOfTournamentsRecyclerView.scrollToPosition(it)
        }

        subscribe(viewModel.sportsSelected) { selected ->
            (0..(viewModel.sportsList.value?.size ?: 0)).forEach { position ->
                kindsOfSportsRecyclerView?.layoutManager?.findViewByPosition(position)?.let {
                    it.isSelected = (position) == viewModel.sportsViewSelectedPosition
                }
            }
            loadTournament(searchText)
        }

        search_button.setOnClickListener {
            if (search_layout?.isVisible == true) {
                search_layout?.visibility = View.GONE
                searchText = ""
                loadTournament(searchText)
            } else {
                search_layout?.visibility = View.VISIBLE
                searchText = ""
                loadTournament(searchText)
            }
        }
        applay_button.setOnClickListener {
            viewModel.applyMypreferencesClicked()
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            viewModel.onFinishClicked()
        }

        listItem.onFocusSearchListener =
            BrowseFrameLayout.OnFocusSearchListener { _, direction ->
//                Timber.tag("TAG").d("$focused, $direction")
                if (listOfTournamentsRecyclerView.hasFocus() && direction == 17) {
                    viewModel.sportsViewSelectedPosition?.let {
                        kindsOfSportsRecyclerView.selectedPosition = it
                    }
                    return@OnFocusSearchListener if (kindsOfSportsRecyclerView?.selectedPosition != null) kindsOfSportsRecyclerView?.selectedPosition?.let {
                        kindsOfSportsRecyclerView?.getChildAt(
                            it
                        )
                    } else null
                } else if (kindsOfSportsRecyclerView.hasFocus() && direction == 33) {
                    viewModel.kindOfSportSelected(null, null)
                    (0..(viewModel.sportsList.value?.size ?: 0)).forEach { position ->
                        kindsOfSportsRecyclerView?.layoutManager?.findViewByPosition(position)
                            ?.let {
                                it.isSelected = (position) == viewModel.sportsViewSelectedPosition
                            }
                    }
                    return@OnFocusSearchListener null
                } else
                    return@OnFocusSearchListener null
            }
    }

    private fun loadTournament(text: String) {
        requireActivity().findViewById<ProgressBar>(
            R.id.load_progress
        ).visibility = View.VISIBLE

        tournamentAdapter.submitList(
            null
        )
        searchJob?.cancel()
        searchJob = lifecycleScope.launch {
            requireActivity().findViewById<ProgressBar>(
                R.id.load_progress
            ).visibility = View.VISIBLE
            requireActivity().findViewById<TextView>(
                R.id.textView_match_not_find_preference
            ).visibility = View.GONE
            viewModel.findClicked(
                text, resources.getString(R.string.lang)
            ).collect {
                if (it.isEmpty()) {
                    requireActivity().findViewById<ProgressBar>(
                        R.id.load_progress
                    ).visibility = View.GONE
                    requireActivity().findViewById<TextView>(
                        R.id.textView_match_not_find_preference
                    ).visibility = View.VISIBLE
                } else {
                    requireActivity().findViewById<ProgressBar>(
                        R.id.load_progress
                    ).visibility = View.GONE
                    requireActivity().findViewById<TextView>(
                        R.id.textView_match_not_find_preference
                    ).visibility = View.GONE
                }
                val list = it.toTournamentTranslateDTO(
                    resources.getString(
                        R.string.lang
                    )
                )
                tournamentAdapter.submitList(
                    list
                )
            }
        }
    }

    override fun getParameters(): ParametersDefinition ={
        parametersOf(PreferencesArgs.fromBundle(requireArguments()))
    }
}


