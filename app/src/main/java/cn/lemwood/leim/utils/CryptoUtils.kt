package cn.lemwood.leim.utils

import android.util.Base64
import java.security.MessageDigest
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.SecretKeySpec
import kotlin.random.Random

/**
 * 加密工具类
 * 提供各种加密、解密、哈希功能
 */
object CryptoUtils {
    
    private const val AES_TRANSFORMATION = "AES/GCM/NoPadding"
    private const val AES_KEY_LENGTH = 256
    private const val GCM_IV_LENGTH = 12
    private const val GCM_TAG_LENGTH = 16
    
    /**
     * 生成MD5哈希
     */
    fun md5(input: String): String {
        return try {
            val md = MessageDigest.getInstance("MD5")
            val digest = md.digest(input.toByteArray())
            digest.joinToString("") { "%02x".format(it) }
        } catch (e: Exception) {
            ""
        }
    }
    
    /**
     * 生成SHA-256哈希
     */
    fun sha256(input: String): String {
        return try {
            val md = MessageDigest.getInstance("SHA-256")
            val digest = md.digest(input.toByteArray())
            digest.joinToString("") { "%02x".format(it) }
        } catch (e: Exception) {
            ""
        }
    }
    
    /**
     * 生成SHA-1哈希
     */
    fun sha1(input: String): String {
        return try {
            val md = MessageDigest.getInstance("SHA-1")
            val digest = md.digest(input.toByteArray())
            digest.joinToString("") { "%02x".format(it) }
        } catch (e: Exception) {
            ""
        }
    }
    
    /**
     * 生成随机AES密钥
     */
    fun generateAESKey(): SecretKey? {
        return try {
            val keyGenerator = KeyGenerator.getInstance("AES")
            keyGenerator.init(AES_KEY_LENGTH)
            keyGenerator.generateKey()
        } catch (e: Exception) {
            null
        }
    }
    
    /**
     * 从字节数组创建AES密钥
     */
    fun createAESKey(keyBytes: ByteArray): SecretKey {
        return SecretKeySpec(keyBytes, "AES")
    }
    
    /**
     * 将密钥转换为Base64字符串
     */
    fun keyToBase64(key: SecretKey): String {
        return Base64.encodeToString(key.encoded, Base64.NO_WRAP)
    }
    
    /**
     * 从Base64字符串创建密钥
     */
    fun keyFromBase64(base64Key: String): SecretKey? {
        return try {
            val keyBytes = Base64.decode(base64Key, Base64.NO_WRAP)
            createAESKey(keyBytes)
        } catch (e: Exception) {
            null
        }
    }
    
    /**
     * AES-GCM加密
     */
    fun encryptAES(plainText: String, key: SecretKey): String? {
        return try {
            val cipher = Cipher.getInstance(AES_TRANSFORMATION)
            val iv = ByteArray(GCM_IV_LENGTH)
            SecureRandom().nextBytes(iv)
            val spec = GCMParameterSpec(GCM_TAG_LENGTH * 8, iv)
            cipher.init(Cipher.ENCRYPT_MODE, key, spec)
            
            val cipherText = cipher.doFinal(plainText.toByteArray())
            val encryptedData = iv + cipherText
            Base64.encodeToString(encryptedData, Base64.NO_WRAP)
        } catch (e: Exception) {
            null
        }
    }
    
    /**
     * AES-GCM解密
     */
    fun decryptAES(encryptedText: String, key: SecretKey): String? {
        return try {
            val encryptedData = Base64.decode(encryptedText, Base64.NO_WRAP)
            val iv = encryptedData.sliceArray(0 until GCM_IV_LENGTH)
            val cipherText = encryptedData.sliceArray(GCM_IV_LENGTH until encryptedData.size)
            
            val cipher = Cipher.getInstance(AES_TRANSFORMATION)
            val spec = GCMParameterSpec(GCM_TAG_LENGTH * 8, iv)
            cipher.init(Cipher.DECRYPT_MODE, key, spec)
            
            val plainText = cipher.doFinal(cipherText)
            String(plainText)
        } catch (e: Exception) {
            null
        }
    }
    
    /**
     * 生成随机字符串
     */
    fun generateRandomString(length: Int): String {
        val chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"
        return (1..length)
            .map { chars[Random.nextInt(chars.length)] }
            .joinToString("")
    }
    
    /**
     * 生成随机数字字符串
     */
    fun generateRandomNumbers(length: Int): String {
        return (1..length)
            .map { Random.nextInt(10) }
            .joinToString("")
    }
    
    /**
     * 生成UUID
     */
    fun generateUUID(): String {
        return java.util.UUID.randomUUID().toString()
    }
    
    /**
     * 生成短UUID（去掉连字符）
     */
    fun generateShortUUID(): String {
        return generateUUID().replace("-", "")
    }
    
    /**
     * Base64编码
     */
    fun base64Encode(input: String): String {
        return Base64.encodeToString(input.toByteArray(), Base64.NO_WRAP)
    }
    
    /**
     * Base64解码
     */
    fun base64Decode(input: String): String? {
        return try {
            String(Base64.decode(input, Base64.NO_WRAP))
        } catch (e: Exception) {
            null
        }
    }
    
    /**
     * 简单的字符串混淆（用于非敏感数据）
     */
    fun obfuscate(input: String, key: String = "leim"): String {
        val keyBytes = key.toByteArray()
        return input.toByteArray().mapIndexed { index, byte ->
            (byte.toInt() xor keyBytes[index % keyBytes.size].toInt()).toByte()
        }.let { Base64.encodeToString(it.toByteArray(), Base64.NO_WRAP) }
    }
    
    /**
     * 简单的字符串反混淆
     */
    fun deobfuscate(input: String, key: String = "leim"): String? {
        return try {
            val keyBytes = key.toByteArray()
            val data = Base64.decode(input, Base64.NO_WRAP)
            String(data.mapIndexed { index, byte ->
                (byte.toInt() xor keyBytes[index % keyBytes.size].toInt()).toByte()
            }.toByteArray())
        } catch (e: Exception) {
            null
        }
    }
    
    /**
     * 验证哈希值
     */
    fun verifyHash(input: String, hash: String, algorithm: String = "SHA-256"): Boolean {
        val computedHash = when (algorithm.uppercase()) {
            "MD5" -> md5(input)
            "SHA-1" -> sha1(input)
            "SHA-256" -> sha256(input)
            else -> return false
        }
        return computedHash.equals(hash, ignoreCase = true)
    }
    
    /**
     * 生成文件校验和
     */
    fun generateFileChecksum(fileBytes: ByteArray): String {
        return try {
            val md = MessageDigest.getInstance("SHA-256")
            val digest = md.digest(fileBytes)
            digest.joinToString("") { "%02x".format(it) }
        } catch (e: Exception) {
            ""
        }
    }
}