package com.natife.streaming.data.dto.team

import com.google.gson.annotations.SerializedName
import com.natife.streaming.data.dto.country.CountryDTO

data class TeamDTO(
    val country: CountryDTO,
    val id: Int,
    @SerializedName("is_favorite")
    val isFavorite: Boolean,
    @SerializedName("name_eng")
    val nameEng: String,
    @SerializedName("name_rus")
    val nameRus: String,
    @SerializedName("short_name_eng")
    val shortNameEng: String,
    @SerializedName("short_name_rus")
    val shortNameRus: String
)