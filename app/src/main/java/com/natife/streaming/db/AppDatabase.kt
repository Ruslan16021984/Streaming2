package com.natife.streaming.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.google.gson.Gson
import com.natife.streaming.data.Sport
import com.natife.streaming.data.dto.country.CountryDTO
import com.natife.streaming.db.dao.ActionDao
import com.natife.streaming.db.dao.LexicDao
import com.natife.streaming.db.dao.LocalSqlTasksDao
import com.natife.streaming.db.dao.SearchDao
import com.natife.streaming.db.entity.*

@Database(
    entities = [
        ActionEntity::class,
        SearchEntity::class,
        LexicEntity::class,
        GlobalSettings::class,
        PreferencesTournament::class,
        PreferencesSport::class],
    version = 4
)
@TypeConverters(RoomConverters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun actionDao(): ActionDao
    abstract fun searchDao(): SearchDao
    abstract fun lexicDao(): LexicDao
    abstract fun localSqlTasksDao(): LocalSqlTasksDao

}

class RoomConverters {
    companion object {
        @TypeConverter
        @JvmStatic
        fun fromCountry(country: CountryDTO): String {
            return Gson().toJson(country)
        }

        @TypeConverter
        @JvmStatic
        fun toCountry(data: String): CountryDTO {
            return Gson().fromJson(data, CountryDTO::class.java)
        }

        @TypeConverter
        @JvmStatic
        fun fromLang(lang: Lang): String {
            return lang.name
        }

        @TypeConverter
        @JvmStatic
        fun toLang(data: String): Lang {
            return Lang.valueOf(data)
        }
    }
}
