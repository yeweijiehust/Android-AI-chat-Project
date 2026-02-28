# AIChatApp

AIChatApp is a modern, high-performance Android application built with Kotlin and Jetpack Compose. It provides a seamless interface for interacting with OpenAI-compatible AI models, featuring real-time streaming, local history, and robust security.

## Features

- **Real-time AI Streaming:** Leverages Server-Sent Events (SSE) via OkHttp for instant, word-by-word response delivery.
- **Rich Markdown Support:** Renders AI responses with full Markdown support, including code blocks and formatted text, using `multiplatform-markdown-renderer`.
- **Customizable System Prompts:** Define specific personas or instructions for the AI on a per-session basis.
- **Secure API Management:** Encrypts sensitive API keys using the **Android KeyStore System** (AES/GCM) before storing them in DataStore.
- **Local Chat History:** Persists chat sessions and messages locally using **Room Database** for offline access.
- **Flexible Configuration:** Support for any OpenAI-compatible API endpoint, custom model selection, and adjustable conversation history context.
- **Modern UI:** Built entirely with **Jetpack Compose** and **Material 3**, featuring a responsive design and dark mode support.

## Tech Stack

- **UI:** [Jetpack Compose](https://developer.android.com/jetpack/compose) with Material 3.
- **Architecture:** Clean Architecture with MVVM pattern.
- **Dependency Injection:** [Hilt](https://dagger.dev/hilt/) for dependency management.
- **Local Storage:** 
    - [Room](https://developer.android.com/training/data-storage/room) for structured chat data.
    - [DataStore (Preferences)](https://developer.android.com/topic/libraries/architecture/datastore) for application settings.
- **Networking:** 
    - [Retrofit](https://square.github.io/retrofit/) & [OkHttp](https://square.github.io/okhttp/) for REST API calls.
    - [OkHttp SSE](https://github.com/square/okhttp/tree/master/okhttp-sse) for real-time streaming.
- **Serialization:** [Kotlinx Serialization](https://github.com/Kotlin/kotlinx.serialization) for type-safe JSON parsing.
- **Security:** Hardware-backed encryption via [Android KeyStore](https://developer.android.com/training/articles/keystore).
- **Markdown:** [Multiplatform Markdown Renderer](https://github.com/mikepenz/Multiplatform-Markdown-Renderer).

## Project Structure

```
com.example.aichatapp/
├── data/           
│   ├── local/      # Room DB entities, DAOs, and AppDatabase
│   ├── network/    # API definitions, DTOs, and SSE Stream Client 
│   ├── security/   # Encryption/Decryption logic for sensitive data
│   └── repository/ # Implementation of data sources and logic
├── di/             # Hilt dependency injection modules
├── domain/         # Business logic utilities (e.g., NetworkResult)
├── presentation/   # UI layer (Jetpack Compose)
│   ├── chat/       # Chat interface, streaming logic, and ViewModel
│   ├── home/       # Session management and main dashboard
│   ├── settings/   # Configuration UI for API and model preferences
│   ├── navigation/ # App routing and screen destinations
│   └── util/       # UI helpers (e.g., UiEvent)
└── MainActivity.kt # Single Activity entry point
```

## Getting Started

1. **Clone the repository:**
   ```bash
   git clone https://github.com/yourusername/AIChatApp.git
   ```
2. **Open in Android Studio:** Ensure you are using the latest version of Flamingo or higher.
3. **Configure the API:**
   - Launch the app and navigate to **Settings**.
   - Enter your **API Base URL** (e.g., `https://api.openai.com/v1`).
   - Enter your **API Key** (stored securely).
   - Specify the **Model** name (e.g., `gemini-3-flash`).
4. **Build and Run:** Run on an emulator or physical device (Min SDK: 24).

## License

This project is open-source for educational purposes and available under the MIT License.
