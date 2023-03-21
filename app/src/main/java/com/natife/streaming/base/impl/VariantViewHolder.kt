package com.natife.streaming.base.impl

import android.view.View
import com.natife.streaming.base.BaseViewHolder
import kotlinx.android.synthetic.main.item_dialog.view.*

class VariantViewHolder(view: View): BaseViewHolder<String>(view) {
    override fun onBind(data: String) {
        itemView.variant.text = data
    }
}