package com.natife.streaming.data.dto.tournament

import com.google.gson.annotations.SerializedName
import com.natife.streaming.data.dto.country.CountryDTO

data class TournamentListDTOItem(
    val country: CountryDTO,
    val gender: Int,
    val id: Int,
    @SerializedName("name_eng")
    val nameEng: String,
    @SerializedName("name_rus")
    val nameRus: String,
    val sport: Int,
    @SerializedName("team_type")
    val teamType: Int,
    @SerializedName("tournament_type")
    val tournamentType: Int
)