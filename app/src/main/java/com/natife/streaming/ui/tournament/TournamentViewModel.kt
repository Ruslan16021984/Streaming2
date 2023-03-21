package com.natife.streaming.ui.tournament

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.natife.streaming.base.BaseViewModel
import com.natife.streaming.data.Tournament
import com.natife.streaming.data.match.Match
import com.natife.streaming.data.matchprofile.Player
import com.natife.streaming.data.search.SearchResult
import com.natife.streaming.datasource.MatchParams
import com.natife.streaming.db.LocalSqlDataSourse
import com.natife.streaming.router.Router
import com.natife.streaming.usecase.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import java.util.concurrent.atomic.AtomicBoolean

class TournamentViewModel(
    private val sportId: Int,
    private val additionalId: Int,
    private val type: SearchResult.Type,
    private val router: Router,
    private val tournamentUseCase: TournamentUseCase,
    private val teamUseCase: TeamUseCase,
    private val playerUseCase: PlayerUseCase,
    private val matchUseCase: ProfileUseCase,
    private val saveDeleteFavoriteUseCase: SaveDeleteFavoriteUseCase,
    private val localSqlDataSourse: LocalSqlDataSourse,
    private val profileColorUseCase: ProfileColorUseCase,
    private val favoritesUseCase: FavoritesUseCase,
) : BaseViewModel() {
    private val _tournament = MutableLiveData<Tournament>()
    private val _team = MutableLiveData<SearchResult>()
    private val _player = MutableLiveData<Player>()
    private val _list = MutableLiveData<List<Match>>()
    private val _color = MutableLiveData<String>()
    val list: LiveData<List<Match>> = _list
    val tournament: LiveData<Tournament> = _tournament
    val team: LiveData<SearchResult> = _team
    val player: LiveData<Player> = _player
    val color: LiveData<String> = _color
    private var params = MatchParams(
        date = null,
        pageSize = 60,
        sportId = null,
        subOnly = false,
        additionalId = null
    )

    private val mutex = Mutex()
    private var isLoading = AtomicBoolean(false)
    fun loadList() {
        launch {
            mutex.withLock {
                if (isLoading.get()) {
                    return@launch
                }
            }

            isLoading.set(true)

            when (type) {
                SearchResult.Type.PLAYER -> matchUseCase.load(ProfileUseCase.Type.PLAYER)
                SearchResult.Type.TEAM -> matchUseCase.load(ProfileUseCase.Type.TEAM)
                SearchResult.Type.TOURNAMENT -> matchUseCase.load(ProfileUseCase.Type.TOURNAMENT)
                SearchResult.Type.NON -> matchUseCase.load(ProfileUseCase.Type.TOURNAMENT)
            }
            isLoading.set(false)

        }
    }

//    private fun saveSecond(){
//        launch {
//            var a = saveUserLiveMatchSecond.execute(
//                _sportId = 1,
//                _matchId = 1724780,
//                _half = 1,
//                _second = 20
//            )
//        }
//    }
//
//    private fun getSecond(){
//        launch {
//            var a =getUserLiveMatchSecond.execute(
//                _sportId = 1,
//                _matchId = 1724780,
//            )
//        }
//    }

    init {
        launchCatching {
            val isShowScore = localSqlDataSourse.getGlobalSettings()?.showScore
            val favorites = favoritesUseCase.execute()
            val tournamentFavorites: List<SearchResult> =
                favorites.filter { it.type == SearchResult.Type.TOURNAMENT }
            val teamFavorites = favorites.filter { it.type == SearchResult.Type.TEAM }
            collect(matchUseCase.list) { listMatch ->
                listMatch.map { match ->
                    match.copy(
                        isShowScore = isShowScore ?: false,
                        isFavoriteTournament = tournamentFavorites.firstOrNull {
                            it.id == match.tournament.id
                        }?.isFavorite ?: false,
                        isFavoriteTeam1 = teamFavorites.firstOrNull {
                            it.id == match.team1.id
                        }?.isFavorite ?: false,
                        isFavoriteTeam2 = teamFavorites.firstOrNull {
                            it.id == match.team2.id
                        }?.isFavorite ?: false,
                    )
                }.let { matchList ->
                    _list.value = matchList.filter { match -> match.access }
                }
            }
        }

        params = params.copy(additionalId = additionalId, sportId = sportId)
        when (type) {
            SearchResult.Type.PLAYER -> launch {
                _player.value = playerUseCase.execute(sportId, additionalId)
                _color.value = profileColorUseCase.execute(sportId, "player", additionalId)
            }
            SearchResult.Type.TEAM -> launch {
                _team.value = teamUseCase.execute(sportId, additionalId)
                _color.value = profileColorUseCase.execute(sportId, "team", additionalId)
            }
            SearchResult.Type.TOURNAMENT -> launch {
                _tournament.value = tournamentUseCase.execute(sportId, additionalId)
                _color.value = profileColorUseCase.execute(sportId, "tournament", additionalId)
            }
            SearchResult.Type.NON -> launch { cancel() }
        }

        launch {
            matchUseCase.prepare(params)
        }
        launch {
            matchUseCase.prepare(params)
            withContext(Dispatchers.IO) {
                loadList()
            }

        }

    }

    fun addToFavorite(tournament: Tournament) {
        launch {
            if (tournament.isFavorite) {
                saveDeleteFavoriteUseCase.executeDelete(
                    sportId = sportId,
                    id = additionalId,
                    type = SearchResult.Type.TOURNAMENT
                )
            } else {
                saveDeleteFavoriteUseCase.executeSave(
                    sportId = sportId,
                    id = additionalId,
                    type = SearchResult.Type.TOURNAMENT
                )
            }
            _tournament.value = tournament.copy(isFavorite = !tournament.isFavorite)
        }


    }

    fun addToFavorite(player: Player) {
        launch {
            if (player.isFavorite) {
                saveDeleteFavoriteUseCase.executeDelete(
                    sportId = sportId,
                    id = additionalId,
                    type = SearchResult.Type.PLAYER
                )
            } else {
                saveDeleteFavoriteUseCase.executeSave(
                    sportId = sportId,
                    id = additionalId,
                    type = SearchResult.Type.PLAYER
                )
            }
            _player.value = player.copy(isFavorite = !player.isFavorite)
        }


    }

    fun addToFavorite(team: SearchResult) {
        launch {
            if (team.isFavorite) {
                saveDeleteFavoriteUseCase.executeDelete(
                    sportId = sportId,
                    id = additionalId,
                    type = SearchResult.Type.TEAM
                )
            } else {
                saveDeleteFavoriteUseCase.executeSave(
                    sportId = sportId,
                    id = additionalId,
                    type = SearchResult.Type.TEAM
                )
            }
            _team.value = team.copy(isFavorite = !team.isFavorite)
        }


    }

    fun toProfile(match: Match) {
//        router.navigate(
//            TournamentFragmentDirections.actionTournamentFragmentToPopupVideo(      //    old ver     actionTournamentFragmentToMatchProfileFragment
//                match.id,
//                match.sportId
//            )
//        )


        when {
            (match.live && match.subscribed) -> {
                router.navigate(
                    TournamentFragmentDirections.actionTournamentFragmentToLiveDialog(
                        sportId = match.sportId,
                        matchId = match.id,
                        title = "${match.team1} - ${match.team2}"
                    )
                )
            }
            (match.hasVideo && match.subscribed) -> {
                router.navigate(
                    TournamentFragmentDirections.actionTournamentFragmentToPopupVideo(
                        sportId = match.sportId,
                        matchId = match.id
                    )
                )
            }
            (match.live && !match.subscribed) -> {
                router.navigate(
                    TournamentFragmentDirections.actionTournamentFragmentToBillingFragment(
                        sportId = match.sportId,
                        matchId = match.id,
                        tournamentId = match.tournament.id,
                        tournamentTitle = match.tournament.name,
                        live = match.live,
                        team1 = match.team1.name,
                        team2 = match.team2.name
                    )
                )
            }
            (match.hasVideo && !match.subscribed) -> {
                router.navigate(
                    TournamentFragmentDirections.actionTournamentFragmentToBillingFragment(
                        sportId = match.sportId,
                        matchId = match.id,
                        tournamentId = match.tournament.id,
                        tournamentTitle = match.tournament.name,
                        live = match.live,
                        team1 = match.team1.name,
                        team2 = match.team2.name
                    )
                )
            }
            else -> return
        }
    }

    fun onBackToSearchClicked() {
//        router.toSearch()
        router.navigate(TournamentFragmentDirections.actionTournamentFragmentToSearchFragment())
    }

    fun onBackToFavoritesClicked() {
        router.navigateUp()
    }
}