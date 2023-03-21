package com.natife.streaming.data.dto.videoPosition

import com.google.gson.annotations.SerializedName


data class StoreVideoPositionRequest(
    @SerializedName("_p_sport")
    private val sport: Int,

    @SerializedName("_p_match_id")
    private val matchId: Int,

    @SerializedName("_p_half")
    private val half: Int,

    @SerializedName("_p_second")
    private val second: Int,
)

