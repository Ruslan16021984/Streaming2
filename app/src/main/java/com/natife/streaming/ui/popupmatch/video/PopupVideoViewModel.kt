package com.natife.streaming.ui.popupmatch.video

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.natife.streaming.R
import com.natife.streaming.base.BaseViewModel
import com.natife.streaming.data.Lexic
import com.natife.streaming.data.Video
import com.natife.streaming.data.match.Match
import com.natife.streaming.data.matchprofile.Episode
import com.natife.streaming.data.matchprofile.MatchInfo
import com.natife.streaming.data.matchprofile.Player
import com.natife.streaming.data.player.PlayerSetup
import com.natife.streaming.data.player.WatchType
import com.natife.streaming.data.search.SearchResult
import com.natife.streaming.router.Router
import com.natife.streaming.ui.popupmatch.video.watch.WatchFill
import com.natife.streaming.usecase.LexisUseCase
import com.natife.streaming.usecase.MatchInfoUseCase
import com.natife.streaming.usecase.MatchProfileUseCase
import com.natife.streaming.usecase.VideoUseCase
import kotlin.collections.ArrayList

abstract class PopupVideoViewModel : BaseViewModel() {
    abstract var popupVideoNames: MutableList<String>
    abstract val tabList: LiveData<List<String>>
    abstract val team1: LiveData<List<Player>>
    abstract val team2: LiveData<List<Player>>
    abstract val info: LiveData<MatchInfo>
    abstract val episodes: LiveData<List<Episode>>
    abstract val fullVideoDuration: LiveData<Long>
    abstract val match: LiveData<Match>
    abstract val strings: LiveData<List<Lexic>>
    abstract fun play(
        episode: Episode? = null,
        playList: List<Episode>? = null,
        playerPlayList: Pair<String, List<Episode>>? = null
    )
    abstract fun seVideoType(
        videoType: WatchFill? = null
    )

    abstract fun onStatisticClicked()
    abstract fun onCommandClicked(teamNumber: Int)
    abstract fun onFinishClicked()
    abstract fun updateData(strings: Array<String>)
}

