package cn.lemwood.leim.utils

import android.app.Activity
import android.content.Context
import android.graphics.Rect
import android.view.View
import android.view.ViewTreeObserver
import android.view.inputmethod.InputMethodManager
import android.widget.EditText

/**
 * 键盘工具类
 * 提供软键盘显示、隐藏、监听等功能
 */
object KeyboardUtils {
    
    private const val MIN_KEYBOARD_HEIGHT = 200 // 最小键盘高度（dp）
    
    /**
     * 显示软键盘
     */
    fun showKeyboard(view: View) {
        view.requestFocus()
        val imm = view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
    }
    
    /**
     * 显示软键盘（强制）
     */
    fun showKeyboardForced(view: View) {
        view.requestFocus()
        val imm = view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(view, InputMethodManager.SHOW_FORCED)
    }
    
    /**
     * 隐藏软键盘
     */
    fun hideKeyboard(view: View) {
        val imm = view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }
    
    /**
     * 隐藏软键盘（Activity）
     */
    fun hideKeyboard(activity: Activity) {
        val currentFocus = activity.currentFocus
        if (currentFocus != null) {
            hideKeyboard(currentFocus)
        }
    }
    
    /**
     * 切换软键盘显示状态
     */
    fun toggleKeyboard(context: Context) {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
    }
    
    /**
     * 判断软键盘是否显示
     */
    fun isKeyboardVisible(activity: Activity): Boolean {
        val rootView = activity.findViewById<View>(android.R.id.content)
        val rect = Rect()
        rootView.getWindowVisibleDisplayFrame(rect)
        
        val screenHeight = rootView.height
        val keypadHeight = screenHeight - rect.bottom
        val minKeyboardHeight = DeviceUtils.dp2px(activity, MIN_KEYBOARD_HEIGHT.toFloat())
        
        return keypadHeight > minKeyboardHeight
    }
    
    /**
     * 获取软键盘高度
     */
    fun getKeyboardHeight(activity: Activity): Int {
        val rootView = activity.findViewById<View>(android.R.id.content)
        val rect = Rect()
        rootView.getWindowVisibleDisplayFrame(rect)
        
        val screenHeight = rootView.height
        val keypadHeight = screenHeight - rect.bottom
        val minKeyboardHeight = DeviceUtils.dp2px(activity, MIN_KEYBOARD_HEIGHT.toFloat())
        
        return if (keypadHeight > minKeyboardHeight) keypadHeight else 0
    }
    
