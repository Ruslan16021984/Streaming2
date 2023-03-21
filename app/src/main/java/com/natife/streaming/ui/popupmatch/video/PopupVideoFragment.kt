package com.natife.streaming.ui.popupmatch.video


import android.annotation.SuppressLint
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.navigation.navGraphViewModels
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import com.natife.streaming.R
import com.natife.streaming.base.BaseFragment
import com.natife.streaming.ext.bindTeamImage
import com.natife.streaming.ext.predominantColorToGradient
import com.natife.streaming.ext.subscribe
import com.natife.streaming.ext.subscribeEvent
import com.natife.streaming.ui.main.MainActivity
import com.natife.streaming.ui.popupmatch.PopupSharedViewModel
import com.natife.streaming.ui.popupmatch.video.additionaly.TabAdditionallyFragment
import com.natife.streaming.ui.popupmatch.video.byplayers.TabByPlayersFragment
import com.natife.streaming.ui.popupmatch.video.langues.TabLanguagesFragment
import com.natife.streaming.ui.popupmatch.video.matchivents.TabMatchEventsFragment
import com.natife.streaming.ui.popupmatch.video.watch.TabWatchFragment
import com.natife.streaming.utils.findLexicText
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_popup_video.*
import org.koin.core.parameter.ParametersDefinition
import org.koin.core.parameter.parametersOf


class PopupVideoFragment : BaseFragment<PopupVideoViewModel>() {

    override fun getLayoutRes(): Int = R.layout.fragment_popup_video

    private val popupSharedViewModel: PopupSharedViewModel by navGraphViewModels(R.id.popupVideo)
    private var page = 0

    private var onPage: ViewPager2.OnPageChangeCallback? = null

    @SuppressLint("SetTextI18n")
    override fun onStart() {
        super.onStart()

        subscribe(viewModel.strings) { listLexics ->
            (activity as MainActivity).statistics_button?.text =
                listLexics.findLexicText(context, R.integer.statistics)
            val resourcesVideoNames = resources.getStringArray(R.array.popup_video_names)
            val popupVideoNames = arrayOf<String>(
                listLexics.findLexicText(context, R.integer.watch)
                    ?: resourcesVideoNames[0],
                listLexics.findLexicText(context, R.integer.additionally)
                    ?: resourcesVideoNames[1],
                listLexics.findLexicText(context, R.integer.by_players)
                    ?: resourcesVideoNames[2],
                listLexics.findLexicText(context, R.integer.match_events)
                    ?: resourcesVideoNames[3],
                listLexics.findLexicText(context, R.integer.languages)
                    ?: resourcesVideoNames[4]
            )
            viewModel.updateData(popupVideoNames)
        }

        popap_layout.visibility = View.VISIBLE
        (activity as MainActivity).score_text?.text = ""
        arguments?.getInt("matchId")?.let {
            popupSharedViewModel.matchId = it
        }
        arguments?.getInt("sportId")?.let {
            popupSharedViewModel.sportId = it
        }
        (activity as MainActivity).main_group?.visibility = View.GONE
        (activity as MainActivity).popup_group?.visibility = View.VISIBLE
        //Heading in the predominant team color
        (activity as MainActivity).mainMotion?.predominantColorToGradient("#CCCB312A")

        (activity as MainActivity).statistics_button?.apply {
            visibility = View.INVISIBLE
            setOnClickListener {
                viewModel.onStatisticClicked()
            }
        }
        (activity as MainActivity).name_first_team?.apply {
            isEnabled = false
            setOnClickListener {
                viewModel.onCommandClicked(1)
            }
        }
        (activity as MainActivity).name_second_team?.apply {
            isEnabled = false
            setOnClickListener {
                viewModel.onCommandClicked(2)
            }
        }

        subscribe(viewModel.tabList) {
            val popupVideoAdapter = PopupVideoFragmentAdapter(
                childFragmentManager,
                this.lifecycle, it.size
            )

            onPage = object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    position.apply {
                        page = this
                        popupSharedViewModel.startViewID.value?.let { start ->
                            it.forEachIndexed { index, _ ->
                                tab_layout.getTabAt(index)?.view?.nextFocusDownId = start[this]
                            }
                        }
                    }
                }
            }
            popup_video_pager.adapter = popupVideoAdapter
            onPage?.let{ onPage -> popup_video_pager.registerOnPageChangeCallback(onPage) }

