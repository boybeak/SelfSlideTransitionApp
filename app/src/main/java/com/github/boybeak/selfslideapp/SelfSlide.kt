package com.github.boybeak.selfslideapp

import android.animation.Animator
import android.animation.LayoutTransition
import android.animation.ObjectAnimator
import android.animation.TypeEvaluator
import android.animation.ValueAnimator
import android.graphics.Rect
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.core.animation.addListener
import java.lang.ref.WeakReference
import java.util.HashMap
import kotlin.math.max
import kotlin.math.min

class SelfSlide private constructor(from: Int, to: Int) : LayoutTransition() {

    companion object {
        private const val TAG = "SelfSlide"
        const val START = 0
        const val END = 1
        const val TOP = 2
        const val BOTTOM = 3
    }

    private val inAnimatorTargetMap = HashMap<Animator, Any?>()
    var appearingTarget: View? = null

    private val outAnimatorTargetMap = HashMap<Animator, Any?>()

    init {
        initAppearing(from)
        initDisappearing(to)
    }

    override fun addChild(parent: ViewGroup?, child: View?) {
        Log.d(TAG, "addChild start")
        super.addChild(parent, child)
        Log.d(TAG, "addChild end")
    }

    override fun removeChild(parent: ViewGroup?, child: View?) {
        Log.d(TAG, "removeChild start")
        super.removeChild(parent, child)
        Log.d(TAG, "removeChild end")
    }

    private fun initAppearing(from: Int) {
        val (inPropertyName, inStartValue) = when (from) {
            TOP -> {
                Pair("translationY", -1f)
            }

            BOTTOM -> {
                Pair("translationY", 1f)
            }

            END -> {
                Pair("translationX", 1f)
            }

            else -> {
                Pair("translationX", -1f)
            }
        }

        /*val innerInAnimator = ObjectAnimator.ofFloat(null, inPropertyName, inStartValue, 0f)
        val inAnimator = AppearProxyAnimator(
            innerInAnimator,
            onSetTarget = { animator, target: Any? ->
//                appearingTarget = target as? View
                inAnimatorTargetMap[animator] = target
            }
        )*/
        val inAnimator = ObjectAnimator.ofFloat(null, inPropertyName, inStartValue, 0f)
        inAnimator.duration = getDuration(APPEARING)
        inAnimator.setEvaluator { fraction, startValue, endValue ->
            val target = appearingTarget
            val value = target?.let { view ->
                -view.width * (1 - fraction)
            } ?: 0f
//            Log.d(TAG, "inAnimator eva.value=$value")
            value
        }
        inAnimator.addListener(
            onStart = {
                Log.d(TAG, "inAnimator start")
            }
        )
        /*val inListener = DisappearingListener(
            APPEARING,
            dir = from,
            onGetTarget = { animator ->
                inAnimatorTargetMap[animator]
            }
        )
        inAnimator.addListener(inListener)
        inAnimator.addUpdateListener(inListener)
        inAnimator.setEvaluator(inListener)*/

        setAnimator(APPEARING, inAnimator)
    }

    private fun initDisappearing(to: Int) {
        val (outPropertyName, outEndValue) = when (to) {
            TOP -> {
                Pair("translationY", -1f)
            }

            BOTTOM -> {
                Pair("translationY", 1f)
            }

            END -> {
                Pair("translationX", 1f)
            }

            else -> {
                Pair("translationX", -1f)
            }
        }
        val outAnimator = ProxyAnimator(
            ObjectAnimator.ofFloat(null, outPropertyName, 0f, outEndValue),
            onSetTarget = { animator, target: Any? ->
                outAnimatorTargetMap[animator] = target
            })
        outAnimator.duration = getDuration(DISAPPEARING)
        val outListener = DisappearingListener(
            DISAPPEARING,
            dir = to,
            onGetTarget = { animator ->
                outAnimatorTargetMap[animator]
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

}