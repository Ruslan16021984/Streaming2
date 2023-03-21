package com.natife.streaming.base.impl

import android.content.Context
import android.os.Bundle
import android.view.*
import androidx.recyclerview.widget.LinearLayoutManager
import com.natife.streaming.R
import com.natife.streaming.base.BaseDialog
import com.natife.streaming.base.BaseViewModel
import kotlinx.android.synthetic.main.dialog_variant.*
import timber.log.Timber


open class VariantDialog<VM : BaseViewModel> : BaseDialog<VM>() {
    override fun getLayoutRes() = R.layout.dialog_variant
    protected open val adapter: VariantAdapter by lazy { VariantAdapter() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.Theme_App_Dialog_FullScreen)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog?.window?.setGravity(Gravity.END);

        dialog?.window?.setLayout(440,ViewGroup.LayoutParams.MATCH_PARENT)

        dialogRecycler.layoutManager = LinearLayoutManager(context)
        dialogRecycler.adapter = adapter
    }

}