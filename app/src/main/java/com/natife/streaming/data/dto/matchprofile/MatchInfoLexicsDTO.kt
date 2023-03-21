package com.natife.streaming.data.dto.matchprofile

import com.google.gson.annotations.SerializedName

data class MatchInfoLexicsDTO(
    @SerializedName("ball_in_play")
    val ballInPlay: Int,
    @SerializedName("full_game")
    val fullGame: Int,
    val goals: Int,
    val highlights: Int,
    val interview: Int,
    val players: Int
)