package com.natife.streaming.data.matchprofile

import android.os.Parcelable
import com.natife.streaming.data.dto.matchprofile.MatchInfoEpisodeDTO
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Episode(
    val title: String = "",
    val endMs: Long,
    val half: Int,
    var startMs: Long,
    val image: String = "",
    val placeholder: String = ""
): Parcelable

fun MatchInfoEpisodeDTO.toEpisode():Episode{
    return Episode(
        endMs = this.e,
        half = this.h.toInt(),
        startMs = this.s
    )
}