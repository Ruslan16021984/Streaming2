package com.natife.streaming.db.dao

import androidx.room.*
import com.natife.streaming.db.entity.ActionEntity

@Dao
interface ActionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAction(action:ActionEntity)

    @Transaction
    suspend fun insertActions(actions: List<ActionEntity>){
        actions.forEach {
            insertAction(it)
        }

    }

    @Query("SELECT* FROM `action` WHERE sportId == :sportId")
    suspend fun getActions(sportId: Int): List<ActionEntity>

    @Query("SELECT* FROM `action` WHERE sportId == :sportId AND selected = 1")
    suspend fun getSelectedActions(sportId: Int): List<ActionEntity>
}