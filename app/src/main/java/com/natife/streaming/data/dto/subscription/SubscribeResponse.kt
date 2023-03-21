package com.natife.streaming.data.dto.subscription

data class SubscribeResponse(
    val data: List<SubscribeVersion>,
    val season: Season?
)

data class SubscribeVersion(
    val id: Int,
    val lexic: Int,
    val lexi2: Int,
    val lexic3: Int,
    val packages: SubscribePackage
)

data class SubscribePackage(
    val month: Month?,
    val pay_per_view: PayPerView?,
    val year: Year?
)

data class Month(
    val all: All?,
    val team1: All?,
    val team2: All?,
    val team_away: All?,
    val team_home: All?
)

data class PayPerView(
    val currency_id: Int?,
    val currency_iso: String?,
    val price: Int?,
    val sub: Sub?
)

data class Season(
    val id: Int?,
    val name: String?
)

data class Year(
    val all: All?,
    val team1: All?,
    val team2: All?,
    val team_away: All?,
    val team_home: All?
)

data class All(
    val currency_id: Int?,
    val currency_iso: String?,
    val id: Int?,
    val lexic: Int?,
    val price: Int?,
    val sub: Sub?
)

data class Sub(
    val active_from: String?,
    val active_to: String?,
    val currency_id: Int?,
    val id: Int?,
    val option: Int?,
    val price: Int?,
    val sport: Int?,
    val team_id: Int?
)

