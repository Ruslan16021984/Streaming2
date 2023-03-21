package com.natife.streaming.usecase

import com.natife.streaming.API_SPORTS
import com.natife.streaming.API_TRANSLATE
import com.natife.streaming.api.MainApi
import com.natife.streaming.data.Sport
import com.natife.streaming.data.request.BaseRequest
import com.natife.streaming.data.request.EmptyRequest
import com.natife.streaming.data.request.TranslateRequest
import com.natife.streaming.db.LocalSqlDataSourse
import com.natife.streaming.db.entity.PreferencesSport
import kotlinx.coroutines.flow.Flow

//new
interface GetSportUseCase {
    suspend fun execute(reload: Boolean = false): List<Sport>
    fun getAllUserPreferencesInSportFlow(): Flow<List<PreferencesSport>>
    suspend fun getAllUserPreferencesInSport(): List<PreferencesSport>
}

class GetSportUseCaseImpl(
    private val api: MainApi,
    private val localSqlDataSourse: LocalSqlDataSourse
) : GetSportUseCase {

    private var catche: List<Sport>? = null

    override suspend fun execute(reload: Boolean): List<Sport> {
        if (catche == null || reload) {
            val sports = api.getSports(BaseRequest(procedure = API_SPORTS, params = EmptyRequest()))
            val translates =
                api.getTranslate(BaseRequest(procedure = API_TRANSLATE, params = TranslateRequest(
                    language = "ru",
                    params = sports.map { it.lexic }
                )))
            catche = sports.map {
                Sport(id = it.id, translates[it.lexic.toString()]?.text ?: "", lexic = it.lexic)
            }
        }
        return catche!!
    }

    override fun getAllUserPreferencesInSportFlow(): Flow<List<PreferencesSport>> =
        localSqlDataSourse.getPreferencesSportFlow()

    override suspend fun getAllUserPreferencesInSport(): List<PreferencesSport> =
        localSqlDataSourse.getPreferencesSport()
}