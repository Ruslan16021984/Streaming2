package com.natife.streaming.data.dto.matchprofile

data class HighlightsDTO(
    val data: List<MatchInfoEpisodeDTO>?,
    val dur: Long = 0
)