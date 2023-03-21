package com.natife.streaming.ui.favorites

import android.view.View
import com.natife.streaming.base.BaseViewHolder
import com.natife.streaming.base.BaseViewModel
import com.natife.streaming.data.search.SearchResult
import com.natife.streaming.ext.urlCircled
import kotlinx.android.synthetic.main.item_search.view.*


class FavoritesViewHolder(private inline val onClick: ((SearchResult)->Unit),view: View): BaseViewHolder<FavoritesAdapter.Favorite>(view) {
    override fun onBind(data: FavoritesAdapter.Favorite) {
        (data as SearchResult).let {result->
            itemView.icon.urlCircled(data.image,data.placeholder)
            itemView.name.text = data.name
            itemView.setOnClickListener {
                onClick.invoke(result)
            }
        }
    }
}