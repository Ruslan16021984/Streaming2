package com.natife.streaming.ext

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LiveData
import kotlin.reflect.KClass


fun <T> Fragment.subscribe(liveData: (LiveData<T>)?, onNext: (t: T) -> Unit) {
    liveData?.observe(viewLifecycleOwner, {
        if (it != null) {
            onNext(it)
        }
    })

}

fun <T> Fragment.subscribeNullable(liveData: LiveData<T>, onNext: (t: T?) -> Unit) {
    liveData.observe(viewLifecycleOwner, { onNext(it) })
}

fun <T> AppCompatActivity.subscribe(liveData: (LiveData<T>)?, onNext: (t: T) -> Unit) {
    liveData?.observe(this, {
        if (it != null) {
            onNext(it)
        }
    })
}

fun <T> AppCompatActivity.subscribeNullable(liveData: LiveData<T>, onNext: (t: T?) -> Unit) {
    liveData.observe(this, { onNext(it) })
}

fun <T> DialogFragment.subscribe(liveData: (LiveData<T>)?, onNext: (t: T) -> Unit) {
    liveData?.observe(viewLifecycleOwner, {
        if (it != null) {
            onNext(it)
        }
    })
}

fun <T : AppCompatActivity> AppCompatActivity.startClearActivity(kClass: KClass<T>) {
    finish()
    startActivity(
        Intent(this, kClass.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
    )
}

fun FragmentActivity.showToast(text: CharSequence, duration: Int = Toast.LENGTH_LONG) =
    globalToast(this, text, duration)

fun FragmentActivity.showToast(@StringRes resId: Int, duration: Int = Toast.LENGTH_LONG) =
    globalToast(this, getString(resId), duration)

fun Fragment.showToast(text: CharSequence, duration: Int = Toast.LENGTH_LONG) =
    requireActivity().showToast(text, duration)

fun Fragment.showToast(@StringRes resId: Int, duration: Int = Toast.LENGTH_LONG) =
    requireActivity().showToast(resId, duration)

private fun globalToast(context: Context, text: CharSequence, duration: Int = Toast.LENGTH_LONG) =
    Toast.makeText(context, text, duration).show()

