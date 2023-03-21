package com.natife.streaming.ui.calendar

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.natife.streaming.R
import com.natife.streaming.base.BaseViewModel
import com.natife.streaming.data.Lexic
import com.natife.streaming.ext.fromCalendar
import com.natife.streaming.preferenses.SettingsPrefs
import com.natife.streaming.router.Router
import com.natife.streaming.usecase.LexisUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.util.*

abstract class CalendarViewModel : BaseViewModel() {
    abstract val date: LiveData<Date>
    abstract val strings: LiveData<List<Lexic>>
    abstract val lang: LiveData<String>
    abstract fun select(time: LocalDate)
    abstract fun onProceedButtonClicked()

}

class CalendarViewModelImpl(
    private val settingsPrefs: SettingsPrefs,
    private val router: Router,
    private val lexisUseCase: LexisUseCase
) : CalendarViewModel() {
    override val strings = MutableLiveData<List<Lexic>>()
    override val lang = MutableLiveData<String>()
    override val date = MutableLiveData<Date>()
//        .apply {
//            value = Date()
//        }

    override fun select(time: LocalDate) {
        settingsPrefs.saveDate(time.fromCalendar()?.time ?: Date().time)
    }

    override fun onProceedButtonClicked() {
        router.popBackStack()
    }

    init {
        lang.postValue(settingsPrefs.getLanguage())
        val d = settingsPrefs.getDate()
        if (d != null) {
            date.value = Date(d)
        }
        launch {
            val str = lexisUseCase.execute(
                R.integer.proceed,
                R.integer.january,
                R.integer.february,
                R.integer.march,
                R.integer.april,
                R.integer.may,
                R.integer.june,
                R.integer.july,
                R.integer.august,
                R.integer.september,
                R.integer.october,
                R.integer.november,
                R.integer.december
            )
            strings.value = str
        }
        launchCatching {
            withContext(Dispatchers.IO) {
                collect(settingsPrefs.getDateFlow()) {
                    it?.let {
                        date.value = Date(it)
                    }
                }
            }
        }

    }


}
