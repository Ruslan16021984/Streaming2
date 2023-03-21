package com.natife.streaming.base

import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter

abstract class BaseListAdapter<D, V: BaseViewHolder<D>>(diff: DiffUtil.ItemCallback<D>) : ListAdapter<D, V>(diff) {

    override fun onBindViewHolder(holder: V, position: Int) {
        holder.onBind(getItem(position))
    }

    override fun onBindViewHolder(holder: V, position: Int, payloads: MutableList<Any>) {
        if (payloads.isEmpty()) {
            super.onBindViewHolder(holder, position, payloads)
        } else {
            holder.onBind(getItem(position), payloads)
        }
    }

    override fun onViewRecycled(holder: V) {
        holder.onRecycled()
    }
}