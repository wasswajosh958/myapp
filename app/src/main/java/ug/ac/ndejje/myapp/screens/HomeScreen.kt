package ug.ac.ndejje.myapp.screens

import ug.ac.ndejje.myapp.util.*
import ug.ac.ndejje.myapp.resources.*
import ug.ac.ndejje.myapp.theme.*

import androidx.compose.foundation.*
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    currency: String,
    onLogout: () -> Unit, 
    onNavigateToTransactions: () -> Unit,
    onNavigateToAddTransaction: () -> Unit,
    onNavigateToReports: () -> Unit,
    onNavigateToAccounts: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToNotifications: () -> Unit,
    onCurrencyChange: (String) -> Unit,
    userProfileRepository: UserProfileRepository,
    database: AppDatabase,
    authManager: AuthManager,
    savingsGoalRepository: SavingsGoalRepository
) {
    val context = LocalContext.current
    val currentUserId = authManager.getCurrentUserId()
    val username = authManager.getCurrentUsername()
    val assistant = remember { AiAssistant(context, database, currentUserId) }
    val scope = rememberCoroutineScope()
    val userProfile by userProfileRepository.getUserProfile(currentUserId).collectAsState(initial = null)
    val displayName = userProfile?.username ?: username

    val savingsGoals by savingsGoalRepository.allGoals(currentUserId).collectAsState(initial = emptyList())
    var showAddGoalDialog by remember { mutableStateOf(false) }

    val greeting = remember {
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        when (hour) {
            in 0..11 -> "Good morning"
            in 12..16 -> "Good afternoon"
            else -> "Good evening"
        }
    }

    var showAiChat by remember { mutableStateOf(false) }
    var chatMessages by remember { mutableStateOf(listOf<ChatMessage>()) }
    var userQuery by remember { mutableStateOf("") }
    
    var selectedItem by remember { mutableIntStateOf(0) }
    val items = listOf("Home", "Transactions", "Analytics", "Profile")
    val icons = listOf(Icons.Filled.Home, Icons.Filled.CreditCard, Icons.Filled.BarChart, Icons.Filled.Person)

    var showCurrencyDropdown by remember { mutableStateOf(false) }
    val currencies = listOf("Shs", "$", "€", "£")

    val transactions by database.transactionDao().getAllTransactions(currentUserId).collectAsState(initial = emptyList())
    val accounts by database.accountDao().getAllAccounts(currentUserId).collectAsState(initial = emptyList())

    val totalBalance = accounts.sumOf { it.balance }
    val totalIncome = transactions.filter { !it.isExpense }.sumOf { it.amountValue }
    val totalExpenses = transactions.filter { it.isExpense }.sumOf { it.amountValue }
    val totalSavings = totalIncome - totalExpenses

    // Calculate spending per category for analytics
    val spendingByCategory = transactions.filter { it.isExpense }
        .groupBy { it.category }
        .mapValues { entry -> entry.value.sumOf { it.amountValue } }
    
    val maxSpending = spendingByCategory.values.maxOrNull() ?: 1.0

    if (showAiChat) {
        ModalBottomSheet(
            onDismissRequest = { showAiChat = false },
            sheetState = rememberModalBottomSheetState()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Text("FinTrack AI Assistant", style = MaterialTheme.typography.titleLarge)
                Spacer(modifier = Modifier.height(16.dp))
                
                LazyColumn(modifier = Modifier.weight(1f)) {
                    items(chatMessages) { msg ->
                        Text(
                            text = if (msg.isUser) "You: ${msg.text}" else "AI: ${msg.text}",
                            color = if (msg.isUser) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                    }
                }
                
                Row(verticalAlignment = Alignment.CenterVertically) {
                    OutlinedTextField(
                        value = userQuery,
                        onValueChange = { userQuery = it },
                        modifier = Modifier.weight(1f),
                        placeholder = { Text("Ask me about your spending...") }
                    )
                    IconButton(onClick = { /* Handle Voice */ }) {
                        Icon(Icons.Filled.Mic, contentDescription = "Voice")
                    }
                    Button(onClick = {
                        val q = userQuery
                        userQuery = ""
                        chatMessages = chatMessages + ChatMessage(q, true)
                        scope.launch {
                            val response = assistant.getResponse(q)
                            chatMessages = chatMessages + ChatMessage(response, false)
                            assistant.speak(response)
                        }
                    }) {
                        Text("Send")
                    }
                }
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("FinTrack", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                        Text("$greeting, $displayName", fontSize = 14.sp, fontWeight = FontWeight.Normal)
                    }
                },
                actions = {
                    IconButton(onClick = { showAiChat = true }) {
                        Icon(Icons.Filled.AutoAwesome, contentDescription = "AI Assistant")
                    }
                    Box {
                        IconButton(onClick = { showCurrencyDropdown = true }) {
                            Icon(Icons.Filled.Payments, contentDescription = "Currency")
                        }
                        DropdownMenu(
                            expanded = showCurrencyDropdown,
                            onDismissRequest = { showCurrencyDropdown = false }
                        ) {
                            currencies.forEach { curr ->
                                DropdownMenuItem(
                                    text = { Text(curr) },
                                    onClick = {
                                        onCurrencyChange(curr)
                                        showCurrencyDropdown = false
                                    }
                                )
                            }
                        }
                    }
                    IconButton(onClick = onNavigateToNotifications) {
                        Icon(Icons.Filled.Notifications, contentDescription = "Notifications")
                    }
                    IconButton(onClick = onNavigateToProfile) {
                        Icon(Icons.Filled.AccountCircle, contentDescription = "Profile")
                    }
                    IconButton(onClick = onNavigateToSettings) {
                        Icon(Icons.Filled.Settings, contentDescription = "Settings")
                    }
                    IconButton(onClick = onLogout) {
                        Icon(Icons.Filled.Logout, contentDescription = "Logout")
                    }
                }
            )
        },
        bottomBar = {
            NavigationBar {
                items.forEachIndexed { index, item ->
                    NavigationBarItem(
                        icon = { Icon(icons[index], contentDescription = item) },
                        label = { Text(item) },
                        selected = selectedItem == index,
                        onClick = { 
                            selectedItem = index
                            when (item) {
                                "Transactions" -> onNavigateToTransactions()
                                "Analytics" -> onNavigateToReports()
                                "Profile" -> onNavigateToProfile()
                            }
                        }
                    )
                }
            }
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onNavigateToAddTransaction) {
                Icon(Icons.Filled.Add, contentDescription = "Add")
            }
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

            // 2. Financial Summary Cards
            item {
                Surface(onClick = onNavigateToAccounts) {
                    BalanceCard(balance = "$currency ${String.format("%,.0f", totalBalance)}")
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    SummaryMiniCard("Income", "$currency ${String.format("%,.1f", totalIncome / 1000.0)}K", Color(0xFF4CAF50), Modifier.weight(1f))
                    SummaryMiniCard("Expenses", "$currency ${String.format("%,.1f", totalExpenses / 1000.0)}K", Color(0xFFF44336), Modifier.weight(1f))
                    SummaryMiniCard("Savings", "$currency ${String.format("%,.1f", totalSavings / 1000.0)}K", Color(0xFF2196F3), Modifier.weight(1f))
                }
            }

            // 3. Expense Analytics Section
            item {
                SectionHeader("Expense Analytics")
                if (spendingByCategory.isEmpty()) {
                    Card(modifier = Modifier.fillMaxWidth().height(150.dp)) {
                        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                            Text("No expense data to display", color = Color.Gray)
                        }
                    }
                } else {
                    AnalyticsCard(spendingByCategory, maxSpending)
                }
            }

            // 4. Recent Transactions
            item {
                SectionHeader("Recent Transactions", viewAll = true, onViewAll = onNavigateToTransactions)
            }
            if (transactions.isEmpty()) {
                item {
                    Text("No transactions yet. Start adding!", color = Color.Gray, modifier = Modifier.padding(vertical = 16.dp))
                }
            } else {
                items(transactions.take(10)) { transaction ->
                    TransactionItem(transaction, currency)
                }
            }

            // 5. Savings Goals
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    SectionHeader("Savings Goals")
                    IconButton(onClick = { showAddGoalDialog = true }) {
                        Icon(Icons.Filled.Add, contentDescription = "Add Goal")
                    }
                }
            }
            if (savingsGoals.isEmpty()) {
                item {
                    Text("No savings goals yet.", color = Color.Gray, modifier = Modifier.padding(vertical = 16.dp))
                }
            } else {
                items(savingsGoals) { goal ->
                    val progress = if (goal.targetAmount > 0) (goal.currentAmount / goal.targetAmount).toFloat() else 0f
                    val remaining = goal.targetAmount - goal.currentAmount
                    SavingsGoalItem(
                        name = goal.name,
                        progress = progress,
                        remaining = "$currency ${String.format("%,.0f", remaining)} remaining"
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }

            item { Spacer(modifier = Modifier.height(16.dp)) }
        }

        if (showAddGoalDialog) {
            AddGoalDialog(
                onDismiss = { showAddGoalDialog = false },
                onConfirm = { name, target ->
                    scope.launch {
                        savingsGoalRepository.insert(
                            SavingsGoal(
                                userId = currentUserId,
                                name = name,
                                targetAmount = target,
                                currentAmount = 0.0,
                                currency = currency
                            )
                        )
                        showAddGoalDialog = false
                    }
                }
            )
        }
    }
}

