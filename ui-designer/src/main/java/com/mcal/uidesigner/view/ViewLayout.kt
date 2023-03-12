package com.mcal.uidesigner.view

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.widget.LinearLayoutCompat

class ViewLayout : LinearLayoutCompat {
    private lateinit var mContext: Context

    constructor(context: Context) : super(context) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init(context)
    }

    private fun init(context: Context) {
        mContext = context
        orientation = VERTICAL
    }

    fun setViewClass(clazz: CharSequence) {
        try {
            addView(
                Class.forName(clazz.toString()).getConstructor(
                    Context::class.java
                ).newInstance(mContext) as View
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}