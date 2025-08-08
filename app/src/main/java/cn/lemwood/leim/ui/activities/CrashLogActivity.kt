package cn.lemwood.leim.ui.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import cn.lemwood.leim.R
import cn.lemwood.leim.databinding.ActivityCrashLogBinding
import cn.lemwood.leim.ui.adapters.CrashLogAdapter
import cn.lemwood.leim.utils.CrashLogManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.io.File

/**
 * 崩溃日志查看Activity
 */
class CrashLogActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityCrashLogBinding
    private lateinit var adapter: CrashLogAdapter
    
    companion object {
        fun start(context: Context) {
            val intent = Intent(context, CrashLogActivity::class.java)
            context.startActivity(intent)
        }
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        try {
            binding = ActivityCrashLogBinding.inflate(layoutInflater)
            setContentView(binding.root)
            
            setupToolbar()
            setupRecyclerView()
            loadCrashLogs()
        } catch (e: Exception) {
            android.util.Log.e("CrashLogActivity", "Error in onCreate", e)
            // 如果出现异常，显示错误信息并关闭Activity
            Toast.makeText(this, "加载崩溃日志失败: ${e.message}", Toast.LENGTH_LONG).show()
            finish()
        }
    }
    
    private fun setupToolbar() {
        try {
            setSupportActionBar(binding.toolbar)
            supportActionBar?.apply {
                setDisplayHomeAsUpEnabled(true)
                title = "崩溃日志"
            }
        } catch (e: Exception) {
            android.util.Log.e("CrashLogActivity", "Error in setupToolbar", e)
        }
    }
    
    private fun setupRecyclerView() {
        try {
            adapter = CrashLogAdapter { logInfo ->
                showLogDetail(logInfo)
            }
            
            binding.recyclerView.apply {
                layoutManager = LinearLayoutManager(this@CrashLogActivity)
                adapter = this@CrashLogActivity.adapter
                addItemDecoration(DividerItemDecoration(this@CrashLogActivity, DividerItemDecoration.VERTICAL))
            }
        } catch (e: Exception) {
            android.util.Log.e("CrashLogActivity", "Error in setupRecyclerView", e)
        }
    }
    
    private fun loadCrashLogs() {
        try {
            val logs = CrashLogManager.getCrashLogInfoList(this)
            adapter.submitList(logs)
            
            // 显示或隐藏空状态
            if (logs.isEmpty()) {
                binding.recyclerView.visibility = View.GONE
                binding.emptyView.visibility = View.VISIBLE
            } else {
                binding.recyclerView.visibility = View.VISIBLE
                binding.emptyView.visibility = View.GONE
            }
        } catch (e: Exception) {
            android.util.Log.e("CrashLogActivity", "Error in loadCrashLogs", e)
            // 如果加载失败，显示空状态
            binding.recyclerView.visibility = View.GONE
            binding.emptyView.visibility = View.VISIBLE
        }
    }
    
    private fun showLogDetail(logInfo: CrashLogManager.CrashLogInfo) {
        val content = CrashLogManager.readCrashLog(logInfo.file)
        
        MaterialAlertDialogBuilder(this)
            .setTitle("崩溃详情")
            .setMessage(content)
            .setPositiveButton("确定", null)
            .setNeutralButton("分享") { _, _ ->
                CrashLogManager.shareCrashLog(this, logInfo.file)
            }
            .setNegativeButton("删除") { _, _ ->
                deleteCrashLog(logInfo.file)
            }
            .show()
    }
    
    private fun deleteCrashLog(file: File) {
        MaterialAlertDialogBuilder(this)
            .setTitle("删除日志")
            .setMessage("确定要删除这个崩溃日志吗？")
            .setPositiveButton("删除") { _, _ ->
                CrashLogManager.deleteCrashLog(file)
                loadCrashLogs()
                Toast.makeText(this, "已删除", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("取消", null)
            .show()
    }
    
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_crash_log, menu)
        return true
    }
    
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            R.id.action_clear_all -> {
                clearAllLogs()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
    
    private fun clearAllLogs() {
        MaterialAlertDialogBuilder(this)
            .setTitle("清除所有日志")
            .setMessage("确定要清除所有崩溃日志吗？")
            .setPositiveButton("清除") { _, _ ->
                CrashLogManager.clearAllCrashLogs(this)
                loadCrashLogs()
                Toast.makeText(this, "已清除所有日志", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("取消", null)
            .show()
    }
}