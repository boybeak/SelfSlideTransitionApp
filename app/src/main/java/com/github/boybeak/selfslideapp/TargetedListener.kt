package com.github.boybeak.selfslideapp

import android.animation.Animator
import android.animation.TypeEvaluator
import android.animation.ValueAnimator
import android.view.View
import java.lang.ref.WeakReference
import java.util.WeakHashMap

abstract class TargetedListener :
    Animator.AnimatorListener, ValueAnimator.AnimatorUpdateListener, TypeEvaluator<Any> {

    private val animatorViewMap = WeakHashMap<Animator, View>()
    private var guessTargetRef: WeakReference<View>? = null
    val guessTarget: View? get() = guessTargetRef?.get()

    override fun onAnimationStart(animation: Animator) {
        val target = onTakeTarget() ?: return
        animatorViewMap[animation] = target
        guessTargetRef = WeakReference(target)
    }

    override fun onAnimationEnd(animation: Animator) {
        animatorViewMap.remove(animation)
    }

    override fun onAnimationCancel(animation: Animator) {
        animatorViewMap.remove(animation)
    }

    override fun onAnimationRepeat(animation: Animator) {
    }

    fun targetOf(animation: ValueAnimator): View? {
        return animatorViewMap[animation]
    }

    abstract fun onTakeTarget(): View?

}