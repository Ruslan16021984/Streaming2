package com.natife.streaming.data.request

import com.google.gson.annotations.SerializedName

data class ProfileColorRequest(
    @SerializedName("sport_id")
    private val sportId: Int,
    @SerializedName("profile_type")
    private val profileType: String,
    @SerializedName("profile_id")
    private val profileId: Int
)