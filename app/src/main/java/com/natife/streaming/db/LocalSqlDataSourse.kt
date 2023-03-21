package com.natife.streaming.db

import com.natife.streaming.data.dto.preferences.UserPreferencesDTO
import com.natife.streaming.data.dto.sports.SportTranslateDTO
import com.natife.streaming.data.dto.tournament.TournamentTranslateDTO
import com.natife.streaming.db.dao.LocalSqlTasksDao
import com.natife.streaming.db.entity.GlobalSettings
import com.natife.streaming.db.entity.Lang
import com.natife.streaming.db.entity.PreferencesSport
import com.natife.streaming.db.entity.PreferencesTournament
import com.natife.streaming.preferenses.AuthPrefs
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class LocalSqlDataSourse internal constructor(
    private val localSqlTasksDao: LocalSqlTasksDao,
    private val authPrefs: AuthPrefs,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) {
    private suspend fun getUserAuthEmail(): String =
        withContext(ioDispatcher) {
            authPrefs.getProfile()?.email ?: ""
        }

    //Global Settings
    suspend fun setGlobalSettingsCurrentUser(showScore: Boolean, lang: Lang) {
        withContext(ioDispatcher) {
            getUserAuthEmail().let { userAuthEmail ->
                val currentUser = getGlobalSettings()
                GlobalSettings(
                    authEmail = userAuthEmail,
                    showScore = showScore,
                    lang = lang,
                    userId = currentUser?.userId
                ).let {
                    localSqlTasksDao.setGlobalSettings(it)
                }
            }
        }
    }

    suspend fun setGlobalSettings(showScore: Boolean, lang: Lang, id: Int?) {
        withContext(ioDispatcher) {
            getUserAuthEmail().let { userAuthEmail ->
                GlobalSettings(
                    authEmail = userAuthEmail,
                    showScore = showScore,
                    lang = lang,
                    userId = id
                ).let {
                    localSqlTasksDao.setGlobalSettings(it)
                }
            }
        }
    }

    suspend fun updateGlobalSettingsCurrentUser(showScore: Boolean, lang: Lang) {
        withContext(ioDispatcher) {
            val currentUser = getGlobalSettings()
            if (currentUser != null) {
                getUserAuthEmail().let { userAuthEmail ->
                    GlobalSettings(
                        authEmail = userAuthEmail,
                        showScore = showScore,
                        lang = lang,
                        userId = currentUser.userId
                    ).let {
                        localSqlTasksDao.updateGlobalSettings(it)
                    }
                }
            }
        }
    }

    suspend fun getGlobalSettings(): GlobalSettings? =
        withContext(ioDispatcher) {
            getUserAuthEmail().let { userAuthEmail ->
                localSqlTasksDao.getGlobalSettings(userAuthEmail)
            }
        }

    suspend fun getGlobalSettingsFlow(): Flow<GlobalSettings?> =
        withContext(ioDispatcher) {
            getUserAuthEmail().let { userAuthEmail ->
                localSqlTasksDao.getGlobalSettingsFlow(userAuthEmail)
            }
        }
//
//    fun getGlobalSettingsFlow(): Flow<GlobalSettings> {
//        var userAuthEmail: String =""
//        GlobalScope.launch(ioDispatcher) {
//            userAuthEmail = getUserAuthEmail()
//        }
//        return userAuthEmail.let { userAuthEmail ->
//            localSqlTasksDao.getGlobalSettingsFlow(userAuthEmail!!)
//        }
//    }


    suspend fun updateShowScore(showScore: Boolean) {
        withContext(ioDispatcher) {
            getUserAuthEmail().let { userAuthEmail ->
                getGlobalSettings()?.let { globalSettings ->
                    GlobalSettings(
                        authEmail = userAuthEmail,
                        showScore = showScore,
                        lang = globalSettings.lang,
                        userId = globalSettings.userId
                    ).let {
                        localSqlTasksDao.updateGlobalSettings(it)
                    }
                }
            }
        }
    }

    suspend fun updateLang(lang: Lang) {
        withContext(ioDispatcher) {
            getUserAuthEmail().let { userAuthEmail ->
                getGlobalSettings()?.let { globalSettings ->
                    GlobalSettings(
                        authEmail = userAuthEmail,
                        showScore = globalSettings.showScore,
                        lang = lang,
                        userId = globalSettings.userId
                    ).let {
                        localSqlTasksDao.updateGlobalSettings(it)
                    }
                }
            }
        }
    }


    // PreferencesSports
    suspend fun setPreferencesSportList(sport: List<PreferencesSport>) {
        withContext(ioDispatcher) {
            localSqlTasksDao.setPreferencesSportList(sport)
        }
    }

    suspend fun setCheckedSport(sport: SportTranslateDTO) {
        withContext(ioDispatcher) {
            localSqlTasksDao.getPreferencesSportByID(sport.id)?.let { preferencesSport ->
                updatePreferencesSport(preferencesSport.copy(isChack = !preferencesSport.isChack))
                if (!preferencesSport.isChack) {
                    getPreferencesTournamentBySport(preferencesSport.id).map {
                        it.copy(isPreferred = true)
                    }.let {
                        updatePreferencesTournamentList(it)
                    }
                } else {
                    getPreferencesTournamentBySport(preferencesSport.id).map {
                        it.copy(isPreferred = false)
                    }.let {
                        updatePreferencesTournamentList(it)
                    }
                }
            }
        }
    }

    suspend fun getPreferencesSportByID(sport: PreferencesSport) =
        withContext(ioDispatcher) {
            localSqlTasksDao.getPreferencesSportByID(sport.id)
        }

    fun getPreferencesSportFlow(): Flow<List<PreferencesSport>> =
        localSqlTasksDao.getPreferencesSportFlow()

    suspend fun getPreferencesSport(): List<PreferencesSport> =
        withContext(ioDispatcher) {
            localSqlTasksDao.getPreferencesSport()
        }

    suspend fun updatePreferencesSport(sport: PreferencesSport) {
        withContext(ioDispatcher) {
            localSqlTasksDao.updatePreferencesSport(sport)
        }
    }

    suspend fun deletePreferencesSport(sport: PreferencesSport) {
        withContext(ioDispatcher) {
            localSqlTasksDao.deletePreferencesSport(sport)
        }
    }

    suspend fun deleteAllPreferencesSport() {
        withContext(ioDispatcher) {
            localSqlTasksDao.deleteAllPreferencesSport()
        }
    }

    suspend fun deleteAllPreferencesSportAndCreateAndSynchronize(
        newList: List<PreferencesSport>,
        oldList: List<PreferencesSport>
    ) {
        withContext(ioDispatcher) {
            localSqlTasksDao.deleteAllPreferencesSportAndCreateAndSynchronize(newList, oldList)
        }
    }


    //PreferencesTournament
    suspend fun setlistPreferencesTournament(tournamentList: List<PreferencesTournament>) {
        withContext(ioDispatcher) {
            localSqlTasksDao.setListPreferencesTournament(tournamentList)
        }
    }

    suspend fun updateTournamentList(tournamentList: List<PreferencesTournament>) {
        withContext(ioDispatcher) {
            localSqlTasksDao.updateTournamentList(tournamentList)
        }
    }

    suspend fun deleteAllPreferencesTournamentAndCreate(list: List<PreferencesTournament>) {
        withContext(ioDispatcher) {
            localSqlTasksDao.deleteAllPreferencesTournamentAndCreate(list)
        }
    }

    suspend fun deleteAllPreferencesTournamentAndCreateAndSetAllTournamentOff(list: List<PreferencesTournament>) {
        withContext(ioDispatcher) {
            localSqlTasksDao.deleteAllPreferencesTournamentAndCreateAndSetAllTournamentOff(list)
        }
    }

    suspend fun deleteAllPreferencesTournamentAndCreateAndSynchronize(
        newList: List<PreferencesTournament>,
        listForSynchronize: List<UserPreferencesDTO>
    ) {
        withContext(ioDispatcher) {
            localSqlTasksDao.deleteAllPreferencesTournamentAndCreateAndSynchronize(
                newList,
                listForSynchronize
            )
        }
    }

    suspend fun setCheckedTournament(tournamentId: TournamentTranslateDTO) {
        withContext(ioDispatcher) {
            val a = localSqlTasksDao.getPreferencesTournamentByID(
                tournamentId.id,
                tournamentId.sport,
                tournamentId.tournamentType
            ).let {
                updatePreferencesTournament(it.copy(isPreferred = !it.isPreferred))
            }
        }
    }

    suspend fun getPreferencesTournament(): List<PreferencesTournament> =
        withContext(ioDispatcher) {
            localSqlTasksDao.getPreferencesTournament()
        }

    suspend fun getOnlyIsCheckedPreferencesTournament(): List<PreferencesTournament> =
        withContext(ioDispatcher) {
            localSqlTasksDao.getOnlyIsCheckedPreferencesTournament()
        }

    suspend fun searchPreferencesTournament(
        queryString: String,
        id: Int?,
        lang: String
    ): List<PreferencesTournament> =
        withContext(ioDispatcher) {
            val dbQuery = "%${queryString.replace(' ', '%')}%"
            when (lang) {
                "en", "EN" -> if (id == null) localSqlTasksDao.searchENPreferencesTournament(dbQuery) else localSqlTasksDao.searchENPreferencesTournamentSportID(
                    dbQuery,
                    id
                )
                "ru", "RU" -> if (id == null) localSqlTasksDao.searchRUPreferencesTournament(dbQuery) else localSqlTasksDao.searchRUPreferencesTournamentSportID(
                    dbQuery,
                    id
                )
                else -> if (id == null) localSqlTasksDao.searchENPreferencesTournament(dbQuery) else localSqlTasksDao.searchENPreferencesTournamentSportID(
                    dbQuery,
                    id
                )
            }
        }

    fun searchPreferencesTournamentFlow(
        queryString: String,
        id: Int?,
        lang: String
    ): Flow<List<PreferencesTournament>> {
        val dbQuery = "%${queryString.replace(' ', '%')}%"
        return when (lang) {
            "en", "EN" -> if (id == null) localSqlTasksDao.searchENPreferencesTournamentFlow(
                dbQuery
            ) else localSqlTasksDao.searchENPreferencesTournamentSportIDFlow(dbQuery, id)
            "ru", "RU" -> if (id == null) localSqlTasksDao.searchRUPreferencesTournamentFlow(
                dbQuery
            ) else localSqlTasksDao.searchRUPreferencesTournamentSportIDFlow(dbQuery, id)
            else -> if (id == null) localSqlTasksDao.searchENPreferencesTournamentFlow(
                dbQuery
            ) else localSqlTasksDao.searchENPreferencesTournamentSportIDFlow(
                dbQuery,
                id
            )
        }
    }

    suspend fun getPreferencesTournamentBySport(sportId: Int): List<PreferencesTournament> =
        withContext(ioDispatcher) {
            localSqlTasksDao.getPreferencesTournamentBySport(sportId)
        }

    suspend fun getPreferencesTournamentBySportIDPrefID(
        sportId: Int,
        prefID: Int
    ): PreferencesTournament? =
        withContext(ioDispatcher) {
            localSqlTasksDao.getPreferencesTournamentBySportIDPrefID(sportId, prefID)
        }

    fun getPreferencesTournamentFlow(): Flow<List<PreferencesTournament>> =
        localSqlTasksDao.getPreferencesTournamentFlow()

    suspend fun updatePreferencesTournament(tournament: PreferencesTournament) {
        withContext(ioDispatcher) {
            localSqlTasksDao.updatePreferencesTournament(tournament)
        }
    }

    suspend fun updatePreferencesTournamentList(tournament: List<PreferencesTournament>) {
        withContext(ioDispatcher) {
            localSqlTasksDao.updatePreferencesTournamentList(tournament)
        }
    }

    suspend fun deletePreferencesTournament(tournament: PreferencesTournament) {
        withContext(ioDispatcher) {
            localSqlTasksDao.deletePreferencesTournament(tournament)
        }
    }
}






