package com.natife.streaming.db.entity

import androidx.room.Entity

@Entity(tableName = "lexic",
    primaryKeys = ["id","lang"])
data class LexicEntity(
    val id:Int,
    val lexisLangId: String,
    val lang: String,
    val text: String
)