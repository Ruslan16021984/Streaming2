package com.natife.streaming.ui.home

import android.annotation.SuppressLint
import android.view.View
import com.natife.streaming.R
import com.natife.streaming.base.BaseViewHolder
import com.natife.streaming.data.match.Match
import com.natife.streaming.ext.*
import kotlinx.android.synthetic.main.item_match_new.view.*
import java.util.*

open class MatchViewHolderNew(val view: View) : BaseViewHolder<Match>(view) {
    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onBind(data: Match) {
        itemView.match_imageView.url(data.image)
//        itemView.match_imageView.url(data.image, data.placeholder)
        itemView.flag_of_command_country_image_l.bindFlagImage(data.countryId)
        itemView.type_of_game_image_l.bindSportImage(data.sportId)
        when {
            data.live -> {
                itemView.alert_textView.text = view.resources.getString(R.string.live)
                itemView.alert_textView.background =
                    view.resources.getDrawable(R.drawable.alert_background, view.context.theme)
            }
            data.date.fromResponse().after(Date()) -> {
                itemView.alert_textView.text =
                    data.date.fromResponse().toDisplay3(view.resources.getString(R.string.lang))
                itemView.alert_textView.background =
                    view.resources.getDrawable(R.drawable.time_background, view.context.theme)
            }
            else -> {
                itemView.alert_textView.visibility = View.INVISIBLE
            }
        }

        itemView.first_team_textView.text = data.team1.name
        itemView.first_team_score_textView.text =
            if (data.isShowScore) data.team1.score.toString() else ""

        itemView.second_team_textView.text = data.team2.name
        itemView.second_team_score_textView.text =
            if (data.isShowScore) data.team2.score.toString() else ""

        itemView.tournament_name_text.text = data.tournament.name

        itemView.logo_first_team.bindTeamImage(data.sportId, data.team1.id)
        itemView.logo_second_team.bindTeamImage(data.sportId, data.team2.id)
        if (data.subscribed) {
            itemView.paid_content_image.visibility = View.GONE
        } else itemView.paid_content_image.visibility = View.VISIBLE

        itemView.favorite_first_team_imageView.visibility =
            if (data.isFavoriteTeam1) View.VISIBLE else View.GONE
        itemView.favorite_second_team_imageView.visibility =
            if (data.isFavoriteTeam2) View.VISIBLE else View.GONE
        itemView.favorite_imageView.visibility =
            if (data.isFavoriteTournament) View.VISIBLE else View.GONE

        itemView.messageContainer.visibility = View.GONE
    }
}
