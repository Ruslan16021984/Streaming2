package com.natife.streaming.mock

import com.natife.streaming.data.Login
import kotlinx.coroutines.delay

class MockLoginRepository() {
    suspend fun login(email: String, password: String): Login {
        delay(500)
        return Login(status = 1, token = "KLK:LK:LK:L<:L<L<")
    }
}