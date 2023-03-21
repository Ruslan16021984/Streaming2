package com.natife.streaming.ext

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import androidx.constraintlayout.widget.ConstraintLayout

fun ConstraintLayout.predominantColorToGradient(color:String) {
    val drawable = GradientDrawable(
        GradientDrawable.Orientation.BL_TR,
        intArrayOf(
            Color.parseColor("#4D000000"),
            Color.parseColor("#99000000"),
            Color.parseColor(color),

            )
    )
    drawable.shape = GradientDrawable.RECTANGLE
    drawable.gradientType = GradientDrawable.LINEAR_GRADIENT
    this.background = drawable
}