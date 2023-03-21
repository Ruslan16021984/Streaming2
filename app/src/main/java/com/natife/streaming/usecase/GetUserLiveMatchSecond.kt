package com.natife.streaming.usecase

import android.content.Context
import com.natife.streaming.API_GET_USER_MATCH_SECOND
import com.natife.streaming.api.MainApi
import com.natife.streaming.data.dto.videoPosition.GetVideoPositionRequest
import com.natife.streaming.data.dto.videoPosition.StoreVideoPositionResponse
import com.natife.streaming.data.request.BaseRequest


interface GetUserLiveMatchSecond {
    suspend fun execute(
        _sportId: Int,
        _matchId: Int,
    ): StoreVideoPositionResponse
}

class GetUserLiveMatchSecondImpl(
    private val api: MainApi,
    private val context: Context
) : GetUserLiveMatchSecond {
    override suspend fun execute(_sportId: Int, _matchId: Int): StoreVideoPositionResponse {
        val response = api.getUserLiveMatchSecond(
            BaseRequest(
                procedure = API_GET_USER_MATCH_SECOND,
                params = GetVideoPositionRequest(
                    sport = _sportId,
                    matchId = _matchId
                )
            )
        )
        return response
    }
}