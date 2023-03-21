package com.natife.streaming.data

data class Tournament(
    val isFavorite: Boolean,
    val icon: String,
    val placeholder: String,
    val title: String,
    val countryId: Int,
    val countryName: String,
)