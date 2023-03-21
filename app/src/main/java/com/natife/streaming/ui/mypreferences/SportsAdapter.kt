package com.natife.streaming.ui.mypreferences

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.natife.streaming.data.dto.sports.SportTranslateDTO
import com.natife.streaming.databinding.ItemKindsOfSportNewBinding

//new
class SportsAdapter(
    private val onKindOfSportClickListener: ((sport: SportTranslateDTO) -> Unit),
    private val onKindOfSportSelectedListener: ((sport: SportTranslateDTO?, viewId: Int?) -> Unit)
) :
    ListAdapter<SportTranslateDTO, SportsAdapter.SportsAdapterViewHolder>(
        SportsAdapterDiffUtilCallback()
    ) {
//    private val first = Event(true)
override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SportsAdapterViewHolder {
    val binding =
        ItemKindsOfSportNewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    return SportsAdapterViewHolder(binding)
}

    override fun onBindViewHolder(holder: SportsAdapterViewHolder, position: Int) {
        holder.bind(currentList[position])
        holder.itemView.requestFocus()
    }

    inner class SportsAdapterViewHolder(
        private val binding: ItemKindsOfSportNewBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(data: SportTranslateDTO) {
            binding.sportNameText.text = data.text
            binding.checkImage.visibility = if (data.isCheck) View.VISIBLE else View.GONE
            itemView.setOnClickListener {
                onKindOfSportClickListener.invoke(data)
            }
            itemView.onFocusChangeListener = View.OnFocusChangeListener { view, hasFocus ->
                if (hasFocus) {
                    onKindOfSportSelectedListener.invoke(data, bindingAdapterPosition)
                } else {
//                    onKindOfSportSelectedListener.invoke(null,null)
                }
            }

        }
    }
}


class SportsAdapterDiffUtilCallback : DiffUtil.ItemCallback<SportTranslateDTO>() {
    override fun areItemsTheSame(oldItem: SportTranslateDTO, newItem: SportTranslateDTO): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(
        oldItem: SportTranslateDTO,
        newItem: SportTranslateDTO
    ): Boolean {
        return oldItem == newItem
    }

}
