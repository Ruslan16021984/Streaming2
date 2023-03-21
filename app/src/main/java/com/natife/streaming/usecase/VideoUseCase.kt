package com.natife.streaming.usecase

import com.natife.streaming.api.MainApi
import com.natife.streaming.data.Video
import com.natife.streaming.data.dto.VideoDTO
import com.natife.streaming.data.request.VideoRequest

interface VideoUseCase {
    suspend fun execute(matchId: Int, sportId: Int): List<Video>
}

class VideoUseCaseImpl(private val api: MainApi) : VideoUseCase {
    override suspend fun execute(matchId: Int, sportId: Int): List<Video> {
        val video: VideoDTO = api.getVideoFile(VideoRequest(matchId, sportId))
        return video.map {
            Video(
                abc = it.abc,
                abcType = it.abcType?:"",
                duration = it.duration,
                folder = it.folder,
                matchId = it.matchId,
                name = it.name,
                period = it.period,
                quality = it.quality,
                size = it.size,
                startMs = it.startMs,
                url = it.url,
                videoType = it.videoType
            )
        }
    }

}

