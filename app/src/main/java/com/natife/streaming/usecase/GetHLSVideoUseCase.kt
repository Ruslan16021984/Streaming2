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
import com.natife.streaming.preferenses.AuthPrefs
import java.lang.StringBuilder

interface GetHLSVideoUseCase {
    suspend fun execute(matchId: Int, sportId: Int): MediaSource
}

class GetHLSVideoUseCaseImpl(
    private val context: Context,
    private val authPrefs: AuthPrefs
) : GetHLSVideoUseCase {
    override suspend fun execute(matchId: Int, sportId: Int): MediaSource {

        val accessToken = "access_token=" + authPrefs.getAuthToken()
        val urlVideo = StringBuilder()
        urlVideo.append(BuildConfig.BASE_URL)
        urlVideo.append("video/stream/")
        urlVideo.append(sportId)
        urlVideo.append("/")
        urlVideo.append(matchId)
        urlVideo.append(".m3u8")
        urlVideo.append("?")
        urlVideo.append("access_token")
        urlVideo.append("=")
        urlVideo.append(authPrefs.getAuthToken())

        val authHeaders: HashMap<String, String> = HashMap()
        authHeaders["Cookie"] = accessToken
        authHeaders["Origin"] = "https://instat.tv"

        val uri: Uri = Uri.parse(urlVideo.toString())
        val mediaDataSourceFactory = DefaultHttpDataSource.Factory()
            .setUserAgent(Util.getUserAgent(context, "InstatTV"))
            .setConnectTimeoutMs(1000)
            .setReadTimeoutMs(1000)
            .setAllowCrossProtocolRedirects(true)
            .setDefaultRequestProperties(authHeaders)

        return HlsMediaSource.Factory(mediaDataSourceFactory)
            .setLoadErrorHandlingPolicy(DefaultLoadErrorHandlingPolicy(10))
            .setAllowChunklessPreparation(true)
            .createMediaSource(MediaItem.fromUri(uri))
    }

}