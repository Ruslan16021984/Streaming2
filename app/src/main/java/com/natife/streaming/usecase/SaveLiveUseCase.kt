package com.natife.streaming.usecase

import com.natife.streaming.data.LiveType
import com.natife.streaming.preferenses.SettingsPrefs

interface SaveLiveUseCase {
    fun execute(type: LiveType)
}

class SaveLiveUseCaseImpl(private val settingsPrefs: SettingsPrefs) : SaveLiveUseCase {
    override fun execute(type: LiveType) {
        settingsPrefs.saveLive(type)
    }

}