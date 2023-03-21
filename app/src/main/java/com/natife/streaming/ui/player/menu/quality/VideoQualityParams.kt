package com.natife.streaming.ui.player.menu.quality

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class VideoQualityParams(
    val videoQualityList: List<String>
) : Parcelable
