package com.natife.streaming.data.request

import com.google.gson.annotations.SerializedName

data class TournamentsRequest(
    @SerializedName("_p_sport")
    val sportId: Int? = null)