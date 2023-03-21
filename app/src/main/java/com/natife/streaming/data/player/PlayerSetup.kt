package com.natife.streaming.data.player

import android.os.Parcelable
import com.natife.streaming.data.Video
import com.natife.streaming.data.match.Match
import com.natife.streaming.data.matchprofile.Episode
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PlayerSetup(
    val playlist: Map<String, List<Episode>>,
    val playListType: WatchType?,
    val currentEpisode: Episode? = null,
    val match: Match? = null,
    val currentPlaylist: List<Episode>? = null,
    val startTitle: List<String>,
    val video: List<Video>? = null,
    val videoDurations: List<Long>,
    val titlesForButtons: Map<String, String> = mapOf()
) : Parcelable
