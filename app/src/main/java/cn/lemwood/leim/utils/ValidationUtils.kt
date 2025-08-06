package cn.lemwood.leim.utils

import java.util.regex.Pattern

/**
 * 输入验证工具类
 * 用于验证各种用户输入格式
 */
object ValidationUtils {
    
    // 编译正则表达式模式以提高性能
    private val emailPattern = Pattern.compile(Constants.Regex.EMAIL)
    private val phonePattern = Pattern.compile(Constants.Regex.PHONE)
    private val leimIdPattern = Pattern.compile(Constants.Regex.LEIM_ID)
    private val passwordPattern = Pattern.compile(Constants.Regex.PASSWORD)
    private val usernamePattern = Pattern.compile(Constants.Regex.USERNAME)
    private val groupNamePattern = Pattern.compile(Constants.Regex.GROUP_NAME)
    
    /**
     * 验证邮箱格式
     */
    fun isValidEmail(email: String?): Boolean {
        return !email.isNullOrBlank() && emailPattern.matcher(email.trim()).matches()
    }
    
    /**
     * 验证手机号格式
     */
    fun isValidPhone(phone: String?): Boolean {
        return !phone.isNullOrBlank() && phonePattern.matcher(phone.trim()).matches()
    }
    
    /**
     * 验证Leim ID格式
     */
    fun isValidLeimId(leimId: String?): Boolean {
        return !leimId.isNullOrBlank() && leimIdPattern.matcher(leimId.trim()).matches()
    }
    
    /**
     * 验证密码格式
     * 要求：8-20位，包含大小写字母和数字
     */
    fun isValidPassword(password: String?): Boolean {
        return !password.isNullOrBlank() && passwordPattern.matcher(password).matches()
    }
    
    /**
     * 验证用户名格式
     * 支持中文、英文、数字、下划线，2-20位
     */
    fun isValidUsername(username: String?): Boolean {
        return !username.isNullOrBlank() && usernamePattern.matcher(username.trim()).matches()
    }
    
    /**
     * 验证群组名称格式
     * 支持中文、英文、数字、下划线、空格，2-30位
     */
    fun isValidGroupName(groupName: String?): Boolean {
        return !groupName.isNullOrBlank() && groupNamePattern.matcher(groupName.trim()).matches()
    }
    
    /**
     * 验证昵称长度
     */
    fun isValidNickname(nickname: String?): Boolean {
        if (nickname.isNullOrBlank()) return false
        val trimmed = nickname.trim()
        return trimmed.length in 1..Constants.Default.MAX_NICKNAME_LENGTH
    }
    
    /**
     * 验证个性签名长度
     */
    fun isValidSignature(signature: String?): Boolean {
        if (signature.isNullOrBlank()) return true // 个性签名可以为空
        return signature.trim().length <= Constants.Default.MAX_SIGNATURE_LENGTH
    }
    
    /**
     * 验证消息内容长度
     */
    fun isValidMessageContent(content: String?): Boolean {
        if (content.isNullOrBlank()) return false
        return content.length <= Constants.Default.MAX_MESSAGE_LENGTH
    }
    
    /**
     * 验证密码强度
     * @return 密码强度等级：0-弱，1-中，2-强
     */
    fun getPasswordStrength(password: String?): Int {
        if (password.isNullOrBlank()) return 0
        
        var score = 0
        
        // 长度检查
        when {
            password.length >= 12 -> score += 2
            password.length >= 8 -> score += 1
        }
        
        // 包含小写字母
        if (password.any { it.isLowerCase() }) score += 1
        
        // 包含大写字母
        if (password.any { it.isUpperCase() }) score += 1
        
        // 包含数字
        if (password.any { it.isDigit() }) score += 1
        
        // 包含特殊字符
        if (password.any { !it.isLetterOrDigit() }) score += 1
        
        return when {
            score >= 5 -> 2 // 强
            score >= 3 -> 1 // 中
            else -> 0 // 弱
        }
    }
    
    /**
     * 获取密码强度描述
     */
    fun getPasswordStrengthText(password: String?): String {
        return when (getPasswordStrength(password)) {
            2 -> "强"
            1 -> "中"
            else -> "弱"
        }
    }
    
    /**
     * 验证两次密码是否一致
     */
    fun isPasswordMatch(password: String?, confirmPassword: String?): Boolean {
        return !password.isNullOrBlank() && password == confirmPassword
    }
    
    /**
     * 验证URL格式
     */
    fun isValidUrl(url: String?): Boolean {
        if (url.isNullOrBlank()) return false
        return try {
            val pattern = Pattern.compile(
                "^(https?|wss?)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]"
            )
            pattern.matcher(url.trim()).matches()
        } catch (e: Exception) {
            false
        }
    }
    
    /**
     * 验证WebSocket URL格式
     */
    fun isValidWebSocketUrl(url: String?): Boolean {
        if (url.isNullOrBlank()) return false
        return url.trim().startsWith("ws://") || url.trim().startsWith("wss://")
    }
    
