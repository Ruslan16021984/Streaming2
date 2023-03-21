package com.natife.streaming.custom

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.util.AttributeSet
import android.widget.Button
import android.widget.LinearLayout
import com.natife.streaming.R
import com.natife.streaming.data.matchprofile.Episode
import com.natife.streaming.data.player.WatchType
import com.natife.streaming.data.player.valueFromIndex
import com.natife.streaming.ext.dp
import com.natife.streaming.ui.live.LiveViewModel
import com.natife.streaming.ui.player.PlayerViewModel
import kotlinx.android.synthetic.main.custom_playback_control.view.*


class BottomButtonLine @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {
    private val startIndexId = 500
    private lateinit var viewModel: PlayerViewModel

    var textColorStateList = ColorStateList(
        arrayOf(intArrayOf(android.R.attr.state_focused), intArrayOf()), intArrayOf(
            context.resources.getColor(R.color.gray_300, null),
            context.resources.getColor(R.color.white, null)
        )
    )

    init {
        inflate(context, R.layout.view_bottom_button_line, this)

    }

    fun initButtons(data: Map<String, List<Episode>>, viewModel: PlayerViewModel) {
        this.viewModel = viewModel
        bottom_button_line_layout.removeAllViews()
        data.keys.forEachIndexed { index, button ->
            if (data[button]?.isEmpty() == false) {
                val lp = LayoutParams(
                    LayoutParams.WRAP_CONTENT,
                    LayoutParams.WRAP_CONTENT
                ).apply {
                    setMargins(0, 0, 10.dp, 0)
                }
                bottom_button_line_layout.addView(
                    data[button]?.let {
                        initButton(
                            _id = startIndexId + index,
                            buttonText = button,
                            list = it,
                            videoType = valueFromIndex(index)
                        )
                    }, lp
                )
            }
        }
    }

    fun initButtonLive(viewModel: LiveViewModel) {
        bottom_button_line_layout.removeAllViews()
        val lp = LayoutParams(
            LayoutParams.WRAP_CONTENT,
            LayoutParams.WRAP_CONTENT
        ).apply {
            setMargins(0, 0, 10.dp, 0)
        }
        bottom_button_line_layout.addView(
            Button(context).apply {
                text = resources.getText(R.string.returnToLive)
                id = startIndexId
                setTextColor(textColorStateList)
                background =
                    resources.getDrawable(R.drawable.background_button_fild_wite_grey_bigradius, null)
                isFocusable = true
                setPadding(10.dp, 0, 10.dp, 0)
                setOnClickListener {
                    viewModel.returnToLive()
                }
            }, lp
        )
    }


    @SuppressLint("UseCompatLoadingForDrawables")
    private fun initButton(_id: Int, buttonText: String, list: List<Episode>, videoType: WatchType): Button {
        return Button(context).apply {
            text = buttonText
            id = _id
            setTextColor(textColorStateList)
            background =
                resources.getDrawable(R.drawable.background_button_fild_wite_grey_bigradius, null)
            isFocusable = true
            setPadding(10.dp, 0, 10.dp, 0)
            setOnClickListener {
                viewModel.updatePlayList(list, videoType)
            }
        }
    }
}