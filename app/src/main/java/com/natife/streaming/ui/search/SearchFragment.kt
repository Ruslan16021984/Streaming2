package com.natife.streaming.ui.search

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.LinearLayout
import android.widget.TextView.OnEditorActionListener
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.leanback.widget.BrowseFrameLayout
import androidx.lifecycle.Lifecycle
import androidx.navigation.navGraphViewModels
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import com.natife.streaming.R
import com.natife.streaming.base.BaseFragment
import com.natife.streaming.data.search.SearchResult
import com.natife.streaming.ext.hideKeyboard
import com.natife.streaming.ext.predominantColorToGradient
import com.natife.streaming.ext.subscribe
import com.natife.streaming.ext.subscribeEvent
import com.natife.streaming.ui.main.MainActivity
import com.natife.streaming.utils.findLexicText
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_popup_video.*
import kotlinx.android.synthetic.main.fragment_search_new.*
import kotlinx.android.synthetic.main.fragment_search_result.*
import kotlinx.android.synthetic.main.fragment_settings_page.*
import kotlinx.coroutines.*


class SearchFragment : BaseFragment<SearchViewModel>() {
    override fun getLayoutRes(): Int = R.layout.fragment_search_new
    private lateinit var searchTabNames: Array<String>
    private val searchResultViewModel: SearchResultViewModel by navGraphViewModels(R.id.nav_main)

