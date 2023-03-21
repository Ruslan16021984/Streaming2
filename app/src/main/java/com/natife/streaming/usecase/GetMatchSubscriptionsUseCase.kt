package com.natife.streaming.usecase

import com.natife.streaming.API_MATCH_SUBSCRIPTION
import com.natife.streaming.api.MainApi
import com.natife.streaming.data.dto.subscription.SubscribeRequest
import com.natife.streaming.data.dto.subscription.SubscribeResponse
import com.natife.streaming.data.request.BaseRequest

interface GetMatchSubscriptionsUseCase {
    suspend fun execute(sportId: Int, matchId: Int): SubscribeResponse
}

class GetMatchSubscriptionsUseCaseImpl(private val api: MainApi) : GetMatchSubscriptionsUseCase {
    override suspend fun execute(sportId: Int, matchId: Int): SubscribeResponse {
        val response = api.getMatchSubscriptions(
            BaseRequest(
                procedure = API_MATCH_SUBSCRIPTION,
                params = SubscribeRequest(
                    sportId = sportId,
                    matchId = matchId
                )
            )
        )
        return response
    }
}