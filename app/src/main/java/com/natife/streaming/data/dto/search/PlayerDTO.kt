package com.natife.streaming.data.dto.search

import com.google.gson.annotations.SerializedName
import com.natife.streaming.data.dto.matchprofile.Country

data class PlayerDTO(
    @SerializedName("firstname_eng")
    val firstnameEng: String,
    @SerializedName("firstname_rus")
    val firstnameRus: String,
    val gender: Int,
    val id: Int,
    @SerializedName("lastname_eng")
    val lastnameEng: String,
    @SerializedName("lastname_rus")
    val lastnameRus: String,
    @SerializedName("nickname_eng")
    val nicknameEng: String,
    @SerializedName("nickname_rus")
    val nicknameRus: String,
    val sport: Int,
    val team: PlayersTeamDTO,
    @SerializedName("country")
    val country: Country? = Country(),
)