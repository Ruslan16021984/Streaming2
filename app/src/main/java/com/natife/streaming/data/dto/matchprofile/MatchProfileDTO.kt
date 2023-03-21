package com.natife.streaming.data.dto.matchprofile

import com.google.gson.annotations.SerializedName


data class MatchProfileDTO(
    val country: Country,
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