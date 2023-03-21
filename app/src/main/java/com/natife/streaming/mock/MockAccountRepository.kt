package com.natife.streaming.mock

import com.natife.streaming.data.Profile
import com.natife.streaming.data.dto.account.AccountDTO
import kotlinx.coroutines.delay

class MockAccountRepository {
    suspend fun getProfile(): Profile {
        delay(500)
        return Profile(
            firstName = "Иван",
            lastName = "Иванов",
            email = "konstantinopolsky@gmail.com",
            phone = "+380293944935",
            country = AccountDTO.CountryDTO(0,"","")
        )
    }
}