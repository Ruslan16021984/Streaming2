package com.natife.streaming.api.exception

import com.google.android.exoplayer2.upstream.DefaultLoadErrorHandlingPolicy
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.upstream.HttpDataSource
import com.google.android.exoplayer2.upstream.LoadErrorHandlingPolicy
import timber.log.Timber


class ErrorHandlingPolicy(minimumLoadableRetryCount: Int) :
    DefaultLoadErrorHandlingPolicy(minimumLoadableRetryCount) {

    override fun getRetryDelayMsFor(loadErrorInfo: LoadErrorHandlingPolicy.LoadErrorInfo): Long {
        val exception = loadErrorInfo.exception
        Timber.tag("TAG").e("Player getRetryDelayMsFor $exception")
        if (exception is HttpDataSource.InvalidResponseCodeException) {
            val responseCode = exception.responseCode
            return if (responseCode in 500..599) RETRY_DELAY else C.TIME_UNSET
        } else if (exception is HttpDataSource.HttpDataSourceException) {
            return RETRY_DELAY
        }
        return C.TIME_UNSET
    }

    override fun getMinimumLoadableRetryCount(dataType: Int): Int {
        return MINIMUM_RETRY_COUNT
    }

    companion object {
        private const val RETRY_DELAY = 5000L // Retry every 5 seconds
        private const val MINIMUM_RETRY_COUNT = 3
    }
}