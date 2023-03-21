package com.natife.streaming.data.request

import com.google.gson.annotations.SerializedName

data class PlayerActionsRequest(
    @SerializedName("_p_match_id")
    val matchId: Int,
    @SerializedName("_p_player_id")
    val playerId: Int,
    @SerializedName("_p_type")
    val type: Int,
    @SerializedName("_p_actions")
    val actions: List<Int>?,
    @SerializedName("_p_offset_start")
    val offsetStart: Int,
    @SerializedName("_p_offset_end")
    val offsetEnd: Int
)