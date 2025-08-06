package cn.lemwood.leim.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import cn.lemwood.leim.service.WebSocketService
import cn.lemwood.leim.utils.PreferenceManager

class BootReceiver : BroadcastReceiver() {
    
    companion object {
        private const val TAG = "BootReceiver"
    }
    
    override fun onReceive(context: Context, intent: Intent) {
        Log.d(TAG, "Received broadcast: ${intent.action}")
        
        val preferenceManager = PreferenceManager(context)
        
        when (intent.action) {
            Intent.ACTION_BOOT_COMPLETED,
            Intent.ACTION_MY_PACKAGE_REPLACED,
            Intent.ACTION_PACKAGE_REPLACED -> {
                if (preferenceManager.isAutoStartEnabled() && preferenceManager.isLoggedIn()) {
                    Log.d(TAG, "Starting WebSocket service on boot")
                    val serviceIntent = Intent(context, WebSocketService::class.java)
                    context.startForegroundService(serviceIntent)
                }
            }
        }
    }
}