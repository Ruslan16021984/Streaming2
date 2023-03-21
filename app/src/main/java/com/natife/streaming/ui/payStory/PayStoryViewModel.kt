package com.natife.streaming.ui.payStory

import com.natife.streaming.R
import com.natife.streaming.base.BaseViewModel
import com.natife.streaming.router.Router

abstract class PayStoryViewModel : BaseViewModel() {
    abstract fun onBackToAccountClicked()
}
class PayStoryViewModelImpl(
    private val router: Router
) : PayStoryViewModel() {

    override fun onBackToAccountClicked() {
        router.navigate(R.id.accountFragment)
    }
}