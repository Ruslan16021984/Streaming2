package com.natife.streaming.data.dto.player

import com.google.gson.annotations.SerializedName
import com.natife.streaming.data.dto.country.CountryDTO

data class PlayerDTO(
    @SerializedName("id")
    val id: Int,
    @SerializedName("firstname_eng")
    val firstNameEng: String,
    @SerializedName("lastname_eng")
    val lastNameEng: String,
    @SerializedName("nickname_eng")
    val nicknameEng: String,
    @SerializedName("firstname_rus")
    val firstNameRus: String,
    @SerializedName("lastname_rus")
    val lastNameRus: String,
    @SerializedName("nickname_rus")
    val nicknameRus: String,
    @SerializedName("club_team")
    val clubTeam: ClubTeamDTO,
    @SerializedName("c_gender")
    val gender: Int,
    @SerializedName("height")
    val height: Int,
    @SerializedName("weight")
    val weight: Int,
    @SerializedName("is_gk")
    val isGk: Boolean,
    @SerializedName("is_favorite")
    val isFavorite: Boolean,
    @SerializedName("country")
    val country: CountryDTO,
    val sport: Int,
    @SerializedName("dur")
    val duration: Long,
)