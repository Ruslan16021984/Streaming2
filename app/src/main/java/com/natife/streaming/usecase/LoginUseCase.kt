package com.natife.streaming.usecase

import com.natife.streaming.CLIENT_ID
import com.natife.streaming.GRAND_TYPE
import com.natife.streaming.api.MainApi
import com.natife.streaming.api.exception.ApiException
import com.natife.streaming.data.api.response.LoginResponse
import com.natife.streaming.data.api.request.LoginRequest
import com.natife.streaming.preferenses.AuthPrefs
import com.natife.streaming.utils.BaseResult
import com.natife.streaming.utils.TokenRefreshLoop
import kotlinx.coroutines.*

/**
 * Выступает в роли интерфейса между ViewModel и Api.
 * Необходимо реализовать свой UseCaseImpl или отредактировать имеющийся
 * в нем же можно мапить данные.
 */
interface LoginUseCase {
    suspend fun execute(email: String, password: String): BaseResult<LoginResponse>
}

class LoginUseCaseImpl(
    private val api: MainApi,
    private val authPrefs: AuthPrefs,
    private val tokenRefreshLoop: TokenRefreshLoop
) : LoginUseCase {
    override suspend fun execute(email: String, password: String): BaseResult<LoginResponse> {
        val requestBody = LoginRequest(
            grantType = GRAND_TYPE,
            email = email,
            password = password,
            clientId = CLIENT_ID
        )

        return try {
            val result = api.login(requestBody)
            authPrefs.saveAuthToken(result.accessToken)
            authPrefs.saveRefreshAuthToken(result.refreshToken)
            tokenRefreshLoop.start()
            BaseResult.Success(result)
        } catch (e: ApiException) {
            BaseResult.Error(Exception(e.message))
        }
    }
}

