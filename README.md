# AIChatApp

AIChatApp is a modern Android application built with Kotlin and Jetpack Compose that allows users to interact with AI models. The app features a clean architecture and integrates with external AI APIs while maintaining local chat history.

## Features

- **Real-time AI Streaming:** Support for word-by-word response streaming using Server-Sent Events (SSE) for a natural chat experience.
- **Secure Data Protection:** Sensitive information like API keys is encrypted using the Android KeyStore System (AES/GCM) before storage.
- **AI Chat Interface:** Seamlessly chat with AI models using a reactive, state-driven UI.
- **Local History:** Chat sessions and messages are stored locally using the Room database.
- **Settings Management:** User preferences and API configurations are managed via DataStore.
- **Clean Architecture:** Well-defined separation into `data`, `domain`, `presentation`, and `di` layers.

## Tech Stack

- **UI:** [Jetpack Compose](https://developer.android.com/jetpack/compose) with Material 3.
- **Dependency Injection:** [Hilt](https://dagger.dev/hilt/) for robust DI.
- **Local Database:** [Room](https://developer.android.com/training/data-storage/room) for persistent storage.
- **Preferences:** [DataStore](https://developer.android.com/topic/libraries/architecture/datastore) for key-value storage.
- **Networking:** [Retrofit](https://square.github.io/retrofit/) & [OkHttp](https://square.github.io/okhttp/) for API communication.
- **Streaming:** [OkHttp SSE](https://github.com/square/okhttp/tree/master/okhttp-sse) for real-time Server-Sent Events.
- **Security:** [Android KeyStore](https://developer.android.com/training/articles/keystore) for hardware-backed encryption 
- **Serialization:** [Kotlinx Serialization](https://github.com/Kotlin/kotlinx.serialization) for JSON handling.
- **Navigation:** [Compose Navigation](https://developer.android.com/jetpack/compose/navigation).

## Project Structure

```
com.example.aichatapp/
├── data/           
│   ├── local/      # Room DB, DAOs, and DataStore
│   ├── network/    # API interfaces, DTOs, and SSE Client 
│   ├── security/   # Encryption logic for API keys
│   └── repository/ # Single source of truth for data operations
├── di/             # Hilt modules
├── domain/         # Domain models
├── presentation/   # UI components, ViewModels, and Screen implementations
│   ├── chat/       # Chat screen and streaming logic
│   ├── home/       # Session list and navigation
│   ├── settings/   # Configuration UI
│   └── navigation/ # App routing
└── MainActivity.kt 
```

## Getting Started

1. Clone the repository.
2. Open the project in **Android Studio**.
3. Ensure you have an active internet connection for API interactions.
4. Provide your AI API Key in the application settings.
5. Build and run the app on an emulator or a physical device (Min SDK: 24).

## License

This project is for educational purposes.
