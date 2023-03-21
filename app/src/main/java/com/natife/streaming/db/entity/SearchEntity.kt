package com.natife.streaming.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.natife.streaming.data.search.SearchResult

@Entity(tableName = "search_result",primaryKeys = ["id","type","sport"])
data class SearchEntity(
    val id: Int,
    val name: String,
    val type: String,
    val image: String,
    val placeholder: String,
    val sport: Int,
    val gender: Int
)
