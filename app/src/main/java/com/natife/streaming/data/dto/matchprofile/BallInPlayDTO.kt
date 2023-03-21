package com.natife.streaming.data.dto.matchprofile

data class BallInPlayDTO(
    val data: List<MatchInfoEpisodeDTO?>?,
    val dur: Long = 0
)