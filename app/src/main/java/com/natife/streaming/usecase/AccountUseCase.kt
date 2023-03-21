package com.natife.streaming.usecase

import com.natife.streaming.API_ACCOUNT
import com.natife.streaming.api.MainApi
import com.natife.streaming.data.Profile
import com.natife.streaming.data.dto.account.AccountDTO
import com.natife.streaming.data.request.BaseRequest
import com.natife.streaming.data.request.EmptyRequest
import com.natife.streaming.preferenses.AuthPrefs


/**
 * Выступает в роли интерфейса между ViewModel и Api.
 * Необходимо реализовать свой UseCaseImpl или отредактировать имеющийся
 * в нем же можно мапить данные.
 */
interface AccountUseCase {
    suspend fun getProfile(): Profile
}

class AccountUseCaseImpl(private val api: MainApi, private val authPrefs: AuthPrefs) :
    AccountUseCase {
    override suspend fun getProfile(): Profile {
        val profileDTO =
            api.getProfile(BaseRequest(procedure = API_ACCOUNT, params = EmptyRequest()))
        val profile = Profile(
            firstName = profileDTO.firstname ?: "",
            lastName = profileDTO.lastname ?: "",
            email = profileDTO.email ?: "",
            phone = profileDTO.phone ?: "",
            country = profileDTO.country ?: AccountDTO.CountryDTO(0,"",""),

        )
        authPrefs.saveProfile(profile)
        return profile
    }

}
