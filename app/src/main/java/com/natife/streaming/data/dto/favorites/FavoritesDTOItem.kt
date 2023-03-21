package com.natife.streaming.data.dto.favorites

import com.natife.streaming.data.dto.favorites.savedelete.InfoDTO

data class FavoritesDTOItem(
    val id: Int,
    val info: InfoDTO,
    val sport: Int,
    val type: Int
)