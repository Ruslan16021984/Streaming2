package com.natife.streaming.data.dto.tournamentprofile

import com.google.gson.annotations.SerializedName

data class CountryDTO(
    val id: Int,
    @SerializedName("name_eng")
    val nameEng: String,
    @SerializedName("name_rus")
    val nameRus: String,
)