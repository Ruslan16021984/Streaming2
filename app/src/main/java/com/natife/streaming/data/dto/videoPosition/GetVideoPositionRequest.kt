package com.natife.streaming.data.dto.videoPosition

import com.google.gson.annotations.SerializedName


data class GetVideoPositionRequest(
    @SerializedName("_p_sport")
    private val sport: Int,

    @SerializedName("_p_match_id")
    private val matchId: Int
)

