package com.natife.streaming.data.dto.matchprofile

data class GoalsDTO(
    val data: List<MatchInfoEpisodeDTO>?,
    val dur: Long = 0
)