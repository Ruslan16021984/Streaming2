package com.natife.streaming.usecase

import android.content.Context
import com.google.gson.Gson
import com.natife.streaming.API_REGISTER
import com.natife.streaming.R
import com.natife.streaming.api.MainApi
import com.natife.streaming.api.exception.ApiException
import com.natife.streaming.data.dto.LoginDTO
import com.natife.streaming.data.dto.register.RequestRegister
import com.natife.streaming.data.request.BaseRequest
import com.natife.streaming.utils.Result

interface RegisterUseCase {
    suspend fun execute(
        email: String,
        password: String,
        onComplete: (Result<String>) -> Unit
    )
}

class RegisterUseCaseImpl(
    private val api: MainApi,
    private val context: Context
) : RegisterUseCase {
    override suspend fun execute(
        email: String,
        password: String,
        onComplete: (Result<String>) -> Unit
    ) {
        val request = BaseRequest(
            procedure = API_REGISTER,
            params = RequestRegister(
                email,
                password
            )
        )
        try {
            val register = api.makeRegister(request)
            when (register.status) {
                1 -> onComplete(Result.success("Success"))
                2 -> onComplete(Result.error(register.error))
            }
        } catch (e: ApiException) {
            val body = Gson().fromJson(e.body, LoginDTO::class.java)
            when (body.status) {
                //TODO Надо прописать новые ответы
                else -> {
                    onComplete(
                        Result.error<String>(
                            context.resources.getString(R.string.this_login_is_already_used)
                        )
                    )
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}



