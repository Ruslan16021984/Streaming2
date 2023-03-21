package com.natife.streaming.ui.tournament

import android.view.LayoutInflater
import android.view.ViewGroup
import com.natife.streaming.R
import com.natife.streaming.base.BaseListAdapter
import com.natife.streaming.data.match.Match
import com.natife.streaming.ui.home.MatchDiffUtil

class TournamentAdapter(private val onClick :((Match)->Unit)): BaseListAdapter<Match, TournamentViewHolder>(MatchDiffUtil()) {

    var onBind: ((Int)->Unit)? = null


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TournamentViewHolder {
        return TournamentViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_match_new, parent, false),
            onClick
        )
    }

    override fun onBindViewHolder(holder: TournamentViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)
        if (position> itemCount-20)
            onBind?.invoke(position)
    }
}