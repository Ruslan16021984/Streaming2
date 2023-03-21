package com.natife.streaming.data.request

import com.google.gson.annotations.SerializedName

data class SearchRequest(
    @SerializedName("_p_name")
    val name : String
)
