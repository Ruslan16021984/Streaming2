package com.natife.streaming.ui.billing

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import com.natife.streaming.base.BaseListAdapter
import com.natife.streaming.base.BaseViewHolder
import com.natife.streaming.data.dto.subscription.SubscribePackage
import com.natife.streaming.databinding.ItemBillingSubscriptionTypeBinding

class BillingSubscriptionTypeAdapter(
    val onClick: (BillingType) -> Unit
) : BaseListAdapter<BillingType, BillingSubscriptionTypeAdapter.BillingTypeViewHolderNew>(
    BillingTypeDiffUtil()
) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BillingTypeViewHolderNew {
        return BillingTypeViewHolderNew(
            ItemBillingSubscriptionTypeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: BillingTypeViewHolderNew, position: Int) {
        super.onBindViewHolder(holder, position)
        holder.itemView.setOnClickListener {
            onClick.invoke(currentList[position])
        }
    }

    class BillingTypeViewHolderNew(
        private val binding: ItemBillingSubscriptionTypeBinding
    ) : BaseViewHolder<BillingType>(binding.root) {
        override fun onBind(data: BillingType) {
            with(binding) {
                itemBillingTypeTitle.text = data.lexic
                itemBillingTypeDescriptions1.text = data.lexic2
                itemBillingTypeDescriptions2.text = data.lexic3
            }
        }
    }
}

class BillingTypeDiffUtil : DiffUtil.ItemCallback<BillingType>() {
    override fun areItemsTheSame(oldItem: BillingType, newItem: BillingType): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: BillingType, newItem: BillingType): Boolean {
        return oldItem == newItem
    }
}

data class BillingType(
    val id: Int,
    val lexic: String,
    val lexic2: String,
    val lexic3: String,
    val packages: SubscribePackage,
    val seasonName: String
)