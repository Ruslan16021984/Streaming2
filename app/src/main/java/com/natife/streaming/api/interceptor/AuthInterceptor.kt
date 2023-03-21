package com.natife.streaming.api.interceptor

import android.util.Log
import com.natife.streaming.AUTHORIZATION
import com.natife.streaming.BEARER
import com.natife.streaming.preferenses.AuthPrefs
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.Interceptor
import okhttp3.Response
import org.koin.core.KoinComponent

class AuthInterceptor(
    private val authPrefs: AuthPrefs
) : Interceptor, KoinComponent {
    override fun intercept(chain: Interceptor.Chain): Response {
        synchronized(this) {
            val original = chain.request()
            val builder = original.newBuilder()

            authPrefs.getAuthToken()?.let { token ->
                builder.addHeader(AUTHORIZATION, "$BEARER $token")
            }

            val response = chain.proceed(builder.build())


            response.header(AUTHORIZATION)?.let {
                authPrefs.saveAuthToken(it)
            }

            return response
        }
    }
}