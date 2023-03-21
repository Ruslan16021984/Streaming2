package com.natife.streaming.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.natife.streaming.db.LocalSqlDataSourse
import com.natife.streaming.usecase.SaveUserPreferencesUseCase
import org.koin.core.KoinComponent
import org.koin.core.get

class UpdatePreferencesInRemote(
    ctx: Context, params: WorkerParameters
) : CoroutineWorker(ctx, params), KoinComponent {
    private val localSqlDataSourse: LocalSqlDataSourse = get()
    private val saveUserPreferencesUseCase: SaveUserPreferencesUseCase = get()
    override suspend fun doWork(): Result {
        val preferencesTournamentInDB = localSqlDataSourse.getOnlyIsCheckedPreferencesTournament()
        localSqlDataSourse.getGlobalSettings()?.let {
            val data = preferencesTournamentInDB.toUserPreferencesDTO()
            it.userId?.let { id ->
                saveUserPreferencesUseCase.execute(
                    id,
                    if (data.isEmpty()) null else data
                )
            }
        }
        return Result.success()
    }
}



