package com.example.lingro

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.lingro.ui.theme.LingroTheme
import com.example.lingro.ui.screens.MainScreen
import com.example.lingro.ui.screens.ThemeMode
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.isSystemInDarkTheme
import dagger.hilt.android.AndroidEntryPoint
import androidx.compose.runtime.mutableStateOf
import com.example.lingro.ui.screens.OnboardingScreen
import com.example.lingro.ui.components.TTSManager
import com.example.lingro.ui.components.VoicePreferences
import kotlinx.coroutines.runBlocking
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.DisposableEffect
import com.example.lingro.ui.screens.SplashScreen

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val context = LocalContext.current
            var showSplash by rememberSaveable { mutableStateOf(true) }
            var showOnboarding by rememberSaveable { mutableStateOf(false) }
            var themeMode by rememberSaveable { mutableStateOf(ThemeMode.SYSTEM) }
            val ttsManager = remember { TTSManager(context) }
            DisposableEffect(Unit) {
                onDispose {
                    ttsManager.stop()
                }
            }
            var onboardingTheme by rememberSaveable { mutableStateOf(themeMode.name) }
            LaunchedEffect(Unit) {
                runBlocking {
                    showOnboarding = !VoicePreferences.loadOnboardingDone(context)
                    val themeStr = VoicePreferences.loadTheme(context)
                    themeMode = when(themeStr) {
                        "DARK" -> ThemeMode.DARK
                        "LIGHT" -> ThemeMode.LIGHT
                        "SYSTEM", null -> ThemeMode.SYSTEM
                        else -> ThemeMode.SYSTEM
                    }
                }
            }
            LingroTheme(
                darkTheme = when(themeMode) {
                    ThemeMode.DARK -> true
                    ThemeMode.LIGHT -> false
                    ThemeMode.SYSTEM -> isSystemInDarkTheme()
                }
            ) {
                if (showSplash) {
                    SplashScreen(
                        onSplashFinished = {
                            showSplash = false
                        }
                    )
                } else {
                    AnimatedContent(
                        targetState = showOnboarding,
                        transitionSpec = {
                            scaleIn(initialScale = 0.96f) + fadeIn() togetherWith scaleOut(targetScale = 1.04f) + fadeOut()
                        }
                    ) { onboarding ->
                        if (onboarding) {
                            OnboardingScreen(
                                onFinish = {
                                    showOnboarding = false
                                    themeMode = ThemeMode.valueOf(onboardingTheme)
                                },
                                ttsManager = ttsManager,
                                initialTheme = themeMode.name,
                                onThemeSelected = { code ->
                                    onboardingTheme = code
                                    themeMode = ThemeMode.valueOf(code)
                                }
                            )
                        } else {
                            MainScreen(
                                themeMode = themeMode,
                                onThemeModeChange = { newMode ->
                                    themeMode = newMode
                                    runBlocking {
                                        VoicePreferences.saveTheme(context, newMode.name)
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}