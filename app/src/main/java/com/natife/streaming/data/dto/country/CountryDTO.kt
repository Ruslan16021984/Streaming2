package com.natife.streaming.data.dto.country

import com.google.gson.annotations.SerializedName

data class CountryDTO(
    val id: Int? = -1,
    @SerializedName("name_eng")
    val nameEng: String? = "",
    @SerializedName("name_rus")
    val nameRus: String? = ""
)