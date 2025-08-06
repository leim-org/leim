package cn.lemwood.leim.utils

import android.content.Context
import android.media.*
import android.net.Uri
import android.os.Build
import java.io.File
import java.io.IOException
import kotlin.math.log10

/**
 * 音频处理工具类
 * 提供音频录制、播放、格式转换等功能
 */
object AudioUtils {
    
    private var mediaRecorder: MediaRecorder? = null
    private var mediaPlayer: MediaPlayer? = null
    private var audioRecord: AudioRecord? = null
    
    // 音频录制参数
    private const val SAMPLE_RATE = 44100
    private const val CHANNEL_CONFIG = AudioFormat.CHANNEL_IN_MONO
    private const val AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT
    
    /**
     * 开始录音
     */
    fun startRecording(outputFile: File): Boolean {
        return try {
            stopRecording() // 确保之前的录音已停止
            
            mediaRecorder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                MediaRecorder(null as Context?)
            } else {
                @Suppress("DEPRECATION")
                MediaRecorder()
            }.apply {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setOutputFormat(MediaRecorder.OutputFormat.AAC_ADTS)
                setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
                setAudioSamplingRate(SAMPLE_RATE)
                setAudioEncodingBitRate(128000)
                setOutputFile(outputFile.absolutePath)
                
                prepare()
                start()
            }
            true
        } catch (e: Exception) {
            stopRecording()
            false
        }
    }
    
    /**
     * 停止录音
     */
    fun stopRecording() {
        try {
            mediaRecorder?.apply {
                stop()
                release()
            }
        } catch (e: Exception) {
            // 忽略停止时的异常
        } finally {
            mediaRecorder = null
        }
    }
    
    /**
     * 是否正在录音
     */
    fun isRecording(): Boolean {
        return mediaRecorder != null
    }
    
    /**
     * 开始播放音频
     */
    fun startPlaying(audioFile: File, onCompletion: (() -> Unit)? = null): Boolean {
        return try {
            stopPlaying() // 确保之前的播放已停止
            
            mediaPlayer = MediaPlayer().apply {
                setDataSource(audioFile.absolutePath)
                setOnCompletionListener {
                    onCompletion?.invoke()
                    stopPlaying()
                }
                setOnErrorListener { _, _, _ ->
                    stopPlaying()
                    false
                }
                prepare()
                start()
            }
            true
        } catch (e: Exception) {
            stopPlaying()
            false
        }
    }
    
    /**
     * 开始播放音频（Uri）
     */
    fun startPlaying(context: Context, audioUri: Uri, onCompletion: (() -> Unit)? = null): Boolean {
        return try {
            stopPlaying()
            
            mediaPlayer = MediaPlayer().apply {
                setDataSource(context, audioUri)
                setOnCompletionListener {
                    onCompletion?.invoke()
                    stopPlaying()
                }
                setOnErrorListener { _, _, _ ->
                    stopPlaying()
                    false
                }
                prepare()
                start()
            }
            true
        } catch (e: Exception) {
            stopPlaying()
            false
        }
    }
    
    /**
     * 停止播放音频
     */
    fun stopPlaying() {
        try {
            mediaPlayer?.apply {
                if (isPlaying) {
                    stop()
                }
                release()
            }
        } catch (e: Exception) {
            // 忽略停止时的异常
        } finally {
            mediaPlayer = null
        }
    }
    
    /**
     * 暂停播放
     */
    fun pausePlaying(): Boolean {
        return try {
            mediaPlayer?.pause()
            true
        } catch (e: Exception) {
            false
        }
    }
    
    /**
     * 恢复播放
     */
    fun resumePlaying(): Boolean {
        return try {
            mediaPlayer?.start()
            true
        } catch (e: Exception) {
            false
        }
    }
    
    /**
     * 是否正在播放
     */
    fun isPlaying(): Boolean {
        return try {
            mediaPlayer?.isPlaying == true
        } catch (e: Exception) {
            false
        }
    }
    
    /**
     * 获取播放进度
     */
    fun getCurrentPosition(): Int {
        return try {
            mediaPlayer?.currentPosition ?: 0
        } catch (e: Exception) {
            0
        }
    }
    
    /**
     * 获取音频总时长
     */
    fun getDuration(): Int {
        return try {
            mediaPlayer?.duration ?: 0
        } catch (e: Exception) {
            0
        }
    }
    
    /**
     * 设置播放进度
     */
    fun seekTo(position: Int): Boolean {
        return try {
            mediaPlayer?.seekTo(position)
            true
        } catch (e: Exception) {
            false
        }
    }
    
    /**
     * 设置音量
     */
    fun setVolume(leftVolume: Float, rightVolume: Float): Boolean {
        return try {
            mediaPlayer?.setVolume(leftVolume, rightVolume)
            true
        } catch (e: Exception) {
            false
        }
    }
    
    /**
     * 获取音频文件时长
     */
    fun getAudioDuration(audioFile: File): Long {
        return try {
            val mediaPlayer = MediaPlayer().apply {
                setDataSource(audioFile.absolutePath)
                prepare()
            }
            val duration = mediaPlayer.duration.toLong()
            mediaPlayer.release()
            duration
        } catch (e: Exception) {
            0L
        }
    }
    
    /**
     * 获取音频文件时长（Uri）
     */
    fun getAudioDuration(context: Context, audioUri: Uri): Long {
        return try {
            val mediaPlayer = MediaPlayer().apply {
                setDataSource(context, audioUri)
                prepare()
            }
            val duration = mediaPlayer.duration.toLong()
            mediaPlayer.release()
            duration
        } catch (e: Exception) {
            0L
        }
    }
    
    /**
     * 检查是否为音频文件
     */
    fun isAudioFile(filePath: String): Boolean {
        val extension = filePath.substringAfterLast('.', "").lowercase()
        return extension in listOf("mp3", "aac", "wav", "m4a", "ogg", "flac", "wma")
    }
    
    /**
     * 获取音频文件信息
     */
    fun getAudioInfo(audioFile: File): AudioInfo? {
        return try {
            val retriever = MediaMetadataRetriever()
            retriever.setDataSource(audioFile.absolutePath)
            
            val duration = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)?.toLongOrNull() ?: 0L
            val title = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE) ?: audioFile.nameWithoutExtension
            val artist = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST) ?: "Unknown"
            val album = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM) ?: "Unknown"
            val bitrate = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_BITRATE)?.toIntOrNull() ?: 0
            
            retriever.release()
            
            AudioInfo(
                title = title,
                artist = artist,
                album = album,
                duration = duration,
                bitrate = bitrate,
                size = audioFile.length()
            )
        } catch (e: Exception) {
            null
        }
    }
    
    /**
     * 格式化音频时长
     */
    fun formatDuration(durationMs: Long): String {
        val seconds = (durationMs / 1000) % 60
        val minutes = (durationMs / (1000 * 60)) % 60
        val hours = (durationMs / (1000 * 60 * 60))
        
        return if (hours > 0) {
            String.format("%d:%02d:%02d", hours, minutes, seconds)
        } else {
            String.format("%d:%02d", minutes, seconds)
        }
    }
    
    /**
     * 获取录音权限状态
     */
    fun hasRecordPermission(context: Context): Boolean {
        return PermissionUtils.hasPermission(context, android.Manifest.permission.RECORD_AUDIO)
    }
    
    /**
     * 开始实时音频监听（用于音量检测）
     */
    fun startAudioLevelMonitoring(callback: (level: Int) -> Unit): Boolean {
        return try {
            val bufferSize = AudioRecord.getMinBufferSize(SAMPLE_RATE, CHANNEL_CONFIG, AUDIO_FORMAT)
            
            audioRecord = AudioRecord(
                MediaRecorder.AudioSource.MIC,
                SAMPLE_RATE,
                CHANNEL_CONFIG,
                AUDIO_FORMAT,
                bufferSize
            )
            
            audioRecord?.startRecording()
            
            Thread {
                val buffer = ShortArray(bufferSize)
                while (audioRecord?.recordingState == AudioRecord.RECORDSTATE_RECORDING) {
                    val read = audioRecord?.read(buffer, 0, bufferSize) ?: 0
                    if (read > 0) {
                        val level = calculateAudioLevel(buffer, read)
                        callback(level)
                    }
                    Thread.sleep(100) // 100ms间隔
                }
            }.start()
            
            true
        } catch (e: Exception) {
            false
        }
    }
    
    /**
     * 停止音频监听
     */
    fun stopAudioLevelMonitoring() {
        try {
            audioRecord?.apply {
                stop()
                release()
            }
        } catch (e: Exception) {
            // 忽略异常
        } finally {
            audioRecord = null
        }
    }
    
    /**
     * 计算音频音量等级
     */
    private fun calculateAudioLevel(buffer: ShortArray, readSize: Int): Int {
        var sum = 0.0
        for (i in 0 until readSize) {
            sum += (buffer[i] * buffer[i]).toDouble()
        }
        val rms = kotlin.math.sqrt(sum / readSize)
        val db = 20 * log10(rms / 32767.0)
        
        // 将分贝值转换为0-100的等级
        return when {
            db < -60 -> 0
            db > -10 -> 100
            else -> ((db + 60) / 50 * 100).toInt()
        }
    }
    
    /**
     * 释放所有资源
     */
    fun release() {
        stopRecording()
        stopPlaying()
        stopAudioLevelMonitoring()
    }
    
    /**
     * 音频信息数据类
     */
    data class AudioInfo(
        val title: String,
        val artist: String,
        val album: String,
        val duration: Long,
        val bitrate: Int,
        val size: Long
    )
}