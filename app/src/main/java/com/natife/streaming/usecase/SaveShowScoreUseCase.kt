package com.natife.streaming.usecase

import com.natife.streaming.preferenses.SettingsPrefs

@Deprecated("localSqlDataSourse.getGlobalSettings()")
interface SaveShowScoreUseCase {
    suspend fun execute(yes: Boolean)
}

@Deprecated("localSqlDataSourse.getGlobalSettings()")
class SaveShowScoreUseCaseImpl(private val settingsPrefs: SettingsPrefs) : SaveShowScoreUseCase {
    override suspend fun execute(yes: Boolean) {
        settingsPrefs.saveScore(yes)
    }
}