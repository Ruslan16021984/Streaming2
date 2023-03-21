package com.natife.streaming.custom

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import com.natife.streaming.R
import kotlinx.android.synthetic.main.view_timed_button.view.*


class TimedButton @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {
    private val view: View =
        LayoutInflater.from(context).inflate(R.layout.view_timed_button, this, false)

    init {
        view.layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
        this.addView(view)
        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.TimedButton,
            0, 0
        ).apply {

            try {
                setText(getString(R.styleable.TimedButton_text) ?: "")
                setTime(getString(R.styleable.TimedButton_time) ?: "")
            } finally {
                recycle()
            }
        }
    }

    fun setText(text: String) {
        timedButtonText.text = text
    }

    fun setTime(text: String) {
        timedButtonTime.text = text
    }
}
