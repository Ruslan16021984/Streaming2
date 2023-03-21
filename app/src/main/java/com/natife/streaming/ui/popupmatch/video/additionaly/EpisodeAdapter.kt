package com.natife.streaming.ui.popupmatch.video.additionaly

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import com.natife.streaming.R
import com.natife.streaming.base.BaseListAdapter
import com.natife.streaming.data.matchprofile.Episode
import com.natife.streaming.ui.popupmatch.PopupSharedViewModel
import kotlinx.android.synthetic.main.item_episode.view.*

class EpisodeAdapter(val popupSharedViewModel: PopupSharedViewModel? = null) :
    BaseListAdapter<Episode, EpisodeViewHolder>(EpisodeDiffUtil()) {
    var onClick: ((Episode) -> Unit)? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EpisodeViewHolder {
        return EpisodeViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_episode, parent, false)
        )
    }

    override fun onBindViewHolder(holder: EpisodeViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)
        holder.itemView.setOnClickListener {
            onClick?.invoke(currentList[position])
        }
        if (holder.layoutPosition == 0) {
            popupSharedViewModel?.startViewID?.value?.let {
                val array = it
                array[1] = holder.itemView.id
                popupSharedViewModel.setStartId(array)
            }
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onViewAttachedToWindow(holder: EpisodeViewHolder) {
        super.onViewAttachedToWindow(holder)
        holder.itemView.onFocusChangeListener = View.OnFocusChangeListener { view, hasFocus ->
//            if (hasFocus) view.additional_video.requestFocus() else view.additional_video.clearFocus()
            view.additional_video.background = if (hasFocus) {
                view.context.resources.getDrawable(R.drawable.item_background_wite, null)
            } else {
                view.context.resources.getDrawable(R.drawable.item_background_gray, null)
            }
        }
    }
}

class EpisodeDiffUtil : DiffUtil.ItemCallback<Episode>() {

    override fun areItemsTheSame(oldItem: Episode, newItem: Episode): Boolean {
        return false
    }

    override fun areContentsTheSame(oldItem: Episode, newItem: Episode): Boolean {
        return false
    }

}