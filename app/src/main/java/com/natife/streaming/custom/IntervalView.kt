package com.natife.streaming.custom

import android.content.Context
import android.graphics.Color
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.children
import androidx.core.widget.addTextChangedListener
import androidx.core.widget.doAfterTextChanged
import androidx.core.widget.doOnTextChanged
import com.natife.streaming.R
import com.natife.streaming.preferenses.MatchSettingsPrefs
import kotlinx.android.synthetic.main.view_interval.view.*
import org.koin.core.KoinComponent
import timber.log.Timber

class IntervalView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr), KoinComponent {

    private var currentView: TextView
    var onBeforeValueChanged:((Int)->Unit)? = null
    var onAfterValueChanged:((Int)->Unit)? = null
    val afterTextChanged1 = object :TextWatcher{
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        override fun afterTextChanged(s: Editable?) {
            onBeforeValueChanged?.invoke(s.toString().toInt())
        }

    }
    val afterTextChanged2 = object :TextWatcher{
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        override fun afterTextChanged(s: Editable?) {
            onAfterValueChanged?.invoke(s.toString().toInt())
        }

    }

    init {
        val view = LayoutInflater.from(context).inflate(R.layout.view_interval,this,false)
        view.layoutParams = LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT)
        this.addView(view)
        currentView = secBefore
        select(secBefore)
        secBefore.setOnClickListener { select(secBefore) }
        secAfter.setOnClickListener { select(secAfter) }
        secBefore.addTextChangedListener(afterTextChanged1)
        secAfter.addTextChangedListener(afterTextChanged2)
        keyboard.children.forEach {
            it.setOnClickListener {
                if(it is TextView){
                    currentView.text =  if (currentView.text.trim().toString().toInt() != 0){
                        currentView.text.toString()+(it as TextView).text.toString()
                    }else{
                        (it as TextView).text.toString()
                    }
                }
                if (it is ImageView){
                    currentView.text =if (currentView.text.length>1){
                        currentView.text.subSequence(0,currentView.text.length-1)
                    }else{
                        "0"
                    }
                }
            }

        }


    }

    fun select(view: TextView){
        Timber.e("select ${resources.getResourceName(view.id)}")
        currentView =view
        if(view.id == secBefore.id){
            secBefore.setBackgroundResource(R.drawable.oval_white)
            secBefore.setTextColor(Color.BLACK)
            secAfter.setBackgroundResource(R.drawable.oval_gray)
            secAfter.setTextColor(Color.WHITE)
        }else{
            secBefore.setBackgroundResource(R.drawable.oval_gray)
            secBefore.setTextColor(Color.WHITE)
            secAfter.setBackgroundResource(R.drawable.oval_white)
            secAfter.setTextColor(Color.BLACK)
        }
    }
    fun setInterval(before: Int, after:Int){
        setBefore(before)
        setAfter(after)
    }
    fun setBefore(before: Int){
        secBefore.removeTextChangedListener(afterTextChanged1)
        secBefore.text = before.toString()
        secBefore.addTextChangedListener(afterTextChanged1)
    }
    fun setAfter( after:Int){
        secAfter.removeTextChangedListener(afterTextChanged2)
        secAfter.text = after.toString()
        secAfter.addTextChangedListener(afterTextChanged2)
    }
}
