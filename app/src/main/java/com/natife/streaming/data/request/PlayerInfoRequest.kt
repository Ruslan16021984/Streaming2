package com.natife.streaming.data.request

import com.google.gson.annotations.SerializedName

data class PlayerInfoRequest(
    @SerializedName("_p_sport")
    val sportId: Int,
    @SerializedName("_p_player_id")
    val playerId: Int
)