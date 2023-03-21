package com.natife.streaming.ui.popupmatch

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.natife.streaming.base.BaseViewModel
import com.natife.streaming.data.match.Match
import com.natife.streaming.data.matchprofile.Episode
import com.natife.streaming.data.matchprofile.MatchInfo
import com.natife.streaming.data.matchprofile.Player
import com.natife.streaming.ext.Event
import com.natife.streaming.ui.popupmatch.video.watch.WatchFill
import com.natife.streaming.usecase.PlayerActionUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.withContext
import org.koin.core.KoinComponent
import org.koin.core.get

class PopupSharedViewModel : BaseViewModel(), KoinComponent {
    private val _startViewID = MutableLiveData<ArrayList<Int>>()
        .apply {
            postValue(
                arrayListOf(
                    View.NO_ID,
                    View.NO_ID,
                    View.NO_ID,
                    View.NO_ID,
                    View.NO_ID
                )
            )
        }
    val startViewID: LiveData<ArrayList<Int>> = _startViewID
    private var playerJob: Job? = null
    private val playerUseCase: PlayerActionUseCase = get()

    var matchId: Int = -1
    var sportId: Int = -1

    private val _match = MutableLiveData<Match>()
    val match: LiveData<Match> = _match

    private val _matchInfo = MutableLiveData<MatchInfo>()
    val matchInfo: LiveData<MatchInfo> = _matchInfo

    private val _fullVideoDuration = MutableLiveData<Long>()
    val fullVideoDuration: LiveData<Long> = _fullVideoDuration

    private val _episodes = MutableLiveData<List<Episode>>()
    val episodes: LiveData<List<Episode>> = _episodes

    private val _team1 = MutableLiveData<List<Player>>()
    val team1: LiveData<List<Player>> = _team1

    private val _team2 = MutableLiveData<List<Player>>()
    val team2: LiveData<List<Player>> = _team2

    private val _playEpisode = MutableLiveData<Event<Episode?>>()
    val playEpisode: LiveData<Event<Episode?>> = _playEpisode

    private val _playPlayList = MutableLiveData<Event<List<Episode>?>>()
    val playPlayList: LiveData<Event<List<Episode>?>> = _playPlayList

    private val _playPlayListPlayers = MutableLiveData<Event<Pair<String, List<Episode>>>>()
    val playPlayListPlayers: LiveData<Event<Pair<String, List<Episode>>>> = _playPlayListPlayers

    private val _videoType = MutableLiveData<WatchFill>()
    val videoType: LiveData<WatchFill> = _videoType

    fun setStartId(startId: ArrayList<Int>) {
        _startViewID.value = startId
    }

    fun setMatch(math: Match) {
        _match.value = math
    }

    fun setMatchInfo(mathInfo: MatchInfo) {
        _matchInfo.value = mathInfo
    }

    fun setFullVideoDuration(fullVideoDuration: Long) {
        _fullVideoDuration.value = fullVideoDuration
    }

    fun setEpisodes(episodes: List<Episode>) {
        _episodes.value = episodes
    }

    fun setTeam1(team1: List<Player>) {
        _team1.value = team1
    }

    fun setTeam2(team2: List<Player>) {
        _team2.value = team2
    }

    fun goals() {
        matchInfo?.let {
            if (!it.value?.goals.isNullOrEmpty()) {
                _videoType.value = WatchFill.FieldGoals("FieldGoals", "${it.value?.goalsDuration}")
                _playPlayList.value = Event(it.value?.goals)
            }
        }
    }

    fun review() {
        matchInfo?.let {
            //episodes.value = it.highlights
            if (!it.value?.highlights.isNullOrEmpty()) {
                _videoType.value = WatchFill.Highlights("Highlights", "${it.value?.highlightsDuration}")
                _playPlayList.value = Event(it.value?.highlights)
            }

        }
    }

    fun ballInPlay() {
        matchInfo?.let {
            //episodes.value = it.ballInPlay
            if (!it.value?.ballInPlay.isNullOrEmpty()) {
                _videoType.value = WatchFill.BallInPlay("BallInPlay", "${it.value?.ballInPlayDuration}")
                _playPlayList.value = Event(it.value?.ballInPlay)
            }
        }
    }

    fun fullMatch() {
        _videoType.value = WatchFill.FullGame("FullGame", "")
        _match?.let {
            _playEpisode.value = Event(
                Episode(
                    startMs = 0,
                    endMs = -1,
                    half = 0,
                    title = "${it.value?.info}",
                    image = it.value?.image ?: "",
                    placeholder = it.value?.placeholder ?: ""
                )
            )
        }
    }

    fun playEpisode(episod: Episode) {
        _playEpisode.value = Event(episod)
    }

    fun player(player: Player) {
        if (matchId == -1 || sportId == -1) return
        playerJob?.cancel()
        playerJob = launch {
            withContext(Dispatchers.IO) {
                val episodes = playerUseCase.execute(matchId, sportId, player.id)
                    .filterNot { it.startMs == it.endMs }
                withContext(Dispatchers.Main) {
                    _playPlayListPlayers.value = Event(Pair(player.name, episodes))
                }
            }
        }
    }
}