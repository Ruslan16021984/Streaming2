package com.natife.streaming.usecase

import android.content.Context
import com.natife.streaming.API_SAVE_USER_MATCH_SECOND
import com.natife.streaming.api.MainApi
import com.natife.streaming.data.dto.videoPosition.StoreVideoPositionRequest
import com.natife.streaming.data.dto.videoPosition.StoreVideoPositionResponse
import com.natife.streaming.data.request.BaseRequest

interface SaveUserLiveMatchSecond {
    suspend fun execute(
        _sportId: Int,
        _matchId: Int,
        _half: Int,
        _second: Int
    ): StoreVideoPositionResponse
}

class SaveUserLiveMatchSecondImpl(
    private val api: MainApi,
    private val context: Context
) : SaveUserLiveMatchSecond {
    override suspend fun execute(
        _sportId: Int,
        _matchId: Int,
        _half: Int,
        _second: Int
    ): StoreVideoPositionResponse {
        val response = api.saveUserLiveMatchSecond(
            BaseRequest(
                procedure = API_SAVE_USER_MATCH_SECOND,
                params = StoreVideoPositionRequest(
                    sport = _sportId,
                    matchId = _matchId,
                    half = _half,
                    second = _second
                )
            )
        )
        return response
    }
}