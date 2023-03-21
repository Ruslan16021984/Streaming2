package com.natife.streaming.ui.tournament

import android.view.View
import androidx.core.view.isVisible
import com.natife.streaming.R
import com.natife.streaming.data.match.Match
import com.natife.streaming.ext.fromResponse
import com.natife.streaming.ext.toDisplayDate
import com.natife.streaming.ui.home.MatchViewHolderNew
import kotlinx.android.synthetic.main.item_match_new.view.*

class TournamentViewHolder(view: View, private val onClick: ((Match) -> Unit)) :
    MatchViewHolderNew(view) {
    override fun onBind(data: Match) {
        super.onBind(data)
        itemView.paid_content_image.isVisible = !data.subscribed
        itemView.setOnClickListener {
            onClick.invoke(data)
        }
        itemView.alert_textView.visibility = View.VISIBLE
        if (data.live) {
            itemView.alert_textView.text = view.resources.getString(R.string.live)
            itemView.alert_textView.background =
                view.resources.getDrawable(R.drawable.alert_background, view.context.theme)
        }
        else {
            itemView.alert_textView.text =
                data.date.fromResponse().toDisplayDate(view.resources.getString(R.string.lang))
            itemView.alert_textView.background =
                view.resources.getDrawable(R.drawable.time_background, view.context.theme)
        }
    }

}