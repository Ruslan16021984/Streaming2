package com.natife.streaming.usecase

import com.natife.streaming.data.api.Params
import com.natife.streaming.data.api.request.GetMatchInfoRequest
import com.natife.streaming.data.model.MatchInfo

import com.natife.streaming.mock.MockMatchRepository

interface GetMatchInfoUseCase {
    //suspend fun getMatchInfo(sportId: Int, matchId: Int): MatchInfo
}

class GetMatchInfoUseCaseImpl(private val mockedMatchRepository: MockMatchRepository): GetMatchInfoUseCase {
//    override suspend fun getMatchInfo(sportId: Int, matchId: Int): MatchInfo {
//        val request = GetMatchInfoRequest(
//            Params(sportId = sportId, matchId = matchId)
//        )
//        return mockedMatchRepository.getMatchInfo(request).toMatchInfoItem()
//    }
}