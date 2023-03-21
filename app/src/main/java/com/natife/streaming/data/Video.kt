package com.natife.streaming.data

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Video(
    val abc: String,
    val abcType: String,
    val duration: Long,
    val folder: String,
    val matchId: Int,
    val name: String,
    val period: Int,
    val quality: String,
    val size: Long,
    var startMs: Long,
    val url: String,
    val videoType: String
): Parcelable