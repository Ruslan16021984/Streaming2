package com.natife.streaming.ui.search

import android.util.TypedValue
import android.view.View
import com.natife.streaming.R
import com.natife.streaming.base.BaseViewHolder
import com.natife.streaming.data.search.SearchResult
import com.natife.streaming.ext.bindFlagImage
import com.natife.streaming.ext.bindSportImage
import com.natife.streaming.utils.setSize
import com.natife.streaming.utils.setMarginHorizontal
import kotlinx.android.synthetic.main.item_list_of_tournaments_new.view.*

class SearchViewHolder(view: View): BaseViewHolder<SearchResult>(view) {
    override fun onBind(data: SearchResult) {
//        itemView.icon.urlCircled(data.image,data.placeholder)
//        itemView.name.text = data.name
        itemView.flag_of_command_country_image_l.bindFlagImage(data.countryId)
        itemView.type_of_game_image_l.bindSportImage(data.sport)
        itemView.name_of_comand_text_l.text = data.name
        itemView.setOnFocusChangeListener { _, hasFocus ->
            val margin: Int
            val textSize: Pair<Float, Float>
            val typeOfGameSize: Int
            val flagSize: Pair<Int, Int>
            if (hasFocus) {
                textSize = Pair(20f, 11f)
                margin = itemView.resources.getDimension(R.dimen.dp_6).toInt()
                typeOfGameSize = itemView.resources.getDimension(R.dimen.dp_10).toInt()
                flagSize = Pair(itemView.resources.getDimension(R.dimen.dp_14).toInt(), itemView.resources.getDimension(R.dimen.dp_10).toInt())
            } else {
                textSize = Pair(16f, 9f)
                margin = itemView.resources.getDimension(R.dimen.dp_10).toInt()
                typeOfGameSize = itemView.resources.getDimension(R.dimen.dp_8).toInt()
                flagSize = Pair(itemView.resources.getDimension(R.dimen.dp_12).toInt(), itemView.resources.getDimension(R.dimen.dp_10).toInt())
            }
            itemView.item_tournament.setMarginHorizontal(margin)
            // text
            itemView.name_of_comand_text_l.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize.first)
            itemView.country_name_text_l.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize.second)
            // icons
            itemView.type_of_game_image_l.setSize(typeOfGameSize, typeOfGameSize)
            itemView.flag_of_command_country_image_l.setSize(flagSize.first, flagSize.second)
        }
        itemView.country_name_text_l.text = data.countryName
        itemView.check_image.visibility = View.GONE
    }
}