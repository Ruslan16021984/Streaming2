package com.natife.streaming.base

import android.view.View
import androidx.recyclerview.widget.RecyclerView

abstract class BaseViewHolder<T>(view: View) : RecyclerView.ViewHolder(view){

    abstract fun onBind(data: T)

    open fun onBind(data: T, payloads: List<Any>) {

    }

    open fun onRecycled() {

    }
}