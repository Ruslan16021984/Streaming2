package com.natife.streaming.mock

import com.natife.streaming.data.match.Match
import com.natife.streaming.data.match.Team
import com.natife.streaming.data.match.Tournament
import com.natife.streaming.datasource.MatchParams
import kotlinx.coroutines.delay
import java.util.*
import kotlin.random.Random

class MockMatchRepository {
    var requestParams: MatchParams? = null

    fun prepare(requestParams: MatchParams) {
        this.requestParams = requestParams
    }
//    suspend fun getMatches(
//        date: String,
//        limit: Int,
//        offset: Int,
//        sportId: Int?,
//        subOnly: Boolean,
//        tournamentId: Int?
//    ): List<Match> {
//        delay(500)
//        val resp = mutableListOf<Match>()
//        for (i in 0..60) {
//            resp.add(
//                Match(
//                    Random(5).nextInt(),
//                    Random(50).nextInt(),
//                    "basketball",
//                    "25.01.2020 25:47",
//                    Tournament(
//                        Random(100).nextInt(),
//                        "Молодежная хоккейная лига"
//                    ),
//                    Team(
//                        Random(5).nextInt(),
//                        "МХК Динамо Мск",
//                        0
//                    ),
//                    Team(
//                        Random(5).nextInt(),
//                        "МХК Динамо Мск",
//                        0
//                    ),
//                    "Россия Суперлига 34 тур",
//                    true,
//                    true,
//                    true,
//                    true,
//                    Random(Date().time +40).nextBoolean(),
//                    "https://instatscout.com/images/tournaments/180/39.png",
//                    ImageUrlBuilder.getPlaceholder(match.sport,ImageUrlBuilder.Companion.Type.TOURNAMENT)
//                )
//            )
//        }
//        return resp
//    }

}