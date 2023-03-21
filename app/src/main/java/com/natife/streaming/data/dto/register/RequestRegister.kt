package com.natife.streaming.data.dto.register

import com.google.gson.annotations.SerializedName


data class RequestRegister(
    @SerializedName("_p_email")
    val email: String,

    @SerializedName("_p_password")
    val password: String
)



