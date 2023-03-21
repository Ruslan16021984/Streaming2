package com.natife.streaming.data.dto.country


data class CountryTranslateDTO(
    val id: Int,
    val name: String,
)

//fun CountryDTO.toCountryTranslateDTO(lang: String): CountryTranslateDTO {
//    return CountryTranslateDTO(
//        id = this.id,
//        name = when (lang) {
//            "en", "EN" -> this.nameEng ?:""
//            "ru","RU" -> this.nameRus ?:""
//            else -> this.nameEng ?:""
//        }
//    )
//}