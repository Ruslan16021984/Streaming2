package com.natife.streaming.ext

import android.content.res.Resources
import android.util.TypedValue

inline val Int.dp: Int get() = (Resources.getSystem().displayMetrics.density * this).toInt()

inline val Int.sp: Float
    get() = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_SP,
        this.toFloat(),
        Resources.getSystem().displayMetrics
    )

inline val Float.sp: Float
    get() = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_SP,
        this,
        Resources.getSystem().displayMetrics
    )