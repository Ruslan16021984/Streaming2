package com.natife.streaming.data.api

import com.google.gson.annotations.SerializedName

data class Params (
    @SerializedName("_p_sport") val sportId: Int,
    @SerializedName("_p_match_id") val matchId: Int,
)