class PopupVideoViewModelImpl(
    private val sport: Int,
    private val matchId: Int,
    private val matchProfileUseCase: MatchProfileUseCase,
    private val matchInfoUseCase: MatchInfoUseCase,
    private val router: Router,
    private val videoUseCase: VideoUseCase,
    private val lexisUseCase: LexisUseCase
) : PopupVideoViewModel() {

    private var _match: Match? = null
    private var videoType: WatchFill? = null
    override val match = MutableLiveData<Match>()

    private var videos: List<Video>? = null
    override val team1 = MutableLiveData<List<Player>>()
    override val team2 = MutableLiveData<List<Player>>()
    override val info = MutableLiveData<MatchInfo>()
    override val episodes = MutableLiveData<List<Episode>>()
    override val fullVideoDuration = MutableLiveData<Long>()
    private var matchInfo: MatchInfo? = null

    override val tabList = MutableLiveData<List<String>>()
    override var popupVideoNames = mutableListOf<String>()
    override val strings = MutableLiveData<List<Lexic>>()

    override fun updateData(strings: Array<String>) {
        popupVideoNames.clear()
        popupVideoNames.addAll(strings)
        val list = mutableListOf<String>()
        list.add(popupVideoNames[0])
        list.add(popupVideoNames[2])
        tabList.value = list

//        For now TabAdditionallyFragment(), TabMatchEventsFragment() and TabLanguagesFragment() are always empty
    }

    override fun play(
        episode: Episode?,
        playList: List<Episode>?,
        playerPlayList: Pair<String, List<Episode>>?
    ) {
        matchInfo?.let {
            val timeList = videos
                ?.filter { it.abc == "0" }
                ?.groupBy { it.quality }?.get("720")
                ?.sortedBy { it.period }
                ?.mapIndexed { index, video ->
                    Episode(
                        title = "${_match?.info}",
                        endMs = video.duration / 1000,
                        half = video.period,//  TODO иногда приходят не правильные данные
                        startMs = video.startMs,
                        image = _match?.image ?: "",
                        placeholder = _match?.placeholder ?: ""
                    )
                }
            if (!timeList.isNullOrEmpty()) {
                router.navigate(
                    PopupVideoFragmentDirections.actionGlobalPlayerFragment(
                        PlayerSetup(
                            playlist =
                            mutableMapOf<String, List<Episode>>(
                                playerPlayList?.first.toString() to (playerPlayList?.second
                                    ?: listOf()),
                                it.translates.fullGameTranslate to (timeList ?: listOf()),
                                it.translates.ballInPlayTranslate to it.ballInPlay,
                                it.translates.highlightsTranslate to it.highlights,
                                it.translates.goalsTranslate to it.goals,
                            ),
                            video = videos,
                            videoDurations = listOf(
                                if (playerPlayList?.second.isNullOrEmpty()) 0 else {
                                (ArrayList(playerPlayList?.second))                             // PLAYER
                                    .sumOf { episode -> (episode.endMs - episode.startMs)}},
                                fullVideoDuration.value ?:
                                (videos?.groupBy { it.quality }!!["720"] as ArrayList)          // FULL GAME
                                    .sumOf { it.duration.div(1000) },
                                it.ballInPlayDuration,
                                it.highlightsDuration,
                                it.goalsDuration
                            ),
                            playListType = when (videoType) {
                                is WatchFill.FullGame -> WatchType.FULL_GAME
                                is WatchFill.BallInPlay -> WatchType.BALL_IN_PLAY
                                is WatchFill.Highlights -> WatchType.HIGHLIGHTS
                                is WatchFill.FieldGoals -> WatchType.GOALS
                                else -> WatchType.PLAYER
                            },
                            currentEpisode = episode,
                            currentPlaylist = playList ?: playerPlayList?.second,
                            startTitle = listOf(playerPlayList?.first ?: "",
                                "${_match?.team1?.name} ${_match?.team1?.score} - ${_match?.team2?.score} ${_match?.team2?.name}"),
                            match = _match,
                            titlesForButtons = mapOf(
                                Pair(
                                    playerPlayList?.first.toString(),
                                    playerPlayList?.first.toString()
                                ),
                                Pair(
                                    it.translates.fullGameTranslate,
                                    "${_match?.team1?.name} : ${_match?.team2?.name}"
                                ),
                                Pair(
                                    it.translates.ballInPlayTranslate,
                                    "${_match?.team1?.name} : ${_match?.team2?.name}"
                                ),
                                Pair(
                                    it.translates.highlightsTranslate,
                                    "${_match?.team1?.name} : ${_match?.team2?.name}"
                                ),
                                Pair(
                                    it.translates.goalsTranslate,
                                    "${_match?.team1?.name} : ${_match?.team2?.name}"
                                ),
                            )
                        ),
                        matchId = matchId,
                        sportId = sport
                    )
                )
            } else {
                router.toast("Video not found!")
                router.popBackStack()
            }
        }
    }

    override fun seVideoType(videoType: WatchFill?) {
        this.videoType = videoType
    }

    override fun onStatisticClicked() {
        val direction =
            PopupVideoFragmentDirections.actionPopupVideoFragmentToPopupStatisticsFragment(
                sportId = sport,
                matchId = matchId
            )
        router.navigate(direction)
    }

    override fun onCommandClicked(teamNumber: Int) {
        val direction =
            PopupVideoFragmentDirections.actionPopupVideoFragmentToTournamentFragment(
                sportId = match.value?.sportId ?: 0,
                tournamentId = when (teamNumber) {
                    1 -> match.value?.team1?.id ?: 0
                    2 -> match.value?.team2?.id ?: 0
                    else -> 0
                },
                type = SearchResult.Type.TEAM
            )
        router.navigate(direction)
    }

    override fun onFinishClicked() {
        router.navigate(R.id.action_global_nav_main)
    }

    init {
        launch {
            val str = lexisUseCase.execute(
                R.integer.watch,
                R.integer.additionally,
                R.integer.by_players,
                R.integer.match_events,
                R.integer.languages,
                R.integer.statistics
            )
            strings.value = str
        }
        launch {
            _match = matchInfoUseCase.execute(sportId = sport, matchId = matchId)
            match.value = _match
        }
        launch {
            matchInfo = matchProfileUseCase.getMatchInfo(matchId, sport)
            info.value = matchInfo
            team1.value = matchInfo?.players1
            team2.value = matchInfo?.players2
            episodes.value = matchInfo?.highlights
        }
        launch {
            val video = videoUseCase.execute(matchId, sport)
            videos = video

            fullVideoDuration.value = (videos
                ?.filter { it.abc == "0" && it.quality == "720" }
                ?.sortedBy { it.period }?.sumOf { it.duration })?.div(1000)
        }
    }
}
