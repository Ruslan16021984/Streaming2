package com.natife.streaming.usecase

import com.natife.streaming.API_GET_FAVORITES
import com.natife.streaming.api.MainApi
import com.natife.streaming.data.request.BaseRequest
import com.natife.streaming.data.request.EmptyRequest
import com.natife.streaming.data.search.SearchResult
import com.natife.streaming.preferenses.SettingsPrefs
import com.natife.streaming.utils.ImageUrlBuilder

interface FavoritesUseCase {
    suspend fun execute(): List<SearchResult>
}

class FavoritesUseCaseImpl(
    private val api: MainApi,
    private val settingsPrefs: SettingsPrefs
) : FavoritesUseCase {

    override suspend fun execute(): List<SearchResult> {
        val lang = settingsPrefs.getLanguage().toUpperCase()
        val response = api.getFavorite(
            BaseRequest(
                procedure = API_GET_FAVORITES,
                params = EmptyRequest()
            )
        )
        return response.map {
            SearchResult(
                id = it.id,
                name = when (lang) {
                    "ru", "RU" -> {
                        it.info.nameRus ?: "${it.info.firstnameRus} ${it.info.lastnameRus}"
                    }
                    "en", "EN" -> {
                        it.info.nameEng ?: "${it.info.firstnameEng} ${it.info.lastnameEng}"
                    }
                    else -> {
                        it.info.nameEng ?: "${it.info.firstnameEng} ${it.info.lastnameEng}"
                    }
                },
                type = when (it.type) {
                    1 -> SearchResult.Type.TOURNAMENT
                    2 -> SearchResult.Type.TEAM
                    else -> SearchResult.Type.PLAYER
                },
                image = ImageUrlBuilder.getUrl(
                    it.sport,
                    when (it.type) {
                        1 -> ImageUrlBuilder.Companion.Type.TOURNAMENT
                        2 -> ImageUrlBuilder.Companion.Type.TEAM
                        else->ImageUrlBuilder.Companion.Type.PLAYER
                    },
                    it.id
                ),
                placeholder = ImageUrlBuilder.getPlaceholder(
                    it.sport,
                    when(it.type){
                        1 ->ImageUrlBuilder.Companion.Type.TOURNAMENT
                        2 -> ImageUrlBuilder.Companion.Type.TEAM
                        else->ImageUrlBuilder.Companion.Type.PLAYER
                    }
                ),
                gender = 1,
                sport = it.sport,
                isFavorite = true
            )
        }
    }
}