    //    private val searchResultViewModel: SearchResultViewModel by activityViewModels()
    private var positionMotionLayout: String? = null
    private var page = 0
    val onPage = object : ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
            super.onPageSelected(position)
            position.apply {
                page = this
                searchResultViewModel.startViewID.value?.let { start ->
                    search_tab_layout.getTabAt(0)?.view?.nextFocusDownId = start[this]
                    search_tab_layout.getTabAt(1)?.view?.nextFocusDownId = start[this]
                    search_tab_layout.getTabAt(2)?.view?.nextFocusDownId = start[this]
                }

            }
        }
    }

    private val transitionListener = object :
        MotionLayout.TransitionListener {
        override fun onTransitionStarted(p0: MotionLayout?, p1: Int, p2: Int) {
        }

        override fun onTransitionChange(p0: MotionLayout?, p1: Int, p2: Int, p3: Float) {
        }

        override fun onTransitionCompleted(p0: MotionLayout?, p1: Int) {
            when (p1) {
                R.id.start -> {
                    if (positionMotionLayout.equals("end")) {
                        search_tab_layout?.getTabAt(0)?.view?.let {
                            it.requestFocus()
                        }
                    }
                    positionMotionLayout = "start"
                }
                R.id.end -> {
                    positionMotionLayout = "end"
                }
            }
        }

        override fun onTransitionTrigger(p0: MotionLayout?, p1: Int, p2: Boolean, p3: Float) {
        }

    }

    @SuppressLint("RestrictedApi")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        subscribe(viewModel.strings) {
            val resourcesSearchNames = resources.getStringArray(R.array.search_names)
            val searchNames = arrayOf(
                it.findLexicText(context, R.integer.players)
                    ?: resourcesSearchNames[0],
                it.findLexicText(context, R.integer.teams)
                    ?: resourcesSearchNames[1],
                it.findLexicText(context, R.integer.tournaments)
                    ?: resourcesSearchNames[2]
            )
            searchTabNames = searchNames
            val searchFragmentAdapter = SearchFragmentAdapter(
                childFragmentManager,
                this.lifecycle,
                searchTabNames.size
            )
            search_pager.adapter = searchFragmentAdapter
            search_pager.registerOnPageChangeCallback(onPage)

            TabLayoutMediator(search_tab_layout, search_pager) { tab, position ->
                tab.text = searchTabNames[position]
            }.attach()
        }

        search_tab_layout.getTabAt(0)?.view?.apply {
            this.requestFocus()
            isSelected = true
        }

    }

    override fun onResume() {
        ((activity as MainActivity).mainMotion as MotionLayout).addTransitionListener(
            transitionListener
        )
        requireActivity().mainMenu.isFocusable = false
        requireActivity().mainMenu.isFocusable = true

        (activity as MainActivity).search_text_field?.editText?.setText(searchResultViewModel.searchText.value?: "")
        actionSearch()
        super.onResume()
    }



    override fun onStart(){
        super.onStart()
        (activity as MainActivity).main_group?.visibility = View.GONE
        (activity as MainActivity).main_background_group?.visibility = View.GONE
        (activity as MainActivity).search_background_group?.visibility = View.VISIBLE
        //Heading in the predominant team color
        (activity as MainActivity).mainMotion?.predominantColorToGradient("#CB312A")
        (activity as MainActivity).search_text_field?.editText?.setText(searchResultViewModel.searchText.value?: "")




        tab_layout_search.onFocusSearchListener =
            BrowseFrameLayout.OnFocusSearchListener { focused, direction ->
                if (search_tab_layout.hasFocus())
                    when (direction) {
                        130 -> { // низ
                            when (focused) {
                                search_tab_layout.getTabAt(0)?.view -> {
                                    val temp = searchResultViewModel.startViewID.value?.get(0) ?: -1
                                    val temp1 = searchResultViewModel.startViewID.value?.get(1) ?: -1
                                    val temp2 = searchResultViewModel.startViewID.value?.get(2) ?: -1
                                    if (temp >= 0) return@OnFocusSearchListener tab_layout_search.findViewById(
                                        temp
                                    ) else if(temp1>=0) return@OnFocusSearchListener tab_layout_search.findViewById(
                                        temp1
                                    ) else if(temp2>=0) return@OnFocusSearchListener tab_layout_search.findViewById(
                                        temp2
                                    ) else return@OnFocusSearchListener focused
                                }
                                search_tab_layout.getTabAt(1)?.view -> {
                                    val temp = searchResultViewModel.startViewID.value?.get(0) ?: -1
                                    val temp1 = searchResultViewModel.startViewID.value?.get(1) ?: -1
                                    val temp2 = searchResultViewModel.startViewID.value?.get(2) ?: -1
                                    if (temp >= 0) return@OnFocusSearchListener tab_layout_search.findViewById(
                                        temp
                                    ) else if(temp1>=0) return@OnFocusSearchListener tab_layout_search.findViewById(
                                        temp1
                                    ) else if(temp2>=0) return@OnFocusSearchListener tab_layout_search.findViewById(
                                        temp2
                                    ) else return@OnFocusSearchListener focused
                                }
                                search_tab_layout.getTabAt(2)?.view -> {
                                    val temp = searchResultViewModel.startViewID.value?.get(0) ?: -1
                                    val temp1 = searchResultViewModel.startViewID.value?.get(1) ?: -1
                                    val temp2 = searchResultViewModel.startViewID.value?.get(2) ?: -1
                                    if (temp >= 0) return@OnFocusSearchListener tab_layout_search.findViewById(
                                        temp
                                    ) else if(temp1>=0) return@OnFocusSearchListener tab_layout_search.findViewById(
                                        temp1
                                    ) else if(temp2>=0) return@OnFocusSearchListener tab_layout_search.findViewById(
                                        temp2
                                    ) else return@OnFocusSearchListener focused
                                }
                                else -> return@OnFocusSearchListener focused
                            }
                        }
                        17 -> {
                            if (focused == search_tab_layout.getTabAt(0)?.view) {
                                return@OnFocusSearchListener requireActivity().findViewById<LinearLayout>(
                                    R.id.menuHome
                                )
                            } else return@OnFocusSearchListener null
                        }
                        else -> return@OnFocusSearchListener null
                    }
                else
                    return@OnFocusSearchListener null
            }



        (activity as MainActivity).search_text_field?.editText?.onFocusChangeListener =
            View.OnFocusChangeListener { v, hasFocus ->
                if (!hasFocus) v.hideKeyboard()
            }
        (activity as MainActivity).search_text_field?.editText?.setOnEditorActionListener(
            OnEditorActionListener { _, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    actionSearch()
                    return@OnEditorActionListener true
                }
                false
            })


        subscribe(viewModel.resultsPlayer) {
            searchResultViewModel.setResultsPlayer(it)
            load_progress.visibility = View.GONE
        }
        subscribe(viewModel.resultsTeam) {
            searchResultViewModel.setResultsTeam(it)
            load_progress.visibility = View.GONE
        }
        subscribe(viewModel.resultsTournament) {
            searchResultViewModel.setResultsTournament(it)
            load_progress.visibility = View.GONE
        }

        subscribe(viewModel.setupTypeSearchResult) { searchType ->
            when (searchType) {
                SearchResult.Type.PLAYER -> search_tab_layout.getTabAt(0)?.select()
                SearchResult.Type.TEAM -> search_tab_layout.getTabAt(1)?.select()
                SearchResult.Type.TOURNAMENT -> search_tab_layout.getTabAt(2)?.select()
            }
        }

        subscribe(searchResultViewModel.startViewID) { start ->
            page.apply {
                search_tab_layout.getTabAt(0)?.view?.nextFocusDownId = start[this]
                search_tab_layout.getTabAt(1)?.view?.nextFocusDownId = start[this]
                search_tab_layout.getTabAt(2)?.view?.nextFocusDownId = start[this]
            }
        }
        subscribeEvent(searchResultViewModel.searchResultClicked, viewModel::select)
    }

    private fun actionSearch() {
        (activity as MainActivity).search_text_field?.editText?.clearFocus()
        load_progress.visibility = View.VISIBLE
        viewModel.search((activity as MainActivity).search_text_field?.editText?.text.toString())
        searchResultViewModel.setSearchText((activity as MainActivity).search_text_field?.editText?.text.toString())
    }

    override fun onStop() {
        super.onStop()
        (activity as MainActivity).main_group?.visibility = View.VISIBLE
        (activity as MainActivity).mainMotion?.predominantColorToGradient("#3560E1")
        (activity as MainActivity).main_background_group?.visibility = View.VISIBLE
        (activity as MainActivity).search_background_group?.visibility = View.GONE
        search_pager.unregisterOnPageChangeCallback(onPage)
    }

    inner class SearchFragmentAdapter(
        fragmentManager: FragmentManager,
        lifecycle: Lifecycle,
        private val itemCount: Int
    ) : FragmentStateAdapter(fragmentManager, lifecycle) {
        override fun getItemCount(): Int {
            return itemCount
        }

        override fun createFragment(position: Int): Fragment {
            return SearchResultFragment.newInstance(position, viewModel)
        }
    }
}