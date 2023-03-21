package com.natife.streaming.data.match

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Match(
    val id: Int,
    val sportId: Int,
    val countryId: Int,
    val sportName: String,
    val date: String,
    val tournament: Tournament,
    val isFavoriteTournament: Boolean = false,
    val team1: Team,
    val isFavoriteTeam1: Boolean = false,
    val team2: Team,
    val isFavoriteTeam2: Boolean = false,
    val info: String,
    val hasVideo: Boolean,
    val live: Boolean,
    val storage: Boolean,
    val subscribed: Boolean,
    val access: Boolean,
    val image: String,
    val placeholder: String,
    val isShowScore: Boolean
): Parcelable

