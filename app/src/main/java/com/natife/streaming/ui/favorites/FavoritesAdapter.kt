package com.natife.streaming.ui.favorites

import android.view.LayoutInflater
import android.view.ViewGroup
import com.natife.streaming.R
import com.natife.streaming.base.BaseAdapter
import com.natife.streaming.base.BaseViewHolder
import com.natife.streaming.data.search.SearchResult


class FavoritesAdapter(private inline val onClick: ((SearchResult)->Unit)): BaseAdapter<FavoritesAdapter.Favorite>() {
    interface Favorite


    data class Header(val text : String): Favorite

    companion object{
        const val HEADER = 1
        const val ITEM = 2
    }

    override fun getItemViewType(position: Int): Int {
        return when(list[position]){
            is Header -> HEADER
            else -> ITEM
        }
    }

    override fun submitList(list: List<Favorite>) {
        super.submitList(list)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BaseViewHolder<FavoritesAdapter.Favorite> {
        return when(viewType){
            HEADER -> HeaderViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_header,parent,false))
            else -> FavoritesViewHolder(onClick,LayoutInflater.from(parent.context).inflate(R.layout.item_favorite,parent,false))
        }
    }

}