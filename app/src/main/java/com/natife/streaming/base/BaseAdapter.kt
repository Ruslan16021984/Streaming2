package com.natife.streaming.base

import androidx.annotation.CallSuper
import androidx.recyclerview.widget.RecyclerView

abstract class BaseAdapter<D> : RecyclerView.Adapter<BaseViewHolder<D>>() {

    val list: ArrayList<D> = ArrayList()

    override fun getItemCount() = list.size

    override fun onBindViewHolder(holder: BaseViewHolder<D>, position: Int) {
        holder.onBind(list[position])
    }

    override fun onBindViewHolder(holder: BaseViewHolder<D>, position: Int, payloads: MutableList<Any>) {
        if (payloads.isEmpty()) {
            super.onBindViewHolder(holder, position, payloads)
        } else {
            holder.onBind(list[position], payloads)
        }
    }

    override fun onViewRecycled(holder: BaseViewHolder<D>) {
        holder.onRecycled()
    }

    fun getItem(position: Int): D {
        return list[position]
    }

    @CallSuper
    open fun submitList(list: List<D>) {
        this.list.clear()
        this.list.addAll(list)
        notifyDataSetChanged()
    }

}