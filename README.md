# FinTrack - Advanced Financial Tracking App

FinTrack is a production-ready financial management application built with modern Android technologies. It features AI integration, secure local storage, biometric authentication, and comprehensive financial tracking capabilities.

## Key Features

- **AI Hybrid Assistant**: Integrated with MediaPipe GenAI for local insights and cloud fallback for advanced financial planning.
- **Secure Storage**: Uses Room database with SQLCipher for full-disk encryption.
- **Biometric Authentication**: Login using fingerprint or face recognition (via Camera/BiometricPrompt).
- **Notification Center**: Detailed notification management with swipe-to-delete and click-to-read functionality.
- **Subscription Model**: Google Play Billing integration for premium feature access.
- **Cloud Sync**: Optional backups to Google Drive using the Drive API with folder selection.
- **Bank Integration**: Transaction auto-importing using the Plaid Link SDK.
- **Home Widget**: Glance-based home screen widget for real-time balance tracking.
- **Customizable UI**: Jetpack Compose-based Material 3 theme with multiple accent colors and dark/light modes.
- **Voice Interaction**: Text-to-Speech (UK English) and Speech-to-Text for a hands-free experience.

## Architecture

The app follows a modern Clean Architecture approach:
- **UI Layer**: Jetpack Compose with ViewModels.
- **Domain Layer**: Repositories managing data flow.
- **Data Layer**: Room database (DAOs), EncryptedSharedPreferences (Auth), and external APIs (Plaid, Drive).
- **DI**: AppContainer for dependency management.

## Tech Stack

- **Language**: Kotlin 2.1.0
- **UI**: Jetpack Compose (BOM 2024.02.01)
- **Database**: Room 2.6.1 + SQLCipher 4.5.4
- **Security**: Android Biometric, Security Crypto (EncryptedSharedPreferences)
- **Background Tasks**: WorkManager
- **Networking**: Retrofit + Gson
- **Image Loading**: Coil
- **AI**: MediaPipe GenAI, MLKit Speech
- **Widget**: Jetpack Glance

## Getting Started

1. Clone the repository.
2. Add your Plaid API keys and Google Drive client IDs to `strings.xml`.
3. Build and run on an Android device (Min SDK 23).
4. For AI features, ensure the device supports MediaPipe GenAI.

## License

This project is licensed under the MIT License.
