package com.natife.streaming.data.request

import com.google.gson.annotations.SerializedName

data class TournamentProfileRequest(
    @SerializedName("_p_sport")
    val sportId: Int,
    @SerializedName("_p_tournament_id")
    val tournamentId: Int
)