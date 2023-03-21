package com.natife.streaming.data.dto

import com.google.gson.annotations.SerializedName


data class LoginDTO(
    @SerializedName("_p_status")
    val status: Int,
    @SerializedName("_p_user_id")
    val userId: Int,
    @SerializedName("_p_token")
    val token: String
)

