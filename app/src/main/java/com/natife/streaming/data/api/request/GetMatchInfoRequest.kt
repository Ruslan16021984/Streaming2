package com.natife.streaming.data.api.request

import com.google.gson.annotations.SerializedName
import com.natife.streaming.data.api.Params

data class GetMatchInfoRequest (
    @SerializedName("params") val params: Params,
    @SerializedName("proc") val proc: String = "get_match_info"
)
