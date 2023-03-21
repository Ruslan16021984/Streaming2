package com.natife.streaming.utils

class Result<T : Any>(val status: Status, val message: String? = null, var userId: Int? = null) {

    lateinit var data: T

    companion object {

        fun <T : Any> success(data: T, _userId: Int? = null) = Result<T>(Status.SUCCESS).apply {
            this.data = data
            this.userId = _userId
        }

        fun <T : Any> error(message: String? = null) = Result<T>(Status.ERROR, message)

    }

    enum class Status {
        SUCCESS, ERROR
    }
}