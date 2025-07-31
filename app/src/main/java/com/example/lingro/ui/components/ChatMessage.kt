package com.example.lingro.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material.icons.outlined.Download
import androidx.compose.material.icons.automirrored.outlined.VolumeUp
import androidx.compose.material.icons.outlined.Stop
import androidx.compose.ui.graphics.Color
import com.example.lingro.data.model.Message
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import androidx.compose.foundation.background
import androidx.compose.ui.text.style.TextOverflow
import java.text.SimpleDateFormat
import java.util.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.clickable
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.painterResource
import coil.compose.AsyncImage
import androidx.compose.ui.draw.clip
import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Environment
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.graphics.Shape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.draw.alpha
import androidx.compose.foundation.background
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.IntOffset
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.animation.core.tween
import android.content.Intent
import android.widget.Toast
import com.example.lingro.ui.components.ShareUtils
import androidx.compose.material.icons.outlined.Share
import androidx.compose.ui.res.stringResource
import com.example.lingro.R

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ChatMessage(
    message: Message,
    onSpeak: (Message) -> Unit = {},
    isSpeaking: Boolean = false,
    isTtsLoading: Boolean = false,
    onStopSpeak: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val shareUtils = remember { ShareUtils(context) }
    val clipboardManager = LocalClipboardManager.current
    var showCopied by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val parsed = parseMarkdown(message.content)
    val time = remember(message) {
        val date = Date(message.timestamp)
        SimpleDateFormat("HH:mm", Locale.getDefault()).format(date)
    }
    // (удалена переменная configuration)
    val userBubbleColor = MaterialTheme.colorScheme.primaryContainer
    val assistantBubbleColor = MaterialTheme.colorScheme.surfaceVariant
    val userShape: Shape = RoundedCornerShape(topStart = 20.dp, topEnd = 4.dp, bottomEnd = 20.dp, bottomStart = 20.dp)
    val assistantShape: Shape = RoundedCornerShape(topStart = 4.dp, topEnd = 20.dp, bottomEnd = 20.dp, bottomStart = 20.dp)
    val imageShapeUser = RoundedCornerShape(12.dp)
    val imageShapeAssistant = RoundedCornerShape(12.dp)
    val ttsScale by animateFloatAsState(
        targetValue = if (isSpeaking) 1.25f else 1f,
        animationSpec = tween(durationMillis = 600), label = "ttsScale"
    )
    val ttsAlpha by animateFloatAsState(
        targetValue = if (isSpeaking) 1f else 0.7f,
        animationSpec = tween(durationMillis = 600), label = "ttsAlpha"
    )
    // TTS ICON
    val hasVisibleText = message.content.isNotBlank()
    val isImageOnlyMessage = message.attachmentUrl != null && !hasVisibleText

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp, horizontal = 0.dp),
        horizontalArrangement = if (message.isUser) Arrangement.End else Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Кнопки для пользователя (слева, вертикально)
        if (message.isUser) {
            Column(
                modifier = Modifier.padding(end = 8.dp).fillMaxHeight(),
                verticalArrangement = Arrangement.Center
            ) {
                // TTS ICON
                if (!isImageOnlyMessage) {
                    if (isTtsLoading) {
                        Box(
                            modifier = Modifier.size(20.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                strokeWidth = 2.dp
                            )
                        }
                    } else {
                        IconButton(
                            onClick = { if (isSpeaking) onStopSpeak() else onSpeak(message) },
                            modifier = Modifier
                                .size(20.dp)
                                .alpha(ttsAlpha)
                                .graphicsLayer { scaleX = ttsScale; scaleY = ttsScale }
                        ) {
                            if (isSpeaking) {
                                Icon(
                                    imageVector = Icons.Outlined.Stop,
                                    contentDescription = "Остановить озвучивание",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(16.dp)
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Outlined.VolumeUp,
                                    contentDescription = "Озвучить",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
                if (message.attachmentUrl != null && message.attachmentType != null) {
                    IconButton(
                        onClick = {
                            clipboardManager.setText(AnnotatedString(message.attachmentUrl))
                            showCopied = true
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar("Скопировано!")
                            }
                        },
                        modifier = Modifier.size(20.dp).alpha(0.5f)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.ContentCopy,
                            contentDescription = "Копировать",
                            tint = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f),
                            modifier = Modifier.size(14.dp)
                        )
                    }
                } else {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        IconButton(
                            onClick = {
                                clipboardManager.setText(AnnotatedString(message.content))
                                showCopied = true
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar("Скопировано!")
                                }
                            },
                            modifier = Modifier.size(20.dp).alpha(0.5f)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.ContentCopy,
                                contentDescription = "Копировать",
                                tint = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f),
                                modifier = Modifier.size(14.dp)
                            )
                        }
                        
                        IconButton(
                            onClick = { shareUtils.shareMessage(message) },
                            modifier = Modifier.size(20.dp).alpha(0.5f)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Share,
                                contentDescription = stringResource(R.string.chat_share_message),
                                tint = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f),
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                }
            }
        }
        // Бабл
        Card(
            modifier = Modifier
                .wrapContentWidth()
                .wrapContentHeight(),
            colors = CardDefaults.cardColors(
                containerColor = if (message.isUser) userBubbleColor else assistantBubbleColor
            ),
            shape = if (message.isUser) userShape else assistantShape,
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(horizontal = 14.dp, vertical = 10.dp)
            ) {
                parsed.forEach { part ->
                    when (part) {
                        is MarkdownPart.Code -> Text(
                            text = part.text,
                            fontFamily = FontFamily.Monospace,
                            color = MaterialTheme.colorScheme.primary,
                            fontSize = MaterialTheme.typography.bodySmall.fontSize,
                            modifier = Modifier
                                .padding(vertical = 2.dp, horizontal = 2.dp)
                                .background(MaterialTheme.colorScheme.surface, MaterialTheme.shapes.small)
                                .padding(4.dp)
                        )
                        is MarkdownPart.Bold -> Text(
                            text = part.text,
                            fontWeight = FontWeight.Bold,
                            fontSize = MaterialTheme.typography.bodySmall.fontSize,
                            color = if (message.isUser) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.wrapContentWidth()
                        )
                        is MarkdownPart.Italic -> Text(
                            text = part.text,
                            fontSize = MaterialTheme.typography.bodySmall.fontSize,
                            color = if (message.isUser) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.wrapContentWidth()
                        )
                        is MarkdownPart.Text -> Text(
                            text = part.text,
                            fontSize = MaterialTheme.typography.bodySmall.fontSize,
                            color = if (message.isUser) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant,
                            softWrap = true
                        )
                    }
                }
                // Отображение вложения, если оно есть
                if (message.attachmentUrl != null && message.attachmentType != null) {
                    Spacer(Modifier.height(8.dp))
                    when (message.attachmentType) {
                        "image" -> {
                            AsyncImage(
                                model = message.attachmentUrl,
                                contentDescription = "Вложенное изображение",
                                modifier = Modifier
                                    .padding(8.dp)
                                    .clip(if (message.isUser) imageShapeUser else imageShapeAssistant)
                            )
                        }
                        "file" -> {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Outlined.ContentCopy, // Можно заменить на иконку файла
                                    contentDescription = "Вложение файл",
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(Modifier.width(8.dp))
                                Text(
                                    text = message.attachmentUrl.substringAfterLast('/'),
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.clickable {
                                        // TODO: Реализовать открытие/скачивание файла
                                    }
                                )
                            }
                        }
                    }
                }
                Spacer(Modifier.height(2.dp))
                Row(
                    horizontalArrangement = if (message.isUser) Arrangement.Start else Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = time,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.7f),
                        modifier = Modifier.padding(end = 2.dp)
                    )
                }
            }
        }
        // Кнопки для ассистента (справа, вертикально)
        if (!message.isUser) {
            val context = LocalContext.current
            Column(
                modifier = Modifier.padding(start = 8.dp).fillMaxHeight(),
                verticalArrangement = Arrangement.Center
            ) {
                // TTS ICON
                if (!isImageOnlyMessage) {
                    if (isTtsLoading) {
                        Box(
                            modifier = Modifier.size(20.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                strokeWidth = 2.dp
                            )
                        }
                    } else {
                        IconButton(
                            onClick = { if (isSpeaking) onStopSpeak() else onSpeak(message) },
                            modifier = Modifier
                                .size(20.dp)
                                .alpha(ttsAlpha)
                                .graphicsLayer { scaleX = ttsScale; scaleY = ttsScale }
                        ) {
                            if (isSpeaking) {
                                Icon(
                                    imageVector = Icons.Outlined.Stop,
                                    contentDescription = "Остановить озвучивание",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(16.dp)
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Outlined.VolumeUp,
                                    contentDescription = "Озвучить",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
                if (message.attachmentUrl != null && message.attachmentType == "image") {
                    IconButton(
                        onClick = {
                            val url = message.attachmentUrl
                            try {
                                val request = DownloadManager.Request(Uri.parse(url))
                                    .setTitle("Сохранение изображения")
                                    .setDescription(url.substringAfterLast('/'))
                                    .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                                    .setDestinationInExternalPublicDir(Environment.DIRECTORY_PICTURES, url.substringAfterLast('/'))
                                val dm = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
                                dm.enqueue(request)
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar("Изображение сохранено")
                                }
                            } catch (e: Exception) {
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar("Ошибка сохранения: ${e.message}")
                                }
                            }
                        },
                        modifier = Modifier.size(20.dp).alpha(0.5f)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Download,
                            contentDescription = "Сохранить изображение",
                            tint = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
                if (message.attachmentUrl != null) {
                    IconButton(
                        onClick = {
                            clipboardManager.setText(AnnotatedString(message.attachmentUrl))
                            showCopied = true
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar("Скопировано!")
                            }
                        },
                        modifier = Modifier.size(20.dp).alpha(0.5f)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.ContentCopy,
                            contentDescription = "Копировать",
                            tint = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                } else {
                    IconButton(
                        onClick = {
                            clipboardManager.setText(AnnotatedString(message.content))
                            showCopied = true
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar("Скопировано!")
                            }
                        },
                        modifier = Modifier.size(20.dp).alpha(0.5f)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.ContentCopy,
                            contentDescription = "Копировать",
                            tint = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f),
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AnimatedChatMessage(message: Message, content: @Composable (Message) -> Unit = { ChatMessage(it) }) {
    var appeared by remember { mutableStateOf(false) }
    val alpha by animateFloatAsState(if (appeared) 1f else 0f, label = "alpha")
    val offsetX by animateDpAsState(
        if (appeared) 0.dp else if (message.isUser) 60.dp else (-60).dp,
        label = "offset"
    )
    LaunchedEffect(Unit) { appeared = true }

    Box(
        Modifier
            .graphicsLayer { this.alpha = alpha }
            .offset(x = offsetX)
    ) {
        content(message)
    }
}

sealed class MarkdownPart {
    data class Text(val text: String) : MarkdownPart()
    data class Bold(val text: String) : MarkdownPart()
    data class Italic(val text: String) : MarkdownPart()
    data class Code(val text: String) : MarkdownPart()
}

fun parseMarkdown(text: String): List<MarkdownPart> {
    val result = mutableListOf<MarkdownPart>()
    var i = 0
    while (i < text.length) {
        when {
            text.startsWith("```", i) -> {
                val end = text.indexOf("```", i + 3)
                if (end != -1) {
                    result.add(MarkdownPart.Code(text.substring(i + 3, end)))
                    i = end + 3
                } else {
                    result.add(MarkdownPart.Text(text.substring(i)))
                    break
                }
            }
            text.startsWith("**", i) -> {
                val end = text.indexOf("**", i + 2)
                if (end != -1) {
                    result.add(MarkdownPart.Bold(text.substring(i + 2, end)))
                    i = end + 2
                } else {
                    result.add(MarkdownPart.Text(text.substring(i)))
                    break
                }
            }
            text.startsWith("*", i) -> {
                val end = text.indexOf("*", i + 1)
                if (end != -1) {
                    result.add(MarkdownPart.Italic(text.substring(i + 1, end)))
                    i = end + 1
                } else {
                    result.add(MarkdownPart.Text(text.substring(i)))
                    break
                }
            }
            else -> {
                val next = listOf(
                    text.indexOf("```", i).takeIf { it != -1 },
                    text.indexOf("**", i).takeIf { it != -1 },
                    text.indexOf("*", i).takeIf { it != -1 }
                ).filterNotNull().minOrNull() ?: text.length
                result.add(MarkdownPart.Text(text.substring(i, next)))
                i = next
            }
        }
    }
    return result
} 