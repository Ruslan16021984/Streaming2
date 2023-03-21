package com.natife.streaming.data.dto.match

import com.google.gson.annotations.SerializedName

data class BroadcastDTO(
    val access: Boolean,
    val date: String,
    @SerializedName("country_id")
    val countryId: Int,
    @SerializedName("has_video")
    val hasVideo: Boolean,
    val id: Int,
    val live: Boolean,
    val sport: Int?,
    val storage: Boolean,
    val sub: Boolean,
    val team1: Team1DTO,
    val team2: Team2DTO,
    val tournament: TournamentDTO?
)