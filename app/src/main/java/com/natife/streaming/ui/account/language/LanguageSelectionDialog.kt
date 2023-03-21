package com.natife.streaming.ui.account.language

import android.content.Intent
import android.os.Bundle
import android.view.View
import com.natife.streaming.R
import com.natife.streaming.base.BaseDialog
import com.natife.streaming.db.entity.Lang
import com.natife.streaming.db.entity.LanguageModel
import com.natife.streaming.ext.subscribe
import com.natife.streaming.ext.subscribeEvent
import com.natife.streaming.ui.main.MainActivity
import com.natife.streaming.utils.findLexicText
import kotlinx.android.synthetic.main.dialog_language_selection.*

class LanguageSelectionDialog : BaseDialog<LanguageSelectionViewModel>() {

    override fun getLayoutRes(): Int = R.layout.dialog_language_selection

    private val languageAdapter by lazy {
        LanguageSelectionAdapter {
            onLanguageClicked(it)
        }
    }

    override fun onStart() {
        super.onStart()

        subscribeEvent(viewModel.restart) {
            if (it) {
                val mStartActivity = Intent(requireContext(), MainActivity::class.java)
                requireActivity().finish()
                requireActivity().startActivity(mStartActivity)
            }
            dismiss()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.Theme_App_Dialog_FullScreen)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        rvLanguages.adapter = languageAdapter

        subscribe(viewModel.languages) { listLexics ->
            tvTitle.text = listLexics.findLexicText(context, R.integer.selection_language)
            val english = listLexics.findLexicText(context, R.integer.english) ?: "English"
            val russian = listLexics.findLexicText(context, R.integer.russian) ?: "Русский"
            val languages = listOf(
                LanguageModel(0, russian, Lang.RU, viewModel.getCurrentLang() == Lang.RU.name),
                LanguageModel(1, english, Lang.EN, viewModel.getCurrentLang() == Lang.EN.name)
            )
            languageAdapter.submitList(languages)
        }
    }

    private fun onLanguageClicked(language: LanguageModel) {
        viewModel.setLang(language)
    }
}