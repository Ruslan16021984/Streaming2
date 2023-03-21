package com.natife.streaming.data.dto.match

import com.google.gson.annotations.SerializedName

data class MatchesDTO(
    @SerializedName("is_video_sections")
    val isVideoSections: Boolean?,
    val show: Boolean?,
    @SerializedName("video_content")
    val videoContent: VideoContentDTO
)