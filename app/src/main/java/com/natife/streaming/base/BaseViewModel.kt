package com.natife.streaming.base

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.natife.streaming.SingleLiveEvent
import com.natife.streaming.data.match.Match
import com.natife.streaming.router.Router
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import timber.log.Timber

abstract class BaseViewModel : ViewModel() {

    private val _defaultErrorLiveData = SingleLiveEvent<Throwable>()
    val defaultErrorLiveData: LiveData<Throwable> = _defaultErrorLiveData

    private val _defaultLoadingLiveData = MutableLiveData<Boolean>()
    val defaultLoadingLiveData: LiveData<Boolean> = _defaultLoadingLiveData

    protected val defaultErrorHandler = CoroutineExceptionHandler { _, throwable ->
        _defaultErrorLiveData.value = throwable
        throwable.printStackTrace()
        Timber.e(throwable)
    }

    protected fun <T> collect(flow: Flow<T>, block: (T) -> Unit): Job {
        return viewModelScope.launch(Dispatchers.Main + defaultErrorHandler) {
            _defaultLoadingLiveData.value = true
            flow.collect {
                _defaultLoadingLiveData.value = false
                block(it)
            }
        }
    }

    protected fun <T> collectCatching(flow: Flow<T>, block: (T) -> Unit): Job {
        return viewModelScope.launch(Dispatchers.IO + defaultErrorHandler) {
            flow.collect {
                block(it)
            }
        }
    }

    protected fun launch(block: suspend CoroutineScope.() -> Unit): Job {
        return viewModelScope.launch(Dispatchers.Main + defaultErrorHandler) {
            _defaultLoadingLiveData.value = true
            this.block()
            _defaultLoadingLiveData.value = false
        }
    }

    protected fun launchCatching(block: suspend CoroutineScope.() -> Unit): Job {
        return viewModelScope.launch(Dispatchers.Main + defaultErrorHandler) {
            this.block()
        }
    }


}