@Composable
fun AnalyticsCard(spendingByCategory: Map<String, Double>, maxSpending: Double) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Spending by Category", style = MaterialTheme.typography.labelMedium, color = Color.Gray)
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.Bottom
            ) {
                val colors = listOf(Color.Red, Color.Green, Color.Blue, Color.Yellow, Color.Magenta, Color.Cyan)
                spendingByCategory.entries.take(6).forEachIndexed { index, entry ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .fillMaxHeight((entry.value / maxSpending).toFloat().coerceIn(0.1f, 1f))
                                .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                                .background(colors[index % colors.size].copy(alpha = 0.6f))
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (entry.key.length > 5) entry.key.take(4) + "." else entry.key,
                            fontSize = 10.sp,
                            maxLines = 1
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun BalanceCard(balance: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Total Balance", fontSize = 16.sp)
            Text(balance, fontSize = 32.sp, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.onPrimaryContainer)
        }
    }
}

@Composable
fun SummaryMiniCard(title: String, amount: String, color: Color, modifier: Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.1f))
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(title, fontSize = 12.sp, color = color, fontWeight = FontWeight.Bold)
            Text(amount, fontSize = 14.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun SectionHeader(title: String, viewAll: Boolean = false, onViewAll: () -> Unit = {}) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(title, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        if (viewAll) {
            TextButton(onClick = onViewAll) {
                Text("View All")
            }
        }
    }
}

