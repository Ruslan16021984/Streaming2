package com.natife.streaming.ui.account.language

import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.natife.streaming.R
import com.natife.streaming.base.BaseAdapter
import com.natife.streaming.base.BaseViewHolder
import com.natife.streaming.db.entity.LanguageModel
import kotlinx.android.synthetic.main.item_language.view.*

class LanguageSelectionAdapter(private val onClick: (LanguageModel) -> Unit) :
    BaseAdapter<LanguageModel>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BaseViewHolder<LanguageModel> {
        return LanguageViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_language, parent, false)
        )
    }

    override fun onBindViewHolder(holder: BaseViewHolder<LanguageModel>, position: Int) {
        super.onBindViewHolder(holder, position)
        holder.itemView.setOnClickListener {
            onClick.invoke(list[position])
        }
    }

    class LanguageViewHolder(view: View) : BaseViewHolder<LanguageModel>(view) {
        override fun onBind(data: LanguageModel) {
            itemView.tvLang.text = data.language

            itemView.setOnFocusChangeListener { _, hasFocus ->
                if (hasFocus) {
                    itemView.tvLang.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20f)
                } else {
                    itemView.tvLang.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)
                }
            }
        }
    }
}