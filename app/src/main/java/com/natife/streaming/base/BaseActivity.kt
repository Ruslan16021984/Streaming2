package com.natife.streaming.base

import android.app.Application
import android.content.res.Configuration
import android.os.Bundle
import androidx.annotation.IdRes
import androidx.appcompat.app.AppCompatActivity
import com.natife.streaming.App
import com.natife.streaming.R
import com.natife.streaming.db.LocalSqlDataSourse
import com.natife.streaming.ext.showToast
import com.natife.streaming.ext.subscribe
import com.natife.streaming.preferenses.SettingsPrefs
import com.natife.streaming.router.Router
import org.koin.android.ext.android.get
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.getViewModel
import timber.log.Timber
import java.lang.reflect.ParameterizedType
import java.util.*
import kotlin.reflect.KClass

abstract class BaseActivity<VM : BaseViewModel> : AppCompatActivity() {

    val router by inject<Router>()
    private val localSqlDataSourse: LocalSqlDataSourse = get()
    private val settingsPrefs: SettingsPrefs = get()
    private val app: Application by inject()

    protected lateinit var viewModel: VM

    abstract fun getLayoutRes(): Int?

    @IdRes
    abstract fun getNavHostId(): Int

    override fun onCreate(savedInstanceState: Bundle?) {
        router.attach(this, getNavHostId())

        val language = settingsPrefs.getLanguage()
        if (language != resources.getString(R.string.lang)) {
            val locale = Locale(language.toLowerCase(Locale.ROOT))
            Locale.setDefault(locale)
            val configuration = Configuration()
            configuration.setLocale(locale)
            this.resources.updateConfiguration(configuration, this.resources.displayMetrics)
            // Если поставить то что закоментировано на эмуляторе глюки
//            resources.displayMetrics.setTo(resources.displayMetrics)
//            resources.configuration.setTo(configuration)
        }

        super.onCreate(savedInstanceState)
        (app as App).onKoinRestart = {
            Timber.e("jndofidfo ")
            router.attach(this, getNavHostId())
        }
        getLayoutRes()?.also {
            setContentView(it)
        }

        viewModel = getViewModel(clazz = getViewModelKClass())

        subscribe(viewModel.defaultErrorLiveData) {
            onError(it)
        }
    }


    protected open fun onError(throwable: Throwable) {
        throwable.localizedMessage?.let {
            showToast(it)
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun getViewModelKClass(): KClass<VM> {
        val actualClass =
            (javaClass.genericSuperclass as ParameterizedType).actualTypeArguments[0] as Class<VM>
        return actualClass.kotlin
    }
}