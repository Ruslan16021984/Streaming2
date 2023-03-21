package com.natife.streaming.usecase

import android.content.Context
import androidx.annotation.IntegerRes
import com.natife.streaming.API_TRANSLATE
import com.natife.streaming.api.MainApi
import com.natife.streaming.data.Lexic
import com.natife.streaming.data.request.BaseRequest
import com.natife.streaming.data.request.TranslateRequest
import com.natife.streaming.db.dao.LexicDao
import com.natife.streaming.db.entity.LexicEntity
import com.natife.streaming.preferenses.SettingsPrefs

interface LexisUseCase {
    suspend fun execute(@IntegerRes vararg ids: Int): List<Lexic>
}

class LexisUseCaseImpl(
    private val api: MainApi,
    private val prefs: SettingsPrefs,
    private val dao: LexicDao,
    private val context: Context
) : LexisUseCase {
    override suspend fun execute(vararg ids: Int): List<Lexic> {
        val fromDb = dao.getLexics(ids.asList().map {
//            Timber.e("jndofnodf ${context.resources.getInteger(it)}")
            context.resources.getInteger(it)
        }, prefs.getLanguage())
        if (fromDb.size != ids.size) {
            val fromServer =
                api.getTranslate(BaseRequest(procedure = API_TRANSLATE, params = TranslateRequest(
                    language = prefs.getLanguage().toLowerCase(),
                    params = ids.asList().map { context.resources.getInteger(it) }
                )))

            dao.insertLexics(fromServer.map {
                LexicEntity(
                    id = it.value.id.toInt(),
                    lang = it.value.lang,
                    lexisLangId = it.value.lexisLangId,
                    text = it.value.text
                )
            })
            //
            return fromServer.map {
                Lexic(
                    id = it.value.id.toInt(),
                    lang = it.value.lang,
                    lexisLangId = it.value.lexisLangId,
                    text = it.value.text
                )
            }
        }
        return fromDb.map {
            Lexic(
                id = it.id,
                lang = it.lang,
                lexisLangId = it.lexisLangId,
                text = it.text
            )
        }
    }
}