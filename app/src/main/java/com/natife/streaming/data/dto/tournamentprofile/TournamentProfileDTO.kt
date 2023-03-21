package com.natife.streaming.data.dto.tournamentprofile

import com.google.gson.annotations.SerializedName

data class TournamentProfileDTO(
    val country: CountryDTO,
    val id: Int,
    @SerializedName("is_favorite")
    val isFavorite: Boolean,
    @SerializedName("name_eng")
    val nameEng: String,
    @SerializedName("name_rus")
    val nameRus: String,
    @SerializedName("short_name_eng")
    val shortNameEng: Any,
    @SerializedName("short_name_rus")
    val shortNameRus: Any
)