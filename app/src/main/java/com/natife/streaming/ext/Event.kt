package com.natife.streaming.ext

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer

/**
 * Used as a wrapper for data that is exposed via a LiveData that represents an event.
 */
open class Event<out T>(private val content: T) {

    @Suppress("MemberVisibilityCanBePrivate")
    var hasBeenHandled = false
        private set // Allow external read but not write

    /**
     * Returns the content and prevents its use again.
     */
    fun getContentIfNotHandled(): T? {
        return if (hasBeenHandled) {
            null
        } else {
            hasBeenHandled = true
            content
        }
    }

    /**
     * Returns the content, even if it's already been handled.
     */
    fun peekContent(): T = content
}

/**
 * An [Observer] for [Event]s, simplifying the pattern of checking if the [Event]'s content has
 * already been handled.
 *
 * [onEventUnhandledContent] is *only* called if the [Event]'s contents has not been handled.
 */
class EventObserver<T>(private val onEventUnhandledContent: (T) -> Unit) : Observer<Event<T>> {
    override fun onChanged(event: Event<T>?) {
        event?.getContentIfNotHandled()?.let {
            onEventUnhandledContent(it)
        }
    }
}

fun <T> Fragment.subscribeEvent(liveData: (LiveData<Event<T>>)?, onNext: (t: T) -> Unit) {
    liveData?.observe(viewLifecycleOwner, EventObserver {
        if (it != null) {
            onNext(it)
        }
    })
}

fun <T> FragmentActivity.subscribeEvent(liveData: (LiveData<Event<T>>)?, onNext: (t: T) -> Unit) {
    liveData?.observe(this, EventObserver {
        if (it != null) {
            onNext(it)
        }
    })
}

fun <T> Fragment.subscribeNullableEvent(liveData: LiveData<Event<T>>, onNext: (t: T?) -> Unit) {
    liveData.observe(viewLifecycleOwner, EventObserver { onNext(it) })
}