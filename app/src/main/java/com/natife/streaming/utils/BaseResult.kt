package com.natife.streaming.utils

import java.lang.Exception

sealed class BaseResult<out T : Any> {
    data class Success<out T : Any>(val data: T) : BaseResult<T>()
    data class Error(val exception: Exception) : BaseResult<Nothing>()
}

inline fun <T : Any> BaseResult<T>.onSuccessValue(block: (T) -> Unit): BaseResult<T> {
    if (this is BaseResult.Success) {
        block.invoke(data)
    }
    return this
}

inline fun <T: Any> BaseResult<T>.onErrorValue(block: (Throwable) -> Unit): BaseResult<T> {
    if (this is BaseResult.Error) {
        block.invoke(exception)
    }
    return this
}
