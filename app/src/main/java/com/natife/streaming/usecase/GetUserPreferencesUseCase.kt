package com.natife.streaming.usecase

import com.natife.streaming.API_GET_USER_PREFERENCES
import com.natife.streaming.api.MainApi
import com.natife.streaming.data.dto.preferences.GetUserPreferencesRequest
import com.natife.streaming.data.dto.preferences.UserPreferencesDTO
import com.natife.streaming.data.request.BaseRequest
import com.natife.streaming.db.LocalSqlDataSourse

interface GetUserPreferencesUseCase {
    suspend fun execute(_userId: Int): List<UserPreferencesDTO>?
}

class GetUserPreferencesUseCaseImpl(
    private val api: MainApi,
    private val localSqlDataSourse: LocalSqlDataSourse
) : GetUserPreferencesUseCase {
    override suspend fun execute(_userId: Int): List<UserPreferencesDTO>? {
        val list = api.getUserPreferences(
            BaseRequest(
                procedure = API_GET_USER_PREFERENCES,
                params = GetUserPreferencesRequest(userId = _userId)
            )
        )
        return list
    }
}