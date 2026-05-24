package ug.ac.ndejje.myapp

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountsScreen(
    currency: String,
    onNavigateBack: () -> Unit,
    accountRepository: AccountRepository,
    authManager: AuthManager
) {
    val scope = rememberCoroutineScope()
    val accounts by accountRepository.allAccounts.collectAsState(initial = emptyList())
    var editingAccount by remember { mutableStateOf<AccountEntity?>(null) }
    var showChangePinDialog by remember { mutableStateOf(false) }

    val totalAssets = accounts.filter { it.balance > 0 }.sumOf { it.balance }
    val totalLiabilities = accounts.filter { it.balance < 0 }.sumOf { it.balance }.let { Math.abs(it) }
    val netWorth = totalAssets - totalLiabilities

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Accounts") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { showChangePinDialog = true }) {
                        Icon(Icons.Filled.Lock, contentDescription = "Change PIN")
                    }
                    TextButton(onClick = { 
                        editingAccount = AccountEntity(name = "New Account", type = "CHECKING", balance = 0.0)
                    }) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                            Text("Add")
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item { Spacer(modifier = Modifier.height(8.dp)) }

            // Net Worth Summary Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text("Net worth", fontSize = 14.sp)
                        Text("$currency ${String.format("%,.2f", netWorth)}", fontSize = 28.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Column {
                                Text("Total assets", fontSize = 12.sp, color = Color.Gray)
                                Text("$currency ${String.format("%,.0f", totalAssets)}", fontWeight = FontWeight.Bold, color = Color(0xFF4CAF50))
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text("Liabilities", fontSize = 12.sp, color = Color.Gray)
                                Text("$currency ${String.format("%,.0f", totalLiabilities)}", fontWeight = FontWeight.Bold, color = Color.Red)
                            }
                        }
                    }
                }
            }

            // Accounts List
            items(accounts) { account ->
                AccountItem(
                    account = account, 
                    currency = currency,
                    onEdit = { editingAccount = account }
                )
            }

            item { Spacer(modifier = Modifier.height(24.dp)) }
        }
    }

    if (editingAccount != null) {
        EditAccountDialog(
            account = editingAccount!!,
            onDismiss = { editingAccount = null },
            onSave = { updatedAccount ->
                scope.launch {
                    accountRepository.insert(updatedAccount)
                    editingAccount = null
                }
            }
        )
    }

    if (showChangePinDialog) {
        ChangePinDialog(
            authManager = authManager,
            onDismiss = { showChangePinDialog = false }
        )
    }
}