    /**
     * 验证IP地址格式
     */
    fun isValidIpAddress(ip: String?): Boolean {
        if (ip.isNullOrBlank()) return false
        return try {
            val pattern = Pattern.compile(
                "^((25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$"
            )
            pattern.matcher(ip.trim()).matches()
        } catch (e: Exception) {
            false
        }
    }
    
    /**
     * 验证端口号
     */
    fun isValidPort(port: String?): Boolean {
        if (port.isNullOrBlank()) return false
        return try {
            val portNum = port.trim().toInt()
            portNum in 1..65535
        } catch (e: Exception) {
            false
        }
    }
    
    /**
     * 验证端口号
     */
    fun isValidPort(port: Int): Boolean {
        return port in 1..65535
    }
    
    /**
     * 清理和验证输入文本
     * 移除首尾空格，检查是否为空
     */
    fun cleanAndValidateText(text: String?): String? {
        val cleaned = text?.trim()
        return if (cleaned.isNullOrBlank()) null else cleaned
    }
    
    /**
     * 验证文件名格式
     * 不能包含特殊字符
     */
    fun isValidFileName(fileName: String?): Boolean {
        if (fileName.isNullOrBlank()) return false
        val invalidChars = charArrayOf('/', '\\', ':', '*', '?', '"', '<', '>', '|')
        return fileName.none { it in invalidChars }
    }
    
    /**
     * 验证文件大小
     */
    fun isValidFileSize(fileSize: Long, maxSize: Long): Boolean {
        return fileSize > 0 && fileSize <= maxSize
    }
    
    /**
     * 验证图片文件大小
     */
    fun isValidImageSize(fileSize: Long): Boolean {
        return isValidFileSize(fileSize, Constants.File.MAX_IMAGE_SIZE.toLong())
    }
    
    /**
     * 验证普通文件大小
     */
    fun isValidGeneralFileSize(fileSize: Long): Boolean {
        return isValidFileSize(fileSize, Constants.File.MAX_FILE_SIZE.toLong())
    }
    
    /**
     * 验证音频时长
     */
    fun isValidAudioDuration(duration: Long): Boolean {
        return duration > 0 && duration <= Constants.File.MAX_AUDIO_DURATION
    }
    
    /**
     * 验证视频时长
     */
    fun isValidVideoDuration(duration: Long): Boolean {
        return duration > 0 && duration <= Constants.File.MAX_VIDEO_DURATION
    }
    
    /**
     * 验证群组成员数量
     */
    fun isValidGroupMemberCount(count: Int): Boolean {
        return count in 2..Constants.Default.MAX_GROUP_MEMBERS
    }
    
    /**
     * 验证验证码格式（6位数字）
     */
    fun isValidVerificationCode(code: String?): Boolean {
        if (code.isNullOrBlank()) return false
        return code.trim().matches(Regex("^\\d{6}$"))
    }
    
    /**
     * 验证年龄
     */
    fun isValidAge(age: Int): Boolean {
        return age in 1..150
    }
    
    /**
     * 验证生日年份
     */
    fun isValidBirthYear(year: Int): Boolean {
        val currentYear = java.util.Calendar.getInstance().get(java.util.Calendar.YEAR)
        return year in (currentYear - 150)..(currentYear - 1)
    }
    
    /**
     * 获取输入错误提示信息
     */
    object ErrorMessages {
        const val INVALID_EMAIL = "请输入有效的邮箱地址"
        const val INVALID_PHONE = "请输入有效的手机号码"
        const val INVALID_LEIM_ID = "Leim号只能包含字母、数字、下划线，长度6-20位"
        const val INVALID_PASSWORD = "密码必须包含大小写字母和数字，长度8-20位"
        const val INVALID_USERNAME = "用户名只能包含中文、英文、数字、下划线，长度2-20位"
        const val INVALID_GROUP_NAME = "群组名称长度2-30位"
        const val INVALID_NICKNAME = "昵称长度不能超过${Constants.Default.MAX_NICKNAME_LENGTH}位"
        const val INVALID_SIGNATURE = "个性签名长度不能超过${Constants.Default.MAX_SIGNATURE_LENGTH}位"
        const val INVALID_MESSAGE_CONTENT = "消息内容长度不能超过${Constants.Default.MAX_MESSAGE_LENGTH}位"
        const val PASSWORD_NOT_MATCH = "两次输入的密码不一致"
        const val INVALID_URL = "请输入有效的URL地址"
        const val INVALID_WEBSOCKET_URL = "请输入有效的WebSocket地址"
        const val INVALID_IP_ADDRESS = "请输入有效的IP地址"
        const val INVALID_PORT = "端口号必须在1-65535之间"
        const val INVALID_FILE_NAME = "文件名包含非法字符"
        const val FILE_TOO_LARGE = "文件大小超出限制"
        const val INVALID_VERIFICATION_CODE = "验证码必须为6位数字"
        const val FIELD_REQUIRED = "此字段为必填项"
        const val FIELD_TOO_SHORT = "输入内容过短"
        const val FIELD_TOO_LONG = "输入内容过长"
    }
}