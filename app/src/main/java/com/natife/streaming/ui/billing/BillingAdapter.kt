package com.natife.streaming.ui.billing

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import com.natife.streaming.R
import com.natife.streaming.base.BaseListAdapter

class BillingAdapter(
    private val sharedBillingViewModel: BillingTypeListViewModel,
    private val typeOfBilling: BillingTypeListFragment.Companion.BillingType?
) : BaseListAdapter<OfferData, BillingViewHolder>(BillingDiffUtil()) {
    var onClick: ((OfferData) -> Unit)? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BillingViewHolder {
        return BillingViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_billing_offers, parent, false)
        )
    }

    override fun onBindViewHolder(holder: BillingViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)
        holder.itemView.setOnClickListener {
            onClick?.invoke(currentList[position])
        }
        if (holder.layoutPosition == 0) {
            sharedBillingViewModel.startViewID.value?.let {
                val array = it
                when (typeOfBilling) {
                    BillingTypeListFragment.Companion.BillingType.MONTH -> array[0] =
                        holder.itemView.id
                    BillingTypeListFragment.Companion.BillingType.YEAR -> array[1] =
                        holder.itemView.id
                    BillingTypeListFragment.Companion.BillingType.PAY_PER_VIEW -> array[2] =
                        holder.itemView.id
                }
                sharedBillingViewModel.setStartId(array)
            }
        }
    }
}

class BillingDiffUtil : DiffUtil.ItemCallback<OfferData>() {
    override fun areItemsTheSame(oldItem: OfferData, newItem: OfferData): Boolean {
        return oldItem.matchId == newItem.matchId && oldItem.sportId == newItem.sportId && oldItem.tournamentId == newItem.tournamentId
    }

    override fun areContentsTheSame(oldItem: OfferData, newItem: OfferData): Boolean {
        return oldItem == newItem
    }

}
