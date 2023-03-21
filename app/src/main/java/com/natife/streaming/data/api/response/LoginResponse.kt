package com.natife.streaming.data.api.response

import com.google.gson.annotations.SerializedName

data class LoginResponse(
    @SerializedName("refresh_token")
    val refreshToken: String?,
    @SerializedName("access_token")
    val accessToken: String?,
    @SerializedName("id_token")
    val idToken: String?,
    @SerializedName("error")
    val error: String?
)
