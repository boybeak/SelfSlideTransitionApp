package com.github.boybeak.selfslideapp

import android.animation.Animator
import android.animation.LayoutTransition
import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.animation.TimeInterpolator
import android.animation.TypeEvaluator
import android.animation.ValueAnimator
import android.graphics.Rect
import android.os.Build
import android.util.Log
import android.view.View
import java.lang.ref.WeakReference
import java.util.ArrayList
import java.util.HashMap
import kotlin.math.max

class SelfSlide private constructor(from: Int, to: Int) : LayoutTransition() {

    companion object {
        private const val TAG = "SelfSlide"
        const val START = 0
        const val END = 1
        const val TOP = 2
        const val BOTTOM = 3
    }

    private val animatorTargetMap = HashMap<Animator, Any?>()

    init {
        val (inPropertyName, inStartValue) = when (from) {
            TOP -> {
                Pair("transitionY", -1f)
            }

            BOTTOM -> {
                Pair("transitionY", 1f)
            }

            END -> {
                Pair("transitionX", 1f)
            }

            else -> {
                Pair("transitionX", -1f)
            }
        }
        val (outPropertyName, outEndValue) = when (to) {
            TOP -> {
                Pair("transitionY", -1f)
            }

            BOTTOM -> {
                Pair("transitionY", 1f)
            }

            END -> {
                Pair("transitionX", 1f)
            }

            else -> {
                Pair("transitionX", -1f)
            }
        }
        val inAnimator = ObjectAnimator.ofFloat(null, inPropertyName, inStartValue, 0f)
        /*inAnimator.setEvaluator { fraction, startValue, endValue ->

        }*/
        setAnimator(APPEARING, inAnimator)

        val outAnimator = ProxyAnimator(
            ObjectAnimator.ofFloat(null, outPropertyName, 0f, outEndValue),
            onSetTarget = { animator, target: Any? ->
                Log.d(TAG, "onSetTarget animator=${animator} target=$target")
                animatorTargetMap[animator] = target
            })
        outAnimator.duration = getDuration(DISAPPEARING)
        val outListener = Listener(
            onGetTarget = { animator ->
                Log.d(TAG, "onGetTarget animator=${animator}")
                animatorTargetMap[animator]
            }
        )
        outAnimator.addListener(outListener)
        outAnimator.addUpdateListener(outListener)
        outAnimator.setEvaluator(outListener)
        setAnimator(DISAPPEARING, outAnimator)

    }

    class Builder {
        var from: Int = START
            private set
        var to: Int = START
            private set

        fun inFrom(from: Int): Builder {
            this.from = from
            return this
        }

        fun outTo(to: Int): Builder {
            this.to = to
            return this
        }

        fun build(): SelfSlide {
            return SelfSlide(from, to)
        }
    }

    private class Listener(private val onGetTarget: (animator: Animator) -> Any?) :
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
            return (target.width * fraction).apply {
                Log.d(TAG, "evaluate return $this")
            }
        }

        override fun onAnimationUpdate(animation: ValueAnimator) {
            Log.d(TAG, "onAnimationUpdate ${animation.animatedValue}")
            targetView?.get()?.let { view  ->
                val dx = animation.animatedValue as Float
                val left = max(0F, -dx) // 计算裁剪左边界
                clipBounds.set(left.toInt(), 0, view.width, view.height)
                view.clipBounds = clipBounds
            }
        }

    }

}