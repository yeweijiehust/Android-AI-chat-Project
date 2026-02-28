# AIChatApp

AIChatApp is a modern Android application built with Kotlin and Jetpack Compose that allows users to interact with AI models. The app features a clean architecture and integrates with external AI APIs while maintaining local chat history.

## Features

- **AI Chat Interface:** Seamlessly chat with AI models using a reactive UI.
- **Local History:** Chat sessions are stored locally using Room database.
- **Settings Management:** User preferences and API configurations managed via DataStore.
- **Clean Architecture:** Separated into `data`, `domain`, `presentation`, and `di` layers for maintainability and testability.
- **Secure Networking:** Includes an `AuthInterceptor` for handling API authentication.

## Tech Stack

- **UI:** [Jetpack Compose](https://developer.android.com/jetpack/compose) with Material 3.
- **Dependency Injection:** [Hilt](https://dagger.dev/hilt/) for robust DI.
- **Local Database:** [Room](https://developer.android.com/training/data-storage/room) for persistent storage.
- **Preferences:** [DataStore](https://developer.android.com/topic/libraries/architecture/datastore) for key-value storage.
- **Networking:** [Retrofit](https://square.github.io/retrofit/) & [OkHttp](https://square.github.io/okhttp/) for API communication.
- **Serialization:** [Kotlinx Serialization](https://github.com/Kotlin/kotlinx.serialization) for JSON handling.
- **Navigation:** [Compose Navigation](https://developer.android.com/jetpack/compose/navigation).

## Project Structure

```
com.example.aichatapp/
├── data/           # Repositories, DAOs, API interfaces, and DataSources
├── di/             # Hilt modules for Dependency Injection
├── domain/         # Business logic and domain models
├── presentation/   # UI components, ViewModels, and Screen implementations
│   ├── chat/       # Chat screen logic and UI
│   ├── home/       # Home screen
│   ├── settings/   # App settings
│   └── navigation/ # Navigation graph and routes
└── MainActivity.kt # Entry point of the application
```

## Getting Started

1. Clone the repository.
2. Open the project in **Android Studio**.
3. Ensure you have an active internet connection for API interactions.
4. Build and run the app on an emulator or a physical device (Min SDK: 24).

## License

This project is for educational purposes.
