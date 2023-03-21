package com.natife.streaming.data.dto.preferences

import com.google.gson.annotations.SerializedName

data class UserPreferencesDTO(
    @SerializedName("sport")
    val sport: Int? = null,
    @SerializedName("tournament_id")
    val tournamentId: Int? = null,
)
