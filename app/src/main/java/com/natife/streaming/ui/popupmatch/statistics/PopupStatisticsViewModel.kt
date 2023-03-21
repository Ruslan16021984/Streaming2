package com.natife.streaming.ui.popupmatch.statistics

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.natife.streaming.R
import com.natife.streaming.base.BaseViewModel
import com.natife.streaming.data.Lexic
import com.natife.streaming.router.Router
import com.natife.streaming.usecase.LexisUseCase


abstract class PopupStatisticsViewModel : BaseViewModel() {
    abstract val strings: LiveData<List<Lexic>>

    abstract fun onStatisticClicked()
    abstract fun onFinishClicked()
}

class PopupStatisticsViewModelImpl(
    private val sport: Int,
    private val matchId: Int,
    private val router: Router,
    private val lexisUseCase: LexisUseCase
) : PopupStatisticsViewModel() {
    override val strings = MutableLiveData<List<Lexic>>()

    init {
        launch {
            val str = lexisUseCase.execute(
                R.integer.video,
                R.integer.teams,
                R.integer.table
            )
            strings.value = str
        }
    }

    override fun onStatisticClicked() {
        val direction =
            PopupStatisticsFragmentDirections.actionPopupStatisticsFragmentToPopupVideoFragment(
                sportId = sport,
                matchId = matchId
            )
        router.navigate(direction)
    }

    override fun onFinishClicked() {
        router.navigate(R.id.action_global_nav_main)
    }

}