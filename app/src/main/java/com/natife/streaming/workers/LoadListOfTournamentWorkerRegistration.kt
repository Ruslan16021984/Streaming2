package com.natife.streaming.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.natife.streaming.db.LocalSqlDataSourse
import com.natife.streaming.db.entity.PreferencesTournament
import com.natife.streaming.db.entity.toPreferencesTournament
import com.natife.streaming.usecase.GetTournamentUseCase
import com.natife.streaming.usecase.GetUserPreferencesUseCase
import com.natife.streaming.usecase.SaveTournamentUseCase
import org.koin.core.KoinComponent
import org.koin.core.get

class LoadListOfTournamentWorkerRegistration(
    ctx: Context, params: WorkerParameters
) : CoroutineWorker(ctx, params), KoinComponent {
    private val tournamentUseCase: GetTournamentUseCase = get()
    private val saveTournamentUseCase: SaveTournamentUseCase = get()
//    private val getTournamentUseCase: GetTournamentUseCase = get()

    private val getUserPreferencesUseCase: GetUserPreferencesUseCase = get()

    //    private val saveUserPreferencesUseCase: SaveUserPreferencesUseCase = get()
    private val localSqlDataSourse: LocalSqlDataSourse = get()

    override suspend fun doWork(): Result {
        val preferencesTournament: List<PreferencesTournament> =
            tournamentUseCase.execute().toPreferencesTournament()
        localSqlDataSourse.getGlobalSettings()?.let {
            it.userId?.let { id ->
                val userPrefInServer = getUserPreferencesUseCase.execute(id)
                when {
                    userPrefInServer == null -> {
                        saveTournamentUseCase.deleteAllPreferencesTournamentAndCreateAndSetAllTournamentOff(
                            newList = preferencesTournament
                        )
                    }
                    userPrefInServer.isEmpty() -> {
                        //есть неоднозначность в АПИ клиента при пустом списке предпочтений он всеравно выдаст все лиги поєтому обработка Null и empty делаем одинаковой
                        saveTournamentUseCase.deleteAllPreferencesTournamentAndCreate(
                            newList = preferencesTournament,
                        )
//                        saveTournamentUseCase.deleteAllPreferencesTournamentAndCreateAndSetAllTournamentOff(
//                            newList = preferencesTournament
//                        )
                    }
                    else -> {
                        saveTournamentUseCase.deleteAllPreferencesTournamentAndCreateAndSynchronize(
                            newList = preferencesTournament,
                            listForSynchronize = userPrefInServer
                        )
                    }
                }
            }
        }
        return Result.success()
    }

//    init {
//        launch {
//            val a = mutableListOf<UserPreferencesDTO>()// 1 maya
//            a.add(UserPreferencesDTO(sport = 1, tournamentId = 39))
//            a.add(UserPreferencesDTO(sport = 2, tournamentId = 7))
//
//            val test = getUserPreferencesUseCase.execute(189)
////            val test2 = saveUserPreferencesUseCase.execute(189, a )
//            val test2 = saveUserPreferencesUseCase.execute(189, listOf())
//
//        }
//    }
}

