package ug.ac.ndejje.myapp

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

enum class AccountType {
    CHECKING, CASH, CREDIT, SAVINGS
}

data class Account(
    val id: Int,
    val name: String,
    val type: AccountType,
    val balance: Double,
    val detail: String? = null,
    val alert: String? = null,
    val lastFour: String? = null
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountsScreen(currency: String, onNavigateBack: () -> Unit) {
    val accounts = remember {
        mutableStateListOf(
            Account(1, "Chase Checking", AccountType.CHECKING, 5420.50, lastFour = "1234"),
            Account(2, "Cash", AccountType.CASH, 340.00),
            Account(3, "Amex Credit Card", AccountType.CREDIT, -1250.00, detail = "Due: May 5th", alert = "Payment due in 3 days"),
            Account(4, "Savings Account", AccountType.SAVINGS, 6500.00, detail = "APY: 4.2%")
        )
    }

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
                    TextButton(onClick = { /* Add Account */ }) {
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
                AccountItem(account, currency)
            }

            // Total Balance Summary
            item {
                Text(
                    text = "Total balance across all accounts: $currency ${String.format("%,.2f", netWorth)}",
                    fontSize = 14.sp,
                    color = Color.Gray,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }

            // AI Suggestion
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.4f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Text("🤖", fontSize = 24.sp)
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                "AI: Your credit card payment is due soon. Set up auto-pay?",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            TextButton(onClick = { /* Enable Auto-pay */ }) {
                                Text("Enable")
                            }
                        }
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(24.dp)) }
        }
    }
}

@Composable
fun AccountItem(account: Account, currency: String) {
    val icon: ImageVector = when (account.type) {
        AccountType.CHECKING, AccountType.CREDIT -> Icons.Filled.CreditCard
        AccountType.CASH -> Icons.Filled.Payments
        AccountType.SAVINGS -> Icons.Filled.AccountBalance
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
                        text = buildString {
                            append("$currency ${String.format("%,.2f", account.balance)}")
                            if (account.lastFour != null) append("  •  Last 4: ${account.lastFour}")
                            if (account.detail != null) append("  •  ${account.detail}")
                        },
                        fontSize = 14.sp,
                        color = if (account.balance < 0) Color.Red else Color.Unspecified
                    )
                }
            }

            if (account.alert != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Warning, contentDescription = null, tint = Color(0xFFFFA000), modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(account.alert, color = Color(0xFFFFA000), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                if (account.type == AccountType.CREDIT) {
                    Button(onClick = { /* Pay now */ }, contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)) {
                        Text("Pay now", fontSize = 12.sp)
                    }
                } else if (account.type == AccountType.SAVINGS) {
                    OutlinedButton(onClick = { /* Transfer */ }, contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)) {
                        Text("Transfer", fontSize = 12.sp)
                    }
                }

                OutlinedButton(onClick = { /* View Transactions */ }, contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)) {
                    Text("View transactions", fontSize = 12.sp)
                }
                
                if (account.type != AccountType.CREDIT && account.type != AccountType.SAVINGS) {
                    OutlinedButton(onClick = { /* Edit */ }, contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)) {
                        Text("Edit", fontSize = 12.sp)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(onNavigateBack: () -> Unit, onNavigateToAccounts: () -> Unit) {
    var userName by remember { mutableStateOf("Joshua Wasswa") }
    var userEmail by remember { mutableStateOf("joshua@example.com") }
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Profile") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(scrollState)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Profile Photo Section
            Box(contentAlignment = Alignment.BottomEnd) {
                Surface(
                    modifier = Modifier.size(100.dp),
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Icon(
                        Icons.Filled.Person, 
                        contentDescription = null, 
                        modifier = Modifier.size(60.dp).padding(20.dp),
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
                SmallFloatingActionButton(
                    onClick = { /* Upload photo */ },
                    shape = CircleShape,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(Icons.Filled.PhotoCamera, contentDescription = "Change photo", modifier = Modifier.size(16.dp))
                }
            }

            // User Info Section
            Column(verticalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                Text("Personal Information", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                OutlinedTextField(
                    value = userName,
                    onValueChange = { userName = it },
                    label = { Text("Name") },
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = { Icon(Icons.Filled.Badge, null) }
                )
                OutlinedTextField(
                    value = userEmail,
                    onValueChange = { userEmail = it },
                    label = { Text("Email") },
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = { Icon(Icons.Filled.Email, null) }
                )
            }

            // Security Section
            Column(verticalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                Text("Security", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Button(
                    onClick = { /* Change password logic */ },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                ) {
                    Icon(Icons.Filled.Lock, null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Change Password")
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
            }

            Spacer(modifier = Modifier.weight(1f))

            // AI Suggestion
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.4f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text("🤖", fontSize = 24.sp)
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            "AI: Your credit card payment is due soon. Set up auto-pay?",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        TextButton(onClick = { /* Enable Auto-pay */ }) {
                            Text("Enable")
                        }
                    }
                }
            }
        }
    }
}

