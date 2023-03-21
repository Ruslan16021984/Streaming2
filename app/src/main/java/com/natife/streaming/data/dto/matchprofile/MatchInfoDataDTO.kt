package com.natife.streaming.data.dto.matchprofile

import com.google.gson.annotations.SerializedName

data class MatchInfoDataDTO(
    @SerializedName("ball_in_play")
    val ballInPlay: BallInPlayDTO?,
    val goals: GoalsDTO,
    val highlights: HighlightsDTO,
    val lexics: MatchInfoLexicsDTO,
    val players1: List<PlayersDTO>,
    val players2: List<PlayersDTO>
)