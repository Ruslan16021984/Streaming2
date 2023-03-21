package com.natife.streaming.data.dto

import com.google.gson.annotations.SerializedName
import com.natife.streaming.data.match.Team
import com.natife.streaming.data.match.Tournament

data class MatchInfoDTO (
    @SerializedName("tournament") val tournament: Tournament?,
    @SerializedName("date") val date: String?,
    @SerializedName("has_video") val hasVideo: Boolean?,
    @SerializedName("live") val live: Boolean?,
    @SerializedName("storage") val storage: Boolean?,
    @SerializedName("team1") val team1: Team?,
    @SerializedName("team2") val team2: Team?,
    @SerializedName("sub") val sub: Boolean?,
    @SerializedName("access") val access: Boolean?
)