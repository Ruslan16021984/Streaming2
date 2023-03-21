package com.natife.streaming.ui.player.menu.quality

import android.view.View
import com.natife.streaming.base.BaseViewHolder
import kotlinx.android.synthetic.main.item_dialog_choose_quality.view.*

class VideoQualityViewHolder(view: View) : BaseViewHolder<String>(view) {
    override fun onBind(data: String) {
        itemView.tvQuality.text = data
    }
}
