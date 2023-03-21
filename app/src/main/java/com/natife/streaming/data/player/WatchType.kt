package com.natife.streaming.data.player

enum class WatchType {
    PLAYER, FULL_GAME, BALL_IN_PLAY, HIGHLIGHTS, GOALS
}

fun indexOfValue(type: WatchType) = when (type) {
    WatchType.PLAYER -> 0
    WatchType.FULL_GAME -> 1
    WatchType.BALL_IN_PLAY -> 2
    WatchType.HIGHLIGHTS -> 3
    WatchType.GOALS -> 4
}
fun valueFromIndex(index: Int) = WatchType.values()[index]