    /**
     * 设置软键盘监听器
     */
    fun setKeyboardListener(activity: Activity, listener: KeyboardListener) {
        val rootView = activity.findViewById<View>(android.R.id.content)
        var isKeyboardVisible = false
        var keyboardHeight = 0
        
        rootView.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                val rect = Rect()
                rootView.getWindowVisibleDisplayFrame(rect)
                
                val screenHeight = rootView.height
                val currentKeypadHeight = screenHeight - rect.bottom
                val minKeyboardHeight = DeviceUtils.dp2px(activity, MIN_KEYBOARD_HEIGHT.toFloat())
                
                val currentKeyboardVisible = currentKeypadHeight > minKeyboardHeight
                
                if (currentKeyboardVisible != isKeyboardVisible) {
                    isKeyboardVisible = currentKeyboardVisible
                    keyboardHeight = if (currentKeyboardVisible) currentKeypadHeight else 0
                    
                    if (currentKeyboardVisible) {
                        listener.onKeyboardShow(keyboardHeight)
                    } else {
                        listener.onKeyboardHide()
                    }
                } else if (currentKeyboardVisible && currentKeypadHeight != keyboardHeight) {
                    // 键盘高度变化（可能是输入法切换）
                    keyboardHeight = currentKeypadHeight
                    listener.onKeyboardHeightChanged(keyboardHeight)
                }
            }
        })
    }
    
    /**
     * 移除软键盘监听器
     */
    fun removeKeyboardListener(activity: Activity, layoutListener: ViewTreeObserver.OnGlobalLayoutListener) {
        val rootView = activity.findViewById<View>(android.R.id.content)
        rootView.viewTreeObserver.removeOnGlobalLayoutListener(layoutListener)
    }
    
    /**
     * 设置EditText获得焦点时自动显示键盘
     */
    fun setAutoShowKeyboard(editText: EditText, autoShow: Boolean = true) {
        if (autoShow) {
            editText.setOnFocusChangeListener { _, hasFocus ->
                if (hasFocus) {
                    editText.post {
                        showKeyboard(editText)
                    }
                }
            }
        } else {
            editText.onFocusChangeListener = null
        }
    }
    
    /**
     * 设置点击其他区域隐藏键盘
     */
    fun setHideKeyboardOnTouchOutside(activity: Activity, parentView: View) {
        parentView.setOnTouchListener { _, _ ->
            val currentFocus = activity.currentFocus
            if (currentFocus is EditText) {
                hideKeyboard(currentFocus)
                currentFocus.clearFocus()
            }
            false
        }
    }
    
    /**
     * 延迟显示键盘
     */
    fun showKeyboardDelayed(view: View, delayMillis: Long = 200) {
        view.postDelayed({
            showKeyboard(view)
        }, delayMillis)
    }
    
    /**
     * 延迟隐藏键盘
     */
    fun hideKeyboardDelayed(view: View, delayMillis: Long = 200) {
        view.postDelayed({
            hideKeyboard(view)
        }, delayMillis)
    }
    
    /**
     * 修复软键盘遮挡问题
     */
    fun fixKeyboardCovering(activity: Activity, targetView: View) {
        setKeyboardListener(activity, object : KeyboardListener {
            override fun onKeyboardShow(height: Int) {
                val rootView = activity.findViewById<View>(android.R.id.content)
                val location = IntArray(2)
                targetView.getLocationOnScreen(location)
                
                val targetBottom = location[1] + targetView.height
                val visibleBottom = rootView.height - height
                
                if (targetBottom > visibleBottom) {
                    val scrollY = targetBottom - visibleBottom + DeviceUtils.dp2px(activity, 20f)
                    rootView.scrollBy(0, scrollY)
                }
            }
            
            override fun onKeyboardHide() {
                val rootView = activity.findViewById<View>(android.R.id.content)
                rootView.scrollTo(0, 0)
            }
            
            override fun onKeyboardHeightChanged(height: Int) {
                // 可以在这里处理键盘高度变化
            }
        })
    }
    
    /**
     * 获取输入法管理器
     */
    fun getInputMethodManager(context: Context): InputMethodManager {
        return context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    }
    
    /**
     * 检查是否有输入法可用
     */
    fun hasInputMethod(context: Context): Boolean {
        val imm = getInputMethodManager(context)
        return imm.inputMethodList.isNotEmpty()
    }
    
    /**
     * 获取当前输入法包名
     */
    fun getCurrentInputMethodPackage(context: Context): String? {
        val imm = getInputMethodManager(context)
        return imm.currentInputMethodSubtype?.let { subtype ->
            imm.inputMethodList.find { it.id == subtype.inputMethodId }?.packageName
        }
    }
    
    /**
     * 打开输入法设置
     */
    fun openInputMethodSettings(context: Context) {
        try {
            val intent = android.content.Intent(android.provider.Settings.ACTION_INPUT_METHOD_SETTINGS)
            context.startActivity(intent)
        } catch (e: Exception) {
            LogUtils.e("Failed to open input method settings", e)
        }
    }
    
    /**
     * 软键盘监听接口
     */
    interface KeyboardListener {
        /**
         * 键盘显示
         * @param height 键盘高度（像素）
         */
        fun onKeyboardShow(height: Int)
        
        /**
         * 键盘隐藏
         */
        fun onKeyboardHide()
        
        /**
         * 键盘高度变化
         * @param height 新的键盘高度（像素）
         */
        fun onKeyboardHeightChanged(height: Int)
    }
    
    /**
     * 简化的键盘监听器
     */
    abstract class SimpleKeyboardListener : KeyboardListener {
        override fun onKeyboardShow(height: Int) {}
        override fun onKeyboardHide() {}
        override fun onKeyboardHeightChanged(height: Int) {}
    }
}