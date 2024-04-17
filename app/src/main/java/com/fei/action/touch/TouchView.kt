package com.fei.action.touch

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.RelativeLayout
import com.common.log.Lg

class TouchView : View {

    constructor(context: Context) : super(context)
    constructor(context: Context, attributeSet: AttributeSet) : super(context,attributeSet)

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        Lg.i("TouchView dispatchTouchEvent action = ${ev!!.action}")
        return super.dispatchTouchEvent(ev)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        Lg.i("TouchView onTouchEvent action = ${event!!.action}")
        return super.onTouchEvent(event)
    }
}