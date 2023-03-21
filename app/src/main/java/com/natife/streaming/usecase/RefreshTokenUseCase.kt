package com.natife.streaming.usecase

import com.natife.streaming.API_REFRESH_TOKEN
import com.natife.streaming.CLIENT_ID
import com.natife.streaming.api.MainApi
import com.natife.streaming.api.exception.ApiException
import com.natife.streaming.data.api.request.RefreshTokenRequest
import com.natife.streaming.data.api.response.RefreshTokenResponse
import com.natife.streaming.preferenses.AuthPrefs
import com.natife.streaming.utils.BaseResult

interface RefreshTokenUseCase {
    suspend fun refreshToken(token: String?): BaseResult<RefreshTokenResponse>
}

class RefreshTokenUseCaseImpl(
    private val api: MainApi
) : RefreshTokenUseCase {
    override suspend fun refreshToken(token: String?): BaseResult<RefreshTokenResponse> {
        val body = RefreshTokenRequest(
            grantType = API_REFRESH_TOKEN,
            clientId = CLIENT_ID,
            refreshToken = token
        )
        return try {
            val response = api.refreshToken(body)
            BaseResult.Success(response)
        } catch (e: ApiException) {
            BaseResult.Error(e)
        }
    }
}