package com.natife.streaming.usecase

import com.natife.streaming.VIDEO_1080
import com.natife.streaming.VIDEO_480
import com.natife.streaming.VIDEO_720

interface GetVideoQualityUseCase {
    fun getVideoQuality(): List<String>
}

class GetVideoQualityImpl : GetVideoQualityUseCase {
    override fun getVideoQuality(): List<String> = listOf(VIDEO_1080, VIDEO_720, VIDEO_480)
}
