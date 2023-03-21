package com.natife.streaming.data.dto.preferences

import com.google.gson.annotations.SerializedName

data class GetUserPreferencesRequest(
    @SerializedName(" _p_user_id")
    val userId: Int,
)
