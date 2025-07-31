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

/**
 * Text-to-Speech manager for Lingro application.
 * 
 * This class handles text-to-speech functionality using OpenAI's TTS API
 * through a proxy server. It manages voice selection, audio playback,
 * and resource cleanup to prevent memory leaks.
 * 
 * Features:
 * - Multiple voice options (alloy, echo, fable, onyx, nova, shimmer, ash, sage, coral)
 * - Asynchronous audio processing
 * - Automatic resource cleanup
 * - Error handling and fallback
 * 
 * @param context The application context for file operations and MediaPlayer
 * 
 * @see com.example.lingro.ui.components.VoicePreferences
 */
class TTSManager(private val context: Context) {

    private var mediaPlayer: MediaPlayer? = null
    private var currentVoice by mutableStateOf("alloy") // Default voice
    private var onDone: (() -> Unit)? = null
    private var isSpeaking by mutableStateOf(false)
    private var lastFile: File? = null

    /**
     * List of available TTS voices.
     * 
     * These voices are provided by OpenAI's TTS API and offer different
     * characteristics and styles for text-to-speech conversion.
     */
    val availableVoices = listOf("alloy", "echo", "fable", "onyx", "nova", "shimmer", "ash", "sage", "coral")

    /**
     * Sets the current voice for TTS.
     * 
     * @param voice The voice name to use. Must be one of [availableVoices].
     */
    fun setVoice(voice: String) {
        if (availableVoices.contains(voice)) {
            currentVoice = voice
        }
    }

    /**
     * Converts text to speech and plays it asynchronously.
     * 
     * This method sends the text to the TTS proxy server, downloads the audio file,
     * and plays it using MediaPlayer. The process is fully asynchronous and includes
     * proper error handling and resource management.
     * 
     * @param text The text to convert to speech
     * @param voice The voice to use for speech synthesis
     * @param onDone Callback called when speech finishes or fails
     * @param onLoadingStart Callback called when loading starts
     * @param onLoadingEnd Callback called when loading ends
     */
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

    /**
     * Plays audio from a file using MediaPlayer.
     * 
     * @param file The audio file to play
     */
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

    /**
     * Stops current audio playback and cleans up resources.
     * 
     * This method should be called when the TTSManager is no longer needed
     * to prevent memory leaks. It stops MediaPlayer, releases resources,
     * and deletes temporary audio files.
     */
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