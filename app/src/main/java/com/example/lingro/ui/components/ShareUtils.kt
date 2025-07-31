package com.example.lingro.ui.components

import android.content.Context
import android.content.Intent
import com.example.lingro.data.model.Message

/**
 * Utility class for sharing chat messages.
 * 
 * This class provides functionality to share chat messages with other apps
 * through Android's share intent system.
 * 
 * @param context The application context
 */
class ShareUtils(private val context: Context) {
    
    /**
     * Shares a chat message with other applications.
     * 
     * Creates an intent to share the message content with other apps
     * that can handle text sharing (messaging apps, social media, etc.).
     * 
     * @param message The message to share
     */
    fun shareMessage(message: Message) {
        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, formatMessageForSharing(message))
            putExtra(Intent.EXTRA_SUBJECT, "Lingro Chat Message")
        }
        
        val chooserIntent = Intent.createChooser(shareIntent, "Share via")
        chooserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(chooserIntent)
    }
    
    /**
     * Formats a message for sharing.
     * 
     * Creates a formatted string that includes the message content,
     * timestamp, and app branding.
     * 
     * @param message The message to format
     * @return Formatted string ready for sharing
     */
    private fun formatMessageForSharing(message: Message): String {
        val sender = if (message.isUser) "You" else "AI Assistant"
        val timestamp = message.timestamp?.let { 
            java.text.SimpleDateFormat("HH:mm", java.util.Locale.getDefault()).format(it)
        } ?: ""
        
        return buildString {
            appendLine("$sender ($timestamp):")
            appendLine(message.content)
            appendLine()
            appendLine("Shared from Lingro - AI Assistant")
        }
    }
    
    /**
     * Shares multiple messages as a conversation.
     * 
     * @param messages List of messages to share
     */
    fun shareConversation(messages: List<Message>) {
        val conversationText = buildString {
            appendLine("Lingro Chat Conversation")
            appendLine("=".repeat(30))
            appendLine()
            
            messages.forEach { message ->
                val sender = if (message.isUser) "You" else "AI Assistant"
                val timestamp = message.timestamp?.let { 
                    java.text.SimpleDateFormat("HH:mm", java.util.Locale.getDefault()).format(it)
                } ?: ""
                
                appendLine("$sender ($timestamp):")
                appendLine(message.content)
                appendLine()
            }
            
            appendLine("Shared from Lingro - AI Assistant")
        }
        
        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, conversationText)
            putExtra(Intent.EXTRA_SUBJECT, "Lingro Chat Conversation")
        }
        
        val chooserIntent = Intent.createChooser(shareIntent, "Share via")
        chooserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(chooserIntent)
    }
} 