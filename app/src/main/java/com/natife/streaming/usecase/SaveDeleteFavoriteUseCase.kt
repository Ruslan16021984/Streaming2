package com.natife.streaming.usecase

import com.natife.streaming.API_SAVE_DELETE_FAVORITE
import com.natife.streaming.api.MainApi
import com.natife.streaming.data.request.BaseRequest
import com.natife.streaming.data.request.SaveDeleteFavoriteRequest
import com.natife.streaming.data.search.SearchResult

interface SaveDeleteFavoriteUseCase {
    suspend fun executeSave(id: Int, sportId: Int, type: SearchResult.Type)
    suspend fun executeDelete(id: Int, sportId: Int, type: SearchResult.Type)
}

class SaveDeleteFavoriteUseCaseImpl(private val api: MainApi): SaveDeleteFavoriteUseCase  {
    override suspend fun executeSave(id: Int, sportId: Int, type: SearchResult.Type) {
        api.getSaveDeleteFavorite(BaseRequest(procedure = API_SAVE_DELETE_FAVORITE,
        params = SaveDeleteFavoriteRequest(
            action = 1,//add
            sportId = sportId,
            type = when(type){
                SearchResult.Type.TOURNAMENT->1
                SearchResult.Type.PLAYER -> 3
                SearchResult.Type.TEAM -> 2
                else -> -1
            },
            id = id
        )
        ))
    }

    override suspend fun executeDelete(id: Int, sportId: Int, type: SearchResult.Type) {
        api.getSaveDeleteFavorite(BaseRequest(procedure = API_SAVE_DELETE_FAVORITE,
            params = SaveDeleteFavoriteRequest(
                action = 2,//remove
                sportId = sportId,
                type = when(type){
                    SearchResult.Type.TOURNAMENT -> 1
                    SearchResult.Type.PLAYER -> 3
                    SearchResult.Type.TEAM -> 2
                    else -> -1
                },
                id = id
            )
        ))
    }

}