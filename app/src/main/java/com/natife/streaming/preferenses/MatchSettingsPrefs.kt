package com.natife.streaming.preferenses

import android.content.SharedPreferences
import com.natife.streaming.data.LiveType

interface MatchSettingsPrefs: BasePrefs {
    fun saveAllSecAfter(s: Int): Boolean
    fun saveAllSecBefore(s: Int): Boolean

    fun getAllSecAfter(): Int
    fun getAllSecBefore(): Int

    fun saveSelectedSecAfter(s: Int): Boolean
    fun saveSelectedSecBefore(s: Int): Boolean

    fun getSelectedSecAfter(): Int
    fun getSelectedSecBefore(): Int


}

private const val SEC_BEFORE_ALL = "sec_before_all"
private const val SEC_AFTER_ALL= "sec_after_all"
private const val SEC_BEFORE_SELECTED = "sec_before_selected"
private const val SEC_AFTER_SELECTER= "sec_after_selected"


class MatchSettingsPrefsImpl(private val prefs: SharedPreferences): MatchSettingsPrefs{

    override fun saveAllSecAfter(s: Int): Boolean = prefs.edit().putInt(SEC_AFTER_ALL, s).commit()
    override fun saveAllSecBefore(s: Int): Boolean = prefs.edit().putInt(SEC_BEFORE_ALL, s).commit()

    override fun getAllSecAfter(): Int = prefs.getInt(SEC_AFTER_ALL,6)
    override fun getAllSecBefore(): Int = prefs.getInt(SEC_BEFORE_ALL,-6)

    override fun saveSelectedSecAfter(s: Int): Boolean  = prefs.edit().putInt(SEC_AFTER_SELECTER, s).commit()
    override fun saveSelectedSecBefore(s: Int): Boolean = prefs.edit().putInt(SEC_BEFORE_SELECTED, s).commit()

    override fun getSelectedSecAfter(): Int = prefs.getInt(SEC_AFTER_SELECTER,0)
    override fun getSelectedSecBefore(): Int = prefs.getInt(SEC_BEFORE_SELECTED,0)

    override fun clear(): Boolean = prefs.edit().clear().commit()

}