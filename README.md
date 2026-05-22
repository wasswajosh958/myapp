# FinTrack

An Android application built with Jetpack Compose featuring a complete authentication flow and a Hybrid AI Assistant.

## Features
- **Hybrid AI Assistant**: A conversational assistant that handles financial queries on-device (private) and general knowledge in the cloud.
- **Voice Feedback**: Integrated Text-to-Speech (TTS) so the AI reads its responses aloud.
- **Login Screen**: Secure login with password visibility toggle and field validation.
- **Registration Screen**: Create an account with email and password (minimum 8 characters). Includes password matching validation.
- **Home Screen Dashboard**: 
    - Real-time financial summary cards (Balance, Income, Expenses, Savings).
    - Expense Analytics visualization.
    - Recent transactions list with categories.
    - Savings goals progress tracking.
- **Navigation**: Seamless transition between Home, Transactions, Analytics, Goals, and Settings.
- **UI/UX**: 
    - Fully scrollable screens with keyboard awareness (`imePadding`).
    - Custom App Icon and Material 3 design components.
    - Password preview (show/hide) toggle.

## Tech Stack
- **AI**: Google MediaPipe GenAI (On-device LLM), ML Kit Speech.
- **Voice**: Android TextToSpeech (TTS).
- **Language**: Kotlin
- **UI Framework**: Jetpack Compose
- **Navigation**: Jetpack Navigation Compose
- **Build System**: Gradle (Kotlin DSL) with Version Catalogs

## How to Run
1. Clone the repository.
2. Open in Android Studio (Iguana or newer recommended).
3. Sync Gradle and run on an emulator or physical device (e.g., Oppo A57).
