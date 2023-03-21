package com.natife.streaming.db.dao

import androidx.room.*
import com.natife.streaming.db.entity.ActionEntity
import com.natife.streaming.db.entity.LexicEntity

@Dao
interface LexicDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLexic(action: LexicEntity)

    @Transaction
    suspend fun insertLexics(actions: List<LexicEntity>){
        actions.forEach {
            insertLexic(it)
        }

    }
    @Query("SELECT* FROM `lexic` WHERE id == :id AND lang == :language")
    suspend fun getLexic(id: Int, language: String): List<LexicEntity>

    @Query("SELECT* FROM `lexic` WHERE id IN (:ids) AND lang == :language")
    suspend fun getLexics(ids: List<Int>, language: String): List<LexicEntity>



}