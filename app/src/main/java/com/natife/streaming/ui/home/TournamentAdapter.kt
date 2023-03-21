package com.natife.streaming.ui.home

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.leanback.widget.BaseGridView
import androidx.leanback.widget.HorizontalGridView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.natife.streaming.data.match.Match
import com.natife.streaming.databinding.ItemRegularTournamentBinding

class TournamentAdapter(val viewModel: HomeViewModel) :
    ListAdapter<TournamentItem, TournamentAdapter.TournamentViewHolder>(TournamentDiffUtilCallback()) {
    var onBind: ((Int) -> Unit)? = null

    abstract inner class TournamentViewHolder(open val binding: ViewBinding) :
        RecyclerView.ViewHolder(binding.root) {
        abstract fun bind(_data: TournamentItem)
    }

    override fun getItemViewType(position: Int): Int {
        return when (currentList[position]) {
            is TournamentItem.RegularTournamentItem -> TournamentType.REGULAR.value
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TournamentViewHolder {
        val binding =
            ItemRegularTournamentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return when (viewType) {
            TournamentType.REGULAR.value -> RegularTournamentViewHolder(binding)
            else -> throw IllegalStateException("Non - existing viewHolder type")
        }
    }

    override fun onBindViewHolder(holder: TournamentViewHolder, position: Int) {
        // load the next pages if you have left to scroll through 10 matches
        if (position > itemCount - 4) onBind?.invoke(position)

        when (holder) {
            is RegularTournamentViewHolder -> holder.bind(currentList[position])
        }
    }

    inner class RegularTournamentViewHolder(override val binding: ItemRegularTournamentBinding) :
        TournamentViewHolder(binding) {
        @SuppressLint("RestrictedApi")
        override fun bind(_data: TournamentItem) {
            val data = _data as TournamentItem.RegularTournamentItem

            val matchAdapter = MatchAdapter()
            matchAdapter.submitList(data.match)
            matchAdapter.onClick = {
                viewModel.toMatchProfile(it)
            }
            matchAdapter.onBind = {
                viewModel.loadList()
            }

            binding.titleText.text = data.programName

            binding.tournamentRecycler2.windowAlignment = HorizontalGridView.WINDOW_ALIGN_BOTH_EDGE;
            binding.tournamentRecycler2.isFocusable = false
            binding.tournamentRecycler2.adapter = matchAdapter
//            binding.tournamentRecycler.setNumColumns(4)
            binding.tournamentRecycler2.focusScrollStrategy = BaseGridView.FOCUS_SCROLL_ITEM
            binding.tournamentRecycler2.selectedPosition = 0
        }
    }


    enum class TournamentType(val value: Int) {
        REGULAR(0),
    }
}

class TournamentDiffUtilCallback : DiffUtil.ItemCallback<TournamentItem>() {
    override fun areItemsTheSame(oldItem: TournamentItem, newItem: TournamentItem): Boolean {
        return when {
            oldItem is TournamentItem.RegularTournamentItem && newItem is TournamentItem.RegularTournamentItem -> oldItem.tournamentId == newItem.tournamentId
            else -> false
        }
    }

    override fun areContentsTheSame(oldItem: TournamentItem, newItem: TournamentItem): Boolean {
        return oldItem == newItem
    }
}

sealed class TournamentItem {
    abstract val tournamentId: Int?
    abstract val programName: String
    abstract val match: List<Match>

    data class RegularTournamentItem(
        override val tournamentId: Int? = null,
        override val programName: String = "",
        override val match: List<Match> = listOf()
    ) : TournamentItem()
}