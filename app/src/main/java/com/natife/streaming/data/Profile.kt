package com.natife.streaming.data

import com.natife.streaming.data.dto.account.AccountDTO

data class Profile(
    val firstName: String,
    val lastName: String,
    val email: String,
    val phone: String,
    val country: AccountDTO.CountryDTO?
    )