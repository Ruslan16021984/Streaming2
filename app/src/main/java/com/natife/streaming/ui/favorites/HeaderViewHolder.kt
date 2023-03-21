package com.natife.streaming.ui.favorites

import android.view.View

import com.natife.streaming.base.BaseViewHolder
import kotlinx.android.synthetic.main.item_header.view.*

class HeaderViewHolder(  view: View) : BaseViewHolder<FavoritesAdapter.Favorite>(view){
    override fun onBind(data: FavoritesAdapter.Favorite) {
        itemView.isFocusable = false
        itemView.headerText.text = (data as FavoritesAdapter.Header).text
    }
}