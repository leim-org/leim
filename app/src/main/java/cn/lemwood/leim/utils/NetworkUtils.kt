package cn.lemwood.leim.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Build
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import java.net.InetAddress
import java.net.UnknownHostException

/**
 * 网络工具类
 * 处理网络状态检测、连接监听等功能
 */
object NetworkUtils {
    
    private val _networkState = MutableLiveData<NetworkState>()
    val networkState: LiveData<NetworkState> = _networkState
    
    private var connectivityManager: ConnectivityManager? = null
    private var networkCallback: ConnectivityManager.NetworkCallback? = null
    
    /**
     * 网络状态枚举
     */
    enum class NetworkState {
        CONNECTED,      // 已连接
        DISCONNECTED,   // 未连接
        CONNECTING,     // 连接中
        WIFI,          // WiFi连接
        MOBILE,        // 移动网络
        ETHERNET,      // 以太网
        UNKNOWN        // 未知状态
    }
    
    /**
     * 网络类型枚举
     */
    enum class NetworkType {
        WIFI,
        MOBILE,
        ETHERNET,
        VPN,
        UNKNOWN
    }
    
    /**
     * 初始化网络监听
     */
    fun initialize(context: Context) {
        connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        startNetworkMonitoring()
        updateNetworkState()
    }
    
    /**
     * 开始网络监听
     */
    private fun startNetworkMonitoring() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            networkCallback = object : ConnectivityManager.NetworkCallback() {
                override fun onAvailable(network: Network) {
                    super.onAvailable(network)
                    updateNetworkState()
                }
                
                override fun onLost(network: Network) {
                    super.onLost(network)
                    updateNetworkState()
                }
                
                override fun onCapabilitiesChanged(
                    network: Network,
                    networkCapabilities: NetworkCapabilities
                ) {
                    super.onCapabilitiesChanged(network, networkCapabilities)
                    updateNetworkState()
                }
            }
            
            val networkRequest = NetworkRequest.Builder()
                .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                .build()
            
