package com.natife.streaming.ui.billing

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.natife.streaming.R
import com.natife.streaming.base.BaseViewModel
import com.natife.streaming.data.Lexic
import com.natife.streaming.data.dto.subscription.SubscribePackage
import com.natife.streaming.data.dto.subscription.SubscribeResponse
import com.natife.streaming.router.Router
import com.natife.streaming.usecase.GetMatchSubscriptionsUseCase
import com.natife.streaming.usecase.LexisUseCase
import com.natife.streaming.usecase.LexisUseCaseNew
import okhttp3.internal.toImmutableList

abstract class BillingViewModel : BaseViewModel() {
    abstract val application: Application
    abstract val matchId: Int
    abstract val sportId: Int
    abstract val tournamentId: Int
    abstract val tournamentTitle: String
    abstract val live: Boolean
    abstract val team1: String
    abstract val team2: String
    abstract val monthList: LiveData<List<OfferData>>
    abstract val billingTypes: LiveData<List<BillingType>>
    abstract val yearList: LiveData<List<OfferData>>
    abstract val payPerViewList: LiveData<List<OfferData>>
    abstract val title: LiveData<String>
    abstract val strings: LiveData<List<Lexic>>
    abstract fun onFinishClicked()
    abstract fun select(result: OfferData)
    abstract fun selectSubscriptionType(type: BillingType)
}