            page.apply {
                popupSharedViewModel.startViewID.value?.let { start ->
                    it.forEachIndexed { index, _ ->
                        tab_layout.getTabAt(index)?.view?.nextFocusDownId = start[this]
                    }
                }
            }

            TabLayoutMediator(tab_layout, popup_video_pager) { tab, position ->
                tab.text = it[position]
            }.attach()
            tab_layout.getChildAt(0).requestFocus()
            tab_layout.getChildAt(0).isSelected = true
            (activity as MainActivity).name_first_team?.isEnabled = true
            (activity as MainActivity).name_second_team?.isEnabled = true
        }

        subscribe(viewModel.match) {
            popupSharedViewModel.setMatch(it)
            (activity as MainActivity).logo_first_team?.bindTeamImage(it.sportId, it.team1.id)
            (activity as MainActivity).name_first_team?.text = it.team1.name.take(3)
            (activity as MainActivity).logo_second_team?.bindTeamImage(it.sportId, it.team2.id)
            (activity as MainActivity).name_second_team?.text = it.team2.name.take(3)
            (activity as MainActivity).score_text?.text =
                if (it.isShowScore) "${it.team1.score} - ${it.team2.score}"
                else ""
        }
        subscribe(viewModel.info) {
            popupSharedViewModel.setMatchInfo(it)
        }
        subscribe(viewModel.fullVideoDuration) {
            popupSharedViewModel.setFullVideoDuration(it)
        }
        subscribe(viewModel.episodes) {
            popupSharedViewModel.setEpisodes(it)
        }
        subscribe(viewModel.team1) {
            popupSharedViewModel.setTeam1(it)
        }
        subscribe(viewModel.team2) {
            popupSharedViewModel.setTeam2(it)
        }

        subscribeEvent(popupSharedViewModel.playEpisode) {
            viewModel.play(episode = it)
        }
        subscribeEvent(popupSharedViewModel.playPlayList) {
            viewModel.play(playList = it)
        }
        subscribeEvent(popupSharedViewModel.playPlayListPlayers) {
            viewModel.play(playerPlayList = it)
        }
        subscribe(popupSharedViewModel.videoType) {
            viewModel.seVideoType(it)
        }

        subscribe(popupSharedViewModel.startViewID) { start ->
            page.apply {
                viewModel.tabList.value?.forEachIndexed { index, _ ->
                    tab_layout.getTabAt(index)?.view?.nextFocusDownId = start[this]
                }
            }
        }
    }

    override fun getParameters(): ParametersDefinition = {
        parametersOf(PopupVideoFragmentArgs.fromBundle(requireArguments()))
    }


    override fun onStop() {
        super.onStop()
        (activity as MainActivity).main_group?.visibility = View.VISIBLE
        (activity as MainActivity).popup_group?.visibility = View.GONE
        (activity as MainActivity).mainMotion?.predominantColorToGradient("#3560E1")
        onPage?.let { popup_video_pager.unregisterOnPageChangeCallback(it) }
    }

    inner class PopupVideoFragmentAdapter(
        fragmentManager: FragmentManager,
        lifecycle: Lifecycle,
        private val itemCount: Int
    ) : FragmentStateAdapter(fragmentManager, lifecycle) {
        override fun getItemCount(): Int {
            return itemCount
        }

        override fun createFragment(position: Int): Fragment {
            return when (viewModel.tabList.value?.get(position)) {
                viewModel.popupVideoNames[0] -> TabWatchFragment()
                viewModel.popupVideoNames[1] -> TabAdditionallyFragment()
                viewModel.popupVideoNames[2] -> TabByPlayersFragment()
                viewModel.popupVideoNames[3] -> TabMatchEventsFragment()
                viewModel.popupVideoNames[4] -> TabLanguagesFragment()
                else -> TabWatchFragment()
            }
        }
    }
}
