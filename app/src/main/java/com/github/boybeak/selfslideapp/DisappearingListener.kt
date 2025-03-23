package com.github.boybeak.selfslideapp

import android.animation.Animator
import android.animation.LayoutTransition.APPEARING
import android.animation.TypeEvaluator
import android.animation.ValueAnimator
import android.graphics.Rect
import android.view.View
import com.github.boybeak.selfslideapp.SelfSlide.Companion.BOTTOM
import com.github.boybeak.selfslideapp.SelfSlide.Companion.END
import com.github.boybeak.selfslideapp.SelfSlide.Companion.TOP
import java.lang.ref.WeakReference
import kotlin.math.max
import kotlin.math.min

class DisappearingListener(
    private val type: Int,
    private val dir: Int,
    private val onGetTarget: (animator: Animator) -> Any?
) :
    Animator.AnimatorListener, ValueAnimator.AnimatorUpdateListener, TypeEvaluator<Any> {

    private var targetView: WeakReference<View>? = null
    private val clipBounds = Rect()

    override fun onAnimationStart(animation: Animator) {
        val target = onGetTarget.invoke(animation) ?: return
        targetView = WeakReference(target as View)
    }

    override fun onAnimationEnd(animation: Animator) {
    }

    override fun onAnimationCancel(animation: Animator) {
    }

    override fun onAnimationRepeat(animation: Animator) {
    }

    override fun evaluate(fraction: Float, startValue: Any?, endValue: Any?): Any {
        val target = targetView?.get() ?: return 0
        val size = when (dir) {
            TOP, BOTTOM -> target.height
            else -> target.width
        }

        val endValue = endValue as Float
        val startValue = startValue as Float
        val valueDistance = endValue - startValue
        val deltaValue = valueDistance * fraction
        val currentValue = startValue + deltaValue

        val value = size * currentValue
        return value
    }

    override fun onAnimationUpdate(animation: ValueAnimator) {
        targetView?.get()?.let { view ->
            val dx = animation.animatedValue as Float
            when (dir) {
                TOP -> {
                    val top = max(0F, -dx) // 计算裁剪上边界
                    clipBounds.set(0, top.toInt(), view.width, view.height)
                }

                BOTTOM -> {
                    val bottom = min(view.height - dx, view.height.toFloat()) // 计算裁剪下边界
                    clipBounds.set(0, 0, view.width, bottom.toInt())
                }

                END -> {
                    val right = min(view.width - dx, view.width.toFloat()) // 计算裁剪右边界
                    clipBounds.set(0, 0, right.toInt(), view.height)
                }

                else -> {
                    val left = max(0F, -dx) // 计算裁剪左边界
                    clipBounds.set(left.toInt(), 0, view.width, view.height)
                }
            }
            view.clipBounds = clipBounds
        }
    }

}