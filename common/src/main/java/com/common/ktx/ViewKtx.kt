package com.common.ktx
import android.animation.Animator
import android.animation.IntEvaluator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContextWrapper
import android.os.Build
import android.os.SystemClock
import android.text.SpannableStringBuilder
import android.text.TextPaint
import android.text.style.ClickableSpan
import android.view.View
import android.view.ViewGroup
import androidx.core.text.inSpans
import com.common.R
import com.common.ktx.ViewClickDelay.SPACE_TIME
import com.common.ktx.ViewClickDelay.hash
import com.common.ktx.ViewClickDelay.lastClickTime

/**
 * FileName: ViewKtx
 * @author: linyh19
 * Date: 2021/11/19 15:23
 * Description: View相关的扩展方法
 */


/*************************************** View可见性相关 ********************************************/
/**
 * 隐藏View
 */
fun View.gone() {
    visibility = View.GONE
}

/**
 * 显示View
 * @receiver View
 */
fun View.visible() {
    visibility = View.VISIBLE
}

/**
 * View不可见但存在原位置
 */
fun View.invisible() {
    visibility = View.INVISIBLE
}

/**
 * 判断View是不是[View.VISIBLE]状态
 */
val View.isVisible: Boolean
    get() {
        return visibility == View.VISIBLE
    }

/**
 * 判断View是不是[View.INVISIBLE]状态
 */
val View.isInvisible: Boolean
    get() {
        return visibility == View.INVISIBLE
    }

/**
 * 判断View是不是[View.GONE]状态
 */
val View.isGone: Boolean
    get() {
        return visibility == View.GONE
    }

/*************************************** View宽高相关 ********************************************/
/**
 * 设置View的高度
 */
fun View.height(height: Int): View {
    val params = layoutParams ?: ViewGroup.LayoutParams(
        ViewGroup.LayoutParams.MATCH_PARENT,
        ViewGroup.LayoutParams.WRAP_CONTENT
    )
    params.height = height
    layoutParams = params
    return this
}

/**
 * 设置View的宽度
 */
fun View.width(width: Int): View {
    val params = layoutParams ?: ViewGroup.LayoutParams(
        ViewGroup.LayoutParams.MATCH_PARENT,
        ViewGroup.LayoutParams.WRAP_CONTENT
    )
    params.width = width
    layoutParams = params
    return this
}

/**
 * 设置View的宽度和高度
 * @param width 要设置的宽度
 * @param height 要设置的高度
 */
fun View.widthAndHeight(width: Int, height: Int): View {
    val params = layoutParams ?: ViewGroup.LayoutParams(
        ViewGroup.LayoutParams.MATCH_PARENT,
        ViewGroup.LayoutParams.WRAP_CONTENT
    )
    params.width = width
    params.height = height
    layoutParams = params
    return this
}

/**
 * 设置宽度，带有过渡动画
 * @param targetValue 目标宽度
 * @param duration 时长
 * @param action 可选行为
 * @return 动画
 */
fun View.animateWidth(
    targetValue: Int, duration: Long = 400, listener: Animator.AnimatorListener? = null,
    action: ((Float) -> Unit)? = null
): ValueAnimator? {
    var animator: ValueAnimator? = null
    post {
        animator = ValueAnimator.ofInt(width, targetValue).apply {
            addUpdateListener {
                width(it.animatedValue as Int)
                action?.invoke((it.animatedFraction))
            }
            if (listener != null) addListener(listener)
            setDuration(duration)
            start()
        }
    }
    return animator
}

/**
 * 设置高度，带有过渡动画
 * @param targetValue 目标高度
 * @param duration 时长
 * @param action 可选行为
 * @return 动画
 */
fun View.animateHeight(
    targetValue: Int,
    duration: Long = 400,
    listener: Animator.AnimatorListener? = null,
    action: ((Float) -> Unit)? = null
): ValueAnimator? {
    var animator: ValueAnimator? = null
    post {
        animator = ValueAnimator.ofInt(height, targetValue).apply {
            addUpdateListener {
                height(it.animatedValue as Int)
                action?.invoke((it.animatedFraction))
            }
            if (listener != null) addListener(listener)
            setDuration(duration)
            start()
        }
    }
    return animator
}

/**
 * 设置宽度和高度，带有过渡动画
 * @param targetWidth 目标宽度
 * @param targetHeight 目标高度
 * @param duration 时长
 * @param action 可选行为
 * @return 动画
 */
fun View.animateWidthAndHeight(
    targetWidth: Int,
    targetHeight: Int,
    duration: Long = 400,
    listener: Animator.AnimatorListener? = null,
    action: ((Float) -> Unit)? = null
): ValueAnimator? {
    var animator: ValueAnimator? = null
    post {
        val startHeight = height
        val evaluator = IntEvaluator()
        animator = ValueAnimator.ofInt(width, targetWidth).apply {
            addUpdateListener {
                widthAndHeight(
                    it.animatedValue as Int,
                    evaluator.evaluate(it.animatedFraction, startHeight, targetHeight)
                )
                action?.invoke((it.animatedFraction))
            }
            if (listener != null) addListener(listener)
            setDuration(duration)
            start()
        }
    }
    return animator
}

