package cn.lemwood.leim.utils

import android.animation.*
import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.view.animation.*
import android.widget.ImageView
import androidx.core.animation.doOnEnd
import androidx.core.animation.doOnStart
import androidx.interpolator.view.animation.FastOutSlowInInterpolator

/**
 * 动画工具类
 * 提供各种常用的动画效果
 */
object AnimationUtils {
    
    // 动画时长常量
    const val DURATION_SHORT = 200L
    const val DURATION_MEDIUM = 300L
    const val DURATION_LONG = 500L
    
    // 插值器
    val FAST_OUT_SLOW_IN = FastOutSlowInInterpolator()
    val ACCELERATE = AccelerateInterpolator()
    val DECELERATE = DecelerateInterpolator()
    val BOUNCE = BounceInterpolator()
    val OVERSHOOT = OvershootInterpolator()
    
    /**
     * 淡入动画
     */
    fun fadeIn(view: View, duration: Long = DURATION_MEDIUM, onEnd: (() -> Unit)? = null): ObjectAnimator {
        view.alpha = 0f
        view.visibility = View.VISIBLE
        
        return ObjectAnimator.ofFloat(view, "alpha", 0f, 1f).apply {
            this.duration = duration
            interpolator = FAST_OUT_SLOW_IN
            doOnEnd { onEnd?.invoke() }
            start()
        }
    }
    
    /**
     * 淡出动画
     */
    fun fadeOut(view: View, duration: Long = DURATION_MEDIUM, onEnd: (() -> Unit)? = null): ObjectAnimator {
        return ObjectAnimator.ofFloat(view, "alpha", 1f, 0f).apply {
            this.duration = duration
            interpolator = FAST_OUT_SLOW_IN
            doOnEnd { 
                view.visibility = View.GONE
                onEnd?.invoke() 
            }
            start()
        }
    }
    
    /**
     * 滑入动画（从右侧）
     */
    fun slideInFromRight(view: View, duration: Long = DURATION_MEDIUM, onEnd: (() -> Unit)? = null): ObjectAnimator {
        val parent = view.parent as? ViewGroup
        val parentWidth = parent?.width ?: view.context.resources.displayMetrics.widthPixels
        
        view.translationX = parentWidth.toFloat()
        view.visibility = View.VISIBLE
        
        return ObjectAnimator.ofFloat(view, "translationX", parentWidth.toFloat(), 0f).apply {
            this.duration = duration
            interpolator = FAST_OUT_SLOW_IN
            doOnEnd { onEnd?.invoke() }
            start()
        }
    }
    
    /**
     * 滑出动画（向右侧）
     */
    fun slideOutToRight(view: View, duration: Long = DURATION_MEDIUM, onEnd: (() -> Unit)? = null): ObjectAnimator {
        val parent = view.parent as? ViewGroup
        val parentWidth = parent?.width ?: view.context.resources.displayMetrics.widthPixels
        
        return ObjectAnimator.ofFloat(view, "translationX", 0f, parentWidth.toFloat()).apply {
            this.duration = duration
            interpolator = FAST_OUT_SLOW_IN
            doOnEnd { 
                view.visibility = View.GONE
                view.translationX = 0f
                onEnd?.invoke() 
            }
            start()
        }
    }
    
    /**
     * 滑入动画（从左侧）
     */
    fun slideInFromLeft(view: View, duration: Long = DURATION_MEDIUM, onEnd: (() -> Unit)? = null): ObjectAnimator {
        val parent = view.parent as? ViewGroup
        val parentWidth = parent?.width ?: view.context.resources.displayMetrics.widthPixels
        
        view.translationX = -parentWidth.toFloat()
        view.visibility = View.VISIBLE
        
        return ObjectAnimator.ofFloat(view, "translationX", -parentWidth.toFloat(), 0f).apply {
            this.duration = duration
            interpolator = FAST_OUT_SLOW_IN
            doOnEnd { onEnd?.invoke() }
            start()
        }
    }
    
