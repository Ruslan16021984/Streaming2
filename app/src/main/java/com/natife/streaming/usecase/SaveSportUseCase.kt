package com.natife.streaming.usecase

import com.natife.streaming.data.dto.sports.SportTranslateDTO
import com.natife.streaming.db.LocalSqlDataSourse
import com.natife.streaming.db.entity.PreferencesSport
import com.natife.streaming.preferenses.SettingsPrefs

interface SaveSportUseCase {
    @Deprecated("don't use")
    suspend fun execute(sportId: Int)

    suspend fun savePreferencesSportList(list: List<PreferencesSport>)
    suspend fun setSportCheckUncheck(sport: SportTranslateDTO)
    suspend fun deleteAllSports()
    suspend fun deleteAllPreferencesSportAndCreateAndSynchronize(
        newList: List<PreferencesSport>,
        oldList: List<PreferencesSport>
    )
}

class SaveSportUseCaseImpl(
    private val settingsPrefs: SettingsPrefs,
    private val localSqlDataSourse: LocalSqlDataSourse
) : SaveSportUseCase {

    @Deprecated("don't use")
    override suspend fun execute(sportId: Int) {
        settingsPrefs.saveSport(sportId)
    }


    override suspend fun setSportCheckUncheck(sport: SportTranslateDTO) {
        localSqlDataSourse.setCheckedSport(sport)
    }

    override suspend fun savePreferencesSportList(list: List<PreferencesSport>) {
        localSqlDataSourse.setPreferencesSportList(list)
    }

    override suspend fun deleteAllSports() {
        localSqlDataSourse.deleteAllPreferencesSport()
    }

    override suspend fun deleteAllPreferencesSportAndCreateAndSynchronize(
        newList: List<PreferencesSport>,
        oldList: List<PreferencesSport>
    ) {
        localSqlDataSourse.deleteAllPreferencesSportAndCreateAndSynchronize(newList, oldList)
    }


}