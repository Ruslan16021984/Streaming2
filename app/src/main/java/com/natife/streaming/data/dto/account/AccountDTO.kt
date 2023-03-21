package com.natife.streaming.data.dto.account

import com.google.gson.annotations.SerializedName

data class AccountDTO(
    val firstname: String?,
    val lastname: String?,
    val phone: String?,
    val email: String?,
    val password: String?,
    val country: CountryDTO?,
    @SerializedName("postal_code")
    val postalCode: Int?,
    val region: String?,
    val city: String?,
    @SerializedName("city_id")
    val cityId: Int?,
    @SerializedName("address_line1")
    val addressLine1: String?,
    @SerializedName("address_line2")
    val addressLine2: String?,
){
    data class CountryDTO(
        val id: Int,
        @SerializedName("name_eng")
        val nameEng: String,
        @SerializedName("name_rus")
        val name_rus: String,
    )
}