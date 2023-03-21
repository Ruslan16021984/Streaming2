package com.natife.streaming.db.entity

import androidx.room.Entity


@Entity(primaryKeys = ["authEmail"])
data class GlobalSettings(
    val authEmail: String,
    val showScore: Boolean,
    val userId: Int? = null,
    val lang: Lang
)

enum class Lang(code: String) {
    RU("ru"), EN("en")
}

data class LanguageModel(
    val id: Int,
    val language: String,
    val lang: Lang,
    val isCurrent: Boolean = false
)