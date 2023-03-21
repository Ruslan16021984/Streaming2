package com.natife.streaming.usecase

import com.natife.streaming.API_MATCH_INFO
import com.natife.streaming.API_PLAYERS_ACTIONS
import com.natife.streaming.API_PLAYER_INFO
import com.natife.streaming.api.MainApi
import com.natife.streaming.data.dto.player.PlayerDTO
import com.natife.streaming.data.matchprofile.Episode
import com.natife.streaming.data.matchprofile.toEpisode
import com.natife.streaming.data.request.*
import com.natife.streaming.db.dao.ActionDao
import com.natife.streaming.preferenses.MatchSettingsPrefs
import com.natife.streaming.preferenses.SettingsPrefs
import com.natife.streaming.utils.ImageUrlBuilder

interface PlayerActionUseCase {
       suspend fun execute(matchId: Int, sportId: Int, playerId: Int, type: Int = 1): List<Episode>
}

class PlayerActionUseCaseImpl(
    private val api: MainApi,
    private val prefs: MatchSettingsPrefs,
    private val dao: ActionDao,
    private val settingsPrefs: SettingsPrefs
) : PlayerActionUseCase {
    override suspend fun execute(
        matchId: Int,
        sportId: Int,
        playerId: Int,
        type: Int
    ): List<Episode> {

        val lang = settingsPrefs.getLanguage().toUpperCase()
        val fragments = api.getPlayerActions(
            BaseRequest(
                procedure = API_PLAYERS_ACTIONS,
                params = PlayerActionsRequest(
                    matchId = matchId,
                    playerId = playerId,
                    type = type,
                    actions = if (type == 3) {
                        dao.getSelectedActions(sportId).map { it.id }
                    } else {
                        null
                    },
                    offsetStart = if (type == 2 || type == 1) {
                        prefs.getAllSecBefore()
                    } else {
                        prefs.getSelectedSecBefore()
                    },
                    offsetEnd = if (type == 2 || type == 1) {
                        prefs.getAllSecAfter()
                    } else {
                        prefs.getSelectedSecAfter()
                    }
                )
            ), MatchProfileUseCase.getPath(sportId)
        )
        val player: PlayerDTO = api.getPlayerInfo(
            BaseRequest(
                procedure = API_PLAYER_INFO,
                params = PlayerInfoRequest(
                    sportId = sportId,
                    playerId = playerId
                )
            )
        )
        val match = api.getMatchInfoGlobal(
            BaseRequest(
                procedure = API_MATCH_INFO,
                params = MatchInfo(sportId, matchId)
            )
        )
        val previews = api.getMatchPreview(body = PreviewRequest().apply {
            add(PreviewRequestItem(matchId, sportId))
        })

        val image = if (previews.isNotEmpty()) {
            previews[0].preview
        } else {
            ImageUrlBuilder.getUrl(
                sportId,
                ImageUrlBuilder.Companion.Type.TOURNAMENT,
                match.tournament?.id ?: -1
            )
        }
        return fragments.data.map {
            it.toEpisode().copy(
                image = image,
                placeholder = ImageUrlBuilder.getPlaceholder(
                    sportId,
                    ImageUrlBuilder.Companion.Type.TOURNAMENT
                ),
                title = when (lang) {
                    "en", "EN" -> "${player.firstNameEng} ${player.lastNameEng}" ?: ""
                    "ru", "RU" -> "${player.firstNameRus} ${player.lastNameRus}" ?: ""
                    else -> "${player.firstNameEng} ${player.lastNameEng}" ?: ""
                }
            )
        }.sortedBy { it.startMs }
    }
}