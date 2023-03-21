package com.natife.streaming.ui.popupmatch.video.byplayers

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import com.natife.streaming.R
import com.natife.streaming.base.BaseListAdapter
import com.natife.streaming.data.matchprofile.Player
import com.natife.streaming.ui.popupmatch.PopupSharedViewModel

class PlayerAdapter(val popupSharedViewModel: PopupSharedViewModel? = null) :
    BaseListAdapter<Player, PlayerViewHolder>(PlayerDiffCallback()) {
    var onClick: ((Player) -> Unit)? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlayerViewHolder {
        return PlayerViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_player_new, parent, false)
        )
    }

    override fun onBindViewHolder(holder: PlayerViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)
        holder.itemView.setOnClickListener {
            onClick?.invoke(currentList[position])
        }
        if (holder.layoutPosition == 0) {
            popupSharedViewModel?.startViewID?.value?.let {
                val array = it
                array[2] = holder.itemView.id
                popupSharedViewModel.setStartId(array)
            }
        }
    }

    override fun onViewAttachedToWindow(holder: PlayerViewHolder) {
        super.onViewAttachedToWindow(holder)
        holder.itemView.onFocusChangeListener = View.OnFocusChangeListener { view, hasFocus ->
//            (view as MaterialCardView).strokeWidth = if (hasFocus) 5 else 0
        }
    }
}

class PlayerDiffCallback : DiffUtil.ItemCallback<Player>() {
    override fun areItemsTheSame(oldItem: Player, newItem: Player): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Player, newItem: Player): Boolean {
        return oldItem == newItem
    }

}