package com.natife.streaming.data.request

import com.google.gson.annotations.SerializedName

data class SaveDeleteFavoriteRequest(
    @SerializedName("_p_action")
    val action: Int,
    @SerializedName("_p_sport")
    val sportId: Int,
    @SerializedName("_p_type")
    val type: Int,
    @SerializedName("_p_id")
    val id: Int

)
