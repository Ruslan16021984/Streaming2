package com.natife.streaming.ui.player.menu.quality

import android.view.LayoutInflater
import android.view.ViewGroup
import com.natife.streaming.R
import com.natife.streaming.base.BaseAdapter
import com.natife.streaming.base.BaseViewHolder
import kotlinx.android.synthetic.main.item_dialog_choose_quality.view.*

class VideoQualityAdapter(private val onQualityClickListener: ((quality: String) -> Unit)) :
    BaseAdapter<String>() {

    private var currentPosition: Int = -1

    fun setCurrentPosition(position: Int) {
        currentPosition = position
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<String> {
        return VideoQualityViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_dialog_choose_quality, parent, false)
        )
    }

    override fun onBindViewHolder(holder: BaseViewHolder<String>, position: Int) {
        super.onBindViewHolder(holder, position)
        holder.itemView.cvQuality.checkedIcon = null

        if (position == currentPosition) {
            holder.itemView.cvQuality.isChecked = true
            holder.itemView.let { view ->
                view.tvQuality.setTextColor(view.resources.getColor(R.color.black, null))
            }
        }

        holder.itemView.setOnClickListener {
            onQualityClickListener.invoke(list[position])
        }
    }
}
