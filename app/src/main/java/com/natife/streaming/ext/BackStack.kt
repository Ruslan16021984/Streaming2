package com.natife.streaming.ext

import androidx.annotation.IdRes
import androidx.navigation.NavController

fun NavController.isOnBackStack(@IdRes id: Int): Boolean = try {
    getBackStackEntry(id); true
} catch (e: Throwable) {
    false
}