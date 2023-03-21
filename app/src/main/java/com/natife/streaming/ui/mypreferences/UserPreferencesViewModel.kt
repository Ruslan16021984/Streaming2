package com.natife.streaming.ui.mypreferences

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.natife.streaming.API_TRANSLATE
import com.natife.streaming.R
import com.natife.streaming.api.MainApi
import com.natife.streaming.base.BaseViewModel
import com.natife.streaming.data.Lexic
import com.natife.streaming.data.dto.sports.SportTranslateDTO
import com.natife.streaming.data.dto.sports.toSportTranslateDTO
import com.natife.streaming.data.dto.tournament.TournamentTranslateDTO
import com.natife.streaming.data.request.BaseRequest
import com.natife.streaming.data.request.TranslateRequest
import com.natife.streaming.db.LocalSqlDataSourse
import com.natife.streaming.db.entity.PreferencesSport
import com.natife.streaming.db.entity.PreferencesTournament
import com.natife.streaming.router.Router
import com.natife.streaming.usecase.*
import com.natife.streaming.workers.UpdatePreferencesInRemote
import kotlinx.coroutines.flow.Flow


abstract class UserPreferencesViewModel : BaseViewModel() {
    //    abstract val allUserPreferencesInTournament: LiveData<List<PreferencesTournament>>
    abstract val allUserPreferencesInSport: LiveData<List<PreferencesSport>>
    abstract val sportsSelected: LiveData<SportTranslateDTO>
    abstract val sportsViewSelectedPosition: Int?
    abstract val sportsList: LiveData<List<SportTranslateDTO>>
    abstract val strings: LiveData<List<Lexic>>
    abstract val positionPref: LiveData<Int>

    abstract fun applyMypreferencesClicked()
    abstract fun findClicked(text: String, lang: String): Flow<List<PreferencesTournament>>
    abstract fun kindOfSportClicked(sport: SportTranslateDTO)
    abstract fun kindOfSportSelected(sport: SportTranslateDTO?, viewId: Int?)
    abstract fun listOfTournamentsClicked(tournament: TournamentTranslateDTO, position: Int)
    abstract fun onFinishClicked()
    abstract fun getTranslateLexic(sports: List<PreferencesSport>, lang: String)
}

class UserPreferencesViewModelImpl(
    private val navId: Int,
    private val getSportUseCase: GetSportUseCase,
    private val saveSportUseCase: SaveSportUseCase,
    private val tournamentUseCase: GetTournamentUseCase,
    private val saveTournamentUseCase: SaveTournamentUseCase,
    private val api: MainApi,
    private val router: Router,
    private val application: Application,
    private val localSqlDataSourse: LocalSqlDataSourse,
    private val saveUserPreferencesUseCase: SaveUserPreferencesUseCase,
    private val lexisUseCase: LexisUseCase
) : UserPreferencesViewModel() {
    override val sportsList = MutableLiveData<List<SportTranslateDTO>>()
    override val sportsSelected = MutableLiveData<SportTranslateDTO>()
    override var sportsViewSelectedPosition: Int? = null
    override val allUserPreferencesInSport: LiveData<List<PreferencesSport>>
        get() = getSportUseCase.getAllUserPreferencesInSportFlow().asLiveData()
    private val workManager = WorkManager.getInstance(application)
    override val strings = MutableLiveData<List<Lexic>>()
    override val positionPref = MutableLiveData<Int>()


//    init{
//        launch {
//            getUserPreferencesUseCase.execute(189)
//        }
//    }

    override fun applyMypreferencesClicked() {
//        launch {
//            //send to server
//            val preferencesTournamentInDB = localSqlDataSourse.getOnlyIsCheckedPreferencesTournament()
//            localSqlDataSourse.getGlobalSettings()?.let{
//                val data = preferencesTournamentInDB.toUserPreferencesDTO()
//                it.userId?.let { id -> saveUserPreferencesUseCase.execute(id,data) }
//            }
//            router.navigate(R.id.action_global_nav_main)
//        }
        updatePreferences()
        router.navigate(navId)
    }

    override fun findClicked(text: String, lang: String) =
        tournamentUseCase.searchUserPreferencesInTournamentFlow(
            text,
            sportsSelected.value?.id,
            lang
        )

    override fun kindOfSportClicked(sport: SportTranslateDTO) {
        launch {
            saveSportUseCase.setSportCheckUncheck(sport)
        }
    }

    override fun kindOfSportSelected(sport: SportTranslateDTO?, viewId: Int?) {
        sportsSelected.postValue(sport)
        sportsViewSelectedPosition = viewId
    }

    override fun listOfTournamentsClicked(tournament: TournamentTranslateDTO, position: Int) {
        launch {
            positionPref.value = position
            saveTournamentUseCase.setTournamentCheckUncheck(tournament)
        }
    }

    override fun onFinishClicked() {
        router.navigate(navId)
    }


    override fun getTranslateLexic(
        sports: List<PreferencesSport>,
        lang: String
    ) {
        launch {
            val str = lexisUseCase.execute(
                R.integer.my_preference,
                R.integer.apply
            )
            strings.value = str
            val translates =
                api.getTranslate(BaseRequest(procedure = API_TRANSLATE,
                    params = TranslateRequest(
                        language = lang.toLowerCase(),
                        params = sports.map { it.lexic }
                    )))
            sportsList.value = sports.toSportTranslateDTO(translates)
        }
    }

    private fun updatePreferences() {
        val updateListOfTournamentWorker =
            OneTimeWorkRequest.Builder(UpdatePreferencesInRemote::class.java).build()
        val continuation = workManager
            .beginUniqueWork(
                "UPDATE_PREFERENCES",
                ExistingWorkPolicy.REPLACE,
                updateListOfTournamentWorker
            )
        continuation.enqueue()
    }
}

