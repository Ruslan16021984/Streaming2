package com.natife.streaming.db.entity

import androidx.room.Entity
import com.natife.streaming.data.Sport
import com.natife.streaming.data.dto.country.CountryTranslateDTO
import com.natife.streaming.data.dto.tournament.TournamentListDTO
import com.natife.streaming.data.dto.tournament.TournamentTranslateDTO


@Entity(primaryKeys = ["id", "sport", "tournamentType"])
data class PreferencesTournament(
    val id: Int,
    val nameEng: String,
    val nameRus: String,
    val sport: Int,
    val teamType: Int,
    val tournamentType: Int,
    val gender: Int,
    val countryID: Int,
    val countryNameEng: String,
    val countryNameRus: String,
    val isPreferred: Boolean = true // default preferred
)

fun List<PreferencesTournament>.toTournamentTranslateDTO(lang: String): List<TournamentTranslateDTO> {
    return this.map {
        TournamentTranslateDTO(
            id = it.id,
            country = CountryTranslateDTO(
                id = it.countryID,
                name = when (lang) {
                    "en", "EN" -> it.countryNameEng ?: ""
                    "ru", "RU" -> it.countryNameRus ?: ""
                    else -> it.countryNameEng ?: ""
                }
            ),
            gender = it.gender,
            name = when (lang) {
                "en", "EN" -> it.nameEng
                "ru", "RU" -> it.nameRus
                else -> it.nameEng
            },
            sport = it.sport,
            teamType = it.teamType,
            tournamentType = it.tournamentType,
            isCheck = it.isPreferred
        )
    }
}

fun TournamentListDTO.toPreferencesTournament(): List<PreferencesTournament> {
    return this.map {
        PreferencesTournament(
            id = it.id,
            nameEng = it.nameEng,
            nameRus = it.nameRus,
            sport = it.sport,
            teamType = it.teamType,
            tournamentType = it.tournamentType,
            gender = it.gender,
            countryID = it.country?.id ?: -1,
            countryNameEng = it.country.nameEng ?: "",
            countryNameRus = it.country.nameRus ?: "",
        )
    }
}

@Entity(primaryKeys = ["id"])
data class PreferencesSport(
    val id: Int,
    val name: String,
    val lexic: Int,
    var isChack: Boolean = false
)

fun List<Sport>.toPreferencesSport(): List<PreferencesSport> {
    return this.map {
        PreferencesSport(
            id = it.id,
            name = it.name,
            lexic = it.lexic
        )
    }
}
