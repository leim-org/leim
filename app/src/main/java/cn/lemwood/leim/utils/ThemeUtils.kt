package cn.lemwood.leim.utils

import android.app.Activity
import android.content.Context
import android.content.res.Configuration
import android.graphics.Color
import android.os.Build
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat

/**
 * 主题工具类
 * 提供主题切换、状态栏、导航栏等UI相关功能
 */
object ThemeUtils {
    
    // 主题模式
    enum class ThemeMode {
        LIGHT,      // 浅色主题
        DARK,       // 深色主题
        AUTO        // 跟随系统
    }
    
    // 主题颜色
    data class ThemeColors(
        @ColorInt val primary: Int,
        @ColorInt val primaryVariant: Int,
        @ColorInt val secondary: Int,
        @ColorInt val background: Int,
        @ColorInt val surface: Int,
        @ColorInt val onPrimary: Int,
        @ColorInt val onSecondary: Int,
        @ColorInt val onBackground: Int,
        @ColorInt val onSurface: Int
    )
    
    private const val PREF_THEME_MODE = "theme_mode"
    private const val PREF_CUSTOM_THEME = "custom_theme"
    
    /**
     * 设置主题模式
     */
    fun setThemeMode(context: Context, mode: ThemeMode) {
        PreferenceManager.putString(context, PREF_THEME_MODE, mode.name)
        applyTheme(context)
    }
    
    /**
     * 获取当前主题模式
     */
    fun getThemeMode(context: Context): ThemeMode {
        val modeName = PreferenceManager.getString(context, PREF_THEME_MODE, ThemeMode.AUTO.name)
        return try {
            ThemeMode.valueOf(modeName)
        } catch (e: Exception) {
            ThemeMode.AUTO
        }
    }
    
    /**
     * 应用主题
     */
    fun applyTheme(context: Context) {
        val mode = getThemeMode(context)
        val nightMode = when (mode) {
            ThemeMode.LIGHT -> Configuration.UI_MODE_NIGHT_NO
            ThemeMode.DARK -> Configuration.UI_MODE_NIGHT_YES
            ThemeMode.AUTO -> {
                val currentNightMode = context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
                currentNightMode
            }
        }
        
        // 这里可以根据需要设置不同的主题资源
        // 由于我们使用的是简单的框架，暂时使用系统默认主题
    }
    
    /**
     * 判断当前是否为深色主题
     */
    fun isDarkTheme(context: Context): Boolean {
        val mode = getThemeMode(context)
        return when (mode) {
            ThemeMode.DARK -> true
            ThemeMode.LIGHT -> false
            ThemeMode.AUTO -> {
                val nightModeFlags = context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
                nightModeFlags == Configuration.UI_MODE_NIGHT_YES
            }
        }
    }
    
    /**
     * 设置状态栏样式
     */
    fun setStatusBarStyle(activity: Activity, lightStatusBar: Boolean = false) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val window = activity.window
            val decorView = window.decorView
            
