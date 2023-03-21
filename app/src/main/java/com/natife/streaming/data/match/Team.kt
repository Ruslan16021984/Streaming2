package com.natife.streaming.data.match

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Team(
    val id: Int,
    val name: String,
    val score: Int
): Parcelable