package com.natife.streaming.ui.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.natife.streaming.R
import com.natife.streaming.base.BaseViewModel
import com.natife.streaming.data.Lexic
import com.natife.streaming.data.search.SearchResult
import com.natife.streaming.router.Router
import com.natife.streaming.usecase.LexisUseCase
import com.natife.streaming.usecase.SearchUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay

abstract class SearchViewModel : BaseViewModel() {
    abstract val resultsTeam: LiveData<List<SearchResult>>
    abstract val resultsPlayer: LiveData<List<SearchResult>>
    abstract val resultsTournament: LiveData<List<SearchResult>>
    abstract val setupTypeSearchResult: LiveData<SearchResult.Type>
    abstract val strings: LiveData<List<Lexic>>

    abstract fun search(text: String)
    abstract fun select(result: SearchResult)
}

class SearchViewModelImpl(
    private val searchUseCase: SearchUseCase,
    private val router: Router,
    private val lexisUseCase: LexisUseCase
) : SearchViewModel() {
    override val resultsTeam = MutableLiveData<List<SearchResult>>()
    override val resultsPlayer = MutableLiveData<List<SearchResult>>()
    override val resultsTournament = MutableLiveData<List<SearchResult>>()
    override val setupTypeSearchResult = MutableLiveData<SearchResult.Type>()
    override val strings = MutableLiveData<List<Lexic>>()
    private var job: Job? = null

    init {
        launch {
            val str = lexisUseCase.execute(
                R.integer.players,
                R.integer.teams,
                R.integer.tournaments,
                R.integer.tournaments_not_found
            )
            strings.value = str
        }
    }

    override fun search(text: String) {
        job?.cancel()
        job = launch {
            val list = searchUseCase.execute(text)
            delay(500)
            filter(list)
        }
    }

    private fun filter(sourceList: List<SearchResult>) {
        launch {
            resultsTeam.value = sourceList.filter { it.type == SearchResult.Type.TEAM }
            resultsPlayer.value = sourceList.filter { it.type == SearchResult.Type.PLAYER }
            resultsTournament.value = sourceList.filter { it.type == SearchResult.Type.TOURNAMENT }
            delay(500)
            setupTypeSearchResult.postValue(
                when {
                    resultsPlayer.value?.isNotEmpty() == true -> SearchResult.Type.PLAYER
                    resultsTeam.value?.isNotEmpty() == true -> SearchResult.Type.TEAM
                    resultsTournament.value?.isNotEmpty() == true -> SearchResult.Type.TOURNAMENT
                    else -> SearchResult.Type.PLAYER
            })
        }
    }

    override fun select(result: SearchResult) {
        router.navigate(
            SearchFragmentDirections.actionSearchFragmentToTournamentFragment(
                result.sport,
                tournamentId = result.id,
                type = result.type
            )
        )
    }
}