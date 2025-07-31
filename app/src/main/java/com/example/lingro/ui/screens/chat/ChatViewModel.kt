package com.example.lingro.ui.screens.chat

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lingro.data.model.Message
import com.example.lingro.data.repository.ChatRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import android.content.Context
import java.io.File
import java.io.FileOutputStream
import android.util.Log

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val chatRepository: ChatRepository
) : ViewModel() {

    private val _messages = MutableStateFlow<List<Message>>(emptyList())
    val messages: StateFlow<List<Message>> = _messages.asStateFlow()

    private val _isTyping = MutableStateFlow(false)
    val isTyping: StateFlow<Boolean> = _isTyping.asStateFlow()

    private val _chatHistory = mutableListOf<List<Message>>()
    val chatHistory: List<List<Message>> get() = _chatHistory

    fun sendMessage(context: Context, content: String, attachmentUri: Uri? = null) {
        viewModelScope.launch {
            // Add user message (with attachment if exists)
            val userMessage = Message(content = content, isUser = true, attachmentUrl = attachmentUri?.toString(), attachmentType = if (attachmentUri != null) "image" else null)
            _messages.value = _messages.value + userMessage

            // Show typing indicator
            _isTyping.value = true

            try {
                val aiResponseContent: String
                var generatedImageUrl: String? = null

                if (attachmentUri != null) {
                    // Vision (текст + изображение)
                    // Нужно получить File из Uri. Простой способ для примера:
                    val file = getFileFromUri(context, attachmentUri)
                    if (file != null) {
                        aiResponseContent = chatRepository.getVisionResponse(file, content)
                    } else {
                        aiResponseContent = "Ошибка: не удалось обработать файл изображения."
                    }
                } else if (content.trim().startsWith("сгенерируй картинку", ignoreCase = true) ||
                           content.trim().startsWith("нарисуй", ignoreCase = true) ||
                           content.trim().startsWith("создай изображение", ignoreCase = true)) {
                    // Генерация изображения (DALL·E 3)
                    val prompt = content.trim()
                        .replaceFirst("сгенерируй картинку", "", ignoreCase = true)
                        .replaceFirst("нарисуй", "", ignoreCase = true)
                        .replaceFirst("создай изображение", "", ignoreCase = true)
                        .trim()
                    
                    // Для production можно отключить логи ниже
                    // Log.d("ChatViewModel", "Generated image prompt: '$prompt'")

                    if (prompt.isNotBlank()) {
                        generatedImageUrl = chatRepository.generateImage(prompt)
                        aiResponseContent = "Изображение сгенерировано:"
                    } else {
                        aiResponseContent = "Пожалуйста, укажите, что сгенерировать."
                    }
                } else {
                    // Обычный текстовый чат
                    aiResponseContent = chatRepository.getChatResponse(content)
                }

                // Add AI response
                val aiMessage = if (generatedImageUrl != null) {
                    Message(content = aiResponseContent, isUser = false, attachmentUrl = generatedImageUrl, attachmentType = "image")
                } else {
                    Message(content = aiResponseContent, isUser = false)
                }
                _messages.value = _messages.value + aiMessage

            } catch (e: Exception) {
                // Handle error
                val errorMessage = Message(
                    content = "Извините, произошла ошибка: ${e.message}",
                    isUser = false
                )
                _messages.value = _messages.value + errorMessage
            } finally {
                _isTyping.value = false
            }
        }
    }

    // Хелпер функция для получения File из Uri (может потребоваться доработка для разных Uri)
    private fun getFileFromUri(context: Context, uri: Uri): File? {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri)
            val file = File(context.cacheDir, "temp_image_${System.currentTimeMillis()}")
            inputStream?.use { input ->
                FileOutputStream(file).use { output ->
                    input.copyTo(output)
                }
            }
            file
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun resetConversation() {
        _messages.value = emptyList()
    }

    fun startNewChat() {
        if (_messages.value.isNotEmpty()) {
            _chatHistory.add(_messages.value)
            _messages.value = emptyList()
        }
    }
} 