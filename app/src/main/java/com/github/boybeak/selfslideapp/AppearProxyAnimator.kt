package com.github.boybeak.selfslideapp

import android.animation.Animator
import android.animation.PropertyValuesHolder
import android.animation.TimeInterpolator
import android.animation.TypeEvaluator
import android.animation.ValueAnimator
import android.os.Build
import android.util.Log

class AppearProxyAnimator(
    private val animator: ValueAnimator,
    private val onSetTarget: (animator: Animator, target: Any?) -> Unit
    ) : Animator() {

        companion object {
            private const val TAG = "ProxyAnimator"
        }

        override fun getStartDelay(): Long {
            return animator.startDelay
        }

        override fun setStartDelay(startDelay: Long) {
            animator.startDelay = startDelay
        }

        override fun setDuration(duration: Long): Animator {
            animator.duration = duration
            return this
        }

        override fun getDuration(): Long {
            return animator.duration
        }

        override fun setInterpolator(value: TimeInterpolator?) {
            animator.interpolator = value
        }

        override fun isRunning(): Boolean {
            return animator.isRunning
        }

        override fun setTarget(target: Any?) {
            onSetTarget.invoke(animator, target)
            animator.setTarget(target)
        }

        override fun clone(): Animator {
            return AppearProxyAnimator(animator.clone(), onSetTarget)
        }

        override fun start() {
            animator.start()
        }

        override fun cancel() {
            animator.cancel()
        }

        override fun end() {
            animator.end()
        }

        override fun pause() {
            animator.pause()
        }

        override fun resume() {
            animator.resume()
        }

        override fun isPaused(): Boolean {
            return animator.isPaused
        }

        override fun getTotalDuration(): Long {
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                animator.totalDuration
            } else {
                animator.duration
            }
        }

        override fun getInterpolator(): TimeInterpolator {
            return animator.interpolator
        }

        override fun isStarted(): Boolean {
            return animator.isStarted
        }

        override fun addListener(listener: AnimatorListener?) {
            animator.addListener(listener)
        }

        override fun removeListener(listener: AnimatorListener?) {
            animator.removeListener(listener)
        }

        override fun getListeners(): ArrayList<AnimatorListener> {
            return animator.listeners
        }

        override fun addPauseListener(listener: AnimatorPauseListener?) {
            animator.addPauseListener(listener)
        }

        override fun removePauseListener(listener: AnimatorPauseListener?) {
            animator.removePauseListener(listener)
        }

        override fun removeAllListeners() {
            animator.removeAllListeners()
        }

        override fun setupStartValues() {
            animator.setupStartValues()
        }

        override fun setupEndValues() {
            animator.setupEndValues()
        }
    }