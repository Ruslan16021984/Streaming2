package com.natife.streaming.ui.billing

import android.os.Bundle
import android.view.View
import androidx.activity.addCallback
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.navigation.navGraphViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import com.natife.streaming.R
import com.natife.streaming.base.BaseFragment
import com.natife.streaming.ext.predominantColorToGradient
import com.natife.streaming.ext.subscribe
import com.natife.streaming.utils.findLexicText
import kotlinx.android.synthetic.main.fragment_billing.*
import kotlinx.android.synthetic.main.fragment_mypreferences_new.topConstraintLayout
import org.koin.core.parameter.ParametersDefinition
import org.koin.core.parameter.parametersOf

class BillingFragment : BaseFragment<BillingViewModel>() {
    override fun getLayoutRes(): Int = R.layout.fragment_billing
    private val sharedBillingViewModel: BillingTypeListViewModel by navGraphViewModels(R.id.billing)
    private lateinit var billingType: Array<String>
    private var page = 0
    private val onPage = object : ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
            super.onPageSelected(position)
            position.apply {
                page = this
                sharedBillingViewModel.startViewID.value?.let { start ->
                    tab_billing_layout.getTabAt(0)?.view?.nextFocusDownId = start[this]
                    tab_billing_layout.getTabAt(1)?.view?.nextFocusDownId = start[this]
                    tab_billing_layout.getTabAt(2)?.view?.nextFocusDownId = start[this]
                }

            }
        }
    }

    private val billingTypeAdapter = BillingSubscriptionTypeAdapter {
        viewModel.selectSubscriptionType(it)

        billing_state_1.isVisible = false
        billing_state_2.isVisible = true
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //Heading in the predominant team color
        topConstraintLayout.predominantColorToGradient("#CB312A")
        billing_state_1_list.adapter = billingTypeAdapter
        billing_state_1.isVisible = true
        billing_state_2.isVisible = false

        billing_state_2_back.setOnClickListener {
            billing_state_1.isVisible = true
            billing_state_2.isVisible = false
        }

        subscribe(viewModel.billingTypes) {
            billingTypeAdapter.submitList(it)
        }

        subscribe(viewModel.title) {
            pay_title.text = it
        }

        subscribe(viewModel.monthList) {
            sharedBillingViewModel.setMonthList(it)
            tab_billing_layout.getTabAt(0)?.view?.requestFocus()
            tab_billing_layout.getTabAt(0)?.select()
        }

        subscribe(viewModel.yearList) {
            sharedBillingViewModel.setYearList(it)
        }

        subscribe(viewModel.payPerViewList) {
            sharedBillingViewModel.setPayPerViewList(it)
        }

        subscribe(sharedBillingViewModel.startViewID) { start ->
            page.apply {
                tab_billing_layout.getTabAt(0)?.view?.nextFocusDownId = start[this]
                tab_billing_layout.getTabAt(1)?.view?.nextFocusDownId = start[this]
                tab_billing_layout.getTabAt(2)?.view?.nextFocusDownId = start[this]
            }
        }

        subscribe(viewModel.strings) {
            billingType = arrayOf(
                it.findLexicText(context, R.integer.month) ?: "",
                it.findLexicText(context, R.integer.year) ?: "",
                it.findLexicText(context, R.integer.pay_per_view) ?: ""
            )
            val billingAdapter = BillingFragmentAdapter(
                childFragmentManager,
                this.lifecycle, billingType.size
            )
            billing_pager.adapter = billingAdapter
            billing_pager.registerOnPageChangeCallback(onPage)

            TabLayoutMediator(tab_billing_layout, billing_pager) { tab, position ->
                tab.text = billingType[position]
            }.attach()
        }

        sharedBillingViewModel.searchResultClicked.observe(viewLifecycleOwner){
            if (it.matchId != 0) {
                viewModel.select(it)
                sharedBillingViewModel.select()
            }
        }


        tab_billing_layout.getChildAt(0).requestFocus()
        tab_billing_layout.getChildAt(0).isSelected = true

        sharedBillingViewModel.resetStartId()

        tab_billing_layout.getTabAt(0)?.view?.requestFocus()
        tab_billing_layout.getTabAt(0)?.select()

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            viewModel.onFinishClicked()
        }
    }

    override fun onStop() {
        super.onStop()
        billing_pager.unregisterOnPageChangeCallback(onPage)
    }

    override fun getParameters(): ParametersDefinition = {
        parametersOf(BillingFragmentArgs.fromBundle(requireArguments()))
    }

    inner class BillingFragmentAdapter(
        fragmentManager: FragmentManager,
        lifecycle: Lifecycle,
        private val itemCount: Int
    ) : FragmentStateAdapter(fragmentManager, lifecycle) {
        override fun getItemCount(): Int {
            return itemCount
        }

        override fun createFragment(position: Int): Fragment {
            return BillingTypeListFragment.newInstance(position)
        }
    }
}