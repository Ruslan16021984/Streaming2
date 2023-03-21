package com.natife.streaming.ui.popupmatch.video.additionaly

import android.view.View
import com.natife.streaming.base.BaseViewHolder
import com.natife.streaming.data.matchprofile.Episode
import com.natife.streaming.ext.url
import kotlinx.android.synthetic.main.item_episode.view.*


class EpisodeViewHolder(view: View): BaseViewHolder<Episode>(view) {
    override fun onBind(data: Episode) {
        itemView.matchImage.url(data.image,placeholder = data.placeholder)
        itemView.matchTitle.text = data.title
    }
}