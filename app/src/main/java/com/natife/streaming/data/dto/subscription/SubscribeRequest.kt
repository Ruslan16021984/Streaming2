package com.natife.streaming.data.dto.subscription

import com.google.gson.annotations.SerializedName

data class SubscribeRequest(
    @SerializedName("_p_sport")
    val sportId: Int,
    @SerializedName("_p_match_id")
    val matchId: Int,
)
