package com.natife.streaming.base.impl

import android.view.LayoutInflater
import android.view.ViewGroup
import com.natife.streaming.R
import com.natife.streaming.base.BaseAdapter
import com.natife.streaming.base.BaseViewHolder

class VariantAdapter : BaseAdapter<String>() {

    var onSelect:((Int)->Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<String> {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_dialog, parent, false)
        return VariantViewHolder(view)
    }

    override fun onBindViewHolder(holder: BaseViewHolder<String>, position: Int) {

        holder.itemView.setOnClickListener {
            onSelect?.invoke(position)
        }
        super.onBindViewHolder(holder, position)
    }
}