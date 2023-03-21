package com.natife.streaming.data.player

data class PlayerCurrentData (
    val progressSeekBar: Int,
    val positionPlayer: Long,
    val currentEpisodeId: Int,
    val currentHalf: Int,
    val isUpdatePlayer: Boolean,
    val playWhenReady: Boolean
)