@Composable
fun TransactionItem(transaction: Transaction, currency: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(if (transaction.isExpense) Color.Red.copy(0.1f) else Color.Green.copy(0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                if (transaction.isExpense) Icons.Filled.ArrowDownward else Icons.Filled.ArrowUpward,
                contentDescription = null,
                tint = if (transaction.isExpense) Color.Red else Color.Green,
                modifier = Modifier.size(20.dp)
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(transaction.title, fontWeight = FontWeight.Bold)
            Text(transaction.category, fontSize = 12.sp, color = Color.Gray)
        }
        Column(horizontalAlignment = Alignment.End) {
            Text(
                transaction.getFormattedAmount(currency),
                fontWeight = FontWeight.Bold,
                color = if (transaction.isExpense) Color.Red else Color.Green
            )
            Text(transaction.date, fontSize = 10.sp, color = Color.Gray)
        }
    }
}

@Composable
fun SavingsGoalItem(name: String, progress: Float, remaining: String) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(name, fontWeight = FontWeight.Bold)
                Text("${(progress * 100).toInt()}%")
            }
            Spacer(modifier = Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(remaining, fontSize = 12.sp, color = Color.Gray)
        }
    }
}

@Composable
fun AlertCard(message: String, onClick: () -> Unit = {}) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.Yellow.copy(alpha = 0.1f)),
        border = BorderStroke(1.dp, Color.Yellow.copy(alpha = 0.5f))
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Filled.Warning, contentDescription = null, tint = Color(0xFFFFA000))
            Spacer(modifier = Modifier.width(12.dp))
            Text(message, fontSize = 14.sp)
        }
    }
}

@Composable
fun AddGoalDialog(onDismiss: () -> Unit, onConfirm: (String, Double) -> Unit) {
    var name by remember { mutableStateOf("") }
    var targetAmount by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("New Savings Goal") },
        text = {
            Column {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Goal Name") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = targetAmount,
                    onValueChange = { targetAmount = it },
                    label = { Text("Target Amount") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val amount = targetAmount.toDoubleOrNull() ?: 0.0
                    if (name.isNotBlank() && amount > 0) {
                        onConfirm(name, amount)
                    }
                }
            ) {
                Text("Add")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

