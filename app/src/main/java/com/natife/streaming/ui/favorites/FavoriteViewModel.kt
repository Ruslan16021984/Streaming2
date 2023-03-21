package com.natife.streaming.ui.favorites

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.natife.streaming.R
import com.natife.streaming.base.BaseViewModel
import com.natife.streaming.data.match.Match
import com.natife.streaming.data.search.SearchResult
import com.natife.streaming.datasource.MatchParams
import com.natife.streaming.db.LocalSqlDataSourse
import com.natife.streaming.ext.toRequest
import com.natife.streaming.preferenses.SettingsPrefs
import com.natife.streaming.router.Router
import com.natife.streaming.usecase.FavoritesUseCase
import com.natife.streaming.usecase.LexisUseCase
import com.natife.streaming.usecase.MatchUseCase
import com.natife.streaming.utils.findLexicText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.util.*
import java.util.concurrent.atomic.AtomicBoolean

abstract class FavoriteViewModel : BaseViewModel() {
    abstract val favorites: LiveData<List<FavoritesAdapter.Favorite>>
    abstract val matches: LiveData<List<Match>>
    abstract fun onFavoriteSelected(searchResult: SearchResult)
    abstract fun loadNext()
    abstract fun goToProfile(match: Match)
    abstract fun initialization()
}

class FavoriteViewModelImpl(
    private val favoritesUseCase: FavoritesUseCase,
    private val matchUseCase: MatchUseCase,
    private val router: Router,
    private val context: Context,
    private val localSqlDataSourse: LocalSqlDataSourse,
    private val settingsPrefs: SettingsPrefs,
    private val lexisUseCase: LexisUseCase
) : FavoriteViewModel() {
    override val favorites = MutableLiveData<List<FavoritesAdapter.Favorite>>()
    override val matches = MutableLiveData<List<Match>>()
    private var process: Job? = null
    private var selected: SearchResult? = null
    private val showScore = MutableLiveData<Boolean>()

    override fun onFavoriteSelected(searchResult: SearchResult) {
        Timber.e("$searchResult")

        if (searchResult.id > 0) {
            router.navigate(
                FavoritesFragmentDirections.actionFavoritesFragmentToTournamentFragment(
                    searchResult.sport,
                    searchResult.id,
                    searchResult.type
                )
            )
        }

        launchCatching {
            selected = searchResult
        }
    }

    private val mutex = Mutex()
    private var isLoading = AtomicBoolean(false)
    override fun loadNext() {
        launch {
            mutex.withLock {
                if (isLoading.get()) {
                    return@launch
                }
            }
            isLoading.set(true)
            matchUseCase.load(MatchUseCase.Type.SIMPLE)
            isLoading.set(false)
        }
    }

    private fun prepareAndLoad(params: MatchParams) {
        launch {
            withContext(Dispatchers.IO) {
                matchUseCase.prepare(params)
                loadNext()
            }
        }
    }


    override fun goToProfile(match: Match) {
        when {
            (match.live && match.subscribed) -> {
                router.navigate(
                    FavoritesFragmentDirections.actionFavoritesFragmentToLiveDialog(
                        sportId = match.sportId,
                        matchId = match.id,
                        title = "${match.team1.name} - ${match.team2.name}"
                    )
                )
            }
            (match.hasVideo && match.subscribed) -> {
                router.navigate(
                    FavoritesFragmentDirections.actionFavoritesFragmentToPopupVideo(
                        sportId = match.sportId,
                        matchId = match.id
                    )
                )
            }
            (match.live && !match.subscribed) -> {
                router.navigate(
                    FavoritesFragmentDirections.actionFavoritesFragmentToBillingFragment(
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
                    FavoritesFragmentDirections.actionFavoritesFragmentToBillingFragment(
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

    override fun initialization() {
        //tracking global settings
        launch {
            withContext(Dispatchers.IO) {
                collect(localSqlDataSourse.getGlobalSettingsFlow()) { globalSetings ->
                    showScore.value = globalSetings?.showScore
                    matches.value?.let { matchList ->
                        matchList.map { match ->
                            match.copy(isShowScore = globalSetings?.showScore ?: false)
                        }.let {
                            matches.value = it.filter { match -> match.access }
                        }
                    }
                }
            }
        }

        launch {
            val favorites = favoritesUseCase.execute()
            val tournamentFavorites: List<SearchResult> =
                favorites.filter { it.type == SearchResult.Type.TOURNAMENT }
            val teamFavorites = favorites.filter { it.type == SearchResult.Type.TEAM }
            collect(matchUseCase.list) { matchList ->
                matchList.map { match ->
                    match.copy(
                        isShowScore = showScore.value ?: false,
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
                }.let {
                    matches.value = it.filter { match -> match.access }
                }
            }
        }

        launch {
            withContext(Dispatchers.IO) {
                collect(settingsPrefs.getDateFlow()) {
                    it?.let {
                        val params = MatchParams(
                            date = Date(it).toRequest(),
                            sportId = null,
                            subOnly = false,
                            additionalId = null,
                            pageSize = 60,
                        )
                        prepareAndLoad(params)
                    }
                }
            }
        }

        launch {
            val str = lexisUseCase.execute(
                R.integer.all,
                R.integer.players,
                R.integer.teams,
                R.integer.tournaments
            )

            loadNext()
            val _favorites = favoritesUseCase.execute()
            val groups = _favorites.groupBy { it.type }
            val list = mutableListOf<FavoritesAdapter.Favorite>()
            list.add(
                SearchResult(
                    name = str.findLexicText(context, R.integer.all)
                        ?: context.resources.getString(R.string.all),
                    sport = -1,
                    gender = -1,
                    image = "",
                    placeholder = "",
                    id = -1,
                    type = SearchResult.Type.NON,
                    countryId = -1,
                    countryName = ""
                )
            )
            groups.keys.forEach {
                when (it) {
                    SearchResult.Type.PLAYER -> {
                        list.add(
                            FavoritesAdapter.Header(
                                text = str.findLexicText(context, R.integer.players)
                                    ?: context.resources.getString(R.string.players)
                            )
                        )
                        list.addAll(groups[it]?.toList() ?: listOf())
                    }
                    SearchResult.Type.TEAM -> {
                        list.add(
                            FavoritesAdapter.Header(
                                text = str.findLexicText(context, R.integer.teams)
                                    ?: context.resources.getString(R.string.commands)
                            )
                        )
                        list.addAll(groups[it]?.toList() ?: listOf())
                    }
                    SearchResult.Type.TOURNAMENT -> {
                        list.add(
                            FavoritesAdapter.Header(
                                text = str.findLexicText(context, R.integer.tournaments)
                                    ?: context.resources.getString(R.string.tournaments)
                            )
                        )
                        list.addAll(groups[it]?.toList() ?: listOf())
                    }
                }
            }
            favorites.value = list
        }
    }


}