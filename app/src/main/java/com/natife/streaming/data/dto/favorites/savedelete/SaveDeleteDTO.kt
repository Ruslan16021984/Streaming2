package com.natife.streaming.data.dto.favorites.savedelete

import com.google.gson.annotations.SerializedName

data class SaveDeleteDTO(
    @SerializedName("_p_data")
    val data: List<DataItemDTO>,
    @SerializedName("_p_status")
    val status: Int
)