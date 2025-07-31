package com.example.lingro.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.outlined.Send
import androidx.compose.material.icons.automirrored.outlined.VolumeUp
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.selection.selectable
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.shape.RoundedCornerShape
import android.speech.tts.Voice
import com.example.lingro.ui.components.TTSManager
import java.util.Locale
import androidx.compose.material.icons.automirrored.outlined.HelpOutline
import androidx.compose.material.icons.automirrored.outlined.Send
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.outlined.Language
import androidx.compose.material.icons.outlined.People
import kotlinx.coroutines.launch
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.material.icons.outlined.ColorLens
import androidx.compose.ui.res.stringResource
import com.example.lingro.R

@Composable
fun SettingCard(
    icon: ImageVector,
    title: String,
    subtitle: String? = null,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(24.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 1.dp,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                indication = rememberRipple(),
                interactionSource = remember { MutableInteractionSource() }
            ) { onClick() }
    ) {
        Row(
            Modifier.padding(horizontal = 20.dp, vertical = 18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = null, modifier = Modifier.size(28.dp))
            Spacer(Modifier.width(20.dp))
            Column {
                Text(title, style = MaterialTheme.typography.titleLarge)
                if (subtitle != null) {
                    Text(subtitle, style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant))
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    currentTheme: ThemeMode,
    onThemeChange: (ThemeMode) -> Unit,
    onAboutClick: () -> Unit,
    onClose: () -> Unit,
    onClearChat: () -> Unit,
    onVoiceSelected: (String) -> Unit = {},
    selectedVoice: String? = null,
    selectedLanguage: String? = null,
    onLanguageSelected: (String) -> Unit = {},
    paddingValues: PaddingValues = PaddingValues(0.dp),
    ttsManager: TTSManager
) {
    BackHandler(onBack = onClose)
    val context = LocalContext.current
    var showClearDialog by remember { mutableStateOf(false) }
    var showVoiceDialog by remember { mutableStateOf(false) }
    var showNoVoicesDialog by remember { mutableStateOf(false) }
    var showLanguageDialog by remember { mutableStateOf(false) }
    var showThemeDialog by remember { mutableStateOf(false) }
    var showTTSHelpDialog by remember { mutableStateOf(false) }
    val languageList = listOf(
        "ru" to "Русский",
        "en" to "English",
        "de" to "Deutsch",
        "fr" to "Français",
        "es" to "Español",
        "it" to "Italiano",
        "zh" to "中文",
        "ja" to "日本語",
        "ko" to "한국어"
    )
    val activity = (LocalContext.current as? android.app.Activity)
    val scope = rememberCoroutineScope()
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(0.dp)
    ) {
        item {
            Text("Тема", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(bottom = 6.dp, top = 16.dp))
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = MaterialTheme.colorScheme.surfaceVariant,
                tonalElevation = 2.dp,
                modifier = Modifier.padding(bottom = 28.dp)
            ) {
                Column(Modifier.padding(horizontal = 16.dp, vertical = 10.dp)) {
                    ThemeMode.entries.forEachIndexed { idx, mode ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(20.dp))
                                .clickable(
                                    indication = rememberRipple(bounded = true),
                                    interactionSource = remember { MutableInteractionSource() }
                                ) { onThemeChange(mode) }
                                .padding(vertical = 16.dp, horizontal = 8.dp)
                        ) {
                            RadioButton(
                                selected = currentTheme == mode,
                                onClick = { onThemeChange(mode) },
                                colors = RadioButtonDefaults.colors(selectedColor = MaterialTheme.colorScheme.primary)
                            )
                            Spacer(Modifier.width(16.dp))
                            Text(mode.displayName, style = MaterialTheme.typography.bodyLarge)
                        }
                        if (idx != ThemeMode.entries.lastIndex) {
                            Box(
                                Modifier
                                    .fillMaxWidth()
                                    .height(1.dp)
                                    .padding(start = 52.dp)
                                    .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f))
                            )
                        }
                    }
                }
            }
        }
        item {
            Text("Озвучивание", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(bottom = 6.dp, top = 8.dp))
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = MaterialTheme.colorScheme.surfaceVariant,
                tonalElevation = 2.dp,
                modifier = Modifier.padding(bottom = 28.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .clickable(
                            indication = rememberRipple(bounded = true),
                            interactionSource = remember { MutableInteractionSource() }
                        ) { showVoiceDialog = true }
                        .padding(vertical = 16.dp, horizontal = 16.dp)
                ) {
                    Icon(Icons.AutoMirrored.Outlined.VolumeUp, contentDescription = "Изменить голос", modifier = Modifier.size(24.dp))
                    Spacer(Modifier.width(16.dp))
                    Text("Изменить голос", style = MaterialTheme.typography.bodyLarge)
                }
            }
        }
        item {
            Text("Информация", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(bottom = 6.dp, top = 8.dp))
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = MaterialTheme.colorScheme.surfaceVariant,
                tonalElevation = 2.dp,
                modifier = Modifier.padding(bottom = 28.dp)
            ) {
                Column(Modifier.padding(horizontal = 16.dp, vertical = 10.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(20.dp))
                            .clickable(
                                indication = rememberRipple(bounded = true),
                                interactionSource = remember { MutableInteractionSource() }
                            ) {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://t.me/Language_assistant1_bot"))
                                context.startActivity(intent)
                            }
                            .padding(vertical = 16.dp, horizontal = 8.dp)
                    ) {
                        Icon(Icons.Outlined.People, contentDescription = "Мы в Telegram", modifier = Modifier.size(24.dp))
                        Spacer(Modifier.width(16.dp))
                        Text("Мы в Telegram", style = MaterialTheme.typography.bodyLarge)
                    }
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .padding(start = 52.dp)
                            .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f))
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(20.dp))
                            .clickable(
                                indication = rememberRipple(bounded = true),
                                interactionSource = remember { MutableInteractionSource() }
                            ) { onAboutClick() }
                            .padding(vertical = 16.dp, horizontal = 8.dp)
                    ) {
                        Icon(Icons.Outlined.Info, contentDescription = "Справка", modifier = Modifier.size(24.dp))
                        Spacer(Modifier.width(16.dp))
                        Text("Справка", style = MaterialTheme.typography.bodyLarge)
                    }
                }
            }
        }
        item {
            Text("Действия", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(bottom = 6.dp, top = 8.dp))
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = MaterialTheme.colorScheme.surfaceVariant,
                tonalElevation = 2.dp,
                modifier = Modifier.padding(bottom = 28.dp)
            ) {
                Column(Modifier.padding(horizontal = 16.dp, vertical = 10.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(20.dp))
                            .clickable(
                                indication = rememberRipple(bounded = true),
                                interactionSource = remember { MutableInteractionSource() }
                            ) { showLanguageDialog = true }
                            .padding(vertical = 16.dp, horizontal = 8.dp)
                    ) {
                        Icon(Icons.Outlined.Language, contentDescription = "Сменить язык", modifier = Modifier.size(24.dp))
                        Spacer(Modifier.width(16.dp))
                        Text("Сменить язык", style = MaterialTheme.typography.bodyLarge)
                    }
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .padding(start = 52.dp)
                            .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f))
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(20.dp))
                            .clickable(
                                indication = rememberRipple(bounded = true),
                                interactionSource = remember { MutableInteractionSource() }
                            ) { showClearDialog = true }
                            .padding(vertical = 16.dp, horizontal = 8.dp)
                    ) {
                        Icon(Icons.Outlined.Delete, contentDescription = "Очистить чат", modifier = Modifier.size(24.dp))
                        Spacer(Modifier.width(16.dp))
                        Text("Очистить чат", style = MaterialTheme.typography.bodyLarge)
                    }
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .padding(start = 52.dp)
                            .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f))
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(20.dp))
                            .clickable(
                                indication = rememberRipple(bounded = true),
                                interactionSource = remember { MutableInteractionSource() }
                            ) {
                                scope.launch {
                                    com.example.lingro.ui.components.VoicePreferences.saveOnboardingDone(context, false)
                                    activity?.recreate()
                                }
                            }
                            .padding(vertical = 16.dp, horizontal = 8.dp)
                    ) {
                        Icon(Icons.Outlined.Refresh, contentDescription = "Начать первоначальную настройку", modifier = Modifier.size(24.dp))
                        Spacer(Modifier.width(16.dp))
                        Text("Начать первоначальную настройку", style = MaterialTheme.typography.bodyLarge)
                    }
                }
            }
        }
    }
    if (showThemeDialog) {
        AlertDialog(
            onDismissRequest = { showThemeDialog = false },
            title = { Text("Выберите тему оформления") },
            text = {
                Column {
                    ThemeMode.entries.forEach { mode ->
                        ListItem(
                            headlineContent = { Text(mode.displayName) },
                            leadingContent = {
                                RadioButton(
                                    selected = currentTheme == mode,
                                    onClick = { onThemeChange(mode); showThemeDialog = false }
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onThemeChange(mode); showThemeDialog = false }
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showThemeDialog = false }) {
                    Text("Закрыть")
                }
            }
        )
    }
    if (showClearDialog) {
        AlertDialog(
            onDismissRequest = { showClearDialog = false },
            title = { Text("Вы уверены?") },
            text = { Text("Вы потеряете всю историю чата") },
            confirmButton = {
                Button(
                    onClick = {
                        showClearDialog = false
                        onClearChat()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Да", color = MaterialTheme.colorScheme.onError)
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearDialog = false }) {
                    Text("Нет")
                }
            }
        )
    }
    if (showNoVoicesDialog) {
        AlertDialog(
            onDismissRequest = { showNoVoicesDialog = false },
            title = { Text("Нет голосов") },
            text = { Text("На устройстве не найдено голосов для озвучивания. Проверьте настройки TTS или установите голосовые движки.") },
            confirmButton = {
                TextButton(onClick = { showNoVoicesDialog = false }) {
                    Text("ОК")
                }
            }
        )
    }
    var selectedLanguage by remember { mutableStateOf("ru") } // Default to Russian
    
    // Language selection dialog
    if (showLanguageDialog) {
        AlertDialog(
            onDismissRequest = { showLanguageDialog = false },
            title = { Text(stringResource(R.string.settings_language)) },
            text = {
                Column {
                    Text(
                        text = stringResource(R.string.settings_language_coming_soon),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Language options
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(stringResource(R.string.settings_language_russian))
                        RadioButton(
                            selected = selectedLanguage == "ru",
                            onClick = { selectedLanguage = "ru" }
                        )
                    }
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(stringResource(R.string.settings_language_english))
                        RadioButton(
                            selected = selectedLanguage == "en",
                            onClick = { selectedLanguage = "en" }
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showLanguageDialog = false }) {
                    Text(stringResource(R.string.settings_clear_history_confirm_negative))
                }
            }
        )
    }
    if (showVoiceDialog) {
        AlertDialog(
            onDismissRequest = { showVoiceDialog = false },
            title = { Text("Выберите голос ChatGPT") },
            text = {
                Column {
                    ttsManager.availableVoices.forEach { voice ->
                        ListItem(
                            headlineContent = { Text(voice.replaceFirstChar { it.uppercase() }) },
                            leadingContent = {
                                RadioButton(
                                    selected = selectedVoice == voice,
                                    onClick = {
                                        onVoiceSelected(voice)
                                        ttsManager.setVoice(voice)
                                        showVoiceDialog = false
                                    }
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onVoiceSelected(voice)
                                    ttsManager.setVoice(voice)
                                    showVoiceDialog = false
                                }
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showVoiceDialog = false }) {
                    Text("Закрыть")
                }
            }
        )
    }
    if (showTTSHelpDialog) {
        AlertDialog(
            onDismissRequest = { showTTSHelpDialog = false },
            title = { Text("Как сменить или установить голос TTS?") },
            text = {
                Text("1. Нажмите 'Установить новые голоса'. Откроются системные настройки TTS.\n\n2. Выберите движок (например, Google TTS) и перейдите в его настройки.\n\n3. В настройках выберите язык и скачайте нужные голоса.\n\n4. После установки вернитесь в приложение и выберите голос в разделе 'Выбрать голос'.\n\n⚠️ Некоторые голоса требуют интернет или загрузки через Play Market.")
            },
            confirmButton = {
                TextButton(onClick = { showTTSHelpDialog = false }) {
                    Text("Понятно")
                }
            }
        )
    }
}

enum class ThemeMode(val displayName: String) {
    LIGHT("Светлая"),
    DARK("Тёмная"),
    SYSTEM("Системная")
}

@Composable
fun ActionRow(icon: ImageVector, text: String, onClick: () -> Unit, contentPadding: PaddingValues) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(contentPadding)
    ) {
        Icon(icon, contentDescription = text, modifier = Modifier.size(24.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text(text, style = MaterialTheme.typography.bodyLarge)
    }
}

@Composable
fun ActionDivider() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f))
    )
} 