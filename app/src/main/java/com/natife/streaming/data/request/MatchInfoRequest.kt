package com.natife.streaming.data.request

import com.google.gson.annotations.SerializedName

data class MatchInfoWithActionsRequest(
    @SerializedName("_p_match_id")
     val matchId: Int,
    @SerializedName("_p_actions")
     val actions: List<Int>
): BaseParams
data class MatchInfoRequest(
    @SerializedName("_p_match_id")
    val matchId: Int,
): BaseParams
