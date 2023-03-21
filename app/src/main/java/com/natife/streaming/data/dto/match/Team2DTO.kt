package com.natife.streaming.data.dto.match

import com.google.gson.annotations.SerializedName

data class Team2DTO(
    val id: Int,
    @SerializedName("name_eng")
    val nameEng: String,
    @SerializedName("name_rus")
    val nameRus: String,
    val score: Int
)