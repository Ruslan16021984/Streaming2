package com.natife.streaming.data.dto.search

import com.google.gson.annotations.SerializedName

data class PlayersTeamDTO(
    val id: Int,
    @SerializedName("name_eng")
    val nameEng: String,
    @SerializedName("name_rus")
    val nameRus: String
)