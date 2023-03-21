package com.natife.streaming.data.request

import com.google.gson.annotations.SerializedName

data class TeamRequest(
    @SerializedName("_p_sport")
    val sport:Int,
    @SerializedName("_p_team_id")
    val team: Int

)
