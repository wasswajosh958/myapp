package ug.ac.ndejje.myapp

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
    onNavigateToBudgets: () -> Unit,
    onNavigateToAccounts: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToNotifications: () -> Unit,
    onCurrencyChange: (String) -> Unit,
    userProfileRepository: UserProfileRepository,
    database: AppDatabase,
    authManager: AuthManager
) {
    val context = LocalContext.current
    val currentUserId = authManager.getCurrentUserId()
    val username = authManager.getCurrentUsername()
    val assistant = remember { AiAssistant(context, database, currentUserId) }
    val scope = rememberCoroutineScope()
    val userProfile by userProfileRepository.getUserProfile(currentUserId).collectAsState(initial = null)
    val displayName = userProfile?.username ?: username

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
    val items = listOf("Home", "Transactions", "Analytics", "Budgets", "Profile")
    val icons = listOf(Icons.Filled.Home, Icons.Filled.CreditCard, Icons.Filled.BarChart, Icons.Filled.AccountBalanceWallet, Icons.Filled.Person)

    var showCurrencyDropdown by remember { mutableStateOf(false) }
    val currencies = listOf("Shs", "$", "€", "£")

    val transactions by database.transactionDao().getAllTransactions(currentUserId).collectAsState(initial = emptyList())
    val accounts by database.accountDao().getAllAccounts(currentUserId).collectAsState(initial = emptyList())
    val budgets by database.budgetDao().getAllBudgets(currentUserId).collectAsState(initial = emptyList())

    val totalBalance = accounts.sumOf { it.balance }
    val totalIncome = transactions.filter { !it.isExpense }.sumOf { it.amountValue }
    val totalExpenses = transactions.filter { it.isExpense }.sumOf { it.amountValue }
    val totalSavings = totalIncome - totalExpenses

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
                                "Budgets" -> onNavigateToBudgets()
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
                    SummaryMiniCard("Income", "$currency ${String.format("%,.1f", totalIncome / 1000000.0)}M", Color(0xFF4CAF50), Modifier.weight(1f))
                    SummaryMiniCard("Expenses", "$currency ${String.format("%,.1f", totalExpenses / 1000000.0)}M", Color(0xFFF44336), Modifier.weight(1f))
                    SummaryMiniCard("Savings", "$currency ${String.format("%,.1f", totalSavings / 1000.0)}K", Color(0xFF2196F3), Modifier.weight(1f))
                }
            }

            // 3. Expense Analytics Section (Simple Placeholder)
            item {
                SectionHeader("Expense Analytics")
                AnalyticsPlaceholder()
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
                items(transactions.take(5)) { transaction ->
                    TransactionItem(transaction, currency)
                }
            }

            // 5. Savings Goals
            item {
                SectionHeader("Savings Goals")
            }
            item {
                SavingsGoalItem("Laptop Fund", 0.7f, "Shs 300,000 remaining")
            }

            // 6. Budget Alerts
            item {
                SectionHeader("Budgets", viewAll = true, onViewAll = onNavigateToBudgets)
            }
            if (budgets.isEmpty()) {
                item {
                    Text("No budgets set.", color = Color.Gray)
                }
            } else {
                items(budgets.take(2)) { budget ->
                    val progress = if (budget.limit > 0) (budget.spent / budget.limit) else 0.0
                    if (progress >= 0.8) {
                        AlertCard("${budget.category} budget is ${(progress * 100).toInt()}% used.", onClick = onNavigateToBudgets)
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(16.dp)) }
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
fun AnalyticsPlaceholder() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.Bottom) {
                // Mock Bar Chart
                MockBar(0.4f, Color.Red)
                MockBar(0.7f, Color.Green)
                MockBar(0.5f, Color.Blue)
                MockBar(0.9f, Color.Yellow)
                MockBar(0.3f, Color.Magenta)
            }
            Text("Monthly Spending Analytics", color = Color.Gray.copy(alpha = 0.5f), fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun MockBar(heightPercent: Float, color: Color) {
    Box(
        modifier = Modifier
            .width(20.dp)
            .fillMaxHeight(heightPercent)
            .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
            .background(color.copy(alpha = 0.6f))
    )
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
