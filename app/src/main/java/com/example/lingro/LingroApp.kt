package com.example.lingro

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * Main application class for Lingro.
 * 
 * This class serves as the entry point for the application and initializes
 * Hilt dependency injection. It extends [Application] and is annotated with
 * [@HiltAndroidApp] to enable Hilt throughout the application.
 * 
 * The application follows a clean architecture pattern with:
 * - UI layer using Jetpack Compose
 * - Business logic in ViewModels
 * - Data layer with Repository pattern
 * - Dependency injection via Hilt
 * 
 * @see com.example.lingro.ui.screens.MainScreen
 * @see com.example.lingro.ui.screens.chat.ChatViewModel
 * @see com.example.lingro.data.repository.ChatRepository
 */
@HiltAndroidApp
class LingroApp : Application() 