    /**
     * 滑出动画（向左侧）
     */
    fun slideOutToLeft(view: View, duration: Long = DURATION_MEDIUM, onEnd: (() -> Unit)? = null): ObjectAnimator {
        val parent = view.parent as? ViewGroup
        val parentWidth = parent?.width ?: view.context.resources.displayMetrics.widthPixels
        
        return ObjectAnimator.ofFloat(view, "translationX", 0f, -parentWidth.toFloat()).apply {
            this.duration = duration
            interpolator = FAST_OUT_SLOW_IN
            doOnEnd { 
                view.visibility = View.GONE
                view.translationX = 0f
                onEnd?.invoke() 
            }
            start()
        }
    }
    
    /**
     * 滑入动画（从上方）
     */
    fun slideInFromTop(view: View, duration: Long = DURATION_MEDIUM, onEnd: (() -> Unit)? = null): ObjectAnimator {
        val parent = view.parent as? ViewGroup
        val parentHeight = parent?.height ?: view.context.resources.displayMetrics.heightPixels
        
        view.translationY = -parentHeight.toFloat()
        view.visibility = View.VISIBLE
        
        return ObjectAnimator.ofFloat(view, "translationY", -parentHeight.toFloat(), 0f).apply {
            this.duration = duration
            interpolator = FAST_OUT_SLOW_IN
            doOnEnd { onEnd?.invoke() }
            start()
        }
    }
    
    /**
     * 滑入动画（从下方）
     */
    fun slideInFromBottom(view: View, duration: Long = DURATION_MEDIUM, onEnd: (() -> Unit)? = null): ObjectAnimator {
        val parent = view.parent as? ViewGroup
        val parentHeight = parent?.height ?: view.context.resources.displayMetrics.heightPixels
        
        view.translationY = parentHeight.toFloat()
        view.visibility = View.VISIBLE
        
        return ObjectAnimator.ofFloat(view, "translationY", parentHeight.toFloat(), 0f).apply {
            this.duration = duration
            interpolator = FAST_OUT_SLOW_IN
            doOnEnd { onEnd?.invoke() }
            start()
        }
    }
    
    /**
     * 滑出动画（向下方）
     */
    fun slideOutToBottom(view: View, duration: Long = DURATION_MEDIUM, onEnd: (() -> Unit)? = null): ObjectAnimator {
        val parent = view.parent as? ViewGroup
        val parentHeight = parent?.height ?: view.context.resources.displayMetrics.heightPixels
        
        return ObjectAnimator.ofFloat(view, "translationY", 0f, parentHeight.toFloat()).apply {
            this.duration = duration
            interpolator = FAST_OUT_SLOW_IN
            doOnEnd { 
                view.visibility = View.GONE
                view.translationY = 0f
                onEnd?.invoke() 
            }
            start()
        }
    }
    
    /**
     * 缩放动画（放大）
     */
    fun scaleIn(view: View, duration: Long = DURATION_MEDIUM, onEnd: (() -> Unit)? = null): AnimatorSet {
        view.scaleX = 0f
        view.scaleY = 0f
        view.visibility = View.VISIBLE
        
        val scaleX = ObjectAnimator.ofFloat(view, "scaleX", 0f, 1f)
        val scaleY = ObjectAnimator.ofFloat(view, "scaleY", 0f, 1f)
        
        return AnimatorSet().apply {
            playTogether(scaleX, scaleY)
            this.duration = duration
            interpolator = OVERSHOOT
            doOnEnd { onEnd?.invoke() }
            start()
        }
    }
    
    /**
     * 缩放动画（缩小）
     */
    fun scaleOut(view: View, duration: Long = DURATION_MEDIUM, onEnd: (() -> Unit)? = null): AnimatorSet {
        val scaleX = ObjectAnimator.ofFloat(view, "scaleX", 1f, 0f)
        val scaleY = ObjectAnimator.ofFloat(view, "scaleY", 1f, 0f)
        
        return AnimatorSet().apply {
            playTogether(scaleX, scaleY)
            this.duration = duration
            interpolator = FAST_OUT_SLOW_IN
            doOnEnd { 
                view.visibility = View.GONE
                view.scaleX = 1f
                view.scaleY = 1f
                onEnd?.invoke() 
            }
            start()
        }
    }
    
