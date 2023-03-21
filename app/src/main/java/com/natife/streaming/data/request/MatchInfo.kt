package com.natife.streaming.data.request

import com.google.gson.annotations.SerializedName

data class MatchInfo(
    @SerializedName("_p_sport")
    val sportId: Int,
    @SerializedName("_p_match_id")
    val matchId: Int
)
