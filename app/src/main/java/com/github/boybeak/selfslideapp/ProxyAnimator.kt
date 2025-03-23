package com.github.boybeak.selfslideapp

import android.animation.Animator
import android.animation.PropertyValuesHolder
import android.animation.TimeInterpolator
import android.animation.TypeEvaluator
import android.animation.ValueAnimator
import android.os.Build

class ProxyAnimator(
    private val animator: ValueAnimator,
    private val onSetTarget: (animator: Animator, target: Any?) -> Unit
    ) : ValueAnimator() {

        override fun getStartDelay(): Long {
            return animator.startDelay
        }

        override fun setStartDelay(startDelay: Long) {
            animator.startDelay = startDelay
        }

        override fun setDuration(duration: Long): ValueAnimator {
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
            animator.setTarget(target)
            onSetTarget.invoke(animator, target)
        }

        override fun clone(): ValueAnimator {
            return ProxyAnimator(animator.clone(), onSetTarget)
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

        override fun setIntValues(vararg values: Int) {
            animator.setIntValues(*values)
        }

        override fun setFloatValues(vararg values: Float) {
            animator.setFloatValues(*values)
        }

        override fun setObjectValues(vararg values: Any?) {
            animator.setObjectValues(*values)
        }

        override fun setValues(vararg values: PropertyValuesHolder?) {
            animator.setValues(*values)
        }

        override fun getValues(): Array<PropertyValuesHolder> {
            return animator.values
        }

        override fun setCurrentPlayTime(playTime: Long) {
            animator.setCurrentPlayTime(playTime)
        }

        override fun setCurrentFraction(fraction: Float) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
                animator.setCurrentFraction(fraction)
            }
        }

        override fun getCurrentPlayTime(): Long {
            return animator.getCurrentPlayTime()
        }

        override fun getAnimatedValue(): Any {
            return animator.getAnimatedValue()
        }

        override fun getAnimatedValue(propertyName: String?): Any {
            return animator.getAnimatedValue(propertyName)
        }

        override fun setRepeatCount(value: Int) {
            animator.repeatCount = value
        }

        override fun getRepeatCount(): Int {
            return animator.repeatCount
        }

        override fun setRepeatMode(value: Int) {
            animator.repeatMode = value
        }

        override fun getRepeatMode(): Int {
            return animator.repeatMode
        }

        override fun addUpdateListener(listener: AnimatorUpdateListener?) {
            animator.addUpdateListener(listener)
        }

        override fun removeAllUpdateListeners() {
            animator.removeAllUpdateListeners()
        }

        override fun removeUpdateListener(listener: AnimatorUpdateListener?) {
            animator.removeUpdateListener(listener)
        }

        override fun setEvaluator(value: TypeEvaluator<*>?) {
            animator.setEvaluator(value)
        }

        override fun reverse() {
            animator.reverse()
        }

        override fun getAnimatedFraction(): Float {
            return animator.animatedFraction
        }
    }