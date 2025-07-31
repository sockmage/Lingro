package com.example.lingro.ui.components

import android.content.Context
import android.media.MediaPlayer
import android.util.Log
import kotlinx.coroutines.*
import java.io.File
import java.net.HttpURLConnection
import java.net.URL
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import android.media.AudioAttributes
import java.io.FileOutputStream

// Make TTSManager a class that takes Context in constructor
class TTSManager(private val context: Context) {

    private var mediaPlayer: MediaPlayer? = null
    private var currentVoice by mutableStateOf("alloy") // Default voice
    private var onDone: (() -> Unit)? = null
    private var isSpeaking by mutableStateOf(false)
    private var lastFile: File? = null

    val availableVoices = listOf("alloy", "echo", "fable", "onyx", "nova", "shimmer", "ash", "sage", "coral")

    fun setVoice(voice: String) {
        if (availableVoices.contains(voice)) {
            currentVoice = voice
        }
    }

    suspend fun speak(
        text: String,
        voice: String,
        onDone: () -> Unit,
        onLoadingStart: () -> Unit,
        onLoadingEnd: () -> Unit
    ) {
        stop()
        this.onDone = onDone
        isSpeaking = true
        val useVoice = voice
        CoroutineScope(Dispatchers.IO).launch {
            try {
                withContext(Dispatchers.Main) { onLoadingStart.invoke() }
                val url = URL("https://lingro-proxy-production.up.railway.app/tts")
                val postData = "{\"input\":\"${text.replace("\"", "\\\"") }\",\"voice\":\"$useVoice\"}"
                // Для production можно отключить логи ниже
                // Log.d("TTSManager", "Запрос к $url с голосом $useVoice и текстом: $text")
                val conn = url.openConnection() as HttpURLConnection
                conn.requestMethod = "POST"
                conn.setRequestProperty("Content-Type", "application/json")
                conn.doOutput = true
                conn.outputStream.use { it.write(postData.toByteArray()) }
                if (conn.responseCode == 200) {
                    val tempFile = File.createTempFile("tts", ".mp3", context.cacheDir)
                    conn.inputStream.use { input ->
                        FileOutputStream(tempFile).use { output ->
                            input.copyTo(output)
                        }
                    }
                    withContext(Dispatchers.Main) {
                        onLoadingEnd.invoke()
                        playAudio(tempFile)
                    }
                    lastFile = tempFile
                    // Log.d("TTSManager", "Успешно получили и воспроизводим mp3")
                } else {
                    Log.e("TTSManager", "Ошибка HTTP: ${conn.responseCode}")
                    withContext(Dispatchers.Main) {
                        onLoadingEnd.invoke()
                        onDone.invoke()
                    }
                }
            } catch (e: Exception) {
                Log.e("TTSManager", "Ошибка TTS: ${e.message}", e)
                withContext(Dispatchers.Main) {
                    onLoadingEnd.invoke()
                    onDone.invoke()
                }
            }
        }
    }

    private fun playAudio(file: File) {
        stop()
        mediaPlayer = MediaPlayer().apply {
            setDataSource(file.absolutePath)
            setOnCompletionListener {
                isSpeaking = false
                onDone?.invoke()
            }
            setOnErrorListener { _, _, _ ->
                isSpeaking = false
                onDone?.invoke()
                true
            }
            prepare()
            start()
        }
    }

    fun stop() {
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
        isSpeaking = false
        onDone?.invoke()
        onDone = null
        lastFile?.delete()
        lastFile = null
    }
} 