package com.natife.streaming.usecase

import android.app.Application
import com.natife.streaming.App
import com.natife.streaming.preferenses.AuthPrefs
import com.natife.streaming.preferenses.SearchPrefs
import com.natife.streaming.preferenses.SettingsPrefs
import com.natife.streaming.router.Router
import kotlinx.coroutines.*

/**
 * Выступает в роли интерфейса между ViewModel и Api.
 * При необходимости можно реализовать свой UseCaseImpl или отредактировать имеющийся
 * в нем же можно мапить данные.
 */
interface LogoutUseCase {
    fun execute(byUser: Boolean)
}

class LogoutUseCaseImpl(
    private val application: Application,
    private val authPrefs: AuthPrefs,
    private val settingsPrefs: SettingsPrefs,
    private val prefs: SearchPrefs,
    private val router: Router
) : LogoutUseCase {

    private val job = SupervisorJob()
    private val scope = CoroutineScope(Dispatchers.Main + job)
    override fun execute(byUser: Boolean) {
        authPrefs.clear()
        if (byUser){
            settingsPrefs.clear()
            prefs.clear()
        }
       // (application as? App)?.restartKoin()
        scope.launch {
            withContext(Dispatchers.Main) {
                router.toLogin()
            }
        }
    }
}
