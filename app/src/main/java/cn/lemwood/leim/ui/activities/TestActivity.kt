package cn.lemwood.leim.ui.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import cn.lemwood.leim.utils.DebugHelper

/**
 * 测试Activity - 用于诊断应用启动问题
 */
class TestActivity : AppCompatActivity() {
    
    companion object {
        private const val TAG = "TestActivity"
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        Log.d(TAG, "TestActivity onCreate 开始")
        
        try {
            // 创建布局
            val scrollView = ScrollView(this)
            val linearLayout = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                setPadding(50, 50, 50, 50)
            }
            
            // 标题
            val titleText = TextView(this).apply {
                text = "Leim 应用调试页面"
                textSize = 20f
                setPadding(0, 0, 0, 30)
            }
            linearLayout.addView(titleText)
            
            // 状态信息
            val statusText = TextView(this).apply {
                text = DebugHelper.checkAppStatus(this@TestActivity)
                textSize = 14f
                setPadding(0, 0, 0, 30)
            }
            linearLayout.addView(statusText)
            
            // 重新启动主页按钮
            val restartMainButton = Button(this).apply {
                text = "重新启动主页"
                setOnClickListener {
                    try {
                        Log.d(TAG, "尝试重新启动主页")
                        startActivity(Intent(this@TestActivity, MainActivity::class.java))
                        finish()
                    } catch (e: Exception) {
                        Log.e(TAG, "重新启动主页失败", e)
                        statusText.text = "重新启动失败: ${e.message}\n\n${statusText.text}"
                    }
                }
            }
            linearLayout.addView(restartMainButton)
            
            // 重新启动登录页按钮
            val restartLoginButton = Button(this).apply {
                text = "重新启动登录页"
                setOnClickListener {
                    try {
                        Log.d(TAG, "尝试重新启动登录页")
                        startActivity(Intent(this@TestActivity, LoginActivity::class.java))
                        finish()
                    } catch (e: Exception) {
                        Log.e(TAG, "重新启动登录页失败", e)
                        statusText.text = "重新启动失败: ${e.message}\n\n${statusText.text}"
                    }
                }
            }
            linearLayout.addView(restartLoginButton)
            
            // 清除数据按钮
            val clearDataButton = Button(this).apply {
                text = "清除所有数据"
                setOnClickListener {
                    try {
                        Log.d(TAG, "清除所有数据")
                        DebugHelper.clearAllData(this@TestActivity)
                        statusText.text = "数据已清除\n\n${DebugHelper.checkAppStatus(this@TestActivity)}"
                    } catch (e: Exception) {
                        Log.e(TAG, "清除数据失败", e)
                        statusText.text = "清除数据失败: ${e.message}\n\n${statusText.text}"
                    }
                }
            }
            linearLayout.addView(clearDataButton)
            
            // 刷新状态按钮
            val refreshButton = Button(this).apply {
                text = "刷新状态"
                setOnClickListener {
                    try {
                        Log.d(TAG, "刷新状态")
                        statusText.text = DebugHelper.checkAppStatus(this@TestActivity)
                    } catch (e: Exception) {
                        Log.e(TAG, "刷新状态失败", e)
                        statusText.text = "刷新失败: ${e.message}\n\n${statusText.text}"
                    }
                }
            }
            linearLayout.addView(refreshButton)
            
            scrollView.addView(linearLayout)
            setContentView(scrollView)
            
            Log.d(TAG, "TestActivity 创建成功")
            
        } catch (e: Exception) {
            Log.e(TAG, "TestActivity onCreate 失败", e)
            cn.lemwood.leim.utils.CrashLogger.logException(e, "TestActivity_onCreate")
            
            // 如果连测试页面都创建失败，创建最简单的页面
            val errorText = TextView(this).apply {
                text = "严重错误：无法创建测试页面\n错误信息：${e.message}"
                textSize = 16f
                setPadding(50, 50, 50, 50)
            }
            setContentView(errorText)
        }
    }
}