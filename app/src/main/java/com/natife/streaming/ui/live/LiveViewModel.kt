package com.natife.streaming.ui.live

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.android.exoplayer2.source.MediaSource
import com.natife.streaming.*
import com.natife.streaming.base.LexisViewModel
import com.natife.streaming.data.Lexic
import com.natife.streaming.router.Router
import com.natife.streaming.ui.player.menu.quality.VideoQualityParams
import com.natife.streaming.usecase.GetLiveVideoUseCase
import com.natife.streaming.usecase.GetUserLiveMatchSecond
import com.natife.streaming.usecase.LexisUseCase
import com.natife.streaming.usecase.SaveUserLiveMatchSecond
import com.natife.streaming.utils.VideoHeaderUpdater
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

abstract class LiveViewModel : LexisViewModel() {
    abstract val mediaSourceLiveData: LiveData<MediaSource>
    abstract val currentPositionLiveData: LiveData<Int>
    abstract val returnToLiveEvent: LiveData<Boolean>
    abstract val videoQualityParams: LiveData<VideoQualityParams>
    abstract val videoQualityListLiveData: LiveData<List<String>>
    abstract fun onFinishClicked()
    abstract fun onBackClicked(message: String = "")
    abstract fun saveCurrentPosition(position: Long)
    abstract fun returnToLive()
    abstract fun openVideoQualityMenu()
    abstract fun setVideoQualityList(qualityParams: List<String>)
}

class LiveViewModelImpl(
    private val matchId: Int,
    private val sportId: Int,
    private val router: Router,
    private val getLiveVideoUseCase: GetLiveVideoUseCase,
    private val videoHeaderUpdater: VideoHeaderUpdater,
    private val saveUserLiveMatchSecond: SaveUserLiveMatchSecond,
    private val getUserLiveMatchSecond: GetUserLiveMatchSecond,
    private val lexisUseCase: LexisUseCase
) : LiveViewModel() {
    override val strings = MutableLiveData<List<Lexic>>()
    override val mediaSourceLiveData = MutableLiveData<MediaSource>()
    override val currentPositionLiveData = MutableLiveData<Int>()
    override val returnToLiveEvent = MutableLiveData<Boolean>(false)
    override val videoQualityParams = MutableLiveData<VideoQualityParams>()
    override val videoQualityListLiveData = MutableLiveData<List<String>>()

    override fun onFinishClicked() {
        router.navigate(R.id.action_global_nav_main)
    }

    override fun onBackClicked(message: String) {
        if (message.isNotEmpty()) {
            router.toast(message)
            router.popBackStack()
        } else {
            router.popBackStack()
        }
    }

    init {
        launch {
            val str = lexisUseCase.execute(
                R.integer.watch_live_stream,
                R.integer.watch_from_the_start,
                R.integer.continue_with,
                R.integer.choose_quality
            )
            strings.value = str

            val mediaSource = getLiveVideoUseCase.execute(matchId, sportId)
            mediaSourceLiveData.postValue(mediaSource)

            val currentPosition = getUserLiveMatchSecond.execute(sportId, matchId).second
            currentPositionLiveData.postValue(currentPosition)
        }
    }

    override fun setVideoQualityList(qualityParams: List<String>) {
        videoQualityListLiveData.postValue(qualityParams)
    }

    override fun openVideoQualityMenu() {
        videoQualityParams.value = videoQualityListLiveData.value?.let { list ->
            VideoQualityParams(listOf(VIDEO_AUTO).plus(list))
        }
    }

    override fun saveCurrentPosition(position: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            saveUserLiveMatchSecond.execute(
                _sportId = sportId,
                _matchId = matchId,
                _half = 1,
                _second = position.toInt()
            )
        }
    }

    override fun returnToLive() {
        returnToLiveEvent.postValue(true)
    }

    override fun onCleared() {
        videoHeaderUpdater.stop()
        super.onCleared()
    }
}