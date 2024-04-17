package com.fei.action.touch

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.RelativeLayout
import com.common.log.Lg

class TouchViewGroup : RelativeLayout {

    constructor(context: Context) : super(context)
    constructor(context: Context, attributeSet: AttributeSet) : super(context,attributeSet)

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        Lg.i("TouchViewGroup dispatchTouchEvent action = ${ev!!.action}")
        return super.dispatchTouchEvent(ev)
    }

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        if(ev!!.action==MotionEvent.ACTION_MOVE) {
            return true
        }
        Lg.i("TouchViewGroup onInterceptTouchEvent action = ${ev!!.action}")
        return super.onInterceptTouchEvent(ev)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        Lg.i("TouchViewGroup onTouchEvent action = ${event!!.action}")
        return super.onTouchEvent(event)
    }
}