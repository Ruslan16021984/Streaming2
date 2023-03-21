package com.natife.streaming.usecase

import com.natife.streaming.API_TOURNAMENT
import com.natife.streaming.api.MainApi
import com.natife.streaming.data.Tournament
import com.natife.streaming.data.dto.tournamentprofile.TournamentProfileDTO
import com.natife.streaming.data.request.BaseRequest
import com.natife.streaming.data.request.TournamentProfileRequest
import com.natife.streaming.preferenses.SettingsPrefs
import com.natife.streaming.utils.ImageUrlBuilder

interface TournamentUseCase {

    suspend fun execute(sportId: Int, tournamentId: Int): Tournament
}

class TournamentUseCaseImpl(
    private val api: MainApi,
    private val settingsPrefs: SettingsPrefs
) : TournamentUseCase {
    override suspend fun execute(sportId: Int, tournamentId: Int): Tournament {
        val lang = settingsPrefs.getLanguage().toUpperCase()
        val tournament: TournamentProfileDTO = api.getTournamentProfile(
            BaseRequest(
                procedure = API_TOURNAMENT,
                params = TournamentProfileRequest(
                    sportId = sportId,
                    tournamentId = tournamentId
                )
            )
        )
        return Tournament(
            title = when (lang) {
                "en", "EN" -> tournament.nameEng ?: ""
                "ru", "RU" -> tournament.nameRus ?: ""
                else -> tournament.nameEng ?: ""
            },
            icon = ImageUrlBuilder.getUrl(
                sportId,
                ImageUrlBuilder.Companion.Type.TOURNAMENT,
                tournamentId
            ),
            placeholder = ImageUrlBuilder.getPlaceholder(
                sportId,
                ImageUrlBuilder.Companion.Type.TOURNAMENT
            ),
            isFavorite = tournament.isFavorite,
            countryId = tournament.country.id,
            countryName = when (lang) {
                "en", "EN" -> tournament.country.nameEng ?: ""
                "ru", "RU" -> tournament.country.nameRus ?: ""
                else -> tournament.country.nameEng ?: ""
            }
        )
    }
}