package com.natife.streaming.ui.billing

import android.annotation.SuppressLint
import android.view.View
import com.natife.streaming.base.BaseViewHolder
import kotlinx.android.synthetic.main.item_billing_offers.view.*

class BillingViewHolder(view: View) : BaseViewHolder<OfferData>(view) {
    @SuppressLint("SetTextI18n")
    override fun onBind(data: OfferData) {
        itemView.offer_title.text = data.offerTitle
        itemView.team_league_name.text = data.teamLeagueName
        itemView.billing_offer_description.text = data.description
        itemView.billing_offer_curency.text = data.billingOfferCurrency
        itemView.billing_offer_price.text = data.billingOfferPrice.toString()
    }
}