@Composable
fun EditAccountDialog(account: AccountEntity, onDismiss: () -> Unit, onSave: (AccountEntity) -> Unit) {
    var name by remember { mutableStateOf(account.name) }
    var balance by remember { mutableStateOf(account.balance.toString()) }
    var type by remember { mutableStateOf(account.type) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (account.id == 0) "Add Account" else "Edit Account") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Account Name") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = balance,
                    onValueChange = { balance = it },
                    label = { Text("Balance") },
                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth()
                )
                // Simplified type selection
                Text("Type: $type (CHECKING, CASH, CREDIT, SAVINGS)")
            }
        },
        confirmButton = {
            Button(onClick = {
                onSave(account.copy(name = name, balance = balance.toDoubleOrNull() ?: 0.0, type = type))
            }) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun AccountItem(account: AccountEntity, currency: String, onEdit: () -> Unit) {
    val icon: ImageVector = when (account.type) {
        "CREDIT" -> Icons.Filled.CreditCard
        "CASH" -> Icons.Filled.Payments
        "SAVINGS" -> Icons.Filled.AccountBalance
        else -> Icons.Filled.AccountBalanceWallet
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(account.name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Text(
                        text = "$currency ${String.format("%,.2f", account.balance)}",
                        fontSize = 14.sp,
                        color = if (account.balance < 0) Color.Red else Color.Unspecified
                    )
                }
                IconButton(onClick = onEdit) {
                    Icon(Icons.Filled.Edit, contentDescription = "Edit")
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onNavigateBack: () -> Unit,
    onNavigateToAccounts: () -> Unit,
    userProfileRepository: UserProfileRepository,
    authManager: AuthManager
) {
    val scope = rememberCoroutineScope()
    val userProfile by userProfileRepository.userProfile.collectAsState(initial = null)
    val snackbarHostState = remember { SnackbarHostState() }
    
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var photoUri by remember { mutableStateOf<String?>(null) }
    var isBiometricEnabled by remember { mutableStateOf(authManager.isBiometricEnabled()) }

    LaunchedEffect(userProfile) {
        userProfile?.let {
            name = it.name
            email = it.email
            photoUri = it.photoUri
        }
    }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        photoUri = uri?.toString()
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Profile") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    TextButton(onClick = {
                        scope.launch {
                            val profile = UserProfile(name = name, email = email, photoUri = photoUri)
                            userProfileRepository.insert(profile)
                            snackbarHostState.showSnackbar("Profile changes saved successfully")
                        }
                    }) {
                        Text("SAVE", fontWeight = FontWeight.Bold)
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Profile Photo Section
            Box(contentAlignment = Alignment.BottomEnd) {
                Surface(
                    modifier = Modifier.size(120.dp),
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primaryContainer
                ) {
                    if (photoUri != null) {
                        Image(
                            painter = rememberAsyncImagePainter(photoUri),
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize().clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Icon(
                            Icons.Filled.Person, 
                            contentDescription = null, 
                            modifier = Modifier.size(80.dp).padding(20.dp),
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
                Row {
                    if (photoUri != null) {
                        SmallFloatingActionButton(
                            onClick = { photoUri = null },
                            shape = CircleShape,
                            containerColor = MaterialTheme.colorScheme.errorContainer,
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(Icons.Filled.Delete, contentDescription = "Remove photo", modifier = Modifier.size(18.dp))
                        }
                        Spacer(modifier = Modifier.width(4.dp))
                    }
                    SmallFloatingActionButton(
                        onClick = { photoPickerLauncher.launch("image/*") },
                        shape = CircleShape,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(Icons.Filled.PhotoCamera, contentDescription = "Change photo", modifier = Modifier.size(18.dp))
                    }
                }
            }

            // User Info Section
            Column(verticalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                Text("Personal Information", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Name") },
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = { Icon(Icons.Filled.Badge, null) }
                )
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = { Icon(Icons.Filled.Email, null) }
                )
            }

            // Security Section
            Column(verticalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                Text("Security", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.Fingerprint, null, tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.width(12.dp))
                        Text("Enable Biometric Login")
                    }
                    Switch(
                        checked = isBiometricEnabled,
                        onCheckedChange = { 
                            isBiometricEnabled = it
                            authManager.setBiometricEnabled(it)
                        }
                    )
                }

                Button(
                    onClick = { /* show change pin dialog - can reuse from AccountsScreen or common */ },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                ) {
                    Icon(Icons.Filled.Lock, null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Change PIN")
                }
            }

            // Account Management
            Column(verticalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                Text("Account Management", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                OutlinedButton(
                    onClick = onNavigateToAccounts,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Filled.Link, null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Manage Linked Accounts")
                }
                
                OutlinedButton(
                    onClick = { /* show delete account confirmation */ },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) {
                    Icon(Icons.Filled.DeleteForever, null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Delete Account")
                }
            }
        }
    }
}

@Composable
fun ChangePinDialog(authManager: AuthManager, onDismiss: () -> Unit) {
    var oldPin by remember { mutableStateOf("") }
    var newPin by remember { mutableStateOf("") }
    var confirmPin by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Change security PIN") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = oldPin,
                    onValueChange = { if (it.length <= 4) oldPin = it },
                    label = { Text("Current PIN") },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = newPin,
                    onValueChange = { if (it.length <= 4) newPin = it },
                    label = { Text("New 4-digit PIN") },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = confirmPin,
                    onValueChange = { if (it.length <= 4) confirmPin = it },
                    label = { Text("Confirm New PIN") },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth()
                )
                if (error != null) {
                    Text(error!!, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                if (newPin != confirmPin) {
                    error = "New PINs do not match"
                } else if (newPin.length != 4) {
                    error = "PIN must be 4 digits"
                } else if (authManager.changePin(oldPin, newPin)) {
                    onDismiss()
                } else {
                    error = "Incorrect current PIN"
                }
            }) {
                Text("Update")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
