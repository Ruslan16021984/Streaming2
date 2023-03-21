package com.natife.streaming.data.dto

import com.google.gson.annotations.SerializedName

data class PreviewDTOItem(
    @SerializedName("match_id")
    val matchId: Int,
    val preview: String,
    @SerializedName("sport_id")
    val sportId: Int
)