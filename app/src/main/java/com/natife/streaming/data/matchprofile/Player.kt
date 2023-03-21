package com.natife.streaming.data.matchprofile

data class Player(
    val id: Int,
    val team: Int,
    val name: String,
    val nickname: String = "",
    val image: String,
    val imagePlaceholder: String,
    val number: String,
    val countryId: Int = -1,
    val countryName: String = "",
    val isFavorite: Boolean = false,
    val duration: Long
)