            connectivityManager?.registerNetworkCallback(networkRequest, networkCallback!!)
        }
    }
    
    /**
     * 停止网络监听
     */
    fun stopNetworkMonitoring() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N && networkCallback != null) {
            connectivityManager?.unregisterNetworkCallback(networkCallback!!)
            networkCallback = null
        }
    }
    
    /**
     * 更新网络状态
     */
    private fun updateNetworkState() {
        val state = getCurrentNetworkState()
        _networkState.postValue(state)
    }
    
    /**
     * 检查网络是否连接
     */
    fun isNetworkConnected(): Boolean {
        return connectivityManager?.let { cm ->
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val network = cm.activeNetwork
                val capabilities = cm.getNetworkCapabilities(network)
                capabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true
            } else {
                @Suppress("DEPRECATION")
                val networkInfo = cm.activeNetworkInfo
                networkInfo?.isConnected == true
            }
        } ?: false
    }
    
    /**
     * 获取当前网络状态
     */
    fun getCurrentNetworkState(): NetworkState {
        if (!isNetworkConnected()) {
            return NetworkState.DISCONNECTED
        }
        
        return when (getCurrentNetworkType()) {
            NetworkType.WIFI -> NetworkState.WIFI
            NetworkType.MOBILE -> NetworkState.MOBILE
            NetworkType.ETHERNET -> NetworkState.ETHERNET
            else -> NetworkState.CONNECTED
        }
    }
    
    /**
     * 获取当前网络类型
     */
    fun getCurrentNetworkType(): NetworkType {
        return connectivityManager?.let { cm ->
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val network = cm.activeNetwork
                val capabilities = cm.getNetworkCapabilities(network)
                when {
                    capabilities?.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) == true -> NetworkType.WIFI
                    capabilities?.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) == true -> NetworkType.MOBILE
                    capabilities?.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) == true -> NetworkType.ETHERNET
                    capabilities?.hasTransport(NetworkCapabilities.TRANSPORT_VPN) == true -> NetworkType.VPN
                    else -> NetworkType.UNKNOWN
                }
            } else {
                @Suppress("DEPRECATION")
                val networkInfo = cm.activeNetworkInfo
                when (networkInfo?.type) {
                    ConnectivityManager.TYPE_WIFI -> NetworkType.WIFI
                    ConnectivityManager.TYPE_MOBILE -> NetworkType.MOBILE
                    ConnectivityManager.TYPE_ETHERNET -> NetworkType.ETHERNET
                    ConnectivityManager.TYPE_VPN -> NetworkType.VPN
                    else -> NetworkType.UNKNOWN
                }
            }
        } ?: NetworkType.UNKNOWN
    }
    
    /**
     * 检查是否为WiFi连接
     */
    fun isWifiConnected(): Boolean {
        return getCurrentNetworkType() == NetworkType.WIFI
    }
    
    /**
     * 检查是否为移动网络连接
     */
    fun isMobileConnected(): Boolean {
        return getCurrentNetworkType() == NetworkType.MOBILE
    }
    
    /**
     * 检查是否为以太网连接
     */
    fun isEthernetConnected(): Boolean {
        return getCurrentNetworkType() == NetworkType.ETHERNET
    }
    
    /**
     * 检查是否为VPN连接
     */
    fun isVpnConnected(): Boolean {
        return getCurrentNetworkType() == NetworkType.VPN
    }
    
    /**
     * 获取网络状态描述
     */
    fun getNetworkStateDescription(): String {
        return when (getCurrentNetworkState()) {
            NetworkState.WIFI -> "WiFi已连接"
            NetworkState.MOBILE -> "移动网络已连接"
            NetworkState.ETHERNET -> "以太网已连接"
            NetworkState.CONNECTED -> "网络已连接"
            NetworkState.CONNECTING -> "连接中..."
            NetworkState.DISCONNECTED -> "网络未连接"
            NetworkState.UNKNOWN -> "网络状态未知"
        }
    }
    
    /**
     * 检查网络连接质量
     */
    fun getNetworkQuality(): NetworkQuality {
        if (!isNetworkConnected()) {
            return NetworkQuality.NO_CONNECTION
        }
        
        return connectivityManager?.let { cm ->
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val network = cm.activeNetwork
                val capabilities = cm.getNetworkCapabilities(network)
                
                when {
                    capabilities?.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) == true -> {
                        // WiFi连接通常质量较好
                        NetworkQuality.GOOD
                    }
                    capabilities?.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) == true -> {
                        // 移动网络质量取决于信号强度，这里简化处理
                        NetworkQuality.FAIR
                    }
                    capabilities?.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) == true -> {
                        // 以太网连接质量最好
                        NetworkQuality.EXCELLENT
                    }
                    else -> NetworkQuality.POOR
                }
            } else {
                NetworkQuality.UNKNOWN
            }
        } ?: NetworkQuality.UNKNOWN
    }
    
    /**
     * 网络质量枚举
     */
    enum class NetworkQuality {
        EXCELLENT,      // 优秀
        GOOD,          // 良好
        FAIR,          // 一般
        POOR,          // 较差
        NO_CONNECTION, // 无连接
        UNKNOWN        // 未知
    }
    
    /**
     * 检查主机是否可达
     */
    fun isHostReachable(host: String, timeout: Int = 5000): Boolean {
        return try {
            val address = InetAddress.getByName(host)
            address.isReachable(timeout)
        } catch (e: UnknownHostException) {
            false
        } catch (e: Exception) {
            false
        }
    }
    
    /**
     * 异步检查主机是否可达
     */
    fun checkHostReachability(
        host: String,
        timeout: Int = 5000,
        callback: (Boolean) -> Unit
    ) {
        Thread {
            val isReachable = isHostReachable(host, timeout)
            callback(isReachable)
        }.start()
    }
    
    /**
     * 检查WebSocket服务器是否可达
     */
    fun checkWebSocketServerReachability(
        url: String,
        callback: (Boolean) -> Unit
    ) {
        try {
            val host = extractHostFromUrl(url)
            if (host != null) {
                checkHostReachability(host) { isReachable ->
                    callback(isReachable)
                }
            } else {
                callback(false)
            }
        } catch (e: Exception) {
            callback(false)
        }
    }
    
    /**
     * 从URL中提取主机名
     */
    private fun extractHostFromUrl(url: String): String? {
        return try {
            val cleanUrl = url.replace("ws://", "http://").replace("wss://", "https://")
            val uri = java.net.URI(cleanUrl)
            uri.host
        } catch (e: Exception) {
            null
        }
    }
    
    /**
     * 获取网络延迟（简化实现）
     */
    fun getNetworkLatency(host: String = "8.8.8.8", callback: (Long) -> Unit) {
        Thread {
            val startTime = System.currentTimeMillis()
            val isReachable = isHostReachable(host, 3000)
            val latency = if (isReachable) {
                System.currentTimeMillis() - startTime
            } else {
                -1L // 表示无法连接
            }
            callback(latency)
        }.start()
    }
    
    /**
     * 检查是否为计费网络
     */
    fun isMeteredNetwork(): Boolean {
        return connectivityManager?.let { cm ->
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                cm.isActiveNetworkMetered
            } else {
                // 对于较老的版本，假设移动网络为计费网络
                isMobileConnected()
            }
        } ?: false
    }
    
    /**
     * 获取网络运营商名称
     */
    fun getNetworkOperatorName(context: Context): String? {
        return try {
            val telephonyManager = context.getSystemService(Context.TELEPHONY_SERVICE) as android.telephony.TelephonyManager
            telephonyManager.networkOperatorName
        } catch (e: Exception) {
            null
        }
    }
    
    /**
     * 检查是否允许在当前网络下进行大文件传输
     */
    fun isLargeFileTransferAllowed(): Boolean {
        return when (getCurrentNetworkType()) {
            NetworkType.WIFI, NetworkType.ETHERNET -> true
            NetworkType.MOBILE -> !isMeteredNetwork() // 移动网络且非计费时允许
            else -> false
        }
    }
    
    /**
     * 检查是否允许自动下载
     */
    fun isAutoDownloadAllowed(): Boolean {
        return isWifiConnected() || isEthernetConnected()
    }
    
    /**
     * 获取建议的重连间隔
     */
    fun getRecommendedReconnectInterval(): Long {
        return when (getCurrentNetworkState()) {
            NetworkState.WIFI, NetworkState.ETHERNET -> Constants.WebSocket.RECONNECT_INTERVAL_WIFI
            NetworkState.MOBILE -> Constants.WebSocket.RECONNECT_INTERVAL_MOBILE
            else -> Constants.WebSocket.RECONNECT_INTERVAL_DEFAULT
        }
    }
    
    /**
     * 获取建议的心跳间隔
     */
    fun getRecommendedHeartbeatInterval(): Long {
        return when (getCurrentNetworkState()) {
            NetworkState.WIFI, NetworkState.ETHERNET -> Constants.WebSocket.HEARTBEAT_INTERVAL_WIFI
            NetworkState.MOBILE -> Constants.WebSocket.HEARTBEAT_INTERVAL_MOBILE
            else -> Constants.WebSocket.HEARTBEAT_INTERVAL_DEFAULT
        }
    }
    
    /**
     * 网络状态变化监听器
     */
    interface NetworkStateListener {
        fun onNetworkStateChanged(state: NetworkState)
        fun onNetworkQualityChanged(quality: NetworkQuality)
    }
    
    private val networkStateListeners = mutableListOf<NetworkStateListener>()
    
    /**
     * 添加网络状态监听器
     */
    fun addNetworkStateListener(listener: NetworkStateListener) {
        networkStateListeners.add(listener)
    }
    
    /**
     * 移除网络状态监听器
     */
    fun removeNetworkStateListener(listener: NetworkStateListener) {
        networkStateListeners.remove(listener)
    }
    
    /**
     * 通知网络状态变化
     */
    private fun notifyNetworkStateChanged(state: NetworkState) {
        networkStateListeners.forEach { listener ->
            try {
                listener.onNetworkStateChanged(state)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    
    /**
     * 通知网络质量变化
     */
    private fun notifyNetworkQualityChanged(quality: NetworkQuality) {
        networkStateListeners.forEach { listener ->
            try {
                listener.onNetworkQualityChanged(quality)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}