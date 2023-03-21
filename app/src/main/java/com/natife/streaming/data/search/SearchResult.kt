package com.natife.streaming.data.search

import com.natife.streaming.ui.favorites.FavoritesAdapter

data class SearchResult (
    val id: Int,
    val name: String,
    val type: Type,
    val image: String,
    val placeholder: String,
    val sport: Int,
    val gender: Int,
    val countryId: Int = -1,
    val countryName: String = "",
    val isFavorite: Boolean = false
): FavoritesAdapter.Favorite {
    enum class Type{
        PLAYER,
        TEAM,
        TOURNAMENT,
        NON
    }
}