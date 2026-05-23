package ug.ac.ndejje.myapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            var themeMode by remember { mutableStateOf(ThemeMode.SYSTEM) }
            var accentColor by remember { mutableStateOf(AccentColor.GREEN) }
            
            FinTrackTheme(themeMode = themeMode, accentColor = accentColor) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation(
                        currentTheme = themeMode,
                        currentAccent = accentColor,
                        onThemeChange = { themeMode = it },
                        onAccentChange = { accentColor = it }
                    )
                }
            }
        }
    }
}

@Composable
fun AppNavigation(
    currentTheme: ThemeMode,
    currentAccent: AccentColor,
    onThemeChange: (ThemeMode) -> Unit,
    onAccentChange: (AccentColor) -> Unit
) {
    val navController = rememberNavController()
    var selectedCurrency by remember { mutableStateOf("Shs") }
    var showOnboarding by remember { mutableStateOf(true) }

    NavHost(navController = navController, startDestination = if (showOnboarding) "onboarding" else "login") {
        composable("onboarding") {
            OnboardingScreen(onFinish = {
                showOnboarding = false
                navController.navigate("login") {
                    popUpTo("onboarding") { inclusive = true }
                }
            })
        }
        composable("login") {
            LoginScreen(
                onNavigateToRegister = { navController.navigate("register") },
                onLoginSuccess = { navController.navigate("home") }
            )
        }
        composable("register") {
            RegisterScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable("home") {
            HomeScreen(
                currency = selectedCurrency,
                onLogout = { 
                    navController.navigate("login") {
                        popUpTo("home") { inclusive = true }
                    }
                },
                onNavigateToTransactions = { navController.navigate("transactions") },
                onNavigateToAddTransaction = { navController.navigate("add_transaction") },
                onNavigateToReports = { navController.navigate("reports") },
                onNavigateToBudgets = { navController.navigate("budgets") },
                onNavigateToAccounts = { navController.navigate("accounts") },
                onNavigateToProfile = { navController.navigate("profile") },
                onNavigateToSettings = { navController.navigate("settings") },
                onNavigateToNotifications = { navController.navigate("notifications") },
                onCurrencyChange = { selectedCurrency = it }
            )
        }
        composable("notifications") {
            NotificationScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToSettings = { navController.navigate("settings") }
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
            AppearanceScreen(
                currentTheme = currentTheme,
                currentAccent = currentAccent,
                onThemeChange = onThemeChange,
                onAccentChange = onAccentChange,
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable("reports") {
            ReportsScreen(
                currency = selectedCurrency,
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable("budgets") {
            BudgetManagementScreen(
                currency = selectedCurrency,
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable("accounts") {
            AccountsScreen(
                currency = selectedCurrency,
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable("profile") {
            ProfileScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToAccounts = { navController.navigate("accounts") }
            )
        }
        composable("transactions") {
            TransactionsScreen(
                currency = selectedCurrency,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToAddTransaction = { navController.navigate("add_transaction") }
            )
        }
        composable("add_transaction") {
            AddEditTransactionScreen(
                currency = selectedCurrency,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}

@Composable
fun LoginScreen(onNavigateToRegister: () -> Unit, onLoginSuccess: () -> Unit) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp)
            .imePadding()
            .systemBarsPadding(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Login", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                val image = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                val description = if (passwordVisible) "Hide password" else "Show password"
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(imageVector = image, contentDescription = description)
                }
            },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = { 
                if (email.isNotEmpty() && password.isNotEmpty()) {
                    onLoginSuccess()
                }
            },
            enabled = email.isNotEmpty() && password.isNotEmpty(),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Login")
        }
        Spacer(modifier = Modifier.height(8.dp))
        TextButton(onClick = onNavigateToRegister) {
            Text("Don't have an account? Register here")
        }
    }
}

@Composable
fun RegisterScreen(onNavigateBack: () -> Unit) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordError by remember { mutableStateOf<String?>(null) }
    var passwordVisible by remember { mutableStateOf(false) }
    val registrationSuccess = remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()

    if (registrationSuccess.value) {
        AlertDialog(
            onDismissRequest = { registrationSuccess.value = false },
            confirmButton = {
                TextButton(onClick = { 
                    registrationSuccess.value = false
                    onNavigateBack() 
                }) {
                    Text("OK")
                }
            },
            title = { Text("Registration Successful") },
            text = { Text("Your account for $email has been created successfully!") }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp)
            .imePadding()
            .systemBarsPadding(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Register", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = password,
            onValueChange = { 
                password = it
                passwordError = if (it.length < 8) "Password must be at least 8 characters" else null
            },
            label = { Text("Password") },
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                val image = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                val description = if (passwordVisible) "Hide password" else "Show password"
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(imageVector = image, contentDescription = description)
                }
            },
            isError = passwordError != null,
            supportingText = {
                if (passwordError != null) {
                    Text(text = passwordError!!, color = MaterialTheme.colorScheme.error)
                } else {
                    Text(text = "Minimum 8 characters")
                }
            },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = { Text("Confirm Password") },
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            isError = confirmPassword.isNotEmpty() && confirmPassword != password,
            supportingText = {
                if (confirmPassword.isNotEmpty() && confirmPassword != password) {
                    Text(text = "Passwords do not match", color = MaterialTheme.colorScheme.error)
                }
            },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = { 
                if (password.length >= 8 && password == confirmPassword) {
                    registrationSuccess.value = true
                }
            },
            enabled = password.length >= 8 && password == confirmPassword && email.isNotEmpty(),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Register")
        }
        Spacer(modifier = Modifier.height(8.dp))
        TextButton(onClick = onNavigateBack) {
            Text("Already have an account? Login here")
        }
    }
}
