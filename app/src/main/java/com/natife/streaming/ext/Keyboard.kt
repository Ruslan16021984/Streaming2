package com.natife.streaming.ext

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment

fun Fragment.hideKeyboard(view: View) {
    val imm =
        this.requireContext().getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(view.windowToken, 0)
}

fun Activity.hideKeyboard() {
    val imm: InputMethodManager =
        this.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    var currentFocus = this.currentFocus
    if (currentFocus == null) {
        currentFocus = View(this)
    }
    imm.hideSoftInputFromWindow(currentFocus.windowToken, 0)
}

fun View.hideKeyboard() {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(windowToken, 0)
}

fun View.showKeyboard() {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
}

//fun Activity.hideKeyboard() {
//    currentFocus?.also { view ->
//        val imm = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager?
//        imm?.hideSoftInputFromWindow(view.windowToken, 0)
//    }
//}