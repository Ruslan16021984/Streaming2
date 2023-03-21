package com.natife.streaming.data.dto.sports

import com.natife.streaming.data.dto.translate.TranslateDTO
import com.natife.streaming.data.dto.translate.TranslatesDTO
import com.natife.streaming.db.entity.PreferencesSport
import com.natife.streaming.ext.getSingleValue

//new
data class SportTranslateDTO(
    val id: Int,
    val name: String,
    val lexic: Int,
    val lang: String,
    val text: String,
    var isCheck: Boolean
)

fun List<PreferencesSport>.toSportTranslateDTO(translates: TranslatesDTO): List<SportTranslateDTO> {

    return this.map { sport ->
        val translate =
            translates.filterKeys { it.toInt() == sport.lexic }.getSingleValue() ?: TranslateDTO()
        SportTranslateDTO(
            id = sport.id,
            name = sport.name,
            lexic = sport.lexic,
            lang = translate.lang,
            text = translate.text,
            isCheck = sport.isChack
        )
    }
}
