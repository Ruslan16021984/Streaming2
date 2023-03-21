package com.natife.streaming.usecase

import com.natife.streaming.API_SAVE_USER_PREFERENCES
import com.natife.streaming.api.MainApi
import com.natife.streaming.data.dto.preferences.SaveUserPreferencesRequest2
import com.natife.streaming.data.dto.preferences.UserPreferencesDTO
import com.natife.streaming.data.request.BaseRequest
import com.natife.streaming.db.LocalSqlDataSourse


interface SaveUserPreferencesUseCase {
    suspend fun execute(_userId: Int, a: List<UserPreferencesDTO>?): Boolean
}

class SaveUserPreferencesUseCaseImpl(
    private val api: MainApi,
    private val localSqlDataSourse: LocalSqlDataSourse
) : SaveUserPreferencesUseCase {

    override suspend fun execute(_userId: Int, list: List<UserPreferencesDTO>?): Boolean {
        val response = api.saveUserPreferences2(
            BaseRequest(
                procedure = API_SAVE_USER_PREFERENCES,
                params = SaveUserPreferencesRequest2(
                    userId = _userId,
                    listOfPreferences = list
                )
            )
        )
        return response.status == 1
    }
}