package com.natife.streaming.ui.account

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.constraintlayout.widget.Group
import androidx.core.view.isVisible
import androidx.navigation.findNavController
import com.natife.streaming.R
import com.natife.streaming.base.BaseFragment
import com.natife.streaming.db.entity.Lang
import com.natife.streaming.ext.isOnBackStack
import com.natife.streaming.ext.predominantColorToGradient
import com.natife.streaming.ext.subscribe
import com.natife.streaming.ui.account.language.LanguageSelectionDialog
import com.natife.streaming.ui.main.MainActivity
import com.natife.streaming.ui.player.PlayerFragment
import com.natife.streaming.utils.findLexicText
import kotlinx.android.synthetic.main.fragment_account_new.*


class AccountFragment : BaseFragment<AccountViewModel>() {
    override fun getLayoutRes() = R.layout.fragment_account_new

    private var confirmationText = ""
    private var messageLogout = ""
    private var yesText = ""
    private var noText = ""
    private var hideScore = ""
    private var showScore = ""
    private var emailText = ""
    private var phoneText = ""
    private var countryText = ""
    private var englishText = ""
    private var russianText = ""

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        subscribe(viewModel.strings) { listLexics ->
            button_subscriptions.text = listLexics.findLexicText(context, R.integer.subscriptions)
            text_subscriptions.text =
                "4 ${listLexics.findLexicText(context, R.integer.subscriptions)}"
            button_pay_story.text = listLexics.findLexicText(context, R.integer.payment_history)
            button_score_visible.text = listLexics.findLexicText(context, R.integer.hide_score)
            button_language.text = listLexics.findLexicText(context, R.integer.language)
            buttonLogout.text = listLexics.findLexicText(context, R.integer.exit)

            confirmationText = listLexics.findLexicText(context, R.integer.confirmation) ?: ""
            messageLogout = listLexics.findLexicText(context, R.integer.are_you_sure_logout) ?: ""
            yesText = listLexics.findLexicText(context, R.integer.yes) ?: ""
            noText = listLexics.findLexicText(context, R.integer.no) ?: ""
            hideScore = listLexics.findLexicText(context, R.integer.hide_score) ?: ""
            showScore = listLexics.findLexicText(context, R.integer.show_score) ?: ""
            emailText = listLexics.findLexicText(context, R.integer.e_mail) ?: ""
            phoneText = listLexics.findLexicText(context, R.integer.phone) ?: ""
            countryText = listLexics.findLexicText(context, R.integer.country) ?: ""
            englishText = listLexics.findLexicText(context, R.integer.english) ?: ""
            russianText = listLexics.findLexicText(context, R.integer.russian) ?: ""
        }
    }

    override fun onStart() {
        super.onStart()

        requireActivity().findViewById<Group>(R.id.main_group).visibility = View.GONE
        requireActivity().findViewById<Group>(R.id.main_background_group).visibility = View.GONE
        requireActivity().findViewById<Group>(R.id.profile_background_group).visibility =
            View.VISIBLE
        requireActivity().findViewById<MotionLayout>(R.id.mainMotion)
            .predominantColorToGradient("#CB312A")

        buttonLogout.setOnClickListener {

            val alert = AlertDialog.Builder(requireContext()).apply {
                setTitle(confirmationText)
                setMessage(messageLogout)

                setPositiveButton(yesText) { _, _ ->
                    viewModel.logout()
                    val mStartActivity = Intent(requireContext(), MainActivity::class.java)
                    requireActivity().finish()
                    requireActivity().startActivity(mStartActivity)
                }

                setNegativeButton(noText) { _, _ ->
                }
                setCancelable(true)
            }.create()
            alert.show()
            val buttonPos = alert.getButton(DialogInterface.BUTTON_POSITIVE)
            buttonPos.setOnFocusChangeListener { v, hasFocus ->
                if (hasFocus) {
                    buttonPos.setBackgroundColor(resources.getColor(R.color.gray_400))
                } else {
                    buttonPos.setBackgroundColor(resources.getColor(R.color.white))
                }
            }
            buttonPos.setTextColor(resources.getColor(R.color.black))
            buttonPos.setBackgroundColor(resources.getColor(R.color.white))
            val buttonNeg = alert.getButton(DialogInterface.BUTTON_NEGATIVE)
            buttonNeg.setOnFocusChangeListener { v, hasFocus ->
                if (hasFocus) {
                    buttonNeg.setBackgroundColor(resources.getColor(R.color.gray_400))
                } else {
                    buttonNeg.setBackgroundColor(resources.getColor(R.color.white))
                }
            }
            buttonNeg.requestFocus()
            buttonNeg.setTextColor(resources.getColor(R.color.black))
            buttonNeg.setBackgroundColor(resources.getColor(R.color.gray_400))
        }

        viewModel.initialization()

        val adapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.languages,
            R.layout.item_spinner_lang,
        )

        button_subscriptions.setOnClickListener { viewModel.toSubscriptions() }
        button_pay_story.setOnClickListener { viewModel.toPayStory() }
        button_language.setOnClickListener {
            LanguageSelectionDialog().show(parentFragmentManager, PlayerFragment.DIALOG_QUALITY)
        }

        button_score_visible.setOnClickListener {
            viewModel.setScore()
            val button =
                requireActivity().findViewById<com.google.android.material.button.MaterialButton>(R.id.score_button)
            if (!viewModel.settings.value!!.showScore) {
                button.isChecked = false
                button.text = showScore
            } else {
                button.isChecked = true
                button.text = hideScore
            }
        }

        subscribeLiveData()

        if (requireActivity().findNavController(R.id.globalNavFragment)
                .isOnBackStack(R.id.payStoryFragment)
        ) {
            button_pay_story.apply {
                this.requestFocus()
                isSelected = true
            }
        } else {
            button_subscriptions.apply {
                this.requestFocus()
                isSelected = true
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun subscribeLiveData() {
        viewModel.profileLiveData.observe(viewLifecycleOwner, { profile ->
            requireActivity().findViewById<TextView>(R.id.profile_name).text =
                "${profile.firstName} ${profile.lastName}"
            text_phone.text = "$phoneText ${profile.phone}"
//            text_country.text =
//                "$countryText ${profile.country?.nameEng ?: ""}"
            email.text = "$emailText ${profile.email}"
        })

        viewModel.settings.observe(viewModelLifecycleOwner, { setting ->
            if (setting.showScore) text_visible_score.text = noText
            else text_visible_score.text = yesText
            if (setting.lang == Lang.EN) {
                tv_language.text = englishText
            } else {
                tv_language.text = russianText
            }
        })
        viewModel.loadersLiveData.observe(viewLifecycleOwner) {
            progressBar_account.isVisible = it
//            profile_layout.isVisible = !it
        }
    }

    override fun onStop() {
        super.onStop()
        requireActivity().findViewById<Group>(R.id.main_group).visibility = View.VISIBLE
        requireActivity().findViewById<Group>(R.id.main_background_group).visibility = View.VISIBLE
        requireActivity().findViewById<MotionLayout>(R.id.mainMotion)
            .predominantColorToGradient("#3560E1")
        requireActivity().findViewById<Group>(R.id.profile_background_group).visibility = View.GONE
    }
}