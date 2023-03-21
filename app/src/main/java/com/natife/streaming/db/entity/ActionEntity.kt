package com.natife.streaming.db.entity

import androidx.room.Entity

@Entity(tableName = "action",
primaryKeys = ["id","sportId"])
data class ActionEntity(
    val id: Int,
    val sportId: Int,
    val selected: Boolean = false
)