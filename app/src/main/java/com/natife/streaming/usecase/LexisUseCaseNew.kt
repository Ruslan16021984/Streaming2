package com.natife.streaming.usecase

import androidx.annotation.IntegerRes
import com.natife.streaming.API_TRANSLATE
import com.natife.streaming.api.MainApi
import com.natife.streaming.data.Lexic
import com.natife.streaming.data.request.BaseRequest
import com.natife.streaming.data.request.TranslateRequest
import com.natife.streaming.preferenses.SettingsPrefs

interface LexisUseCaseNew {
    suspend fun execute(@IntegerRes vararg ids: Int): List<Lexic>
}

class LexisUseCaseNewImpl(
    private val api: MainApi,
    private val prefs: SettingsPrefs
) : LexisUseCaseNew {
    override suspend fun execute(vararg ids: Int): List<Lexic> {
        val fromServer =
            api.getTranslate(
                BaseRequest(
                    procedure = API_TRANSLATE, params = TranslateRequest(
                        language = prefs.getLanguage().toLowerCase(),
                        params = ids.asList()
                    )
                )
            )
        return fromServer.map {
            Lexic(
                id = it.value.id.toInt(),
                lang = it.value.lang,
                lexisLangId = it.value.lexisLangId,
                text = it.value.text
            )
        }
    }
}