package com.natife.streaming.ui.popupmatch.video.byplayers

import android.annotation.SuppressLint
import android.text.SpannableStringBuilder
import android.view.View
import androidx.core.text.bold
import com.natife.streaming.base.BaseViewHolder
import com.natife.streaming.data.matchprofile.Player
import com.natife.streaming.ext.toDisplayTime
import com.natife.streaming.ext.url
import kotlinx.android.synthetic.main.item_player_new.view.*


class PlayerViewHolder(view: View) : BaseViewHolder<Player>(view) {
    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onBind(data: Player) {
        itemView.player_image.url(data.image, data.imagePlaceholder)
        val span = SpannableStringBuilder()
            .bold { this.append(data.number) }
            .append(" ")
            .append(data.name)
//        itemView.player_name.text = span
        itemView.player_name.text = if (span.length > 35) "${span.take(32)}..." else span
        itemView.player_time.text = data.duration.toDisplayTime()
    }
}