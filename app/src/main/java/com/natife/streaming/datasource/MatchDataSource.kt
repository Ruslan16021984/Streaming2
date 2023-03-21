package com.natife.streaming.datasource

import androidx.annotation.VisibleForTesting
import androidx.paging.InvalidatingPagingSourceFactory
import androidx.paging.PagingSource
import com.natife.streaming.API_MATCHES
import com.natife.streaming.API_MATCH_PROFILE
import com.natife.streaming.API_SPORTS
import com.natife.streaming.api.MainApi
import com.natife.streaming.data.match.Match
import com.natife.streaming.data.match.Team
import com.natife.streaming.data.match.Tournament
import com.natife.streaming.data.request.BaseRequest
import com.natife.streaming.data.request.EmptyRequest
import com.natife.streaming.data.request.MatchProfileRequest
import com.natife.streaming.data.request.MatchesRequestSimpleTournament
import com.natife.streaming.db.LocalSqlDataSourse
import com.natife.streaming.ext.toRequest
import com.natife.streaming.utils.ImageUrlBuilder
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import org.koin.core.KoinComponent
import org.koin.core.get
import timber.log.Timber
import java.util.*

class MatchDataSourceFactory(private val api: MainApi, @Volatile private var requestParams: MatchParams? = null,) :
     () -> PagingSource<Int, Match> {

    fun refresh(requestParams: MatchParams) {
        this.requestParams = requestParams
        Timber.e("kkdjfdffd refresh ${this.requestParams }")
        invalidate()
    }

    @VisibleForTesting
    internal val pagingSources = mutableListOf<PagingSource<Int, Match>>()

    /**
     * @return [PagingSource] which will be invalidated when this factory's [invalidate] method
     * is called
     */
    final override fun invoke(): PagingSource<Int, Match> {
        return pagingSourceFactory().also { pagingSources.add(it) }
    }

    /**
     * Calls [PagingSource.invalidate] on each [PagingSource] that was produced by this
     * [InvalidatingPagingSourceFactory]
     */
    fun invalidate() {
        while (pagingSources.isNotEmpty()) {
            pagingSources.removeFirst().also {
                if (!it.invalid) {
                    it.invalidate()
                }
            }
        }
    }
    val pagingSourceFactory: (()->PagingSource<Int,Match >)=  { Timber.e("new match source $requestParams")

        MatchDataSource(api, requestParams) }
}


class MatchDataSource(
    private val api: MainApi,
    private val requestParams: MatchParams?
) : PagingSource<Int, Match>(), KoinComponent {
    private val localSqlDataSourse: LocalSqlDataSourse = get()
    override val keyReuseSupported: Boolean = true
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Match> {

        Timber.e("load${params.key} ${requestParams}")
        val nextPage = params.key ?: 1

        val offset = nextPage - 1
        Timber.e("load offset ${offset}")
        try {
            val params1 = MatchesRequestSimpleTournament(
                date = requestParams?.date?: Date().toRequest(),
                limit = requestParams?.pageSize ?: 60,
                offset = (requestParams?.pageSize ?: 60) * offset,
                sport = requestParams?.sportId,
                subOnly = requestParams?.subOnly ?: false,
                tournamentId = requestParams?.additionalId
            )
            Timber.e("load ${params1}")
            val matches = api.getMatches(
                BaseRequest(
                    procedure = API_MATCHES,
                    params = params1
                )


            )
            val sports = api.getSports(
                BaseRequest(
                    procedure = API_SPORTS,
                    params = EmptyRequest()
                )
            )

            Timber.e("load success")
            return LoadResult.Page(
                data = matches.videoContent.broadcast!!.map { match ->
                    coroutineScope {
                        val profile = async {
                            api.getMatchProfile(
                                BaseRequest(
                                    procedure = API_MATCH_PROFILE, params = MatchProfileRequest(
                                        sportId = match.sport?: requestParams?.sportId?:0,
                                        tournament = match.tournament?.id ?: requestParams?.additionalId ?:0
                                    )
                                )
                            )
                        }
                        Match(
                            id = match.id,
                            sportId = match.sport ?: requestParams?.sportId ?: 0,
                            countryId = match.countryId,
                            sportName = sports.find { it.id == match.sport }?.name ?: "",
                            date = match.date,
                            tournament = Tournament(
                                match.tournament?.id ?: requestParams?.additionalId ?: 0,
                                match.tournament?.nameRus ?: ""
                            ),// todo multilang
                            team1 = Team(
                                match.team1.id,
                                match.team1.nameRus,
                                score = match.team1.score
                            ),
                            team2 = Team(
                                match.team2.id,
                                match.team2.nameRus,
                                score = match.team2.score
                            ),
                            info = "${profile.await().country} ${profile.await().nameRus}",
                            access = match.access,
                            hasVideo = match.hasVideo,
                            image = ImageUrlBuilder.getUrl(
                                match.sport ?: requestParams?.sportId ?: 0,
                                ImageUrlBuilder.Companion.Type.TOURNAMENT,
                                match.tournament?.id ?: requestParams?.additionalId ?: 0
                            ),//todo image module
                            placeholder = ImageUrlBuilder.getPlaceholder(
                                match.sport ?: requestParams?.sportId ?: 0,
                                ImageUrlBuilder.Companion.Type.TOURNAMENT
                            ),
                            live = match.live,
                            storage = match.storage,
                            subscribed = match.sub,
                            isShowScore = localSqlDataSourse.getGlobalSettings()?.showScore
                                ?: false
                        )
                    }

                },
                prevKey = if (nextPage == 1) null else nextPage - 1,
                nextKey = nextPage + 1
            )

        } catch (e: Exception) {
            Timber.e("load error")
            e.printStackTrace()
            return LoadResult.Error(Throwable("Something went wrong"))


        }


    }

}

data class MatchParams(
    val date: String?,
    val sportId: Int?,
    val subOnly: Boolean,
    val additionalId: Int?,
    val pageSize: Int
)