package com.natife.streaming.usecase

import android.content.Context
import android.net.Uri
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.hls.HlsMediaSource
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource
import com.google.android.exoplayer2.upstream.DefaultLoadErrorHandlingPolicy
import com.google.android.exoplayer2.util.Util
import com.natife.streaming.BuildConfig
import com.natife.streaming.api.MainApi
import com.natife.streaming.api.exception.ApiException
import com.natife.streaming.api.exception.ErrorHandlingPolicy
import com.natife.streaming.preferenses.AuthPrefs
import com.natife.streaming.utils.VideoHeaderUpdater
import java.lang.StringBuilder

interface GetLiveVideoUseCase {
    suspend fun execute(matchId: Int, sportId: Int): MediaSource
}

class GetLiveVideoUseCaseImpl(
    private val context: Context,
    private val api: MainApi,
    private val authPrefs: AuthPrefs,
    private val videoHeaderUpdater: VideoHeaderUpdater
) : GetLiveVideoUseCase {
    override suspend fun execute(matchId: Int, sportId: Int): MediaSource {

        val accessToken = "access_token=" + authPrefs.getAuthToken()
        val urlLive = StringBuilder()
        urlLive.append(BuildConfig.BASE_URL)
        urlLive.append("video/stream/")
        urlLive.append(sportId)
        urlLive.append("/")
        urlLive.append(matchId)
        urlLive.append(".m3u8")
        urlLive.append("?")
        urlLive.append("access_token")
        urlLive.append("=")
        urlLive.append(authPrefs.getAuthToken())

        val authHeaders: HashMap<String, String> = HashMap()
        authHeaders["Cookie"] = accessToken
        authHeaders["Origin"] = "https://instat.tv"

        val uri: Uri = Uri.parse(urlLive.toString())
        val mediaDataSourceFactory = DefaultHttpDataSource.Factory()
            .setUserAgent(Util.getUserAgent(context, "InstatTV"))
            .setConnectTimeoutMs(1000)
            .setReadTimeoutMs(1000)
            .setAllowCrossProtocolRedirects(true)
            .setDefaultRequestProperties(authHeaders)

        return try {
            videoHeaderUpdater.start(mediaDataSourceFactory)
            HlsMediaSource.Factory(mediaDataSourceFactory)
            .setLoadErrorHandlingPolicy(ErrorHandlingPolicy(10))
                .setAllowChunklessPreparation(true)
                .createMediaSource(MediaItem.fromUri(uri))
        } catch (e: ApiException) {
            videoHeaderUpdater.stop()
            throw Exception(e)
        }
    }

}