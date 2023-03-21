package com.natife.streaming.data.dto

import com.google.gson.annotations.SerializedName


data class VideoDTOItem(
    val abc: String,
    @SerializedName("abc_type")
    val abcType: String?,
    val checksum: Long,
    val duration: Long,
    val folder: String,
    val fps: Int,
    @SerializedName("match_id")
    val matchId: Int,
    val name: String,//+
    val period: Int,//+
    val quality: String,
    @SerializedName("server_id")
    val serverId: Int,//+
    val size: Long,
    @SerializedName("start_ms")
    val startMs: Long,
    val url: String,
    @SerializedName("url_root")
    val urlRoot: String,
    @SerializedName("video_type")
    val videoType: String
)

//
data class VideoContent(
    @SerializedName("name")
    val name: String? = null,

    @SerializedName("period")
    val period: Int = 1,

    @SerializedName("server_id")
    val serverId: Int? = null,

    @SerializedName("quality")
    val quality: String = "Original",

    @SerializedName("folder")
    val folder: String? = null,

    @SerializedName("video_type")
    val videoType: String = "live",

    @SerializedName("abc")
    val abc: Int = 0,

    @SerializedName("start_ms")
    val startMs: Int = 0,

    @SerializedName("checksum")
    val checksum: Long? = null,

    @SerializedName("size")
    val size: Long? = null,

    @SerializedName("abc_type")
    val abcType: String? = null,

    @SerializedName("duration")
    val duration: Long = -1L,

    @SerializedName("fps")
    val fps: Float? = null,

    @SerializedName("url_root")
    val urlRoot: String? = null
)

