package com.github.boybeak.selfslideapp

import android.animation.Animator
import android.animation.LayoutTransition
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Rect
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import kotlin.math.max

class AnimatedLinearLayout : LinearLayout {

    companion object {
        private const val TAG = "AnimatedLinearLayout"
    }

    //    private var currentRunningTargetList = mutableListOf<View>()
    private var appearingTarget: View? = null
    private var runningTarget: View? = null

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int, defStyleRes: Int) : super(
        context,
        attrs,
        defStyleAttr,
        defStyleRes
    )

    init {
        val transition = LayoutTransition()
        val appearAnim = ObjectAnimator.ofFloat(null, "translationX", -1f, 0f).setDuration(
            transition.getDuration(
                LayoutTransition.APPEARING
            )
        ).apply {
            setEvaluator { fraction, startValue, endValue ->
                val target = appearingTarget
                val value = target?.let { view ->
                    -view.width * (1 - fraction)
                }?: 0f
                value.apply {
//                    Log.d(TAG, "evaluator return $this")
                }
            }
        }
        transition.setAnimator(LayoutTransition.APPEARING, appearAnim)

        val itemAnimatorListener = object : ItemAnimatorListener() {
            override fun getTarget(): View? {
                return runningTarget
            }
        }

        // 修改后的disappearAnim
        val disappearAnim = ObjectAnimator.ofFloat(null, "translationX", 0f, -1f).setDuration(
            transition.getDuration(
                LayoutTransition.DISAPPEARING
            )
        ).apply {
            addListener(itemAnimatorListener)
            addUpdateListener(itemAnimatorListener)
            // 添加自定义估值器
            setEvaluator { fraction, startValue, endValue ->
                val target = itemAnimatorListener.getTarget()
                target?.let { view ->
                    (-view.width * fraction).apply {
                        Log.d(TAG, "evaluator return $this")
                    }
                } ?: 0f
            }
        }

        transition.setAnimator(LayoutTransition.DISAPPEARING, disappearAnim)
        layoutTransition = transition
    }

    override fun addView(child: View?, index: Int) {
        appearingTarget = child
        super.addView(child, index)
        Log.d(TAG, "addView view=${child?.measuredWidth}")
    }

    override fun removeView(view: View?) {
        view ?: return
//        currentRunningTargetList.add(view)
        runningTarget = view
        super.removeView(view)
    }

    private abstract class ItemAnimatorListener : Animator.AnimatorListener, ValueAnimator.AnimatorUpdateListener {

        private var target: View? = null
        private val clipBounds = Rect()

        override fun onAnimationStart(animation: Animator) {
            target = getTarget()
        }

        override fun onAnimationEnd(animation: Animator) {
            animation.removeListener(this)
        }

        override fun onAnimationCancel(animation: Animator) {
            animation.removeListener(this)
        }

        override fun onAnimationRepeat(animation: Animator) {
        }

        override fun onAnimationUpdate(animation: ValueAnimator) {
            target?.let { view  ->
                val dx = animation.animatedValue as Float
                val left = max(0F, -dx) // 计算裁剪左边界
                clipBounds.set(left.toInt(), 0, view.width, view.height)
                view.clipBounds = clipBounds
            }
        }

        abstract fun getTarget(): View?

    }

}