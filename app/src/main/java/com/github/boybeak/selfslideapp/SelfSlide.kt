package com.github.boybeak.selfslideapp

import android.animation.LayoutTransition
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.graphics.Rect
import android.os.Build
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.annotation.RequiresApi
import java.util.LinkedList
import java.util.Queue
import kotlin.math.max
import kotlin.math.min

class SelfSlide private constructor(from: Int, to: Int) : LayoutTransition() {

    companion object {
        private const val TAG = "SelfSlide"
        @SuppressLint("RtlHardcoded")
        const val LEFT = Gravity.LEFT
        @SuppressLint("RtlHardcoded")
        const val RIGHT = Gravity.RIGHT
        const val TOP = Gravity.TOP
        const val BOTTOM = Gravity.BOTTOM

        @RequiresApi(Build.VERSION_CODES.N)
        fun from(layout: LinearLayout): SelfSlide {
            var gravity = layout.gravity

            when(gravity) {
                Gravity.START -> {
                    gravity = when(layout.getLayoutDirection()) {
                        View.LAYOUT_DIRECTION_RTL -> RIGHT
                        else -> LEFT
                    }
                }
                Gravity.END -> {
                    gravity = when(layout.getLayoutDirection()) {
                        View.LAYOUT_DIRECTION_RTL -> LEFT
                        else -> RIGHT
                    }
                }
            }
            return Builder().inFrom(gravity).outTo(gravity).build()
        }

    }

    private val appearingQueue: Queue<View> = LinkedList()
    private val appearingListener = createListener(from) {
        appearingQueue.poll()
    }

    private val disappearingQueue: Queue<View> = LinkedList()
    private val disappearingListener = createListener(to) {
        disappearingQueue.poll()
    }

    init {
        initAppearing(from)
        initDisappearing(to)
    }

    override fun addChild(parent: ViewGroup?, child: View?) {
        appearingQueue.offer(child)
        super.addChild(parent, child)
    }

    override fun removeChild(parent: ViewGroup?, child: View?) {
        disappearingQueue.offer(child)
        super.removeChild(parent, child)
    }

    private fun initAppearing(from: Int) {
        val (inPropertyName, inStartValue) = when (from) {
            TOP -> {
                Pair("translationY", -1f)
            }

            BOTTOM -> {
                Pair("translationY", 1f)
            }

            RIGHT -> {
                Pair("translationX", 1f)
            }

            else -> {
                Pair("translationX", -1f)
            }
        }

        val inAnimator = ObjectAnimator.ofFloat(null, inPropertyName, inStartValue, 0f)
        inAnimator.duration = getDuration(APPEARING)

        inAnimator.addListener(appearingListener)
        inAnimator.addUpdateListener(appearingListener)
        inAnimator.setEvaluator(appearingListener)

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

            RIGHT -> {
                Pair("translationX", 1f)
            }

            else -> {
                Pair("translationX", -1f)
            }
        }
        val outAnimator = ObjectAnimator.ofFloat(null, outPropertyName, 0f, outEndValue)
        outAnimator.duration = getDuration(DISAPPEARING)

        outAnimator.addListener(disappearingListener)
        outAnimator.addUpdateListener(disappearingListener)
        outAnimator.setEvaluator(disappearingListener)
        setAnimator(DISAPPEARING, outAnimator)
    }

    private fun createListener(gravity: Int, onTakeTarget: () -> View?): TargetedListener {
        return object : TargetedListener() {
            private val clipBounds = Rect()
            override fun onTakeTarget(): View? {
                return onTakeTarget.invoke()
            }

            override fun onAnimationUpdate(animation: ValueAnimator) {
                targetOf(animation)?.let { view ->
                    val dx = animation.animatedValue as Float
                    when (gravity) {
                        TOP -> {
                            val top = max(0F, -dx) // 计算裁剪上边界
                            clipBounds.set(0, top.toInt(), view.width, view.height)
                        }

                        BOTTOM -> {
                            val bottom = min(view.height - dx, view.height.toFloat()) // 计算裁剪下边界
                            clipBounds.set(0, 0, view.width, bottom.toInt())
                        }

                        RIGHT -> {
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

            override fun evaluate(fraction: Float, startValue: Any?, endValue: Any?): Any {
                val target = guessTarget ?: return 0
                val size = when (gravity) {
                    TOP, BOTTOM -> target.height
                    else -> target.width
                }

                val endValueF = endValue as Float
                val startValueF = startValue as Float
                val valueDistance = endValueF - startValueF
                val deltaValue = valueDistance * fraction
                val currentValue = startValue + deltaValue

                val value = size * currentValue
                return value
            }
        }
    }

    class Builder {
        private var from: Int = LEFT
        private var to: Int = LEFT

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