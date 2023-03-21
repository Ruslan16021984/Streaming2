package com.natife.streaming.custom

import android.app.Activity
import android.content.Context
import android.graphics.Rect
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.ImageView
import androidx.core.view.isVisible
import androidx.leanback.widget.BrowseFrameLayout
import androidx.navigation.NavController
import com.natife.streaming.R
import com.natife.streaming.preferenses.AuthPrefs
import com.natife.streaming.router.Router
import com.natife.streaming.usecase.LexisUseCase
import com.natife.streaming.utils.findLexicText
import kotlinx.android.synthetic.main.view_side_menu_new.view.*
import kotlinx.coroutines.*
import org.koin.core.KoinComponent
import org.koin.core.get
import org.koin.core.inject
import timber.log.Timber
import kotlin.properties.Delegates

class SideMenu @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : BrowseFrameLayout(context, attrs, defStyleAttr), KoinComponent {

    private var router: Router? = null
    private val authPrefs: AuthPrefs by inject()
    private val lexisUseCase: LexisUseCase = get()
    var activity: Activity? = null
    var prefered: (() -> Unit)? = null
    private var currentSelected by Delegates.notNull<ImageView>()
    private val navListener =
        NavController.OnDestinationChangedListener { controller, destination, arguments ->

            this.isVisible = when (destination.id) {
                R.id.loginFragment, R.id.registerFragment, R.id.mypreferencesFragment, R.id.action_global_preferences,
//                R.id.matchProfileFragment, R.id.matchSettingsFragment, R.id.watchFragment,
                R.id.playerFragment -> false

                else -> true
            }
            when (destination.id) {
                R.id.homeFragment -> {
                    select(iconHome)
                    currentSelected = iconHome
                }
                R.id.accountFragment -> {
                    select(accountIcon)
                    currentSelected = accountIcon
                }
                R.id.searchFragment -> {
                    select(iconSearch)
                    currentSelected = iconSearch
                }
                R.id.favoritesFragment -> {
                    select(iconFavorites)
                    currentSelected = iconFavorites
                }
//                R.id.settingsFragment -> {
//                    select(iconSettings)
//                    currentSelected = iconSettings
//                }
            }
        }

    fun select(view: ImageView) {
        accountIcon.imageTintList = resources.getColorStateList(R.color.menu_item_state_icon, null)
        iconSearch.imageTintList = resources.getColorStateList(R.color.menu_item_state_icon, null)
        iconHome.imageTintList = resources.getColorStateList(R.color.menu_item_state_icon, null)
        iconFavorites.imageTintList = resources.getColorStateList(R.color.menu_item_state_icon, null)
//        iconSettings.imageTintList = resources.getColorStateList(R.color.menu_item_state_icon, null)
        view.imageTintList =
            resources.getColorStateList(R.color.menu_item_state_icon_selected, null)
    }

    init {
        val view = LayoutInflater.from(context).inflate(R.layout.view_side_menu_new, this, false)
        this.addView(view)

//        // TODO 2 раза подряд оджно и тоже надо оставить FLOW
//        authPrefs.getProfile()?.let {
//            accountText.text = it.firstName + " " + it.lastName
//        }
//
//
        MainScope().launch {
//            authPrefs.getProfileFlow().collect {
//                it?.let {
//                    accountText.text = it.firstName + " " + it.lastName
//                }
//            }
            val str = lexisUseCase.execute(
                R.integer.account,
                R.integer.search,
                R.integer.home,
                R.integer.favorites
            )
            accountText.text = str.findLexicText(context, R.integer.account)
            searchText.text = str.findLexicText(context, R.integer.search)
            homeText.text = str.findLexicText(context, R.integer.home)
            favoritesText.text = str.findLexicText(context, R.integer.favorites)
        }

        menuAccount.setOnClickListener {
            router?.toAccount()
        }
        menuSearch.setOnClickListener {
            router?.toSearch()
        }
        menuHome.setOnClickListener {
            router?.toHome()
        }
        menuFavorites.setOnClickListener {
            router?.toFavorites()
            //router?.navigate(R.id.action_main_tournamentFragment)
        }
//        menuSettings.setOnClickListener {
//            router?.toSettings()
//        }

    }

    fun setProfile(name: String) {
        accountText.text = if (name.isEmpty()) "User" else name
    }

    fun setRouter(router: Router?) {
        this.router = router
        this.router?.addListener(navListener)
    }

    fun setOpenStyle() {
        this.accountIcon.imageTintList =
            resources.getColorStateList(R.color.menu_item_state_icon, null)
        this.iconSearch.imageTintList =
            resources.getColorStateList(R.color.menu_item_state_icon, null)
        this.iconHome.imageTintList =
            resources.getColorStateList(R.color.menu_item_state_icon, null)
        this.iconFavorites.imageTintList =
            resources.getColorStateList(R.color.menu_item_state_icon, null)
//        this.iconSettings.imageTintList =
//            resources.getColorStateList(R.color.menu_item_state_icon_selected, null)
        currentSelected.imageTintList =
            resources.getColorStateList(R.color.menu_item_state_icon, null)
    }

    fun setCloseStyle() {
        this.accountIcon.imageTintList =
            resources.getColorStateList(R.color.menu_item_state_icon, null)
        this.iconSearch.imageTintList =
            resources.getColorStateList(R.color.menu_item_state_icon, null)
        this.iconHome.imageTintList =
            resources.getColorStateList(R.color.menu_item_state_icon, null)
        this.iconFavorites.imageTintList =
            resources.getColorStateList(R.color.menu_item_state_icon, null)
//        this.iconSettings.imageTintList =
//            resources.getColorStateList(R.color.menu_item_state_icon, null)
        currentSelected.imageTintList =
            resources.getColorStateList(R.color.menu_item_state_icon_selected, null)
    }

    override fun onFocusChanged(gainFocus: Boolean, direction: Int, previouslyFocusedRect: Rect?) {
        Timber.e(" onFocusChangeListener $gainFocus")
        super.onFocusChanged(gainFocus, direction, previouslyFocusedRect)
    }


}