class BillingViewModelImpl(
    override val application: Application,
    override val matchId: Int,
    override val sportId: Int,
    override val tournamentId: Int,
    override val tournamentTitle: String,
    override val live: Boolean,
    override val team1: String,
    override val team2: String,
    private val router: Router,
    private val getMatchSubscriptionsUseCase: GetMatchSubscriptionsUseCase,
    private val lexisUseCase: LexisUseCase,
    private val lexisUseCaseNew: LexisUseCaseNew
) : BillingViewModel() {
    override val monthList = MutableLiveData<List<OfferData>>()
    override val title = MutableLiveData<String>()
    override val yearList = MutableLiveData<List<OfferData>>()
    override val billingTypes = MutableLiveData<List<BillingType>>()
    override val payPerViewList = MutableLiveData<List<OfferData>>()
    override val strings = MutableLiveData<List<Lexic>>()
    private lateinit var subscribtionData: SubscribeResponse

    init {
        launch {
            val str = lexisUseCase.execute(
                R.integer.month,
                R.integer.year,
                R.integer.pay_per_view
            )
            strings.value = str

            subscribtionData =
                getMatchSubscriptionsUseCase.execute(sportId, matchId)

            title.value = "$team1 - $team2"

            val billingTypeList = subscribtionData.data.map {
                val result = lexisUseCaseNew.execute(
                    it.lexic,
                    it.lexi2,
                    it.lexic3
                )
                val lexics = when (result.size) {
                    0 -> listOf(Lexic.emptyLexic, Lexic.emptyLexic, Lexic.emptyLexic)
                    1 -> result.plus(listOf(Lexic.emptyLexic, Lexic.emptyLexic))
                    2 -> result.plus(Lexic.emptyLexic)
                    else -> result
                }

                if (lexics.size == 3) {
                    BillingType(
                        id = it.id,
                        lexic = lexics[0].text,
                        lexic2 = lexics[1].text,
                        lexic3 = lexics[2].text,
                        packages = it.packages,
                        seasonName = subscribtionData.season?.name ?: ""
                    )
                } else null
            }.filterIsInstance<BillingType>()
            billingTypes.postValue(billingTypeList)
        }
    }

    override fun selectSubscriptionType(type: BillingType) {
        monthList.value = monthFromOfferData(type.packages, type.seasonName)
        yearList.value = yearFromOfferData(type.packages, type.seasonName)
        payPerViewList.value = payPerViewFromOfferData(type.packages)
    }

    private fun payPerViewFromOfferData(subscriptions: SubscribePackage): List<OfferData> {
        val list = mutableListOf<OfferData>()
        subscriptions.pay_per_view?.let {
            list.add(
                OfferData(
                    matchId = matchId,
                    sportId = sportId,
                    tournamentId = tournamentId,
                    offerTitle = application.getString(R.string.billing_match_access),
                    teamLeagueName = tournamentTitle,
                    description = application.getString(R.string.billing_match_living_on_demand),
                    billingOfferCurrency = "${it.currency_iso}",
                    billingOfferPrice = it.price ?: 0,
                    isLive = live,
                    titleForLivePlayer = "$team1 - $team2"
                )
            )
        }
        return list.toImmutableList()
    }

    private fun yearFromOfferData(
        subscriptions: SubscribePackage,
        seasonName: String
    ): List<OfferData> {
        val list = mutableListOf<OfferData>()
        subscriptions.year?.all?.let {
            val add = list.add(
                OfferData(
                    matchId = matchId,
                    sportId = sportId,
                    tournamentId = tournamentId,
                    offerTitle = application.getString(R.string.billing_league_pass),
                    teamLeagueName = tournamentTitle,
                    description = application.getString(
                        R.string.billing_all_matches_in_season,
                        seasonName
                    ),
                    billingOfferCurrency = application.getString(
                        R.string.billing_slash_year,
                        it.currency_iso
                    ),
                    billingOfferPrice = it.price ?: 0,
                    isLive = live,
                    titleForLivePlayer = "$team1 - $team2"
                )
            )
            add
        }
        subscriptions.year?.team1?.let {
            list.add(
                OfferData(
                    matchId = matchId,
                    sportId = sportId,
                    tournamentId = tournamentId,
                    offerTitle = application.getString(R.string.billing_team_pass),
                    teamLeagueName = tournamentTitle,
                    description = application.getString(
                        R.string.billing_all_matches_team_in_season,
                        team1,
                        seasonName
                    ),
                    billingOfferCurrency = application.getString(
                        R.string.billing_slash_year,
                        it.currency_iso
                    ),
                    billingOfferPrice = it.price ?: 0,
                    isLive = live,
                    titleForLivePlayer = "$team1 - $team2"
                )
            )
        }

        subscriptions.year?.team1?.let {
            list.add(
                OfferData(
                    matchId = matchId,
                    sportId = sportId,
                    tournamentId = tournamentId,
                    offerTitle = application.getString(R.string.billing_team_pass),
                    teamLeagueName = tournamentTitle,
                    description = application.getString(
                        R.string.billing_all_matches_team_in_season,
                        team2,
                        seasonName
                    ),
                    billingOfferCurrency = application.getString(
                        R.string.billing_slash_year,
                        it.currency_iso
                    ),
                    billingOfferPrice = it.price ?: 0,
                    isLive = live,
                    titleForLivePlayer = "$team1 - $team2"
                )
            )
        }
        return list.toImmutableList()
    }

    private fun monthFromOfferData(
        subscriptions: SubscribePackage,
        seasonName: String
    ): List<OfferData> {
        val list = mutableListOf<OfferData>()
        subscriptions.month?.all?.let {
            list.add(
                OfferData(
                    matchId = matchId,
                    sportId = sportId,
                    tournamentId = tournamentId,
                    offerTitle = application.getString(R.string.billing_league_pass),
                    teamLeagueName = tournamentTitle,
                    description = application.getString(
                        R.string.billing_all_matches_in_season,
                        seasonName
                    ),
                    billingOfferCurrency = application.getString(
                        R.string.billing_slash_month,
                        it.currency_iso
                    ),
                    billingOfferPrice = it.price ?: 0,
                    isLive = live,
                    titleForLivePlayer = "$team1 - $team2"
                )
            )
        }
        subscriptions.month?.team1?.let {
            list.add(
                OfferData(
                    matchId = matchId,
                    sportId = sportId,
                    tournamentId = tournamentId,
                    offerTitle = application.getString(R.string.billing_team_pass),
                    teamLeagueName = tournamentTitle,
                    description = application.getString(
                        R.string.billing_all_matches_team_in_season,
                        team1,
                        seasonName
                    ),
                    billingOfferCurrency = application.getString(
                        R.string.billing_slash_month,
                        it.currency_iso
                    ),
                    billingOfferPrice = it.price ?: 0,
                    isLive = live,
                    titleForLivePlayer = "$team1 - $team2"
                )
            )
        }

        subscriptions.month?.team1?.let {
            list.add(
                OfferData(
                    matchId = matchId,
                    sportId = sportId,
                    tournamentId = tournamentId,
                    offerTitle = application.getString(R.string.billing_team_pass),
                    teamLeagueName = tournamentTitle,
                    description = application.getString(
                        R.string.billing_all_matches_team_in_season,
                        team2,
                        seasonName
                    ),
                    billingOfferCurrency = application.getString(
                        R.string.billing_slash_month,
                        it.currency_iso
                    ),
                    billingOfferPrice = it.price ?: 0,
                    isLive = live,
                    titleForLivePlayer = "$team1 - $team2"
                )
            )
        }
        return list.toImmutableList()
    }

    override fun onFinishClicked() {
        router.navigateUp()
    }

    override fun select(result: OfferData) {
        when (result.isLive) {
            true -> {
                router.navigate(
                    BillingFragmentDirections.actionBillingFragmentToLiveDialog(
                        matchId,
                        sportId,
                        result.titleForLivePlayer
                    )
                )
            }
            false -> {
                router.navigate(
                    BillingFragmentDirections.actionBillingFragmentToPopupVideo(matchId, sportId)
                )
            }

        }
    }
}


data class OfferData(
    val matchId: Int = 0,
    val sportId: Int = 0,
    val tournamentId: Int = 0,
    val offerTitle: String = "",
    val teamLeagueName: String = "",
    val description: String = "",
    val billingOfferCurrency: String = "",
    val billingOfferPrice: Int = 0,
    val isLive: Boolean = false,
    val titleForLivePlayer: String = ""
)