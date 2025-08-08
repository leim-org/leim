package cn.lemwood.leim.ui.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
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
        binding = ActivityCrashLogBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        setupToolbar()
        setupRecyclerView()
        loadCrashLogs()
    }
    
    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            title = "崩溃日志"
        }
    }
    
    private fun setupRecyclerView() {
        adapter = CrashLogAdapter { logInfo ->
            showLogDetail(logInfo)
        }
        
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(this@CrashLogActivity)
            adapter = this@CrashLogActivity.adapter
        }
    }
    
    private fun loadCrashLogs() {
        val logs = CrashLogManager.getCrashLogInfoList(this)
        adapter.submitList(logs)
        
        if (logs.isEmpty()) {
            binding.emptyView.visibility = android.view.View.VISIBLE
            binding.recyclerView.visibility = android.view.View.GONE
        } else {
            binding.emptyView.visibility = android.view.View.GONE
            binding.recyclerView.visibility = android.view.View.VISIBLE
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