    /**
     * 旋转动画
     */
    fun rotate(view: View, fromDegrees: Float = 0f, toDegrees: Float = 360f, 
               duration: Long = DURATION_MEDIUM, repeat: Boolean = false): ObjectAnimator {
        return ObjectAnimator.ofFloat(view, "rotation", fromDegrees, toDegrees).apply {
            this.duration = duration
            interpolator = LinearInterpolator()
            if (repeat) {
                repeatCount = ObjectAnimator.INFINITE
                repeatMode = ObjectAnimator.RESTART
            }
            start()
        }
    }
    
    /**
     * 摇摆动画
     */
    fun shake(view: View, amplitude: Float = 10f, duration: Long = DURATION_MEDIUM): ObjectAnimator {
        return ObjectAnimator.ofFloat(view, "translationX", 0f, amplitude, -amplitude, amplitude, -amplitude, 0f).apply {
            this.duration = duration
            interpolator = LinearInterpolator()
            start()
        }
    }
    
    /**
     * 弹跳动画
     */
    fun bounce(view: View, amplitude: Float = 20f, duration: Long = DURATION_MEDIUM): ObjectAnimator {
        return ObjectAnimator.ofFloat(view, "translationY", 0f, -amplitude, 0f).apply {
            this.duration = duration
            interpolator = BOUNCE
            start()
        }
    }
    
    /**
     * 脉冲动画（缩放）
     */
    fun pulse(view: View, scale: Float = 1.1f, duration: Long = DURATION_SHORT, repeat: Boolean = true): AnimatorSet {
        val scaleX = ObjectAnimator.ofFloat(view, "scaleX", 1f, scale, 1f)
        val scaleY = ObjectAnimator.ofFloat(view, "scaleY", 1f, scale, 1f)
        
        return AnimatorSet().apply {
            playTogether(scaleX, scaleY)
            this.duration = duration
            interpolator = FAST_OUT_SLOW_IN
            if (repeat) {
                repeatCount = ObjectAnimator.INFINITE
                repeatMode = ObjectAnimator.RESTART
            }
            start()
        }
    }
    
    /**
     * 闪烁动画
     */
    fun blink(view: View, duration: Long = DURATION_SHORT, repeat: Boolean = true): ObjectAnimator {
        return ObjectAnimator.ofFloat(view, "alpha", 1f, 0f, 1f).apply {
            this.duration = duration
            interpolator = LinearInterpolator()
            if (repeat) {
                repeatCount = ObjectAnimator.INFINITE
                repeatMode = ObjectAnimator.RESTART
            }
            start()
        }
    }
    
    /**
     * 翻转动画
     */
    fun flip(view: View, duration: Long = DURATION_MEDIUM, onEnd: (() -> Unit)? = null): ObjectAnimator {
        return ObjectAnimator.ofFloat(view, "rotationY", 0f, 180f).apply {
            this.duration = duration
            interpolator = FAST_OUT_SLOW_IN
            doOnEnd { onEnd?.invoke() }
            start()
        }
    }
    
    /**
     * 组合动画：淡入 + 缩放
     */
    fun fadeInScale(view: View, duration: Long = DURATION_MEDIUM, onEnd: (() -> Unit)? = null): AnimatorSet {
        view.alpha = 0f
        view.scaleX = 0.8f
        view.scaleY = 0.8f
        view.visibility = View.VISIBLE
        
        val fadeIn = ObjectAnimator.ofFloat(view, "alpha", 0f, 1f)
        val scaleX = ObjectAnimator.ofFloat(view, "scaleX", 0.8f, 1f)
        val scaleY = ObjectAnimator.ofFloat(view, "scaleY", 0.8f, 1f)
        
        return AnimatorSet().apply {
            playTogether(fadeIn, scaleX, scaleY)
            this.duration = duration
            interpolator = FAST_OUT_SLOW_IN
            doOnEnd { onEnd?.invoke() }
            start()
        }
    }
    
