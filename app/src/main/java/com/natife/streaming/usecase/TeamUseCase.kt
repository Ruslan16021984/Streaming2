package com.natife.streaming.usecase

import com.natife.streaming.API_TEAM_INFO
import com.natife.streaming.R
import com.natife.streaming.api.MainApi
import com.natife.streaming.data.dto.team.TeamDTO
import com.natife.streaming.data.request.BaseRequest
import com.natife.streaming.data.request.TeamRequest
import com.natife.streaming.data.search.SearchResult
import com.natife.streaming.preferenses.SettingsPrefs
import com.natife.streaming.utils.ImageUrlBuilder

interface TeamUseCase {
    suspend fun execute(sportId: Int, teamId: Int): SearchResult
}

class TeamUseCaseImpl(
    private val api: MainApi,
    private val settingsPrefs: SettingsPrefs
) : TeamUseCase {
    override suspend fun execute(sportId: Int, teamId: Int): SearchResult {
        val lang = settingsPrefs.getLanguage().toUpperCase()
        val team: TeamDTO = api.getTeamInfo(
            BaseRequest(
                procedure = API_TEAM_INFO,
                params = TeamRequest(sportId, teamId)
            )
        )
        return SearchResult(
            id = team.id,
            image = ImageUrlBuilder.getUrl(
                sportId,
                ImageUrlBuilder.Companion.Type.TEAM,
                team.id ?: -1
            ),
            placeholder = ImageUrlBuilder.getPlaceholder(
                sportId,
                ImageUrlBuilder.Companion.Type.TEAM
            ),
            name = when (lang) {
                "en", "EN" -> team.nameEng ?: ""
                "ru", "RU" -> team.nameRus ?: ""
                else -> team.nameEng ?: ""
            },
            gender = 0,
            sport = sportId,
            type = SearchResult.Type.TEAM,
            countryId = team.country?.id ?: -1,
            countryName = when (lang) {
                "en", "EN" -> team.country.nameEng ?: ""
                "ru", "RU" -> team.country.nameRus ?: ""
                else -> team.country.nameEng ?: ""
            },
            isFavorite = team.isFavorite
        )
    }

}