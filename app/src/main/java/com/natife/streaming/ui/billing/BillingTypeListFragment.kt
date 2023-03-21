package com.natife.streaming.ui.billing

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.leanback.widget.BaseGridView
import androidx.leanback.widget.VerticalGridView
import androidx.navigation.navGraphViewModels
import com.google.android.material.card.MaterialCardView
import com.natife.streaming.R
import com.natife.streaming.base.BaseFragment
import com.natife.streaming.base.EmptyViewModel
import com.natife.streaming.ext.subscribe
import kotlinx.android.synthetic.main.fragment_billing_type_list.*


class BillingTypeListFragment : BaseFragment<EmptyViewModel>() {
    override fun getLayoutRes(): Int = R.layout.fragment_billing_type_list
    private val sharedBillingViewModel: BillingTypeListViewModel by navGraphViewModels(R.id.billing)
    private var typeOfBilling: BillingType? = null

    @SuppressLint("RestrictedApi")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.let {
            typeOfBilling = when (it.getInt(TYPE_OF_BILLING)) {
                0 -> BillingType.MONTH
                1 -> BillingType.YEAR
                2 -> BillingType.PAY_PER_VIEW
                else -> null
            }
        }

        val adapter = BillingAdapter(sharedBillingViewModel, typeOfBilling)
        billing_type_recycler.windowAlignment = VerticalGridView.WINDOW_ALIGN_BOTH_EDGE
        billing_type_recycler.isFocusable = false
        billing_type_recycler.setNumColumns(1)
        billing_type_recycler.focusScrollStrategy = BaseGridView.FOCUS_SCROLL_ITEM
        billing_type_recycler.adapter = adapter
        adapter.onClick = {
            sharedBillingViewModel.searchResultClicked(it)
        }

        subscribe(sharedBillingViewModel.monthList) {
            if (typeOfBilling == BillingType.MONTH) adapter.submitList(it)
        }

        subscribe(sharedBillingViewModel.yearList) {
            if (typeOfBilling == BillingType.YEAR) adapter.submitList(it)
        }

        subscribe(sharedBillingViewModel.payPerViewList) {
            if (typeOfBilling == BillingType.PAY_PER_VIEW) adapter.submitList(it)
        }

    }

    @SuppressLint("RestrictedApi")
    override fun onResume() {
        super.onResume()
        billing_type_recycler.scrollToPosition(0)
        if (billing_type_recycler.findViewHolderForAdapterPosition(0)?.itemView != null) {
            (billing_type_recycler.findViewHolderForAdapterPosition(0)?.itemView as MaterialCardView).requestFocus()
        }
    }

    override fun onPause() {
        super.onPause()
    }


    companion object {
        private const val TYPE_OF_BILLING = "TYPE_OF_BILLING"

        enum class BillingType {
            MONTH,
            YEAR,
            PAY_PER_VIEW
        }

        @JvmStatic
        fun newInstance(type: Int) = BillingTypeListFragment().apply {
            arguments = Bundle().apply {
                putInt(TYPE_OF_BILLING, type)
            }
        }
    }
}




