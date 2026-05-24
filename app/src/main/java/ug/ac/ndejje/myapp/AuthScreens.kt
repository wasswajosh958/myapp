package ug.ac.ndejje.myapp

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.content.Context
import android.content.ContextWrapper
import androidx.fragment.app.FragmentActivity
import kotlinx.coroutines.launch

fun Context.findActivity(): FragmentActivity? {
    var context = this
    while (context is ContextWrapper) {
        if (context is FragmentActivity) return context
        context = context.baseContext
    }
    return null
}

@Composable
fun LoginScreen(
    onNavigateToRegister: () -> Unit,
    onLoginSuccess: () -> Unit,
    userProfileRepository: UserProfileRepository,
    authManager: AuthManager
) {
    var username by remember { mutableStateOf("") }
    var pin by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isPinLogin by remember { mutableStateOf(true) }
    var passwordVisible by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    
    val context = LocalContext.current
    val activity = remember(context) { context.findActivity() }
    val scope = rememberCoroutineScope()
    
    val biometricHelper = remember(activity) { 
        activity?.let {
            BiometricPromptHelper(
                activity = it,
                onSuccess = { 
                    // For biometric success, we ideally need to know WHICH user.
                    // Simplified: Biometrics only for the last logged in user.
                    onLoginSuccess() 
                },
                onFailure = { error = "Biometric authentication failed" }
            )
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Welcome Back", fontSize = 28.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        Text("Access your secure financial hub", color = MaterialTheme.colorScheme.onSurfaceVariant)
        
        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(
            value = username,
            onValueChange = { username = it.trim().lowercase() },
            label = { Text("Username") },
            modifier = Modifier.fillMaxWidth(),
            leadingIcon = { Icon(Icons.Filled.Person, null) },
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(modifier = Modifier.fillMaxWidth()) {
            TextButton(
                onClick = { isPinLogin = true },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.textButtonColors(contentColor = if (isPinLogin) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant)
            ) {
                Text("USE PIN")
            }
            TextButton(
                onClick = { isPinLogin = false },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.textButtonColors(contentColor = if (!isPinLogin) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant)
            ) {
                Text("USE PASSWORD")
            }
        }

        if (isPinLogin) {
            OutlinedTextField(
                value = pin,
                onValueChange = { if (it.length <= 4) pin = it },
                label = { Text("Enter 4-digit PIN") },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = { Icon(Icons.Filled.Lock, null) }
            )
        } else {
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff, null)
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = { Icon(Icons.Filled.VpnKey, null) }
            )
        }

        if (error != null) {
            Text(error!!, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(top = 8.dp))
        }

        Spacer(modifier = Modifier.height(24.dp))
        
        Button(
            onClick = {
                scope.launch {
                    val user = userProfileRepository.getUserByUsername(username)
                    if (user != null) {
                        val authenticated = if (isPinLogin) user.pinHash == pin else user.passwordHash == password
                        if (authenticated) {
                            authManager.setCurrentUser(user.id, user.username)
                            onLoginSuccess()
                        } else {
                            error = if (isPinLogin) "Incorrect PIN" else "Incorrect password"
                        }
                    } else {
                        error = "User not found"
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = username.isNotEmpty() && (if (isPinLogin) pin.length == 4 else password.isNotEmpty())
        ) {
            Text("Login")
        }
        
        if (authManager.isBiometricAvailable() && authManager.isBiometricEnabled()) {
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedButton(
                onClick = { biometricHelper?.authenticate() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Filled.Fingerprint, null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Login with Biometrics")
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
        TextButton(onClick = onNavigateToRegister) {
            Text("Don't have an account? Register here")
        }
    }
}

@Composable
fun RegisterScreen(
    onNavigateBack: () -> Unit,
    userProfileRepository: UserProfileRepository,
    authManager: AuthManager
) {
    var name by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var pin by remember { mutableStateOf("") }
    var confirmPin by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }
    
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Create Account", fontSize = 28.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Full Name") },
            modifier = Modifier.fillMaxWidth(),
            leadingIcon = { Icon(Icons.Filled.Badge, null) }
        )
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = username,
            onValueChange = { username = it.trim().lowercase() },
            label = { Text("Username") },
            modifier = Modifier.fillMaxWidth(),
            leadingIcon = { Icon(Icons.Filled.Person, null) },
            supportingText = { Text("Used for unique identification") }
        )
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth(),
            leadingIcon = { Icon(Icons.Filled.Email, null) }
        )
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Set Password") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            leadingIcon = { Icon(Icons.Filled.VpnKey, null) }
        )
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = pin,
            onValueChange = { if (it.length <= 4) pin = it },
            label = { Text("Set 4-digit PIN") },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
            modifier = Modifier.fillMaxWidth(),
            leadingIcon = { Icon(Icons.Filled.Lock, null) }
        )
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = confirmPin,
            onValueChange = { if (it.length <= 4) confirmPin = it },
            label = { Text("Confirm PIN") },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
            modifier = Modifier.fillMaxWidth(),
            leadingIcon = { Icon(Icons.Filled.Lock, null) }
        )

        if (error != null) {
            Text(error!!, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(top = 8.dp))
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        Button(
            onClick = {
                if (pin != confirmPin) {
                    error = "PINs do not match"
                    return@Button
                }
                scope.launch {
                    val existing = userProfileRepository.getUserByUsername(username)
                    if (existing != null) {
                        error = "Username already taken"
                    } else {
                        val profile = UserProfile(
                            name = name,
                            username = username,
                            email = email,
                            passwordHash = password, // In real app, hash this!
                            pinHash = pin
                        )
                        val newId = userProfileRepository.insert(profile)
                        // Log the user in immediately after registration
                        authManager.setCurrentUser(newId.toInt(), username)
                        onNavigateBack()
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = name.isNotEmpty() && username.isNotEmpty() && password.isNotEmpty() && pin.length == 4 && confirmPin.length == 4
        ) {
            Text("Register")
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        TextButton(onClick = onNavigateBack) {
            Text("Already have an account? Login here")
        }
    }
}
