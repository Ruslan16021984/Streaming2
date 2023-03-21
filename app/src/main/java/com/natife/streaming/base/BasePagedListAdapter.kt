package com.natife.streaming.base


import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter

abstract class BasePagedListAdapter<D : Any, V : BaseViewHolder<D>>(diff: DiffUtil.ItemCallback<D>) : PagingDataAdapter<D, V>(diff) {

    override fun onBindViewHolder(holder: V, position: Int) {
        getItem(position)?.let { holder.onBind(it) }
    }

    override fun onBindViewHolder(holder: V, position: Int, payloads: MutableList<Any>) {
        if (payloads.isEmpty()) {
            super.onBindViewHolder(holder, position, payloads)
        } else {
            getItem(position)?.let { holder.onBind(it, payloads) }
        }
    }

    override fun onViewRecycled(holder: V) {
        holder.onRecycled()
    }
}