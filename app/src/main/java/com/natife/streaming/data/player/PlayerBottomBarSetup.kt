package com.natife.streaming.data.player

import com.natife.streaming.data.matchprofile.Episode

data class PlayerBottomBarSetup(
    val playlist: List<Episode> = listOf(),
    val additionallyPlaylist: List<Episode>? = null,
)

fun PlayerSetup.toInitBottomData(): PlayerBottomBarSetup? {
    return when {
        currentPlaylist != null -> {
            PlayerBottomBarSetup(
                playlist = currentPlaylist.sortedWith(compareBy({ it.half }, { it.startMs })).map {
                    it.copy(
                        endMs = it.endMs * 1000,
                        startMs = it.startMs * 1000,
                        half = it.half - 1
                    )
                }
            )
        }
        currentEpisode != null -> {
            val timeList = this.video
                ?.filter { it.abc == "0" }
                ?.groupBy { it.quality }!!["720"]
                ?.sortedBy { it.period }
                ?.mapIndexed { index, video ->
                    Episode(
                        title = this.currentEpisode.title,
                        endMs = video.duration,
                        half = video.period - 1,//  TODO иногда приходят не правильные данные
                        startMs = video.startMs,
                        image = this.currentEpisode.image,
                        placeholder = this.currentEpisode.placeholder
                    )
                }
            PlayerBottomBarSetup(
                playlist = timeList ?: listOf(),
                additionallyPlaylist = this.playlist.flatMap {
                    it.value
                }
            )
        }
        else -> null
    }
}

