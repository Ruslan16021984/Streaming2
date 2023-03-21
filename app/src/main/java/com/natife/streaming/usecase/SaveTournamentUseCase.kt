package com.natife.streaming.usecase

import com.natife.streaming.data.dto.preferences.UserPreferencesDTO
import com.natife.streaming.data.dto.tournament.TournamentTranslateDTO
import com.natife.streaming.db.LocalSqlDataSourse
import com.natife.streaming.db.entity.PreferencesTournament
import com.natife.streaming.preferenses.SettingsPrefs

interface SaveTournamentUseCase {
    @Deprecated("don't use")
    fun execute(tournamentId: Int)

    suspend fun setTournamentCheckUncheck(tournament: TournamentTranslateDTO)
    suspend fun saveTournamentList(list: List<PreferencesTournament>)
    suspend fun updateTournamentList(list: List<PreferencesTournament>)
    suspend fun deleteAllPreferencesTournamentAndCreate(newList: List<PreferencesTournament>)
    suspend fun deleteAllPreferencesTournamentAndCreateAndSetAllTournamentOff(newList: List<PreferencesTournament>)
    suspend fun deleteAllPreferencesTournamentAndCreateAndSynchronize(
        newList: List<PreferencesTournament>,
        listForSynchronize: List<UserPreferencesDTO>
    )


}
class SaveTournamentUseCaseImpl(
    private val settingsPrefs: SettingsPrefs,
    private val localSqlDataSourse: LocalSqlDataSourse
    ): SaveTournamentUseCase {

    @Deprecated("don't use")
    override fun execute(tournamentId: Int) {
        settingsPrefs.saveTournament(tournamentId)
    }

    override suspend fun setTournamentCheckUncheck(
        tournament: TournamentTranslateDTO
    ) {
        localSqlDataSourse.setCheckedTournament(tournament)
    }

    override suspend fun saveTournamentList(list: List<PreferencesTournament>) {
        localSqlDataSourse.setlistPreferencesTournament(list)
    }

    override suspend fun updateTournamentList(list: List<PreferencesTournament>) {
        localSqlDataSourse.updateTournamentList(list)
    }

    override suspend fun deleteAllPreferencesTournamentAndCreate(newList: List<PreferencesTournament>) {
        localSqlDataSourse.deleteAllPreferencesTournamentAndCreate(newList)
    }

    override suspend fun deleteAllPreferencesTournamentAndCreateAndSetAllTournamentOff(newList: List<PreferencesTournament>) {
        localSqlDataSourse.deleteAllPreferencesTournamentAndCreateAndSetAllTournamentOff(newList)
    }

    override suspend fun deleteAllPreferencesTournamentAndCreateAndSynchronize(
        newList: List<PreferencesTournament>,
        listForSynchronize: List<UserPreferencesDTO>
    ) {
        localSqlDataSourse.deleteAllPreferencesTournamentAndCreateAndSynchronize(
            newList,
            listForSynchronize
        )
    }
}