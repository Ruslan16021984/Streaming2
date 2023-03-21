package com.natife.streaming.usecase

import com.natife.streaming.*
import com.natife.streaming.api.MainApi
import com.natife.streaming.data.match.Match
import com.natife.streaming.data.match.Team
import com.natife.streaming.data.match.Tournament
import com.natife.streaming.data.request.*
import com.natife.streaming.db.LocalSqlDataSourse
import com.natife.streaming.preferenses.SettingsPrefs
import com.natife.streaming.utils.ImageUrlBuilder

interface MatchInfoUseCase {
    suspend fun execute(sportId: Int, matchId: Int): Match
}

class MatchInfoUseCaseImpl(
    private val api: MainApi,
    private val localSqlDataSourse: LocalSqlDataSourse,
    private val settingsPrefs: SettingsPrefs
) : MatchInfoUseCase {
    override suspend fun execute(sportId: Int, matchId: Int): Match {
        val lang = settingsPrefs.getLanguage().toUpperCase()
        val match = api.getMatchInfoGlobal(
            BaseRequest(
                procedure = API_MATCH_INFO,
                params = MatchInfo(sportId = sportId, matchId = matchId)
            )
        )
        val sports = api.getSports(
            BaseRequest(
                procedure = API_SPORTS,
                params = EmptyRequest()
            )
        )
        val sportTranslate = api.getTranslate(
            BaseRequest(
                procedure = API_TRANSLATE,
                TranslateRequest(
                    language = lang,
                    params = sports.map { it.lexic }
                )
            )
        )
        val profile =  api.getMatchProfile(
                BaseRequest(
                    procedure = API_MATCH_PROFILE,
                    params = MatchProfileRequest(
                        sportId = match.sport ?: sportId,
                        tournament = match.tournament?.id ?: 0
                    )
                )
            )
        return Match(
            id = match.id,
            sportId = match.sport ?: sportId ?: 0,
            countryId = match.countryId,
            sportName = sportTranslate[sports.find { it.id == match.sport }?.lexic.toString()]?.text
                ?: "",
            date = match.date,
            tournament = Tournament(
                id = match.tournament?.id ?: -1,
                name = when (lang) {
                    "en", "EN" -> match.tournament?.nameEng ?: ""
                    "ru", "RU" -> match.tournament?.nameRus ?: ""
                    else -> match.tournament?.nameEng ?: ""
                }
            ),
            team1 = Team(
                id = match.team1.id,
                name = when (lang) {
                    "en", "EN" -> match.team1.nameEng ?: ""
                    "ru", "RU" -> match.team1.nameRus ?: ""
                    else -> match.team1.nameEng ?: ""
                },
                score = match.team1.score
            ),
            team2 = Team(
                id = match.team2.id,
                name = when (lang) {
                    "en", "EN" -> match.team2.nameEng ?: ""
                    "ru", "RU" -> match.team2.nameRus ?: ""
                    else -> match.team1.nameEng ?: ""
                },
                score = match.team2.score
            ),
            info = when (lang) {
                "en", "EN" -> "${profile.country.name_eng} ${profile.nameEng}"
                    ?: ""
                "ru", "RU" -> "${profile.country.name_rus} ${profile.nameRus}"
                    ?: ""
                else -> "${profile.country.name_eng} ${profile.nameEng}"
                    ?: ""
            },
            access = match.access,
            hasVideo = match.hasVideo,
            image = ImageUrlBuilder.getUrl(
                match.sport ?: sportId ?: 0,
                ImageUrlBuilder.Companion.Type.TOURNAMENT, match.tournament?.id ?: -1
            ),
            placeholder = ImageUrlBuilder.getPlaceholder(
                match.sport ?: sportId ?: 0,
                ImageUrlBuilder.Companion.Type.TOURNAMENT
            ),
            live = match.live,
            storage = match.storage,
            subscribed = match.sub,
            isShowScore = localSqlDataSourse.getGlobalSettings()?.showScore ?: false
        )
    }

}