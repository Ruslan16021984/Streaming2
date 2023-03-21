package com.natife.streaming.usecase

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.natife.streaming.*
import com.natife.streaming.api.MainApi
import com.natife.streaming.data.dto.match.MatchesDTO
import com.natife.streaming.data.match.Match
import com.natife.streaming.data.match.Team
import com.natife.streaming.data.match.Tournament
import com.natife.streaming.data.request.*
import com.natife.streaming.datasource.MatchDataSourceFactory
import com.natife.streaming.datasource.MatchParams
import com.natife.streaming.db.LocalSqlDataSourse
import com.natife.streaming.preferenses.SettingsPrefs
import com.natife.streaming.utils.ImageUrlBuilder
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.first

interface ProfileUseCase {
    suspend fun prepare(params: MatchParams)
    suspend fun load(type: Type = Type.SIMPLE)
    abstract val list: SharedFlow<List<Match>>
    fun executeFlow(pageSize: Int = 60): Flow<PagingData<Match>>
    enum class Type {
        SIMPLE,
        TOURNAMENT,
        TEAM,
        PLAYER
    }
}

class ProfileUseCaseImpl(
    private val matchDataSourceFactory: MatchDataSourceFactory,
    private val api: MainApi,
    private val localSqlDataSourse: LocalSqlDataSourse,
    private val settingsPrefs: SettingsPrefs
) : ProfileUseCase {

    private val _list =
        MutableSharedFlow<List<Match>>(replay = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST)
    override val list: SharedFlow<List<Match>> = _list
    private var gotEnd = false
    private var page = 0
    private var requestParams: MatchParams? = null
    private var lang = "EN"

    override suspend fun prepare(
        params: MatchParams
    ) {
        if (params != requestParams) {
            requestParams = params
            page = 0
            gotEnd = false
            _list.emit(listOf())
        }
        lang = settingsPrefs.getLanguage().toUpperCase()
    }


    override suspend fun load(type: ProfileUseCase.Type) {
//        Timber.e("list $list")
        if (!gotEnd) {
            val mPrams = when (type) {
                ProfileUseCase.Type.TOURNAMENT, ProfileUseCase.Type.SIMPLE -> MatchesRequestSimpleTournament(
                    date = requestParams?.date,
                    limit = requestParams?.pageSize ?: 60,
                    offset = (requestParams?.pageSize ?: 60) * page,
                    sport = requestParams?.sportId,
                    subOnly = requestParams?.subOnly ?: false,
                    tournamentId = requestParams?.additionalId
                )
                ProfileUseCase.Type.TEAM -> TeamMatchesRequest(
                    date = requestParams?.date,
                    limit = requestParams?.pageSize ?: 60,
                    offset = (requestParams?.pageSize ?: 60) * page,
                    sport = requestParams?.sportId,
                    subOnly = requestParams?.subOnly ?: false,
                    teamId = requestParams?.additionalId
                )
                ProfileUseCase.Type.PLAYER -> PlayerMatchesRequest(
                    date = requestParams?.date,
                    limit = requestParams?.pageSize ?: 60,
                    offset = (requestParams?.pageSize ?: 60) * page,
                    sport = requestParams?.sportId,
                    subOnly = requestParams?.subOnly ?: false,
                    playerId = requestParams?.additionalId
                )
            }
            val matches = api.getMatches(
                BaseRequest(
                    procedure = when (type) {
                        ProfileUseCase.Type.SIMPLE -> API_MATCHES
                        ProfileUseCase.Type.TOURNAMENT -> API_TOURNAMENT_MATCHES
                        ProfileUseCase.Type.TEAM -> API_TEAM_MATCHES
                        ProfileUseCase.Type.PLAYER -> API_PLAYER_MATCHES
                    },
                    params = mPrams
                )
            )
            val newList = mutableListOf<Match>()
            newList.addAll(this._list.first())
            val preload = matches.videoContent.broadcast?.map { match ->
                Match(
                    id = match.id,
                    sportId = match.sport ?: requestParams?.sportId ?: 0,
                    countryId = match.countryId,
                    sportName = ""
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
                    info = "",
                    access = match.access,
                    hasVideo = match.hasVideo,
                    image = "",
                    placeholder = ImageUrlBuilder.getPlaceholder(
                        match.sport ?: requestParams?.sportId ?: 0,
                        ImageUrlBuilder.Companion.Type.TOURNAMENT
                    ),
                    live = match.live,
                    storage = match.storage,
                    subscribed = match.sub,
                    isShowScore = localSqlDataSourse.getGlobalSettings()?.showScore ?: false
                )
            }
            newList.addAll(preload?.toList() ?: emptyList())
            this._list.emit(newList.toList())
            val compl = loadInfo(matches) ?: emptyList()
            val start = ((newList.size - 1) - ((preload?.size ?: 0) - 1))
            val end = newList.size - 1
//            Timber.e("JIDUHDIUDHIUD $start $end ${newList.size} ${preload?.size}")
            if (start in 0 until end) {
                newList.removeAll(preload ?: emptyList())
                newList.addAll(compl)
            }
            val finalList = mutableListOf<Match>()
            finalList.addAll(newList)
            this._list.emit(finalList.toList())
            page++
        }
//         return list
    }


    private suspend fun loadInfo(matches: MatchesDTO): List<Match>? {
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

        val previews = if (!matches.videoContent.broadcast.isNullOrEmpty()) {
            api.getMatchPreview(body = PreviewRequest().apply {
                matches.videoContent.broadcast?.map {
                    PreviewRequestItem(it.id, it.sport ?: requestParams?.sportId ?: 1)
                }?.let {
                    addAll(
                        it
                    )
                }
            })
        } else {
            null
        }

        val data = matches.videoContent.broadcast?.map { match ->
            coroutineScope {
                val profile = async {

                    api.getMatchProfile(
                        BaseRequest(
                            procedure = API_MATCH_PROFILE, params = MatchProfileRequest(
                                sportId = match.sport ?: requestParams?.sportId ?: 0,
                                tournament = match.tournament?.id ?: requestParams?.additionalId
                                ?: 0
                            )
                        )
                    )


                }

                val image = if (!match.hasVideo) {
                    ImageUrlBuilder.getUrl(
                        match.sport ?: requestParams?.sportId ?: 0,
                        ImageUrlBuilder.Companion.Type.TOURNAMENT, match.tournament?.id ?: -1
                    )
                } else {
                    previews?.find { it.matchId == match.id }?.preview ?: ImageUrlBuilder.getUrl(
                        match.sport ?: requestParams?.sportId ?: 0,
                        ImageUrlBuilder.Companion.Type.TOURNAMENT, match.tournament?.id ?: -1
                    )
                }

                Match(
                    id = match.id,
                    sportId = match.sport ?: requestParams?.sportId ?: 0,
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
                        "en", "EN" -> "${profile.await()?.country?.name_eng} ${profile.await()?.nameEng}"
                            ?: ""
                        "ru", "RU" -> "${profile.await()?.country?.name_rus} ${profile.await()?.nameRus}"
                            ?: ""
                        else -> "${profile.await()?.country?.name_eng} ${profile.await()?.nameEng}"
                            ?: ""
                    },
                    access = match.access,
                    hasVideo = match.hasVideo,
                    image = image,
                    placeholder = ImageUrlBuilder.getPlaceholder(
                        match.sport ?: requestParams?.sportId ?: 0,
                        ImageUrlBuilder.Companion.Type.TOURNAMENT
                    ),
                    live = match.live,
                    storage = match.storage,
                    subscribed = match.sub,
                    isShowScore = localSqlDataSourse.getGlobalSettings()?.showScore ?: false
                )
            }
        }
        return data
    }

    @Deprecated("Use method above")
    override fun executeFlow(pageSize: Int): Flow<PagingData<Match>> {
        return Pager(PagingConfig(pageSize = pageSize)) {
            matchDataSourceFactory.invoke()
        }.flow
    }
}