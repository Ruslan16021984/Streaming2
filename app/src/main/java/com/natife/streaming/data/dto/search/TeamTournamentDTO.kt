package com.natife.streaming.data.dto.search

import com.google.gson.annotations.SerializedName
import com.natife.streaming.data.dto.country.CountryDTO

data class TeamTournamentDTO(
    val country: CountryDTO? = CountryDTO(),
    val gender: Int,
    val id: Int,
    @SerializedName("name_eng")
    val nameEng: String,
    @SerializedName("name_rus")
    val nameRus: String,
    val sport: Int
)