package com.fei.action.ffmpeg

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.SurfaceView

class MySurfaceView : SurfaceView {

    private var mWidth = 0
    private var mHeight = 0

    constructor(context: Context) : super(context)
    constructor(context: Context, attributes: AttributeSet) : super(context, attributes)
    constructor(context: Context, attributes: AttributeSet, defStyleAttr: Int) : super(
        context,
        attributes,
        defStyleAttr
    )

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val width = MeasureSpec.getSize(widthMeasureSpec)
        val height = MeasureSpec.getSize(heightMeasureSpec)
        if (mWidth == 0) {
            setMeasuredDimension(width, height)
            return
        }

        // calculate expected width by ratio
        val expectedWidth = height * mWidth / mHeight

        // if expected width is too big, set max width to expected width
        if (expectedWidth >= width) {
            // to maintain aspect ratio, calculate expected height
            val expectedHeight = width * mHeight / mWidth
            setMeasuredDimension(width, expectedHeight)
            Log.i("tag","e width = $width height = $expectedHeight")
        } else {
            // or the expected width can fit in the parent, set the expected width
            setMeasuredDimension(expectedWidth, height)
            Log.i("tag","e width = $expectedWidth height = $height")
        }

    }

    fun onSizeChange(width: Int, height: Int) {
        if (width < 0 || height < 0) throw IllegalArgumentException("width and height must be positive")
        mWidth = width
        mHeight = height
        requestLayout()
    }
}