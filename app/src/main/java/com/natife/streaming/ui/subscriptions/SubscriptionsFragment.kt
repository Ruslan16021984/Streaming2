package com.natife.streaming.ui.subscriptions

import android.os.Bundle
import android.view.View
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.constraintlayout.widget.Group
import androidx.core.content.ContextCompat
import com.google.android.material.button.MaterialButton
import com.natife.streaming.R
import com.natife.streaming.base.BaseFragment
import com.natife.streaming.ext.predominantColorToGradient

class SubscriptionsFragment : BaseFragment<SubscriptionsViewModel>() {
    override fun getLayoutRes() = R.layout.subscriptions_fragment

    private lateinit var football: MaterialButton
    private lateinit var hockey: MaterialButton
    private lateinit var basketball: MaterialButton

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        football = requireActivity().findViewById(R.id.button_subscriptions_football)
        hockey = requireActivity().findViewById(R.id.button_subscriptions_hockey)
        basketball = requireActivity().findViewById(R.id.button_subscriptions_basketball)

        football.requestFocus()
        setActive(SubscriptionsActive.Foodball, true)

        football.setOnFocusChangeListener { v, hasFocus ->
            setActive(SubscriptionsActive.Foodball, hasFocus)
        }
        hockey.setOnFocusChangeListener { v, hasFocus ->
            setActive(SubscriptionsActive.Hockey, hasFocus)
        }
        basketball.setOnFocusChangeListener { v, hasFocus ->
            setActive(SubscriptionsActive.Basketball, hasFocus)
        }

    }

    override fun onStart() {
        super.onStart()

        requireActivity().findViewById<Group>(R.id.main_group).visibility = View.GONE
        requireActivity().findViewById<Group>(R.id.main_background_group).visibility = View.GONE
        requireActivity().findViewById<Group>(R.id.search_background_group).visibility = View.GONE
        requireActivity().findViewById<Group>(R.id.profile_background_group).visibility = View.GONE
        requireActivity().findViewById<com.natife.streaming.custom.SideMenu>(R.id.mainMenu).visibility =
            View.GONE
        requireActivity().findViewById<Group>(R.id.profile_subscriptions_background_group).visibility =
            View.VISIBLE
        requireActivity().findViewById<MaterialButton>(R.id.button_subscriptions_football)
        requireActivity().findViewById<MotionLayout>(R.id.mainMotion)
            .predominantColorToGradient("#CB312A")
    }

    private fun setActive(active: SubscriptionsActive, isFocus: Boolean) {
        football.setTextColor(ContextCompat.getColor(requireContext(), R.color.white40))
        hockey.setTextColor(ContextCompat.getColor(requireContext(), R.color.white40))
        basketball.setTextColor(ContextCompat.getColor(requireContext(), R.color.white40))
        val color = if (isFocus) R.color.white else R.color.white40
        when (active) {
            SubscriptionsActive.Basketball -> {
                basketball.setTextColor(ContextCompat.getColor(requireContext(), color))
            }
            SubscriptionsActive.Foodball -> {
                football.setTextColor(ContextCompat.getColor(requireContext(), color))
            }
            SubscriptionsActive.Hockey -> {
                hockey.setTextColor(ContextCompat.getColor(requireContext(), color))
            }
        }
    }


    override fun onStop() {
        super.onStop()
        requireActivity().findViewById<Group>(R.id.profile_background_group).visibility = View.GONE
        requireActivity().findViewById<Group>(R.id.profile_subscriptions_background_group).visibility =
            View.GONE
        requireActivity().findViewById<Group>(R.id.main_group).visibility = View.VISIBLE
        requireActivity().findViewById<com.natife.streaming.custom.SideMenu>(R.id.mainMenu).visibility =
            View.VISIBLE
        requireActivity().findViewById<Group>(R.id.main_background_group).visibility = View.VISIBLE
        requireActivity().findViewById<MotionLayout>(R.id.mainMotion)
            .predominantColorToGradient("#3560E1")
    }

    sealed class SubscriptionsActive {
        object Foodball : SubscriptionsActive()
        object Basketball : SubscriptionsActive()
        object Hockey : SubscriptionsActive()
    }

}