            if (lightStatusBar) {
                decorView.systemUiVisibility = decorView.systemUiVisibility or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            } else {
                decorView.systemUiVisibility = decorView.systemUiVisibility and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv()
            }
        }
    }
    
    /**
     * 设置状态栏颜色
     */
    fun setStatusBarColor(activity: Activity, @ColorInt color: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            activity.window.statusBarColor = color
        }
    }
    
    /**
     * 设置状态栏透明
     */
    fun setStatusBarTransparent(activity: Activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window = activity.window
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = Color.TRANSPARENT
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        }
    }
    
    /**
     * 设置导航栏颜色
     */
    fun setNavigationBarColor(activity: Activity, @ColorInt color: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            activity.window.navigationBarColor = color
        }
    }
    
    /**
     * 设置导航栏样式
     */
    fun setNavigationBarStyle(activity: Activity, lightNavigationBar: Boolean = false) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val window = activity.window
            val decorView = window.decorView
            
            if (lightNavigationBar) {
                decorView.systemUiVisibility = decorView.systemUiVisibility or View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
            } else {
                decorView.systemUiVisibility = decorView.systemUiVisibility and View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR.inv()
            }
        }
    }
    
    /**
     * 设置沉浸式状态栏
     */
    fun setImmersiveStatusBar(activity: Activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            WindowCompat.setDecorFitsSystemWindows(activity.window, false)
            val controller = WindowInsetsControllerCompat(activity.window, activity.window.decorView)
            controller.isAppearanceLightStatusBars = !isDarkTheme(activity)
        } else {
            setStatusBarTransparent(activity)
            setStatusBarStyle(activity, !isDarkTheme(activity))
        }
    }
    
    /**
     * 隐藏状态栏
     */
    fun hideStatusBar(activity: Activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val controller = WindowInsetsControllerCompat(activity.window, activity.window.decorView)
            controller.hide(androidx.core.view.WindowInsetsCompat.Type.statusBars())
        } else {
            activity.window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        }
    }
    
    /**
     * 显示状态栏
     */
    fun showStatusBar(activity: Activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val controller = WindowInsetsControllerCompat(activity.window, activity.window.decorView)
            controller.show(androidx.core.view.WindowInsetsCompat.Type.statusBars())
        } else {
            activity.window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE
        }
    }
    
    /**
     * 获取主题颜色
     */
    fun getThemeColors(context: Context): ThemeColors {
        return if (isDarkTheme(context)) {
            getDarkThemeColors()
        } else {
            getLightThemeColors()
        }
    }
    
    /**
     * 获取浅色主题颜色
     */
    private fun getLightThemeColors(): ThemeColors {
        return ThemeColors(
            primary = Color.parseColor("#2196F3"),
            primaryVariant = Color.parseColor("#1976D2"),
            secondary = Color.parseColor("#03DAC6"),
            background = Color.parseColor("#FFFFFF"),
            surface = Color.parseColor("#FFFFFF"),
            onPrimary = Color.parseColor("#FFFFFF"),
            onSecondary = Color.parseColor("#000000"),
            onBackground = Color.parseColor("#000000"),
            onSurface = Color.parseColor("#000000")
        )
    }
    
    /**
     * 获取深色主题颜色
     */
    private fun getDarkThemeColors(): ThemeColors {
        return ThemeColors(
            primary = Color.parseColor("#BB86FC"),
            primaryVariant = Color.parseColor("#3700B3"),
            secondary = Color.parseColor("#03DAC6"),
            background = Color.parseColor("#121212"),
            surface = Color.parseColor("#1E1E1E"),
            onPrimary = Color.parseColor("#000000"),
            onSecondary = Color.parseColor("#000000"),
            onBackground = Color.parseColor("#FFFFFF"),
            onSurface = Color.parseColor("#FFFFFF")
        )
    }
    
    /**
     * 获取颜色资源
     */
    @ColorInt
    fun getColor(context: Context, @ColorRes colorRes: Int): Int {
        return ContextCompat.getColor(context, colorRes)
    }
    
    /**
     * 根据主题获取对应颜色
     */
    @ColorInt
    fun getColorByTheme(context: Context, lightColor: Int, darkColor: Int): Int {
        return if (isDarkTheme(context)) darkColor else lightColor
    }
    
    /**
     * 设置Activity全屏
     */
    fun setFullScreen(activity: Activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val controller = WindowInsetsControllerCompat(activity.window, activity.window.decorView)
            controller.hide(androidx.core.view.WindowInsetsCompat.Type.systemBars())
            controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        } else {
            activity.window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN
                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
            )
        }
    }
    
    /**
     * 退出全屏
     */
    fun exitFullScreen(activity: Activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val controller = WindowInsetsControllerCompat(activity.window, activity.window.decorView)
            controller.show(androidx.core.view.WindowInsetsCompat.Type.systemBars())
        } else {
            activity.window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE
        }
    }
    
    /**
     * 设置屏幕亮度
     */
    fun setScreenBrightness(activity: Activity, brightness: Float) {
        val window = activity.window
        val layoutParams = window.attributes
        layoutParams.screenBrightness = brightness.coerceIn(0f, 1f)
        window.attributes = layoutParams
    }
    
    /**
     * 获取屏幕亮度
     */
    fun getScreenBrightness(activity: Activity): Float {
        return activity.window.attributes.screenBrightness
    }
    
    /**
     * 保持屏幕常亮
     */
    fun keepScreenOn(activity: Activity, keepOn: Boolean = true) {
        if (keepOn) {
            activity.window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        } else {
            activity.window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }
    }
    
    /**
     * 设置窗口安全区域
     */
    fun setWindowInsets(activity: Activity, fitSystemWindows: Boolean = true) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            WindowCompat.setDecorFitsSystemWindows(activity.window, fitSystemWindows)
        }
    }
    
    /**
     * 获取状态栏高度
     */
    fun getStatusBarHeight(context: Context): Int {
        var result = 0
        val resourceId = context.resources.getIdentifier("status_bar_height", "dimen", "android")
        if (resourceId > 0) {
            result = context.resources.getDimensionPixelSize(resourceId)
        }
        return result
    }
    
    /**
     * 获取导航栏高度
     */
    fun getNavigationBarHeight(context: Context): Int {
        var result = 0
        val resourceId = context.resources.getIdentifier("navigation_bar_height", "dimen", "android")
        if (resourceId > 0) {
            result = context.resources.getDimensionPixelSize(resourceId)
        }
        return result
    }
}