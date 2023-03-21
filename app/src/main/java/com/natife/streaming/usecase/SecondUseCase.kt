package com.natife.streaming.usecase

import com.natife.streaming.API_SECOND
import com.natife.streaming.api.MainApi
import com.natife.streaming.data.Second
import com.natife.streaming.data.request.BaseRequest
import com.natife.streaming.data.request.MatchInfo

interface SecondUseCase {
    suspend fun execute(matchId: Int, sportId: Int): Second
    suspend fun execute(second: Second)
}

class SecondUseCaseImpl(private val api: MainApi) : SecondUseCase {
    override suspend fun execute(matchId: Int, sportId: Int): Second {

        val sec = api.getSecond(
            BaseRequest(
                procedure = API_SECOND,
                params = MatchInfo(matchId = matchId, sportId = sportId)
            )
        )
        return Second(sec.half,sec.second)
    }

    override suspend fun execute(second: Second) {
        //TODO save second
    }

}