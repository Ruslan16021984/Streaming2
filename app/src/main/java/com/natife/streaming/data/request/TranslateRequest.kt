package com.natife.streaming.data.request

import com.google.gson.annotations.SerializedName


//new
data class TranslateRequest(
    @SerializedName("_p_lang")
    val language: String,
    @SerializedName("_p_param_arr")
    val params: List<Int>
)

