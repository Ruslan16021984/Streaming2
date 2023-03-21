package com.natife.streaming.ui.main

import androidx.lifecycle.MutableLiveData
import com.natife.streaming.R
import com.natife.streaming.base.BaseViewModel
import com.natife.streaming.data.Lexic
import com.natife.streaming.db.LocalSqlDataSourse
import com.natife.streaming.db.entity.GlobalSettings
import com.natife.streaming.db.entity.Lang
import com.natife.streaming.ext.toDate
import com.natife.streaming.preferenses.AuthPrefs
import com.natife.streaming.preferenses.SettingsPrefs
import com.natife.streaming.router.Router
import com.natife.streaming.usecase.LexisUseCase
import kotlinx.coroutines.*
import java.util.*

class MainViewModel(
    private val authPrefs: AuthPrefs,
    private val router: Router,
    private val settingsPrefs: SettingsPrefs,
    private val localSqlDataSourse: LocalSqlDataSourse,
    private val lexisUseCase: LexisUseCase
) : BaseViewModel() {
    val date = MutableLiveData<Date>()
    val settings = MutableLiveData<GlobalSettings>()
    val strings = MutableLiveData<List<Lexic>>()

    init {
        launch {
            val isLoggedIn = authPrefs.isLoggedIn()
            if (isLoggedIn) {
                router.navigate(R.id.action_global_nav_main)
            } else {
                router.navigate(R.id.action_global_nav_auth)
            }
        }
        setPrefToday() // при каждом заходе в приложение всегда сбиваем дату на текущюю
        date.value = Date()

        launchCatching {
            withContext(Dispatchers.IO) {
                collect(settingsPrefs.getDateFlow()) {
                    it?.let {
                        this@MainViewModel.date.value = Date(it)
                    }
                }
            }
        }
    }

    //if global settings are not specified, set the default
    fun initialization() =
        launch {
            val lang = settingsPrefs.getLanguage().toLowerCase()
            val str = lexisUseCase.execute(
                R.integer.confirmation,
                R.integer.are_you_sure_exit,
                R.integer.hide_score,
                R.integer.show_score,
                R.integer.account,
                R.integer.subscriptions,
                R.integer.subscription,
                R.integer.type,
                R.integer.sum,
                R.integer.football,
                R.integer.hockey,
                R.integer.basketball,
                R.integer.payment_history,
                R.integer.payment_date,
                R.integer.yes,
                R.integer.no,
                R.integer.favorites
            )
            strings.value = str

            val globalSettings = localSqlDataSourse.getGlobalSettings()
            if (globalSettings == null) {
                settingsPrefs.saveLanguage(lang.toUpperCase()) // TODO продублировал в преференс тк не нашел решения как брать из бд при загрузке в
                localSqlDataSourse.setGlobalSettingsCurrentUser(
                    showScore = settingsPrefs.getScore() ?: false,
                    lang = Lang.valueOf(lang.toUpperCase()),
                )
                settings.value = localSqlDataSourse.getGlobalSettings()
            } else {
                settings.value = localSqlDataSourse.getGlobalSettings()
            }
        }

    fun getGlobalSettings() {
        launch {
            settings.value = localSqlDataSourse.getGlobalSettings()
        }
    }

    private fun setPrefToday() {
        val calendar = Calendar.getInstance()
        calendar.time = Date()
        settingsPrefs.saveDate(calendar.time.time)
    }

    fun toCalendar() {
        if (router.isCurrentDestination(R.id.homeFragment)) {
            router.navigate(R.id.action_homeFragment_to_calendarFragment)
        } else if (router.isCurrentDestination(R.id.favoritesFragment)) {
            router.navigate(R.id.action_favoritesFragment_to_calendarFragment)
        }
    }

    fun nextDay() {
        val calendar = Calendar.getInstance()
        calendar.time = settingsPrefs.getDate()?.toDate() ?: Date()
        calendar.add(Calendar.DAY_OF_YEAR, 1)
        settingsPrefs.saveDate(calendar.time.time)
    }

    fun previousDay() {
        val calendar = Calendar.getInstance()
        calendar.time = settingsPrefs.getDate()?.toDate() ?: Date()
        calendar.add(Calendar.DAY_OF_YEAR, -1)
        settingsPrefs.saveDate(calendar.time.time)
    }

    fun preferences() {
        router.navigateToPreference(R.id.action_global_preferences)
    }

    fun scoreButtonClicked(b: Boolean) {
        if (b) launch {
            localSqlDataSourse.updateShowScore(false)
            settings.value = localSqlDataSourse.getGlobalSettings()
        } else launch {
            localSqlDataSourse.updateShowScore(true)
            settings.value = localSqlDataSourse.getGlobalSettings()
        }

    }


}