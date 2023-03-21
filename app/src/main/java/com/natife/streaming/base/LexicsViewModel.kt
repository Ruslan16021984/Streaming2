package com.natife.streaming.base

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.natife.streaming.data.Lexic

abstract class LexisViewModel: BaseViewModel() {
    abstract val strings: LiveData<List<Lexic>>
}

class LexisViewModelImpl : LexisViewModel() {
    override val strings = MutableLiveData<List<Lexic>>()
}