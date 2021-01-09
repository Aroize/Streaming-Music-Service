package ru.ilia.streamer.core

import android.animation.ObjectAnimator
import android.view.View
import androidx.core.animation.doOnEnd
import androidx.core.animation.doOnStart

object ViewUtils {

    fun showViewAnimated(view: View) {
        val animator = ObjectAnimator.ofFloat(view, View.ALPHA, 0f, 1f)
        animator.doOnStart { view.visibility = View.VISIBLE }
        animator.duration = DURATION
        animator.start()
    }

    fun hideViewAnimated(view: View) {
        val animator = ObjectAnimator.ofFloat(view, View.ALPHA, 1f, 0f)
        animator.doOnEnd { view.visibility = View.GONE }
        animator.duration = DURATION
        animator.start()
    }

    private const val DURATION = 300L
}