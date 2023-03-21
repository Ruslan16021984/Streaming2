package com.natife.streaming.utils

import com.natife.streaming.preferenses.AuthPrefs
import com.natife.streaming.usecase.RefreshTokenUseCase
import kotlinx.coroutines.*
import timber.log.Timber

const val REFRESH_TOKEN_TIMEOUT = 150000L // 2,5 minutes

interface TokenRefreshLoop {
    fun start()
    fun stop()
}

class TokenRefreshLoopImpl(
    private val refreshTokenUseCase: RefreshTokenUseCase,
    private val authPrefs: AuthPrefs
) : TokenRefreshLoop {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private var refreshTokenJob: Job? = null

    override fun start() {
        if (refreshTokenJob == null) {
            refreshTokenJob = scope.launch {
                while (true) {
                    delay(REFRESH_TOKEN_TIMEOUT)
                    refreshTokenUseCase.refreshToken(authPrefs.getRefreshAuthToken())
                        .onSuccessValue { refreshTokenResponse ->
                            with(authPrefs) {
                                saveAuthToken(refreshTokenResponse.accessToken)
                                saveRefreshAuthToken(refreshTokenResponse.refreshToken)
                            }
                        }
                        .onErrorValue {
                            Timber.e(it)
                        }
                }
            }
        }
    }

    override fun stop() {
        refreshTokenJob?.cancel()
        refreshTokenJob = null
    }
}