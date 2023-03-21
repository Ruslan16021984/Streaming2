package com.natife.streaming.ui.account.language

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.natife.streaming.R
import com.natife.streaming.base.BaseViewModel
import com.natife.streaming.data.Lexic
import com.natife.streaming.db.LocalSqlDataSourse
import com.natife.streaming.db.entity.LanguageModel
import com.natife.streaming.ext.Event
import com.natife.streaming.preferenses.SettingsPrefs
import com.natife.streaming.usecase.LexisUseCase

abstract class LanguageSelectionViewModel : BaseViewModel() {
    abstract fun setLang(lm: LanguageModel)
    abstract fun getCurrentLang(): String

    abstract val languages: LiveData<List<Lexic>>
    abstract val restart: LiveData<Event<Boolean>>
}

class LanguageSelectionViewModelImpl(
    private val localSqlDataSourse: LocalSqlDataSourse,
    private val settingsPrefs: SettingsPrefs,
    private val lexisUseCase: LexisUseCase
) : LanguageSelectionViewModel() {
    override val languages = MutableLiveData<List<Lexic>>()

    init {
        launch {
            val str = lexisUseCase.execute(
                R.integer.selection_language,
                R.integer.english,
                R.integer.russian
            )
            languages.value = str
        }
    }

    override val restart = MutableLiveData<Event<Boolean>>()

    override fun getCurrentLang(): String = settingsPrefs.getLanguage()

    //TODO work with it when data on the BE is ready
    override fun setLang(lm: LanguageModel) {
        if (lm.isCurrent) {
            restart.value = Event(false)
        } else {
            launch {
                settingsPrefs.saveLanguage(lm.lang.name)
                localSqlDataSourse.updateGlobalSettingsCurrentUser(
                    showScore = false,
                    lang = lm.lang
                )
                restart.value = Event(true)
            }
        }
    }
}