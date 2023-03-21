package com.natife.streaming.usecase

import com.natife.streaming.data.model.Video

import com.natife.streaming.mock.MockVideosRepository

interface GetVideosUseCase {
   // suspend fun getVideos(matchId: Int, sportId: Int): List<Video>
}

class GetVideosUseCaseImpl(private val mockedVideosRepository: MockVideosRepository) :
    GetVideosUseCase {
//    override suspend fun getVideos(matchId: Int, sportId: Int): List<Video> =
//        mockedVideosRepository
//            .getVideos(matchId = matchId, sportId = sportId)
//            .map { videoDTO -> videoDTO.toVideoItem() }
}