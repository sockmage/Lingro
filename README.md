# Lingro

![Gradle](https://img.shields.io/badge/Gradle-app?style=flat&logo=Gradle&logoColor=%23000000&labelColor=%23babeff&color=%23000000)
![Android](https://img.shields.io/badge/Android-app?style=flat&logo=Android&logoColor=%23000000&labelColor=%233DDC84&color=%23000000)
![GoogleFonts](https://img.shields.io/badge/Google%20Fonts-app?style=flat&logo=Google%20Fonts&color=%23000000)
![AndroidStudio](https://img.shields.io/badge/Android%20Studio-app?style=flat&logo=Android%20Studio&labelColor=%23000000&color=%23000000)
![JetpackCompose](https://img.shields.io/badge/Jetpack%20Compose-app?style=flat&logo=Jetpack%20Compose&labelColor=%23000000&color=%23000000)

**Lingro** — современное Android-приложение для общения с ИИ-ассистентом на базе OpenAI GPT, с поддержкой поиска и отправки изображений, а также работы с PDF и Vision API. Этот репозиторий можно использовать как пример или основу для создания собственных чат-ботов и ассистентов на Android.

---

## Особенности

- Современный дизайн на базе Material 3 (Jetpack Compose)
  - Кастомный шрифт [Rubik](https://fonts.google.com/specimen/Rubik)
  - Светлая и тёмная тема
- Общение с ChatGPT через собственный прокси (обход региональных ограничений)
- Поиск и отправка изображений через DuckDuckGo (через отдельный Python-прокси)
- Отправка PDF-файлов для анализа через Vision API
- Поддержка вложений, история чатов
- Гибкая архитектура: легко расширять и модифицировать
- Пример интеграции TTS (Text-to-Speech) и выбора голоса
- Приложение — Android-ответвление проекта [tggpt (Telegram-бот)](https://github.com/mxlskh/tggpt)

## Архитектура

- Все текстовые и PDF-запросы идут через [Lingro-Proxy (Node.js)](https://github.com/sockmage/Lingro-Proxy)
- Поиск изображений — через [DuckDuckGo Image API for Lingro (Python FastAPI)](https://github.com/sockmage/DDG-Image-API-for-Lingro)
- Приложение не хранит ключи OpenAI/Unsplash и не зависит от региона пользователя

## Быстрый старт

### Требования
- Android Studio (рекомендуется последняя версия)
- JDK 17+
- Android SDK (API 24+)
- Интернет-соединение

### Сборка и запуск
1. Клонируйте репозиторий:
   ```sh
   git clone https://github.com/yourusername/Lingro.git
   cd Lingro
   ```
2. Откройте проект в Android Studio.
3. Дождитесь синхронизации Gradle (автоматически подтянутся все зависимости).
4. Соберите и запустите проект на эмуляторе или устройстве.

### Настройка backend
Для полноценной работы приложения необходимы backend-сервисы:
- [Lingro-Proxy (Node.js)](https://github.com/sockmage/Lingro-Proxy) — прокси для OpenAI и Vision API
- [DuckDuckGo Image API for Lingro (Python)](https://github.com/sockmage/DDG-Image-API-for-Lingro) — поиск изображений

#### Пример деплоя на Railway
1. Зарегистрируйтесь на [Railway](https://railway.app/).
2. Создайте проект и подключите нужный репозиторий (см. ссылки выше).
3. Для Lingro-Proxy добавьте переменную окружения `OPENAI_API_KEY`.
4. Запустите деплой — Railway сам соберёт и запустит сервисы.

## Использование
- После запуска приложения пройдите onboarding (выбор темы, настройка голоса).
- Основной экран — чат с ИИ (ChatGPT), поддержка вложений, изображений, PDF.
- Можно выбрать роль ассистента, голос для TTS, переключать темы.
- Все сетевые запросы идут через ваши backend-сервисы (см. выше).

## Технологии
- [Jetpack Compose](https://developer.android.com/jetpack/compose) — UI
- [Hilt (Dagger)](https://dagger.dev/hilt/) — DI
- [Retrofit](https://square.github.io/retrofit/) и [OkHttp](https://square.github.io/okhttp/) — сеть
- [Coil](https://coil-kt.github.io/coil/) — загрузка изображений
- [Markwon](https://noties.io/Markwon/) — markdown
- Material Icons, кастомный шрифт Rubik

## Лицензия

Проект распространяется по лицензии MIT. Подробнее см. файл [LICENSE](LICENSE).

---

# Lingro (English)

![Gradle](https://img.shields.io/badge/Gradle-app?style=flat&logo=Gradle&logoColor=%23000000&labelColor=%23babeff&color=%23000000)
![Android](https://img.shields.io/badge/Android-app?style=flat&logo=Android&logoColor=%23000000&labelColor=%233DDC84&color=%23000000)
![GoogleFonts](https://img.shields.io/badge/Google%20Fonts-app?style=flat&logo=Google%20Fonts&color=%23000000)
![AndroidStudio](https://img.shields.io/badge/Android%20Studio-app?style=flat&logo=Android%20Studio&labelColor=%23000000&color=%23000000)
![JetpackCompose](https://img.shields.io/badge/Jetpack%20Compose-app?style=flat&logo=Jetpack%20Compose&labelColor=%23000000&color=%23000000)

**Lingro** is a modern Android app for communicating with an AI assistant based on OpenAI GPT, with support for image search and sending, as well as working with PDF and Vision API. This repository can be used as an example or a base for creating your own chatbots and assistants for Android.

## Features

- Modern Material 3 design (Jetpack Compose)
  - Custom [Rubik](https://fonts.google.com/specimen/Rubik) font
  - Light and dark theme
- Chat with ChatGPT via your own proxy (bypasses regional restrictions)
- Image search and sending via DuckDuckGo (via separate Python proxy)
- PDF file analysis via Vision API
- Attachments support, chat history
- Flexible architecture: easy to extend and modify
- Example of TTS (Text-to-Speech) integration and voice selection
- The app is an Android branch of the [tggpt (Telegram bot)](https://github.com/mxlskh/tggpt) project

## Architecture

- All text and PDF requests go through [Lingro-Proxy (Node.js)](https://github.com/sockmage/Lingro-Proxy)
- Image search — via [DuckDuckGo Image API for Lingro (Python FastAPI)](https://github.com/sockmage/DDG-Image-API-for-Lingro)
- The app does not store OpenAI/Unsplash keys and is not region-dependent

## Quick Start

### Requirements
- Android Studio (latest recommended)
- JDK 17+
- Android SDK (API 24+)
- Internet connection

### Build and Run
1. Clone the repository:
   ```sh
   git clone https://github.com/yourusername/Lingro.git
   cd Lingro
   ```
2. Open the project in Android Studio.
3. Wait for Gradle sync (all dependencies will be downloaded automatically).
4. Build and run the project on an emulator or device.

### Backend setup
To use the app fully, you need backend services:
- [Lingro-Proxy (Node.js)](https://github.com/sockmage/Lingro-Proxy) — proxy for OpenAI and Vision API
- [DuckDuckGo Image API for Lingro (Python)](https://github.com/sockmage/DDG-Image-API-for-Lingro) — image search

#### Example deploy on Railway
1. Register at [Railway](https://railway.app/).
2. Create a project and connect the required repository (see links above).
3. For Lingro-Proxy, add the `OPENAI_API_KEY` environment variable.
4. Deploy — Railway will build and run the services automatically.

## Usage
- After launching the app, go through onboarding (theme selection, voice setup).
- Main screen — chat with AI (ChatGPT), support for attachments, images, PDF.
- You can select assistant role, TTS voice, switch themes.
- All network requests go through your backend services (see above).

## Technologies
- [Jetpack Compose](https://developer.android.com/jetpack/compose) — UI
- [Hilt (Dagger)](https://dagger.dev/hilt/) — DI
- [Retrofit](https://square.github.io/retrofit/) and [OkHttp](https://square.github.io/okhttp/) — networking
- [Coil](https://coil-kt.github.io/coil/) — image loading
- [Markwon](https://noties.io/Markwon/) — markdown
- Material Icons, custom Rubik font

## License

Project is licensed under the MIT License. See [LICENSE](LICENSE) for details.
