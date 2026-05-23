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
- **Transactions List Screen**:
    - Detailed view of all transactions grouped by date.
    - Search functionality with voice command support.
    - Advanced filtering (Income, Expense, Pending).
    - Swipe-to-delete and swipe-to-edit actions.
- **Add/Edit Transaction Screen**:
    - Create or modify expenses, income, and transfers.
    - Custom numeric keypad for fast amount entry.
    - AI-powered "Quick-Add" suggestions based on spending habits.
    - Voice-to-form integration ("Add $45 for groceries at Safeway yesterday").
    - Attachment support for receipts.
- **Budget Management Screen**:
    - Set monthly and category-specific budgets.
    - Real-time progress tracking with visual alerts.
    - Edit or delete budgets with a few taps.
- **Reports & Insights Screen**:
    - Visualize spending trends with interactive daily charts.
    - Category breakdown via donut chart visualization.
    - Comparison metrics (vs last month, income vs expenses).
    - Top spending categories with over-budget indicators.
    - AI Insights for personalized financial advice.
- **Navigation**: Seamless transition between Home, Transactions, Analytics (Reports), Budgets, and Settings.
- **UI/UX**: 
    - Fully scrollable screens with keyboard awareness (`imePadding`).
    - Custom App Icon and Material 3 design components.
    - Password preview (show/hide) toggle.

## Tech Stack
- **AI**: Google MediaPipe GenAI (On-device LLM), ML Kit GenAI Speech Recognition.
- **Voice**: Android TextToSpeech (TTS).
- **Language**: Kotlin 2.1.0+
- **UI Framework**: Jetpack Compose
- **Navigation**: Jetpack Navigation Compose
- **Build System**: Gradle (Kotlin DSL) with Version Catalogs

## How to Run
1. Clone the repository.
2. Open in Android Studio (Iguana or newer recommended).
3. Sync Gradle and run on an emulator or physical device (e.g., Oppo A57).
