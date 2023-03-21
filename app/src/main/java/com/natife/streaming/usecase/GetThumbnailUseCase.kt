package com.natife.streaming.usecase

import android.graphics.Bitmap
import com.natife.streaming.api.MainApi
import com.natife.streaming.data.matchprofile.Episode
import com.natife.streaming.data.request.VideoRequest
import com.natife.streaming.utils.Util

interface GetThumbnailUseCase {
    suspend fun execute(episodes: List<Episode>,matchId: Int, sportId: Int): List<Bitmap?>
}
class GetThumbnailUseCaseImpl(private val api: MainApi): GetThumbnailUseCase {
    override suspend fun execute(episodes: List<Episode>,matchId: Int, sportId: Int): List<Bitmap?> {
        val video = api.getVideoFile(
            VideoRequest(matchId = matchId,
            sportId = sportId)
        )
        return episodes.map {episode->
            Util.getThumbnailFromVideo(video.first {
                it.name.substringBeforeLast(".").endsWith(episode.half.toString(), false)
            }.url, episode.startMs.toLong())
        }
    }
}