package com.example.lingro.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.lingro.ui.screens.role.RoleSelectionScreen
import com.example.lingro.ui.screens.chat.ChatScreen
import androidx.compose.material.icons.Icons
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import com.example.lingro.ui.screens.SettingsScreen
import com.example.lingro.ui.screens.ThemeMode
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.togetherWith
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.lingro.ui.screens.chat.ChatViewModel
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.HelpOutline
import androidx.compose.material.icons.outlined.Close
import android.speech.tts.Voice
import com.example.lingro.ui.components.VoicePreferences
import com.example.lingro.ui.components.TTSManager
import kotlinx.coroutines.launch
import androidx.compose.ui.platform.LocalContext
import androidx.compose.runtime.DisposableEffect


/**
 * Main screen of the Lingro application.
 * 
 * This screen serves as the primary interface for the chat application,
 * providing navigation to different features and managing the overall
 * application state. It includes:
 * - Chat interface with AI assistant
 * - Settings and configuration options
 * - Theme management (light/dark/system)
 * - Voice selection for TTS
 * - Role selection for AI assistant
 * 
 * The screen uses Material 3 design principles and follows the
 * single activity pattern with Compose navigation.
 * 
 * @param themeMode Current theme mode (LIGHT, DARK, SYSTEM)
 * @param onThemeModeChange Callback for theme mode changes
 * 
 * @see com.example.lingro.ui.screens.chat.ChatScreen
 * @see com.example.lingro.ui.screens.SettingsScreen
 * @see com.example.lingro.ui.screens.role.RoleSelectionScreen
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun MainScreen(
    themeMode: ThemeMode,
    onThemeModeChange: (ThemeMode) -> Unit
) {
    var selectedRole by remember { mutableStateOf<String?>(null) }
    var showSettings by remember { mutableStateOf(false) }
    var showAboutDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val ttsManager = remember { TTSManager(context) }
    DisposableEffect(Unit) {
        onDispose {
            ttsManager.stop()
        }
    }
    var selectedVoice by remember { mutableStateOf<String>(ttsManager.availableVoices.firstOrNull() ?: "alloy") }
    val scope = rememberCoroutineScope()

    val chatViewModel: ChatViewModel = hiltViewModel()

    // При старте — загрузить имя голоса и применить
    LaunchedEffect(Unit) {
        val voiceName = VoicePreferences.loadVoiceName(context)
        if (voiceName != null && ttsManager.availableVoices.contains(voiceName)) {
            selectedVoice = voiceName
            ttsManager.setVoice(voiceName)
        }
    }

    fun handleVoiceSelected(voice: String) {
        selectedVoice = voice
        ttsManager.setVoice(voice)
        scope.launch {
            VoicePreferences.saveVoiceName(context, voice)
        }
    }

    // AlertDialog теперь вне AnimatedContent
    if (showAboutDialog) {
        AlertDialog(
            onDismissRequest = { showAboutDialog = false },
            confirmButton = {
                TextButton(onClick = { showAboutDialog = false }) {
                    Text("OK")
                }
            },
            title = { Text("Справка") },
            text = {
                Text(
                    "В этом окне вы можете вести диалог с ИИ-помощником.\n\n" +
                    "• Введите сообщение внизу и отправьте.\n" +
                    "• Используйте вложения для отправки файлов.\n" +
                    "• Кнопка назад — возврат к выбору роли.\n\n" +
                    "Все ваши сообщения и ответы сохраняются в истории чата."
                )
            }
        )
    }

    Scaffold(
        topBar = {
            when {
                showSettings -> {
                    CenterAlignedTopAppBar(
                        title = {
                            Text(
                                "Настройки",
                                style = MaterialTheme.typography.headlineLarge
                            )
                        },
                        navigationIcon = {
                            IconButton(onClick = { showSettings = false }) {
                                Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "Назад")
                            }
                        },
                        actions = {}
                    )
                }
                selectedRole == null -> {
                    CenterAlignedTopAppBar(
                        title = {
                            Text(
                                "Lingro",
                                style = MaterialTheme.typography.headlineSmall.copy(fontFamily = com.example.lingro.ui.theme.Rubik),
                                modifier = Modifier.padding(top = 12.dp)
                            )
                        },
                        actions = {
                            IconButton(onClick = { showSettings = true }) {
                                Icon(Icons.Outlined.Settings, contentDescription = "Настройки")
                            }
                        }
                    )
                }
                else -> {
                    TopAppBar(
                        title = { Text("Чат", style = MaterialTheme.typography.headlineMedium) },
                        navigationIcon = {
                            IconButton(onClick = { selectedRole = null }) {
                                Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "Назад")
                            }
                        },
                        actions = {
                            IconButton(onClick = { showAboutDialog = true }) {
                                Icon(Icons.AutoMirrored.Outlined.HelpOutline, contentDescription = "Помощь")
                            }
                        }
                    )
                }
            }
        },
        containerColor = MaterialTheme.colorScheme.background,
        contentWindowInsets = WindowInsets(0)
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(innerPadding)
        ) {
            AnimatedContent(
                targetState = Triple(selectedRole, showSettings, showAboutDialog),
                transitionSpec = {
                    if (targetState.second && !initialState.second) {
                        slideInHorizontally(initialOffsetX = { it }) + fadeIn() togetherWith slideOutHorizontally(targetOffsetX = { -it }) + fadeOut()
                    } else if (!targetState.second && initialState.second) {
                        slideInHorizontally(initialOffsetX = { -it }) + fadeIn() togetherWith slideOutHorizontally(targetOffsetX = { it }) + fadeOut()
                    } else if (targetState.first == null && initialState.first != null) {
                        slideInHorizontally(initialOffsetX = { -it }) + fadeIn() togetherWith slideOutHorizontally(targetOffsetX = { it }) + fadeOut()
                    } else if (targetState.first != null && initialState.first == null) {
                        slideInHorizontally(initialOffsetX = { it }) + fadeIn() togetherWith slideOutHorizontally(targetOffsetX = { -it }) + fadeOut()
                    } else {
                        fadeIn() togetherWith fadeOut()
                    }
                }
            ) { (role, settings, _) ->
                when {
                    settings -> {
                        SettingsScreen(
                            currentTheme = themeMode,
                            onThemeChange = onThemeModeChange,
                            onAboutClick = { showAboutDialog = true },
                            onClose = { showSettings = false },
                            onClearChat = { chatViewModel.resetConversation() },
                            onVoiceSelected = { handleVoiceSelected(it) },
                            selectedVoice = selectedVoice,
                            ttsManager = ttsManager
                        )
                    }
                    role == null -> {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .widthIn(max = 600.dp)
                                .padding(horizontal = 24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            AnimatedVisibility(visible = true, enter = fadeIn()) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Spacer(modifier = Modifier.height(32.dp))
                                    RoleSelectionScreen(
                                        onRoleSelected = { selectedRole = it },
                                        paddingValues = innerPadding
                                    )
                                }
                            }
                        }
                    }
                    else -> {
                        ChatScreen(
                            onBackPressed = { selectedRole = null },
                            viewModel = chatViewModel,
                            selectedVoice = selectedVoice
                        )
                    }
                }
            }
        }
    }
} 