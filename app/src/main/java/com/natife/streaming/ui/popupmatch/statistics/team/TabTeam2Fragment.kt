package com.natife.streaming.ui.popupmatch.statistics.team

import android.os.Bundle
import android.view.View
import androidx.navigation.navGraphViewModels
import com.natife.streaming.R
import com.natife.streaming.base.BaseFragment
import com.natife.streaming.base.EmptyViewModel
import com.natife.streaming.ui.popupmatch.PopupSharedViewModel

class TabTeam2Fragment : BaseFragment<EmptyViewModel>() {
    override fun getLayoutRes(): Int = R.layout.fragment_tab_team
    private val popupSharedViewModel: PopupSharedViewModel by navGraphViewModels(R.id.popupVideo)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }
}