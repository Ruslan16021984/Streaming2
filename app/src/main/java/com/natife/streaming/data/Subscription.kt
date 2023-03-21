package com.natife.streaming.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Subscription(
    val id:Int,
    val name: String,
    val price: Double,
    val isBayed: Boolean
): Parcelable
