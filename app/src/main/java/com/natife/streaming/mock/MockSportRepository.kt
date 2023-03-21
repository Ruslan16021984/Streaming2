package com.natife.streaming.mock

import com.natife.streaming.data.Sport
import kotlinx.coroutines.delay

class MockSportRepository {
    suspend fun getSportList(): List<Sport> {
        delay(500)
        return listOf(
            Sport(
                id = 1,
                name = "football",
                lexic = 12980
            ),
            Sport(
                id = 2,
                name = "hockey",
                lexic = 69593
            ),
            Sport(
                id = 3,
                name = "basketball",
                lexic = 3556
            )
        )
    }
}