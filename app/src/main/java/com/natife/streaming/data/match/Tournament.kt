package com.natife.streaming.data.match

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Tournament(
    val id: Int,
    val name: String
) : Parcelable


