# My Application

An Android application built with Jetpack Compose featuring a complete authentication flow.

## Features
- **Login Screen**: Secure login with password visibility toggle and field validation.
- **Registration Screen**: Create an account with email and password (minimum 8 characters). Includes password matching validation.
- **Home Screen**: A welcome landing page after successful authentication.
- **Navigation**: Seamless transition between authentication states using Jetpack Navigation.
- **UI/UX**: 
    - Fully scrollable screens to support various device sizes.
    - **Keyboard Awareness**: Optimized layout with `imePadding` and `adjustResize` to ensure text fields remain visible while typing.
    - **System Integration**: Proper handling of system bars for a clean, edge-to-edge look.
    - Password preview (show/hide) toggle.
    - Interactive feedback via Dialogs.
    - Material 3 design components.

## Tech Stack
- **Language**: Kotlin
- **UI Framework**: Jetpack Compose
- **Navigation**: Jetpack Navigation Compose
- **Build System**: Gradle (Kotlin DSL) with Version Catalogs

## How to Run
1. Clone the repository.
2. Open in Android Studio (Iguana or newer recommended).
3. Sync Gradle and run on an emulator or physical device (e.g., Oppo A57).
