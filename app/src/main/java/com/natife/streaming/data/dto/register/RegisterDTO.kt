package com.natife.streaming.data.dto.register

import com.google.gson.annotations.SerializedName


data class RegisterDTO(
    @SerializedName("_p_status")
    val status: Int,
    @SerializedName("_p_error")
    val error: String,
)