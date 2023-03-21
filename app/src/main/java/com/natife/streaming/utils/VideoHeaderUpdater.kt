package com.natife.streaming.utils

import com.google.android.exoplayer2.upstream.DefaultHttpDataSource
import com.natife.streaming.preferenses.AuthPrefs
import kotlinx.coroutines.*

private const val TIME_UPDATE_MS = 10000L

interface VideoHeaderUpdater {
    fun start(mediaDataSourceFactory: DefaultHttpDataSource.Factory)
    fun stop()
}

class VideoHeaderUpdaterImpl(
    private val authPrefs: AuthPrefs
) : VideoHeaderUpdater {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private var videoHeaderJob: Job? = null

    override fun start(mediaDataSourceFactory: DefaultHttpDataSource.Factory) {
        if (videoHeaderJob == null) {
            videoHeaderJob = scope.launch {
                while (true) {
                    val authHeaders: HashMap<String, String> = HashMap()
                    authHeaders["Cookie"] = "access_token=" + authPrefs.getAuthToken()
                    authHeaders["Origin"] = "https://instat.tv"
                    mediaDataSourceFactory.setDefaultRequestProperties(authHeaders)
                    delay(TIME_UPDATE_MS)
                }
            }
        }
    }

    override fun stop() {
        videoHeaderJob?.cancel()
        videoHeaderJob = null
    }
}