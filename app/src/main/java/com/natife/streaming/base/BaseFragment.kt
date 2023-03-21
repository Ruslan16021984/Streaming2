package com.natife.streaming.base

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.annotation.CallSuper
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import com.natife.streaming.ext.showToast
import com.natife.streaming.ext.subscribe
import com.natife.streaming.router.Router
import org.koin.android.ext.android.getKoin
import org.koin.androidx.viewmodel.ViewModelParameters
import org.koin.androidx.viewmodel.getViewModel
import org.koin.core.parameter.ParametersDefinition
import org.koin.core.parameter.emptyParametersHolder
import java.lang.reflect.ParameterizedType
import kotlin.reflect.KClass

abstract class BaseFragment<VM : BaseViewModel> : Fragment() {

    protected lateinit var baseActivity: BaseActivity<*>

    protected lateinit var viewModel: VM

    abstract fun getLayoutRes(): Int

    protected open val viewModelLifecycleOwner: LifecycleOwner = this


    override fun onAttach(context: Context) {
        super.onAttach(context)
        baseActivity = context as BaseActivity<*>
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = getKoin().getViewModel(
            ViewModelParameters(
                clazz = getViewModelKClass(),
                owner = viewModelLifecycleOwner,
                parameters = getParameters()
            )
        )
    }

//    protected open fun onLoading(isLoading: Boolean) {
//        baseProgressBar.isVisible = isLoading
//    }

    protected open fun onError(throwable: Throwable) {
        throwable.localizedMessage?.let {
            showToast(it)
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        subscribe(viewModel.defaultErrorLiveData) {
            onError(it)
            //  onLoading(false)
        }

        subscribe(viewModel.defaultLoadingLiveData) {
            //  onLoading(it)
        }
    }


    @CallSuper
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        val root = inflater.inflate(getLayoutRes(), container, false)

        return root
    }

    override fun onResume() {
        val imm: InputMethodManager =
            activity?.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        var view = activity?.currentFocus
        if (view == null) {
            view = View(activity)
        }
        imm.hideSoftInputFromWindow(view.windowToken, 0)
        super.onResume()
    }

    @Suppress("UNCHECKED_CAST")
    protected fun getViewModelKClass(): KClass<VM> {
        val actualClass = (javaClass.genericSuperclass as ParameterizedType).actualTypeArguments[0] as Class<VM>
        return actualClass.kotlin
    }

    open fun getParameters(): ParametersDefinition = {
        emptyParametersHolder()
    }
}