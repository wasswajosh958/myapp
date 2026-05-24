package ug.ac.ndejje.myapp

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.*
import kotlinx.coroutines.launch
import androidx.navigation.NavType
import androidx.navigation.navArgument

class MainActivity : FragmentActivity() {
    private lateinit var appContainer: AppContainer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appContainer = AppContainer(this)
        
        setContent {
            val context = LocalContext.current
            val settingsDataStore = remember { SettingsDataStore(context) }
            val themeModeState = settingsDataStore.themeModeFlow.collectAsState<ThemeMode, ThemeMode>(initial = ThemeMode.SYSTEM)
            val accentColorState = settingsDataStore.accentColorFlow.collectAsState<AccentColor, AccentColor>(initial = AccentColor.GREEN)

            FinTrackTheme(themeMode = themeModeState.value, accentColor = accentColorState.value) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation(settingsDataStore, appContainer)
                }
            }
        }
    }
}

@Composable
fun AppNavigation(settingsDataStore: SettingsDataStore, appContainer: AppContainer) {
    val navController = rememberNavController()
    val context = LocalContext.current
    val authManager = remember { AuthManager(context) }
    val currencyState = settingsDataStore.currencyFlow.collectAsState(initial = "Shs")
    val currency = currencyState.value
    val currentUserId = authManager.getCurrentUserId()

    NavHost(navController = navController, startDestination = "splash") {
        composable("splash") {
            SplashScreen { isLoggedIn, completedOnboarding ->
                if (!completedOnboarding) {
                    navController.navigate("onboarding") { popUpTo("splash") { inclusive = true } }
                } else if (!isLoggedIn) {
                    navController.navigate("login") { popUpTo("splash") { inclusive = true } }
                } else {
                    navController.navigate("home") { popUpTo("splash") { inclusive = true } }
                }
            }
        }
        composable("onboarding") {
            OnboardingScreen(onFinish = {
                authManager.setOnboardingCompleted(true)
                navController.navigate("login") { popUpTo("onboarding") { inclusive = true } }
            })
        }
        composable("login") {
            LoginScreen(
                onNavigateToRegister = { navController.navigate("register") },
                onLoginSuccess = { 
                    navController.navigate("home") { popUpTo("login") { inclusive = true } }
                },
                userProfileRepository = appContainer.userProfileRepository,
                authManager = authManager
            )
        }
        composable("register") {
            RegisterScreen(
                onNavigateBack = { navController.popBackStack() },
                userProfileRepository = appContainer.userProfileRepository,
                authManager = authManager
            )
        }
        composable("home") {
            HomeScreen(
                currency = currency,
                onLogout = { 
                    authManager.clearSession()
                    navController.navigate("login") { popUpTo("home") { inclusive = true } }
                },
                onNavigateToTransactions = { navController.navigate("transactions") },
                onNavigateToAddTransaction = { navController.navigate("add_transaction") },
                onNavigateToReports = { navController.navigate("reports") },
                onNavigateToBudgets = { navController.navigate("budgets") },
                onNavigateToAccounts = { navController.navigate("accounts") },
                onNavigateToProfile = { navController.navigate("profile") },
                onNavigateToSettings = { navController.navigate("settings") },
                onNavigateToNotifications = { navController.navigate("notifications") },
                onCurrencyChange = { },
                userProfileRepository = appContainer.userProfileRepository,
                database = appContainer.database,
                authManager = authManager
            )
        }
        composable("settings") {
            SettingsScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToProfile = { navController.navigate("profile") },
                onNavigateToAppearance = { navController.navigate("appearance") }
            )
        }
        composable("appearance") {
            val themeModeState = settingsDataStore.themeModeFlow.collectAsState<ThemeMode, ThemeMode>(initial = ThemeMode.SYSTEM)
            val accentColorState = settingsDataStore.accentColorFlow.collectAsState<AccentColor, AccentColor>(initial = AccentColor.GREEN)
            val scope = rememberCoroutineScope()

            AppearanceScreen(
                currentTheme = themeModeState.value,
                currentAccent = accentColorState.value,
                onThemeChange = { mode -> scope.launch { settingsDataStore.setThemeMode(mode) } },
                onAccentChange = { color -> scope.launch { settingsDataStore.setAccentColor(color) } },
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable("reports") {
            ReportsScreen(currency = currency, onNavigateBack = { navController.popBackStack() })
        }
        composable("budgets") {
            BudgetManagementScreen(currency = currency, onNavigateBack = { navController.popBackStack() })
        }
        composable("accounts") {
            AccountsScreen(
                currency = currency,
                onNavigateBack = { navController.popBackStack() },
                accountRepository = appContainer.accountRepository,
                authManager = authManager
            )
        }
        composable("profile") {
            ProfileScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToAccounts = { navController.navigate("accounts") },
                userProfileRepository = appContainer.userProfileRepository,
                authManager = authManager
            )
        }
        composable("notifications") {
            NotificationScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToSettings = { navController.navigate("settings") },
                onNavigateToDetail = { id -> navController.navigate("notification_detail/$id") },
                notificationRepository = appContainer.notificationRepository,
                userId = currentUserId
            )
        }
        composable("notification_detail/{id}") { backStackEntry ->
            val id = backStackEntry.arguments?.getString("id")?.toLongOrNull() ?: 0L
            NotificationDetailScreen(
                notificationId = id,
                onNavigateBack = { navController.popBackStack() },
                notificationRepository = appContainer.notificationRepository
            )
        }
        composable("transactions") {
            TransactionsScreen(
                currency = currency,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToAddTransaction = { navController.navigate("add_transaction") },
                userId = currentUserId
            )
        }
        composable("add_transaction") {
            AddEditTransactionScreen(currency = currency, onNavigateBack = { navController.popBackStack() })
        }
    }
}
