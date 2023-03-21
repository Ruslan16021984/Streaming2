package com.natife.streaming.ui.player

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.exoplayer2.source.MediaSource
import com.natife.streaming.*
import com.natife.streaming.base.LexisViewModel
import com.natife.streaming.data.Lexic
import com.natife.streaming.data.matchprofile.Episode
import com.natife.streaming.data.player.*
import com.natife.streaming.ext.Event
import com.natife.streaming.router.Router
import com.natife.streaming.ui.player.menu.quality.VideoQualityParams
import com.natife.streaming.usecase.GetHLSVideoUseCase
import com.natife.streaming.usecase.LexisUseCase

abstract class PlayerViewModel : LexisViewModel() {
    abstract fun play(it: Episode, playlist: List<Episode>? = null)
    abstract fun setVideoQualityList(qualityParams: List<String>)
    abstract fun openVideoQualityMenu()
    abstract fun changeVideoQuality(videoQuality: String, currentPosition: Long, half: Int)
    abstract fun updatePlayList(list: List<Episode>, videoType: WatchType)
    abstract fun updatePosition(currentPosition: Long, half: Int, isUpdatePlayer: Boolean)
    abstract fun nextEpisode(startPosition: Long)
    abstract fun previewEpisode(isStart: Boolean)
    abstract fun onBackClicked(message: String = "")
    abstract fun setFullVideoDuration(duration: Long)
    abstract fun intervalRewind(sec: Int, currentPosition: Long, half: Int)

    abstract val mediaSourceLiveData: LiveData<MediaSource>
    abstract val currentPlayListEpisodes: LiveData<List<Episode>>
    abstract val videoLiveData: LiveData<Event<List<Pair<String, Long>>>>
    abstract val matchInfoLiveData: LiveData<String>
    abstract val currentVideoDuration: LiveData<Long>
    abstract val fullVideoDuration: LiveData<Long>
    abstract val sourceLiveData: LiveData<Map<String, List<Episode>>>
    abstract val videoQualityListLiveData: LiveData<List<String>>
    abstract val initBottomBarData: LiveData<PlayerBottomBarSetup?>
    abstract var currentWindow: Int
    abstract val videoQualityParams: LiveData<VideoQualityParams>
    abstract val isEnableNextEpisode: LiveData<Boolean>
    abstract val isEnablePreviewEpisode: LiveData<Boolean>
    abstract val updatePosition: LiveData<PlayerCurrentData>
}

