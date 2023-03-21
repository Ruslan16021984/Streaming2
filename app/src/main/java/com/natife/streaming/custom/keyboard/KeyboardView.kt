package com.natife.streaming.custom.keyboard

//@Deprecated("Not use")
//class KeyboardView @JvmOverloads constructor(
//    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
//) : FrameLayout(context, attrs, defStyleAttr) {
//
//    private var isSearchEnabled = false
//    private val localResources = getLocalizedResources(context, Locale.getDefault())
//    private val enResources = getLocalizedResources(context, Locale.ENGLISH)
//
//    private var localAlphabet = localResources.getStringArray(R.array.alphabet)
//    private var enAlphabet = enResources.getStringArray(R.array.alphabet)
//    private val numbers = resources.getStringArray(R.array.numbers)
//    private val dp36 = context.resources.getDimension(R.dimen.dp_32).toInt()
//
//    private var isNumeric = false
//    private var isEnglish = true
//    private var isCaps = true
//
//    private var currentFocus: EditText? = null
//
//    init {
//        inflate(context, R.layout.keyboard, this)
//
//        numContainer.isVisible = false
//        btnContainer.isVisible = true
//
//        initButtons(enAlphabet)
//        initNumbers()
//
//        enableSearch(false)
//
//        inputTypeBtn.setOnCenterClicked {
//            toggleInputType()
//        }
//        capsBtn.setOnCenterClicked {
//            toggleCaps()
//        }
//        languageBtn.setOnCenterClicked {
//            toggleLanguage()
//        }
//        backBtn.setOnCenterClicked {
//            currentFocus?.also {
//                it.setText(it.text.toString().dropLast(1))
//            }
//        }
//        whitespaceBtn.setOnCenterClicked {
//            currentFocus?.also {
//                it.setText(it.text.toString() + " ")
//            }
//        }
//        clearBtn.setOnCenterClicked {
//            currentFocus?.also {
//                it.setText("")
//            }
//        }
//    }
//
//    private fun toggleCaps() {
//        isCaps = !isCaps
//        initButtons(
//            if (isEnglish) {
//                enAlphabet
//            } else {
//                localAlphabet
//            }
//        )
//        btnContainer[getSymbolsDefaultIndex()].requestFocus()
//    }
//
//    private fun toggleLanguage() {
//        isEnglish = !isEnglish
//        initButtons(
//            if (isEnglish) {
//                enAlphabet
//            } else {
//                localAlphabet
//            }
//        )
//        btnContainer[getSymbolsDefaultIndex()].requestFocus()
//    }
//
//    private fun initNumbers() {
//        numbers.forEachIndexed { index, str ->
//            numContainer.addView(
//                generateBtn(str, index, COLUMNS_NUMBERS)
//            )
//        }
//    }
//
//    private fun initButtons(alphabet: Array<String>) {
//        btnContainer.removeAllViews()
//        alphabet.forEachIndexed { index, str ->
//            if (isCaps){
//                btnContainer.addView(
//                    generateBtn(str.toUpperCase(), index, COLUMNS_BUTTONS)
//                )
//            }else{
//                btnContainer.addView(
//                    generateBtn(str, index, COLUMNS_BUTTONS)
//                )
//            }
//
//        }
//    }
//
//    private fun toggleInputType() {
//        isNumeric = !isNumeric
//        numContainer.isVisible = isNumeric
//        btnContainer.isVisible = !isNumeric
//        languageBtn.isVisible = !isNumeric
//
//        if (isNumeric) {
//            inputTypeBtn.text = "ABC"
//            numContainer[getNumDefaultIndex()].requestFocus()
//        } else {
//            inputTypeBtn.text = "&123"
//            btnContainer[getSymbolsDefaultIndex()].requestFocus()
//        }
//    }
//
//    private fun generateBtn(str: String, index: Int, columns: Int): TextView {
//        return TextView(context).apply {
//            layoutParams = FlexboxLayout.LayoutParams(dp36, dp36).apply {
//                isWrapBefore = index % columns == 0
//            }
//            text = str
//            textSize = 18f
//            setBackgroundResource(R.drawable.keyboard_btn)
//            isFocusable = true
//            isFocusableInTouchMode = true
//            gravity = Gravity.CENTER
//            setTextColor(
//                ColorStateList(
//                    arrayOf(
//                        intArrayOf(android.R.attr.state_focused),
//                        intArrayOf()
//                    ),
//                    intArrayOf(
//                        context.getColor(R.color.black),
//                        context.getColor(R.color.white)
//                    )
//                )
//            )
//            setOnCenterClicked {
//
//                currentFocus?.also {
//                    it.setText(it.text.toString() + str)
//                }
//            }
//        }
//    }
//
//    private fun getLocalizedResources(context: Context, locale: Locale): Resources {
//        var conf = context.resources.configuration
//        conf = Configuration(conf)
//        conf.setLocale(locale)
//        val localizedContext = context.createConfigurationContext(conf)
//        return localizedContext.resources
//    }
//
//    fun attachInput(vararg input: TextInputLayout) {
//        input.forEach {
//            it.setOnFocusChangeListener { view, hasFocus ->
//                view as TextInputLayout
//                currentFocus = view.editText
//            }
//            it.editText?.isFocusable = false
//            it.editText?.isFocusableInTouchMode = false
//            it.editText?.setOnCenterClicked {
//                if (isNumeric) {
//                    numContainer[getNumDefaultIndex()].requestFocus()
//                } else {
//                    btnContainer[getSymbolsDefaultIndex()].requestFocus()
//                }
//            }
//        }
//    }
//
//    fun attachInput( input: EditText) {
//        //input.forEach {
//            //it.setOnFocusChangeListener { view, hasFocus ->
//                currentFocus = input //(view as EditText)
//
//          //  }
//        input.isFocusable = false
//        input.isFocusableInTouchMode = false
//        input.setOnCenterClicked {
//                if (isNumeric) {
//                    numContainer[getNumDefaultIndex()].requestFocus()
//                } else {
//                    btnContainer[getSymbolsDefaultIndex()].requestFocus()
//                }
//            }
//       // }
//    }
//
//    fun enableSearch(enable: Boolean) {
//        isSearchEnabled = enable
//        if (enable) {
//            whitespaceBtn.updateLayoutParams<LinearLayout.LayoutParams> {
//                weight = 1f
//                width = 0
//            }
//            clearBtn.updateLayoutParams<LinearLayout.LayoutParams> {
//                weight = 1f
//                width = 0
//            }
//
//            searchBtn.isVisible = true
//            searchBtn.updateLayoutParams<LinearLayout.LayoutParams> {
//                weight = 1f
//                width = 0
//            }
//        } else {
//            whitespaceBtn.updateLayoutParams<LinearLayout.LayoutParams> {
//                weight = 0f
//                width = LinearLayout.LayoutParams.WRAP_CONTENT
//            }
//            clearBtn.updateLayoutParams<LinearLayout.LayoutParams> {
//                weight = 0f
//                width = LinearLayout.LayoutParams.WRAP_CONTENT
//            }
//            searchBtn.isVisible = false
//        }
//    }
//
//    private fun getNumDefaultIndex(): Int {
//        return numbers.size / 2
//    }
//
//    private fun getSymbolsDefaultIndex(): Int {
//        val symbols = if (isEnglish) {
//            enAlphabet
//        } else {
//            localAlphabet
//        }
//        return symbols.size / 2
//    }
//
//    companion object {
//        private const val COLUMNS_BUTTONS = 8
//        private const val COLUMNS_NUMBERS = 7
//    }
//}
//
//private fun View.setOnCenterClicked(block: () -> Unit) {
//
//    this.setOnKeyListener { _, keyCode, event ->
//
//        if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER && event.action == KeyEvent.ACTION_DOWN) {
//            Timber.e("click $keyCode")
//            block()
//        }
//        false
//    }
//}