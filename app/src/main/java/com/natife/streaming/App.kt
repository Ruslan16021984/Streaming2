package com.natife.streaming

import android.app.Application
import androidx.viewbinding.BuildConfig
import androidx.work.Configuration
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import timber.log.Timber

class App : Application(), Configuration.Provider {

    var onKoinRestart: (() -> Unit)? = null

    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
        initKoin()

    }

    private fun initKoin() {
        startKoin {
            modules(appModules)
            androidContext(this@App)
        }
    }

    fun restartKoin() {
        stopKoin()
        initKoin()
        onKoinRestart?.invoke()
    }

    override fun getWorkManagerConfiguration(): Configuration {
        return if (BuildConfig.DEBUG) {
            Configuration.Builder()
                .setMinimumLoggingLevel(android.util.Log.DEBUG)
                .build()
        } else {
            Configuration.Builder()
                .setMinimumLoggingLevel(android.util.Log.ERROR)
                .build()
        }
    }
}