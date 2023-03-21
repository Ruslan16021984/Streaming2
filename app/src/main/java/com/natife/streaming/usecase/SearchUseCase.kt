package com.natife.streaming.usecase

import com.natife.streaming.API_SEARCH
import com.natife.streaming.api.MainApi
import com.natife.streaming.data.request.BaseRequest
import com.natife.streaming.data.request.SearchRequest
import com.natife.streaming.data.search.SearchResult
import com.natife.streaming.db.dao.SearchDao
import com.natife.streaming.preferenses.SettingsPrefs
import com.natife.streaming.utils.ImageUrlBuilder

interface SearchUseCase {
    suspend fun execute(name: String, local: Boolean = false): List<SearchResult>
//    suspend fun execute(result: SearchResult)
}

class SearchUseCaseImpl(
    private val api: MainApi,
    private val dao: SearchDao,
    private val settingsPrefs: SettingsPrefs
) : SearchUseCase {
    override suspend fun execute(name: String, local: Boolean): List<SearchResult> {
        val results = mutableListOf<SearchResult>()
        val lang = settingsPrefs.getLanguage().toUpperCase()

//        if (local){
//            val fromDb = dao.getResults("%$name%")
//            results.addAll(fromDb.map {
//                SearchResult(
//                id=it.id,
//                gender = it.gender,
//                image = it.image,
//                placeholder = it.placeholder,
//                type = SearchResult.Type.valueOf(it.type),
//                sport = it.sport,
//                name =it.name
//            ) })
////        Timber.e("JNOXNONS $fromDb")
//        }else{
        val response = api.search(
            BaseRequest(
                procedure = API_SEARCH,
                params = SearchRequest(name)
            )
        )
        response?.players?.let { list ->
            results.addAll(list.map {
                SearchResult(
                    id = it.id,
                    name = when (lang) {
                        "en", "EN" -> "${it.firstnameEng} ${it.lastnameEng}" ?: ""
                        "ru", "RU" -> "${it.firstnameRus} ${it.lastnameRus}" ?: ""
                        else -> "${it.firstnameEng} ${it.lastnameEng}" ?: ""
                    },
                    type = SearchResult.Type.PLAYER,
                    image = ImageUrlBuilder.getUrl(
                        it.sport,
                        ImageUrlBuilder.Companion.Type.PLAYER,
                        it.id
                    ),
                    placeholder = ImageUrlBuilder.getPlaceholder(
                        it.sport,
                        ImageUrlBuilder.Companion.Type.PLAYER
                    ),
                    gender = it.gender,
                    sport = it.sport,
                    countryId = it.country?.id ?: -1,
                    countryName = when (lang) {
                        "en", "EN" -> it.country?.name_eng ?: ""
                        "ru", "RU" -> it.country?.name_rus ?: ""
                        else -> it.country?.name_eng ?: ""
                    }
                )
            })
        }
        response?.teams?.let { list ->
            results.addAll(list.map {
                SearchResult(
                    id = it.id,
                    name = when (lang) {
                        "en", "EN" -> it.nameEng ?: ""
                        "ru", "RU" -> it.nameRus ?: ""
                        else -> it.nameEng ?: ""
                    },
                    type = SearchResult.Type.TEAM,
                    image = ImageUrlBuilder.getUrl(
                        it.sport,
                        ImageUrlBuilder.Companion.Type.TEAM,
                        it.id
                    ),
                    placeholder = ImageUrlBuilder.getPlaceholder(
                        it.sport,
                        ImageUrlBuilder.Companion.Type.TEAM
                    ),
                    gender = it.gender,
                    sport = it.sport,
                    countryId = it.country?.id ?: -1,
                    countryName = when (lang) {
                        "en", "EN" -> it.country?.nameEng ?: ""
                        "ru", "RU" -> it.country?.nameRus ?: ""
                        else -> it.country?.nameEng ?: ""
                    }
                )
            })
        }
        response?.tournaments?.let { list ->
            results.addAll(list.map {
                SearchResult(
                    id = it.id,
                    name = when (lang) {
                        "en", "EN" -> it.nameEng ?: ""
                        "ru", "RU" -> it.nameRus ?: ""
                        else -> it.nameEng ?: ""
                    },
                    type = SearchResult.Type.TOURNAMENT,
                    image = ImageUrlBuilder.getUrl(
                        it.sport,
                        ImageUrlBuilder.Companion.Type.TOURNAMENT,
                        it.id
                    ),
                    placeholder = ImageUrlBuilder.getPlaceholder(
                        it.sport,
                        ImageUrlBuilder.Companion.Type.TOURNAMENT
                    ),
                    gender = it.gender,
                    sport = it.sport,
                    countryId = it.country?.id ?: -1,
                    countryName = when (lang) {
                        "en", "EN" -> it.country?.nameEng ?: ""
                        "ru", "RU" -> it.country?.nameRus ?: ""
                        else -> it.country?.nameEng ?: ""
                    }
                )
            })
        }
//        }


        return results
    }

//    override suspend fun execute(result: SearchResult) {
//        dao.insert(SearchEntity(
//            id=result.id,
//            gender = result.gender,
//            image = result.image,
//            placeholder = result.placeholder,
//            type = result.type.name,
//            sport = result.sport,
//            name = result.name
//        ))
//
//    }
}