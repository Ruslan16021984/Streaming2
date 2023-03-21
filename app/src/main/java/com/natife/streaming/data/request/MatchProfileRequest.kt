package com.natife.streaming.data.request

import com.google.gson.annotations.SerializedName
import com.natife.streaming.data.match.Tournament

data class MatchProfileRequest(
    @SerializedName("_p_sport")
    val sportId: Int,
    @SerializedName("_p_tournament_id")
    val tournament: Int
)
