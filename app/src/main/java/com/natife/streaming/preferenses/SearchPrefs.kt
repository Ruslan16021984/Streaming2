package com.natife.streaming.preferenses

import android.content.SharedPreferences
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.sendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

interface SearchPrefs : BasePrefs {
   fun getSport(): Int
   fun getGender(): Int
   fun getType(): Int

    fun saveSport(sportId: Int): Boolean
    fun saveGender(gender: Int): Boolean
    fun saveType(type: Int): Boolean

    fun getSportFlow(): Flow<Int>
    fun getGenderFlow(): Flow<Int>
    fun getTypeFlow(): Flow<Int>

}
private const val SPORT = "sport"
private const val GENDER = "gender"
private const val TYPE = "type"
class SearchPrefsImpl (private val prefs: SharedPreferences): SearchPrefs {
    override fun getSport(): Int  = prefs.getInt(SPORT,1)

    override fun getGender(): Int = prefs.getInt(GENDER,1)
    override fun getType(): Int =prefs.getInt(TYPE,1)

    override fun saveSport(sportId: Int): Boolean  = prefs.edit().putInt(SPORT,sportId).commit()

    override fun saveGender(gender: Int): Boolean = prefs.edit().putInt(GENDER,gender).commit()

    override fun saveType(type: Int): Boolean = prefs.edit().putInt(TYPE,type).commit()

    override fun getSportFlow(): Flow<Int> = callbackFlow {
        sendBlocking(getSport())
        val changeListener =
            SharedPreferences.OnSharedPreferenceChangeListener { sharedPreferences, key ->
                if (key == SPORT) {
                    sendBlocking(getSport())
                }
            }
        prefs.registerOnSharedPreferenceChangeListener(changeListener)
        awaitClose { prefs.unregisterOnSharedPreferenceChangeListener(changeListener) }
    }

    override fun getGenderFlow(): Flow<Int> =  callbackFlow {
        sendBlocking(getGender())
        val changeListener =
            SharedPreferences.OnSharedPreferenceChangeListener { sharedPreferences, key ->
                if (key == GENDER) {
                    sendBlocking(getGender())
                }
            }
        prefs.registerOnSharedPreferenceChangeListener(changeListener)
        awaitClose { prefs.unregisterOnSharedPreferenceChangeListener(changeListener) }
    }

    override fun getTypeFlow(): Flow<Int>  = callbackFlow {
        sendBlocking(getType())
        val changeListener =
            SharedPreferences.OnSharedPreferenceChangeListener { sharedPreferences, key ->
                if (key == TYPE) {
                    sendBlocking(getType())
                }
            }
        prefs.registerOnSharedPreferenceChangeListener(changeListener)
        awaitClose { prefs.unregisterOnSharedPreferenceChangeListener(changeListener) }
    }

    override fun clear(): Boolean = prefs.edit().clear().commit()
}
