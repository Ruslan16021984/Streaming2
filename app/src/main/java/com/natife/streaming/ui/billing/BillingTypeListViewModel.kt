package com.natife.streaming.ui.billing

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.natife.streaming.base.BaseViewModel

class BillingTypeListViewModel() : BaseViewModel() {
    private val _startViewID = MutableLiveData<ArrayList<Int>>()
    val startViewID: LiveData<ArrayList<Int>> = _startViewID
    fun setStartId(startId: ArrayList<Int>) {
        _startViewID.value = startId
    }

    fun resetStartId() {
        _startViewID.value =
            arrayListOf(
                View.NO_ID,
                View.NO_ID,
                View.NO_ID,
                View.NO_ID,
                View.NO_ID
            )
    }

    private val _monthList = MutableLiveData<List<OfferData>>()
    val monthList: LiveData<List<OfferData>> = _monthList

    private val _yearList = MutableLiveData<List<OfferData>>()
    val yearList: LiveData<List<OfferData>> = _yearList

    private val _payPerViewList = MutableLiveData<List<OfferData>>()
    val payPerViewList: LiveData<List<OfferData>> = _payPerViewList

    private val _searchResultClicked = MutableLiveData<OfferData>()
    val searchResultClicked: LiveData<OfferData> = _searchResultClicked

    fun setMonthList(monthList: List<OfferData>) {
        _monthList.value = monthList
    }

    fun setYearList(yearList: List<OfferData>) {
        _yearList.value = yearList
    }

    fun setPayPerViewList(payPerViewList: List<OfferData>) {
        _payPerViewList.value = payPerViewList
    }

    fun searchResultClicked(searchResultClicked: OfferData) {
        _searchResultClicked.value = searchResultClicked
    }

    fun select(){
        _searchResultClicked.value = OfferData()
    }
}