/*************************************** View其他 ********************************************/
/**
 * 获取View id，如果没有id：SDK>17, 使用[View.generateViewId]；否则使用[View.hashCode]
 */
@SuppressLint("ObsoleteSdkInt")
fun View.getViewId(): Int {
    var id = id
    if (id == View.NO_ID) {
        id = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            View.generateViewId()
        } else {
            this.hashCode()
        }
    }
    return id
}

object ViewClickDelay {
    var hash: Int = 0
    var lastClickTime: Long = 0
    var SPACE_TIME: Long = 2000  // 间隔时间
}

/**
 * 防快速点击
 * @receiver View
 * @param clickAction 要响应的操作
 */
infix fun View.clickDelay(clickAction: () -> Unit) {
    this.setOnClickListener {
        if (this.hashCode() != hash) {
            hash = this.hashCode()
            lastClickTime = System.currentTimeMillis()
            clickAction()
        } else {
            val currentTime = System.currentTimeMillis()
            if (currentTime - lastClickTime > SPACE_TIME) {
                lastClickTime = System.currentTimeMillis()
                clickAction()
            }
        }
    }
}

const val singleClickInterval: Int = 1000

/**
 * 实现一个 Activity 中所有的 View 共用一个上次单击时间
 * @param isShareSingleClick 是否同一个页面同一时间只能相应一个点击
 */
fun View.onSingleClick(
    interval: Int? = singleClickInterval,
    isShareSingleClick: Boolean? = true,
    listener: View.OnClickListener? = null
) {
    if (listener == null) {
        return
    }

    setOnClickListener {
        determineTriggerSingleClick(
            interval ?: singleClickInterval, isShareSingleClick ?: true, listener
        )
    }
}

/**
 * Determine whether to trigger a single click.
 *
 * @param interval Single click interval.Unit is [TimeUnit.MILLISECONDS].
 * @param isShareSingleClick True if this view is share single click interval whit other view
 *   in same Activity, false otherwise.
 * @param listener Single click listener.
 */
fun View.determineTriggerSingleClick(
    interval: Int = singleClickInterval,
    isShareSingleClick: Boolean = true,
    listener: View.OnClickListener
) {
    val target = if (isShareSingleClick) getActivity(this)?.window?.decorView ?: this else this
    val millis = target.getTag(R.id.single_click_tag_last_single_click_millis) as? Long ?: 0
    if (SystemClock.uptimeMillis() - millis >= interval) {
        target.setTag(R.id.single_click_tag_last_single_click_millis, SystemClock.uptimeMillis())
        listener.onClick(this)
    }
}

private fun getActivity(view: View): Activity? {
    var context = view.context
    while (context is ContextWrapper) {
        if (context is Activity) {
            return context
        }
        context = context.baseContext
    }
    return null
}

/**
* Wrap appended text in [builderAction] in a [ClickableSpan].
* If selected and single clicked, the [listener] will be invoked.
*
* @param listener Single click listener.
* @param interval Single click interval.Unit is [TimeUnit.MILLISECONDS].
* @param isShareSingleClick True if this view is share single click interval whit other view
*   in same Activity, false otherwise.
* @param updateDrawStateAction Update draw state action.
* @see SpannableStringBuilder.inSpans
*/
inline fun SpannableStringBuilder.onSingleClick(
    listener: View.OnClickListener,
    interval: Int = singleClickInterval,
    isShareSingleClick: Boolean = true,
    noinline updateDrawStateAction: ((TextPaint) -> Unit)? = null,
    builderAction: SpannableStringBuilder .() -> Unit
): SpannableStringBuilder = inSpans(
    SingleClickableSpan(
        listener, interval, isShareSingleClick, updateDrawStateAction
    ),
    builderAction = builderAction
)

/**
 * Single clickable span.
 */
class SingleClickableSpan(
    private val listener: View.OnClickListener,
    private val interval: Int = singleClickInterval,
    private val isShareSingleClick: Boolean = true,
    private val updateDrawStateAction: ((TextPaint) -> Unit)? = null
) : ClickableSpan() {

    private var mFakeView: View? = null

    override fun onClick(widget: View) {
        if (isShareSingleClick) {
            widget
        } else {
            if (mFakeView == null) {
                mFakeView = View(widget.context)
            }
            mFakeView!!
        }.determineTriggerSingleClick(interval, isShareSingleClick, listener)
    }

    override fun updateDrawState(ds: TextPaint) {
        updateDrawStateAction?.invoke(ds)
    }
}