package com.natife.streaming.ui.login

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.google.gson.Gson
import com.natife.streaming.R
import com.natife.streaming.base.BaseViewModel
import com.natife.streaming.data.Lexic
import com.natife.streaming.data.api.response.LoginResponse
import com.natife.streaming.data.model.Payload
import com.natife.streaming.db.LocalSqlDataSourse
import com.natife.streaming.db.entity.Lang
import com.natife.streaming.preferenses.SettingsPrefs
import com.natife.streaming.router.Router
import com.natife.streaming.usecase.AccountUseCase
import com.natife.streaming.usecase.LexisUseCase
import com.natife.streaming.usecase.LoginUseCase
import com.natife.streaming.utils.JWTUtils
import com.natife.streaming.utils.onErrorValue
import com.natife.streaming.utils.onSuccessValue
import com.natife.streaming.workers.LoadListOfSportsWorker
import com.natife.streaming.workers.LoadListOfTournamentWorker
import java.util.*

abstract class LoginViewModel : BaseViewModel() {
    abstract val strings: LiveData<List<Lexic>>

    abstract fun getStrings()
    abstract fun login(email: String, password: String, onError: ((String?) -> Unit))
    abstract fun onRegisterClicked()
}

class LoginViewModelImpl(
    private val router: Router,
    private val loginUseCase: LoginUseCase,
    private val accountUseCase: AccountUseCase,
    private val localSqlDataSourse: LocalSqlDataSourse,
    private val application: Application,
    private val settingsPrefs: SettingsPrefs,
    private val lexisUseCase: LexisUseCase
) : LoginViewModel() {
    override val strings = MutableLiveData<List<Lexic>>()
    private val workManager = WorkManager.getInstance(application)

    override fun getStrings() {
        launch {
            val str = lexisUseCase.execute(
                R.integer.entrance,
                R.integer.sign_in_to_access_your_account,
                R.integer.enter_login,
                R.integer.enter_password,
                R.integer.login,
                R.integer.register_now,
                R.integer.wrong_login_or_password
            )
            strings.value = str
//            Timber.tag("Lexic").d("$str")
        }
    }

    override fun login(email: String, password: String, onError: ((String?) -> Unit)) {
        launch {
            loginUseCase.execute(email, password)
                .onSuccessValue { loginResponse ->
                    if (loginResponse.error == null) {
                        setGlobalSettings(loginResponse)
                    } else {
                        onError.invoke(loginResponse.error)
                    }
                }
                .onErrorValue {
                    onError.invoke(it.message)
                }
        }
    }

    private fun setGlobalSettings(loginResponse: LoginResponse) {
        launch {
            val lang = settingsPrefs.getLanguage().toLowerCase()
            accountUseCase.getProfile()
            //проверка всех настроек если клиент вошел под старым аккаунтом на новую установку
            if (localSqlDataSourse.getGlobalSettings() == null) {
                settingsPrefs.saveLanguage(lang.toUpperCase()) // TODO продублировал в преференс тк не нашел решения как брать из бд при загрузке в BaseActivity
                localSqlDataSourse.setGlobalSettings(
                    showScore = true,
                    lang = Lang.valueOf(lang.toUpperCase()),
                    id = getUserId(loginResponse)
                )
            }
            setDate()
            loadPreferences()
            router.navigate(R.id.action_global_nav_main)
        }
    }

    private fun getUserId(loginResponse: LoginResponse): Int? {
        val jsonBody = JWTUtils.decoded(loginResponse.accessToken ?: "")
        val payload = Gson().fromJson(jsonBody, Payload::class.java)
        return payload.sub
    }

    private fun setDate() {
        if (settingsPrefs.getDate() == null) {
            settingsPrefs.saveDate(Date().time)
        }
    }

    private fun loadPreferences() {
        val loadListOfSportsWorker =
            OneTimeWorkRequest.Builder(LoadListOfSportsWorker::class.java).build()
        val loadListOfTournamentWorker =
            OneTimeWorkRequest.Builder(LoadListOfTournamentWorker::class.java).build()
        val continuation = workManager
            .beginUniqueWork(
                "LOAD_PREFERENCES",
                ExistingWorkPolicy.REPLACE,
                loadListOfTournamentWorker
            ).then(loadListOfSportsWorker)
        continuation.enqueue()
    }

    override fun onRegisterClicked() {
        val navDirections = LoginFragmentDirections.actionLoginFragmentToRegisterFragment()
        router.navigate(navDirections)
    }
}

