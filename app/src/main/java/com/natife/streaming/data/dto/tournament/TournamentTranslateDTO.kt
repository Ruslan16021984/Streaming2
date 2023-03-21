package com.natife.streaming.data.dto.tournament

import com.natife.streaming.data.dto.country.CountryTranslateDTO

data class TournamentTranslateDTO (
    val id: Int,
    val country: CountryTranslateDTO,
    val gender: Int,
    val name: String,
    val sport: Int,
    val teamType: Int,
    val tournamentType: Int,
    val isCheck: Boolean
)

//fun TournamentListDTO.toTournamentTranslateDTO(lang: String): List<TournamentTranslateDTO> {
//    return this.map {
//        TournamentTranslateDTO(
//            id = it.id,
//            country = it.country.toCountryTranslateDTO(lang),
//            gender = it.gender,
//            name = when (lang) {
//                "en" -> it.nameEng
//                "ru" -> it.nameRus
//                else -> it.nameEng
//            },
//            sport = it.sport,
//            teamType = it.teamType,
//            tournamentType = it.tournamentType
//        )
//    }
//}