    /**
     * 组合动画：淡出 + 缩放
     */
    fun fadeOutScale(view: View, duration: Long = DURATION_MEDIUM, onEnd: (() -> Unit)? = null): AnimatorSet {
        val fadeOut = ObjectAnimator.ofFloat(view, "alpha", 1f, 0f)
        val scaleX = ObjectAnimator.ofFloat(view, "scaleX", 1f, 0.8f)
        val scaleY = ObjectAnimator.ofFloat(view, "scaleY", 1f, 0.8f)
        
        return AnimatorSet().apply {
            playTogether(fadeOut, scaleX, scaleY)
            this.duration = duration
            interpolator = FAST_OUT_SLOW_IN
            doOnEnd { 
                view.visibility = View.GONE
                view.alpha = 1f
                view.scaleX = 1f
                view.scaleY = 1f
                onEnd?.invoke() 
            }
            start()
        }
    }
    
    /**
     * 加载动画（旋转）
     */
    fun startLoadingAnimation(imageView: ImageView): ObjectAnimator {
        return rotate(imageView, 0f, 360f, 1000L, true)
    }
    
    /**
     * 停止加载动画
     */
    fun stopLoadingAnimation(animator: ObjectAnimator?) {
        animator?.cancel()
    }
    
    /**
     * 视图切换动画
     */
    fun crossFade(viewOut: View, viewIn: View, duration: Long = DURATION_MEDIUM) {
        viewIn.alpha = 0f
        viewIn.visibility = View.VISIBLE
        
        val fadeOut = ObjectAnimator.ofFloat(viewOut, "alpha", 1f, 0f)
        val fadeIn = ObjectAnimator.ofFloat(viewIn, "alpha", 0f, 1f)
        
        val animatorSet = AnimatorSet()
        animatorSet.playTogether(fadeOut, fadeIn)
        animatorSet.duration = duration
        animatorSet.interpolator = FAST_OUT_SLOW_IN
        animatorSet.doOnEnd {
            viewOut.visibility = View.GONE
            viewOut.alpha = 1f
        }
        animatorSet.start()
    }
    
    /**
     * 创建属性动画
     */
    fun createPropertyAnimator(
        target: Any,
        propertyName: String,
        vararg values: Float,
        duration: Long = DURATION_MEDIUM,
        interpolator: TimeInterpolator = FAST_OUT_SLOW_IN,
        onStart: (() -> Unit)? = null,
        onEnd: (() -> Unit)? = null
    ): ObjectAnimator {
        return ObjectAnimator.ofFloat(target, propertyName, *values).apply {
            this.duration = duration
            this.interpolator = interpolator
            doOnStart { onStart?.invoke() }
            doOnEnd { onEnd?.invoke() }
        }
    }
    
    /**
     * 创建值动画
     */
    fun createValueAnimator(
        vararg values: Float,
        duration: Long = DURATION_MEDIUM,
        interpolator: TimeInterpolator = FAST_OUT_SLOW_IN,
        updateListener: (Float) -> Unit
    ): ValueAnimator {
        return ValueAnimator.ofFloat(*values).apply {
            this.duration = duration
            this.interpolator = interpolator
            addUpdateListener { animation ->
                updateListener(animation.animatedValue as Float)
            }
        }
    }
    
    /**
     * 延迟执行动画
     */
    fun delayedAnimation(delay: Long, animation: () -> Unit) {
        val handler = android.os.Handler(android.os.Looper.getMainLooper())
        handler.postDelayed(animation, delay)
    }
    
    /**
     * 取消所有动画
     */
    fun cancelAllAnimations(view: View) {
        view.clearAnimation()
        view.animate().cancel()
    }
    
    /**
     * 重置视图状态
     */
    fun resetViewState(view: View) {
        cancelAllAnimations(view)
        view.alpha = 1f
        view.scaleX = 1f
        view.scaleY = 1f
        view.translationX = 0f
        view.translationY = 0f
        view.rotation = 0f
        view.rotationX = 0f
        view.rotationY = 0f
    }
}