package com.natife.streaming.ui.register

import android.os.Bundle
import android.util.Patterns
import android.view.View
import androidx.core.widget.doOnTextChanged
import com.google.android.material.textfield.TextInputLayout
import com.natife.streaming.R
import com.natife.streaming.base.BaseFragment
import com.natife.streaming.ext.dp
import com.natife.streaming.ext.hideKeyboard
import com.natife.streaming.ext.subscribe
import com.natife.streaming.utils.findLexicText
import kotlinx.android.synthetic.main.fragment_register_new.*


class RegisterFragment : BaseFragment<RegisterViewModel>() {
    override fun getLayoutRes() = R.layout.fragment_register_new

    private var enterLoginHint = ""
    private var enterPasswordHint = ""
    private var errorLoginOrPassword = ""

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getStrings()

        subscribe(viewModel.strings) { listLexics ->
            val registrationText = listLexics.findLexicText(context, R.integer.registration)
            val registerNow = listLexics.findLexicText(context, R.integer.register_now)
            val enterLogin = listLexics.findLexicText(context, R.integer.enter_login)
            val enterPassword = listLexics.findLexicText(context, R.integer.enter_password)
            val wrongLoginOrPassword =
                listLexics.findLexicText(context, R.integer.wrong_login_or_password)

            enterLoginHint = enterLogin ?: resources.getString(R.string.enter_login)
            enterPasswordHint = enterPassword ?: resources.getString(R.string.enter_password)
            errorLoginOrPassword =
                wrongLoginOrPassword ?: resources.getString(R.string.wrong_login_or_password)

            tvEntrance.text = registrationText ?: resources.getString(R.string.check_in)
            registerButton.text = registerNow ?: resources.getString(R.string.register_now)
            loginTextField?.editText?.hint = enterLoginHint
            passwordTextField?.editText?.hint = enterPasswordHint
        }

        loginTextField.editText?.requestFocus()

        loginTextField.editText?.doOnTextChanged { text, start, _, count ->
            if (start + count > 0) text_input_error.text = null
            with(loginTextField) {
//                setEndIconDrawable(R.drawable.ic_done)
//                setEndIconTintList(
//                    android.content.res.ColorStateList.valueOf(
//                        requireActivity().getColor(
//                            R.color.hint_text_field_color
//                        )
//                    )
//                )
                endIconMode = TextInputLayout.END_ICON_CUSTOM
                if (text != null) {
                    isEndIconVisible =
                        text.isNotEmpty() && text.matches(Patterns.EMAIL_ADDRESS.toRegex())
                }
                isErrorEnabled = false
            }
            val isValidEmail = !text?.trim().isNullOrEmpty()
                    && text?.matches(Patterns.EMAIL_ADDRESS.toRegex()) ?: false
            if (!isValidEmail) {
                text_input_error.text = errorLoginOrPassword
            }
            val isValidPassword = !passwordTextField.editText?.text?.trim().isNullOrEmpty()
//            if (!isValidPassword) {
//                text_input_error.text = errorLoginOrPassword
//            }
            registerButton.isEnabled = isValidEmail && isValidPassword
        }

        loginTextField.editText?.onFocusChangeListener =
            View.OnFocusChangeListener { editText, hasFocus ->
                if (hasFocus) {
//                    editText.showKeyboard()
                    with(loginTextField.layoutParams) {
                        width = 346.dp
                        loginTextField.layoutParams = this
                    }
                } else {
                    editText.hideKeyboard()
                    with(loginTextField.layoutParams) {
                        width = 305.dp
                        loginTextField.layoutParams = this
                    }
                }
            }

        passwordTextField.editText?.doOnTextChanged { text, start, _, count ->
            if (start + count > 0) text_input_error.text = null

            with(passwordTextField) {
                isErrorEnabled = false
            }
            registerButton.isEnabled = !text?.trim().isNullOrEmpty()
                    && !loginTextField.editText?.text?.trim().isNullOrEmpty()
                    && loginTextField.editText?.text?.matches(Patterns.EMAIL_ADDRESS.toRegex()) ?: false
        }

        // Если к компоненту перешёл фокус
        passwordTextField.editText?.onFocusChangeListener =
            View.OnFocusChangeListener { editText, hasFocus ->
                if (hasFocus) {
//                    editText.showKeyboard()
                    with(passwordTextField.layoutParams) {
                        width = 346.dp
                        passwordTextField.layoutParams = this
                    }
                } else {
                    editText.hideKeyboard()
                    with(passwordTextField.layoutParams) {
                        width = 305.dp
                        passwordTextField.layoutParams = this
                    }
                }
            }

        registerButton.setOnClickListener {
            viewModel.userRegistration(
                lang = resources.getString(R.string.lang),
                email = loginTextField.editText?.text?.trim().toString(),
                password = passwordTextField.editText?.text?.trim().toString(),
                onError = {
                    with(text_input_error) {
                        text = it
                    }
                    registerButton.isEnabled = false
                    passwordTextField.requestFocus()
                })
        }
    }
}