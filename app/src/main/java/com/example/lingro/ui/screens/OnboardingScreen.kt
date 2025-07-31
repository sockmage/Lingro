package com.example.lingro.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Language
import androidx.compose.material.icons.automirrored.outlined.VolumeUp
import androidx.compose.material.icons.outlined.ColorLens
import androidx.compose.material.icons.filled.PlayArrow
import com.example.lingro.ui.components.TTSManager
import com.example.lingro.ui.components.VoicePreferences
import kotlinx.coroutines.launch
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.material3.IconButton
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.material.icons.outlined.Info
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.windowInsetsTopHeight
import androidx.compose.foundation.layout.windowInsetsBottomHeight

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnboardingScreen(
    onFinish: () -> Unit,
    ttsManager: TTSManager,
    initialTheme: String? = null,
    onThemeSelected: (String) -> Unit = {}
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
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
    val themeList = listOf(
        "LIGHT" to "Светлая",
        "DARK" to "Тёмная",
        "SYSTEM" to "Системная"
    )
    var selectedLanguage by remember { mutableStateOf(languageList.first().first) }
    var selectedTheme by remember { mutableStateOf(initialTheme ?: "SYSTEM") }
    var selectedVoice by remember { mutableStateOf(ttsManager.availableVoices.firstOrNull() ?: "alloy") }
    var isSaving by remember { mutableStateOf(false) }
    var isPlaying by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var showLanguageDialog by remember { mutableStateOf(false) }
    var showThemeDialog by remember { mutableStateOf(false) }
    var showVoiceDialog by remember { mutableStateOf(false) }
    var showInfoDialog by remember { mutableStateOf(false) }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.windowInsetsTopHeight(WindowInsets.statusBars))
            Spacer(modifier = Modifier.height(16.dp))
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Outlined.Language,
                    contentDescription = "Lingro icon",
                    modifier = Modifier.size(48.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    "Добро пожаловать в Lingro!",
                    style = MaterialTheme.typography.headlineLarge,
                    modifier = Modifier
                        .padding(bottom = 0.dp)
                        .fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(8.dp))
                IconButton(
                    onClick = { showInfoDialog = true },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(Icons.Outlined.Info, contentDescription = "Информация", tint = MaterialTheme.colorScheme.primary)
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text("Выберите язык приложения", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(24.dp))
                ElevatedCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp)),
                    elevation = CardDefaults.elevatedCardElevation(defaultElevation = 1.dp),
                    colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(20.dp).fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Outlined.Language, contentDescription = null, modifier = Modifier.size(36.dp))
                        Spacer(Modifier.width(16.dp))
                        Column(Modifier.weight(1f)) {
                            Text(languageList.firstOrNull { it.first == selectedLanguage }?.second ?: "", style = MaterialTheme.typography.titleLarge)
                            Text("Язык интерфейса", style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant))
                        }
                        FilledTonalButton(
                            onClick = { showLanguageDialog = true },
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 0.dp),
                            modifier = Modifier.height(36.dp)
                        ) {
                            Text("Сменить", style = MaterialTheme.typography.labelLarge)
                        }
                    }
                }
                if (showLanguageDialog) {
                    AlertDialog(
                        onDismissRequest = { showLanguageDialog = false },
                        title = { Text("Выберите язык интерфейса") },
                        text = {
                            Column {
                                languageList.forEach { (code, /*name*/) ->
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable {
                                                selectedLanguage = code
                                                showLanguageDialog = false
                                            }
                                            .padding(vertical = 12.dp)
                                    ) {
                                        RadioButton(
                                            selected = selectedLanguage == code,
                                            onClick = {
                                                selectedLanguage = code
                                                showLanguageDialog = false
                                            }
                                        )
                                    }
                                }
                            }
                        },
                        confirmButton = {
                            TextButton(onClick = { showLanguageDialog = false }) {
                                Text("Закрыть")
                            }
                        }
                    )
                }
                Spacer(modifier = Modifier.height(24.dp))
                Text("Тема оформления", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(24.dp))
                ElevatedCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp)),
                    elevation = CardDefaults.elevatedCardElevation(defaultElevation = 1.dp),
                    colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(20.dp).fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Outlined.ColorLens, contentDescription = null, modifier = Modifier.size(36.dp))
                        Spacer(Modifier.width(16.dp))
                        Column(Modifier.weight(1f)) {
                            Text(themeList.firstOrNull { it.first == selectedTheme }?.second ?: "", style = MaterialTheme.typography.titleLarge)
                            Text("Светлая, тёмная или системная", style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant))
                        }
                        FilledTonalButton(
                            onClick = { showThemeDialog = true },
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 0.dp),
                            modifier = Modifier.height(36.dp)
                        ) {
                            Text("Сменить", style = MaterialTheme.typography.labelLarge)
                        }
                    }
                }
                if (showThemeDialog) {
                    AlertDialog(
                        onDismissRequest = { showThemeDialog = false },
                        title = { Text("Выберите тему оформления") },
                        text = {
                            Column {
                                themeList.forEach { (code, name) ->
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable {
                                                selectedTheme = code
                                                onThemeSelected(code)
                                                showThemeDialog = false
                                            }
                                            .padding(vertical = 12.dp)
                                    ) {
                                        RadioButton(
                                            selected = selectedTheme == code,
                                            onClick = {
                                                selectedTheme = code
                                                onThemeSelected(code)
                                                showThemeDialog = false
                                            }
                                        )
                                        Spacer(Modifier.width(12.dp))
                                        Text(name, style = MaterialTheme.typography.bodyLarge)
                                    }
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
                Spacer(modifier = Modifier.height(24.dp))
                Text("Голос ассистента", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(24.dp))
                ElevatedCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp)),
                    elevation = CardDefaults.elevatedCardElevation(defaultElevation = 1.dp),
                    colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(Modifier.padding(20.dp).fillMaxWidth()) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.AutoMirrored.Outlined.VolumeUp, contentDescription = null, modifier = Modifier.size(36.dp))
                            Spacer(Modifier.width(16.dp))
                            Column(Modifier.weight(1f)) {
                                Text(selectedVoice.replaceFirstChar { it.uppercase() }, style = MaterialTheme.typography.titleLarge)
                                Text("Голос для озвучивания", style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant))
                            }
                        }
                        Spacer(Modifier.height(16.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            FilledTonalButton(
                                onClick = { showVoiceDialog = true },
                                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 0.dp),
                                modifier = Modifier.height(36.dp)
                            ) {
                                Text("Сменить", style = MaterialTheme.typography.labelLarge)
                            }
                            Spacer(Modifier.width(12.dp))
                            FilledTonalButton(
                                onClick = {
                                    isLoading = true
                                    isPlaying = true
                                    coroutineScope.launch {
                                        ttsManager.speak(
                                            text = "Это пример голоса ассистента",
                                            voice = selectedVoice,
                                            onDone = { isPlaying = false },
                                            onLoadingStart = { isLoading = true },
                                            onLoadingEnd = { isLoading = false }
                                        )
                                    }
                                },
                                enabled = !isPlaying && !isLoading,
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp),
                                modifier = Modifier.height(36.dp)
                            ) {
                                if (isLoading || isPlaying) {
                                    CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                                } else {
                                    Icon(Icons.Filled.PlayArrow, contentDescription = "Прослушать голос", modifier = Modifier.size(18.dp))
                                    Spacer(Modifier.width(4.dp))
                                    Text("Прослушать", style = MaterialTheme.typography.labelLarge)
                                }
                            }
                        }
                    }
                }
                if (showVoiceDialog) {
                    AlertDialog(
                        onDismissRequest = { showVoiceDialog = false },
                        title = { Text("Выберите голос ассистента") },
                        text = {
                            Column {
                                ttsManager.availableVoices.forEach { voice ->
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable {
                                                selectedVoice = voice
                                                showVoiceDialog = false
                                            }
                                            .padding(vertical = 12.dp)
                                    ) {
                                        RadioButton(
                                            selected = selectedVoice == voice,
                                            onClick = {
                                                selectedVoice = voice
                                                showVoiceDialog = false
                                            }
                                        )
                                        Spacer(Modifier.width(12.dp))
                                        Text(voice.replaceFirstChar { it.uppercase() }, style = MaterialTheme.typography.bodyLarge)
                                    }
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
            }
            Spacer(modifier = Modifier.height(24.dp))
            FilledTonalButton(
                onClick = {
                    isSaving = true
                    coroutineScope.launch {
                        VoicePreferences.saveLanguage(context, selectedLanguage)
                        VoicePreferences.saveVoiceName(context, selectedVoice)
                        VoicePreferences.saveTheme(context, selectedTheme)
                        VoicePreferences.saveOnboardingDone(context, true)
                        isSaving = false
                        onFinish()
                    }
                },
                enabled = !isSaving,
                modifier = Modifier.fillMaxWidth().height(56.dp)
            ) {
                Text(if (isSaving) "Сохраняем..." else "Продолжить", style = MaterialTheme.typography.titleMedium)
            }
            Spacer(modifier = Modifier.windowInsetsBottomHeight(WindowInsets.navigationBars))
            Spacer(modifier = Modifier.height(40.dp))
        }
        if (showInfoDialog) {
            AlertDialog(
                onDismissRequest = { showInfoDialog = false },
                title = { Text("Можно изменить позже") },
                text = { Text("Все эти значения можно изменить позже в настройках.") },
                confirmButton = {
                    TextButton(onClick = { showInfoDialog = false }) {
                        Text("Понятно")
                    }
                }
            )
        }
    }
} 