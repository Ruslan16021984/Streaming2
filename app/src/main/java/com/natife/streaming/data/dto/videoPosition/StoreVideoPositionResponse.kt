package com.natife.streaming.data.dto.videoPosition

import com.google.gson.annotations.SerializedName


data class StoreVideoPositionResponse(
    @SerializedName("_p_status")
    val status: Int? = null,

    @SerializedName("_p_half")
    val half: Int = 0,

    @SerializedName("_p_second")
    val second: Int = 0,
)

