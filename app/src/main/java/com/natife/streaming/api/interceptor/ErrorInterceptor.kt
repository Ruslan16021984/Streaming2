package com.natife.streaming.api.interceptor

import com.natife.streaming.api.exception.ApiException
import com.natife.streaming.usecase.LogoutUseCase
import okhttp3.Interceptor
import okhttp3.Response
import timber.log.Timber

class ErrorInterceptor( private val logoutUseCaseImpl: LogoutUseCase): Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val builder = chain.request().newBuilder()
        val response = chain.proceed(builder.build())

        if (response.code == 403 || response.code == 401) {
            logoutUseCaseImpl.execute(false)
        }
        if (response.code == 400) {
            throw ApiException("400",response.body?.string())
        }
        return response
    }
}