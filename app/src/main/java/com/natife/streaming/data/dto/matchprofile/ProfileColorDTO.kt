package com.natife.streaming.data.dto.matchprofile

import com.google.gson.annotations.SerializedName

data class ProfileColorDTO(
    @SerializedName("r")
    val r: Int? = null,
    @SerializedName("g")
    val g: Int? = null,
    @SerializedName("b")
    val b: Int? = null,
    @SerializedName("code")
    val code: String? = null,
)