class PlayerViewModelImpl(
    private val matchId: Int,
    private val sportId: Int,
    private val setup: PlayerSetup,
    private val router: Router,
    private val lexisUseCase: LexisUseCase,
    private val getHLSVideoUseCase: GetHLSVideoUseCase
) : PlayerViewModel() {
    override val strings = MutableLiveData<List<Lexic>>()
    override val mediaSourceLiveData = MutableLiveData<MediaSource>()
    override val currentPlayListEpisodes = MutableLiveData<List<Episode>>()
    override val videoLiveData = MutableLiveData<Event<List<Pair<String, Long>>>>()
    override val matchInfoLiveData = MutableLiveData<String>()
    override val sourceLiveData = MutableLiveData<Map<String, List<Episode>>>()
    private val currentEpisode = MutableLiveData<Episode>()
    override val fullVideoDuration = MutableLiveData<Long>()
    override val currentVideoDuration = MutableLiveData<Long>()
    override val videoQualityListLiveData = MutableLiveData<List<String>>()
    override val initBottomBarData = MutableLiveData<PlayerBottomBarSetup?>()
    override var currentWindow: Int = 0
        set(value) {
            field = if (value >= 0) value else 0
        }
    override val videoQualityParams = MutableLiveData<VideoQualityParams>()
    override val isEnableNextEpisode = MutableLiveData<Boolean>(true)
    override val isEnablePreviewEpisode = MutableLiveData<Boolean>(false)
    override val updatePosition = MutableLiveData<PlayerCurrentData>()

    init {
        launch {
            strings.value = lexisUseCase.execute(R.integer.choose_quality)
        }
        currentPlayListEpisodes.value = initBottomBarData.value?.playlist
        sourceLiveData.value = setup.playlist
        matchInfoLiveData.value = if (setup.startTitle.first().isEmpty()) setup.startTitle.last()
            else setup.startTitle.first()
        setup.playListType?.let {
            if (it == WatchType.FULL_GAME) {
                launch {
                    val mediaSource = getHLSVideoUseCase.execute(matchId, sportId)
                    mediaSourceLiveData.postValue(mediaSource)
                }
            } else {
                initBottomBarData.value = setup.toInitBottomData()
                videoLiveData.value = setup.video?.filter { it.abc == "0" }
                    ?.sortedByDescending { it.quality }
                    ?.map { it.url to it.duration }
                    ?.let { Event(it) }
                fullVideoDuration.value = setup.videoDurations[indexOfValue(it)]
            }
        }
    }

    override fun setFullVideoDuration(duration: Long) {
        fullVideoDuration.value = duration
    }

    override fun updatePlayList(list: List<Episode>, videoType: WatchType) {
        currentPlayListEpisodes.value = initBottomBarData.value?.playlist
        sourceLiveData.value = setup.playlist
        matchInfoLiveData.value = if (videoType == WatchType.PLAYER)
            setup.startTitle.first() else setup.startTitle.last()
        if (videoType == WatchType.FULL_GAME) {
            launch {
                val mediaSource = getHLSVideoUseCase.execute(matchId, sportId)
                mediaSourceLiveData.postValue(mediaSource)
            }
        } else {
            initBottomBarData.value = PlayerBottomBarSetup(
                playlist = list
                    .sortedWith(compareBy({ it.half }, { it.startMs }))
                    .map {
                        it.copy(
                            endMs = it.endMs * 1000,
                            startMs = it.startMs * 1000,
                            half = it.half - 1
                        )
                    }
            )
            videoLiveData.value = setup.video?.filter { it.abc == "0" }
                ?.sortedByDescending { it.quality }
                ?.map { it.url to it.duration }
                ?.let { Event(it) }
            fullVideoDuration.value = setup.videoDurations[indexOfValue(videoType)]
        }
    }

    override fun play(it: Episode, playlist: List<Episode>?) {
        currentEpisode.value = it
        playlist?.let { currentPlayListEpisodes.value = it }
        updatePlayerCurrentData(
            currentEpisodeId = playlist?.indexOf(it) ?: 0,
            positionPlayer = it.startMs,
            progressSeekBar = 0,
            half = it.half,
            isUpdatePlayer = true,
            playWhenReady = true
        )
    }

    override fun setVideoQualityList(qualityParams: List<String>) {
        videoQualityListLiveData.postValue(qualityParams)
    }

    override fun openVideoQualityMenu() {
        setup.playListType?.let {
            videoQualityParams.value = if (it == WatchType.FULL_GAME) {
                videoQualityListLiveData.value?.let { list ->
                    VideoQualityParams(listOf(VIDEO_AUTO).plus(list))
                }
            } else {
                VideoQualityParams(
                    listOf(VIDEO_AUTO).plus(setup
                        .video
                        ?.sortedByDescending { it.quality.toInt() }
                        ?.map { it.quality }
                        ?.distinct() ?: listOf(
                        VIDEO_AUTO,
                        VIDEO_1080,
                        VIDEO_720,
                        VIDEO_480
                    ))
                )
            }
        }
    }

    override fun changeVideoQuality(videoQuality: String, currentPosition: Long, half: Int) {
        val episode = currentEpisode.value!!
        if (videoQuality == VIDEO_AUTO) {
            videoLiveData.value = setup
                .video
                ?.filter { it.abc == "0" }
                ?.sortedByDescending { it.quality }
                ?.map { it.url to it.duration }?.let { Event(it) }
        } else {
            videoLiveData.value = setup
                .video
                ?.filter { it.abc == "0" }
                ?.groupBy { it.quality }!![videoQuality]
                ?.map { it.url to it.duration }?.let { Event(it) }
        }
        val currentEpisodeId = currentPlayListEpisodes.value!!.indexOf(currentEpisode.value)
        currentEpisode.value = episode
        updatePlayerCurrentData(
            currentEpisodeId = currentEpisodeId,
            positionPlayer = currentPosition,
            progressSeekBar = (currentPosition - currentEpisode.value!!.startMs).toInt(),
            half = half,
            isUpdatePlayer = true,
            playWhenReady = true
        )
        updateEpisodeDuration(currentEpisodeId, currentPosition)
    }

    private fun setEpisode(currentEpisodeId: Int, startPosition: Long) {
        currentEpisode.value = currentPlayListEpisodes.value!![currentEpisodeId]
        updatePlayerCurrentData(
            currentEpisodeId = currentEpisodeId,
            positionPlayer = currentEpisode.value!!.startMs + startPosition,
            progressSeekBar = startPosition.toInt(),
            half = currentEpisode.value!!.half,
            isUpdatePlayer = true,
            playWhenReady = true
        )
        updateEpisodeDuration(currentEpisodeId, currentEpisode.value!!.startMs + startPosition)
    }

    override fun nextEpisode(startPosition: Long) {
        val currentEpisodeId = currentPlayListEpisodes.value!!.indexOf(currentEpisode.value)
        if (isHaveNextEpisode()) {
            currentEpisode.value = currentPlayListEpisodes.value!![currentEpisodeId + 1]
            updatePlayerCurrentData(
                currentEpisodeId = currentEpisodeId + 1,
                positionPlayer = currentEpisode.value!!.startMs + startPosition,
                progressSeekBar = 0,
                half = currentEpisode.value!!.half,
                isUpdatePlayer = true,
                playWhenReady = true
            )
            updateEpisodeDuration(currentEpisodeId + 1, currentEpisode.value!!.startMs)
        }
    }

    override fun previewEpisode(isStart: Boolean) {
        val currentEpisodeId = currentPlayListEpisodes.value!!.indexOf(currentEpisode.value)
        if (isHavePreviewEpisode()) {
            currentEpisode.value = currentPlayListEpisodes.value!![currentEpisodeId - 1]
            updatePlayerCurrentData(
                currentEpisodeId = currentEpisodeId - 1,
                positionPlayer = if (isStart) {
                    currentEpisode.value!!.startMs
                } else {
                    currentEpisode.value!!.endMs
                },
                progressSeekBar = if (isStart) 0 else {
                    (currentEpisode.value!!.endMs - currentEpisode.value!!.startMs).toInt()
                },
                half = currentEpisode.value!!.half,
                isUpdatePlayer = true,
                playWhenReady = true
            )
            updateEpisodeDuration(currentEpisodeId - 1, currentEpisode.value!!.endMs)
        }
    }

    override fun updatePosition(currentPosition: Long, half: Int, isUpdatePlayer: Boolean) {
        val currentEpisodeId = currentPlayListEpisodes.value?.indexOf(currentEpisode.value) ?: 0
        when {
            // плеер не вышел за пределы текущего эпизода
            isPositionInCurrentEpisode(currentPosition, half) -> {
                updatePlayerCurrentData(
                    currentEpisodeId = currentEpisodeId,
                    positionPlayer = currentPosition,
                    progressSeekBar = (currentPosition - currentEpisode.value!!.startMs).toInt(),
                    half = half,
                    isUpdatePlayer = isUpdatePlayer,
                    playWhenReady = true
                )
                updateEpisodeDuration(currentEpisodeId, currentPosition)
            }
            // плеер перешел на следующий эпизод
            isHaveNextEpisode() && isPositionInNextEpisode(currentPosition, half) -> nextEpisode(0)
            // плеер перешел на предыдущий эпизод
            isHavePreviewEpisode() && isPositionInPreviewEpisode(
                currentPosition,
                half
            ) -> previewEpisode(isStart = false)
            // плеер закончил проигрывать последний эпизод
            !isHaveNextEpisode() -> {
                updatePlayerCurrentData(
                    currentEpisodeId = currentEpisodeId,
                    positionPlayer = currentEpisode.value!!.endMs,
                    progressSeekBar = (currentEpisode.value!!.endMs - currentEpisode.value!!.startMs).toInt(),
                    half = currentEpisode.value!!.half,
                    isUpdatePlayer = true,
                    playWhenReady = false
                )
                updateEpisodeDuration(currentEpisodeId, currentEpisode.value!!.endMs)
            }
        }
        isEnablePreviewEpisode.postValue(isHavePreviewEpisode())
        isEnableNextEpisode.postValue(isHaveNextEpisode())
    }

    override fun intervalRewind(sec: Int, currentPosition: Long, half: Int) {
        if (isPositionInCurrentEpisode(currentPosition.plus(sec), half)) {
            updatePosition(currentPosition.plus(sec), half, true)
        } else {
            if (sec > 0) {
                // интервальная перемотка вперед
                var startPosition = currentPosition.plus(sec).minus(currentEpisode.value!!.endMs)
                if (isHaveNextEpisode()) {
                    val nextEpisodeId = currentPlayListEpisodes.value!!.indexOf(currentEpisode.value).plus(1)
                    for (id in nextEpisodeId until currentPlayListEpisodes.value!!.size) {
                        val episode = currentPlayListEpisodes.value!![id]
                        when {
                            startPosition < episode.endMs.minus(episode.startMs) -> {
                                setEpisode(id, startPosition)
                                return
                            }
                            id == currentPlayListEpisodes.value!!.size - 1 -> {
                                setEpisode(id, episode.endMs)
                                return
                            }
                            else -> startPosition -= episode.endMs.minus(episode.startMs)
                        }
                    }
                } else {
                    updatePosition(currentEpisode.value!!.endMs, half, true)
                }
            } else {
                // интервальная перемотка назад
                var startPosition = currentEpisode.value!!.startMs.minus(currentPosition.plus(sec))
                if (isHavePreviewEpisode()) {
                    val previewEpisodeId = currentPlayListEpisodes.value!!.indexOf(currentEpisode.value).minus(1)
                    for (id in previewEpisodeId downTo 0) {
                        val episode = currentPlayListEpisodes.value!![id]
                        val episodeDuration = episode.endMs.minus(episode.startMs)
                        when {
                            startPosition < episodeDuration -> {
                                setEpisode(id, episodeDuration - startPosition)
                                return
                            }
                            id == 0 -> {
                                setEpisode(id, episode.startMs)
                                return
                            }
                            else -> startPosition -= episodeDuration
                        }
                    }
                } else {
                    updatePosition(currentEpisode.value!!.startMs, half, true)
                }
            }
        }
    }

    private fun updateEpisodeDuration(episodeId: Int, position: Long) {
        currentPlayListEpisodes.value?.let { list ->
            var duration = 0L
            // calculate watch time for previous episodes
            for (id in 0..episodeId) {
                if (id > 0) {
                    val previewEpisode = list[id - 1]
                    duration += (previewEpisode.endMs - previewEpisode.startMs).div(1000)
                }
            }
            // add current progress
            duration += position.minus(list[episodeId].startMs).div(1000)
            currentVideoDuration.postValue(duration)
        }
    }

    private fun updatePlayerCurrentData(
        currentEpisodeId: Int,
        positionPlayer: Long,
        progressSeekBar: Int,
        half: Int,
        isUpdatePlayer: Boolean,
        playWhenReady: Boolean
    ) {
        updatePosition.postValue(
            PlayerCurrentData(
                currentEpisodeId = currentEpisodeId,
                positionPlayer = positionPlayer,
                progressSeekBar = progressSeekBar,
                currentHalf = half,
                isUpdatePlayer = isUpdatePlayer,
                playWhenReady = playWhenReady
            )
        )
    }

    private fun isHavePreviewEpisode() =
        currentEpisode.value!! != currentPlayListEpisodes.value?.first()

    private fun isHaveNextEpisode() =
        currentEpisode.value!! != currentPlayListEpisodes.value?.last()

    private fun isPositionInCurrentEpisode(
        currentPosition: Long,
        half: Int
    ): Boolean {
        val e = currentEpisode.value!!
        return (half == e.half
                && (e.startMs <= currentPosition && currentPosition <= e.endMs))
    }

    private fun isPositionInNextEpisode(
        currentPosition: Long,
        half: Int
    ): Boolean {
        val e = currentEpisode.value!!
        return (half > e.half) || (half == e.half && currentPosition > e.endMs)
    }

    private fun isPositionInPreviewEpisode(
        currentPosition: Long,
        half: Int
    ): Boolean {
        val e = currentEpisode.value!!
        return (half < e.half) || (half == e.half && currentPosition < e.endMs)
    }

    override fun onBackClicked(message: String) {
        if (message.isNotEmpty()) {
            router.toast(message)
            router.navigateUp()
        } else {
            router.navigateUp()
        }
    }
}

