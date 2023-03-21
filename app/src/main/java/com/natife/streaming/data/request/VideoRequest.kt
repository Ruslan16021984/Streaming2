package com.natife.streaming.data.request

import com.google.gson.annotations.SerializedName

data class VideoRequest(
    @SerializedName("match_id")
    val matchId: Int,
    @SerializedName("sport_id")
    val sportId: Int
)
