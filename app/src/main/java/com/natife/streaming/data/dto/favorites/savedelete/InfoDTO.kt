package com.natife.streaming.data.dto.favorites.savedelete

import com.google.gson.annotations.SerializedName
import com.natife.streaming.data.dto.country.CountryDTO

data class InfoDTO(
    val country: CountryDTO,
    @SerializedName("name_eng")
    val nameEng: String?,
    @SerializedName("name_rus")
    val nameRus: String?,
    @SerializedName("lastname_eng")
    val lastnameEng: String?,
    @SerializedName("lastname_rus")
    val lastnameRus: String?,
    @SerializedName("firstname_eng")
    val firstnameEng: String?,
    @SerializedName("firstname_rus")
    val firstnameRus: String?
)