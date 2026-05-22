package ug.ac.ndejje.myapp

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(onLogout: () -> Unit) {
    var selectedItem by remember { mutableIntStateOf(0) }
    val items = listOf("Home", "Transactions", "Analytics", "Goals", "Settings")
    val icons = listOf(Icons.Filled.Home, Icons.Filled.CreditCard, Icons.Filled.BarChart, Icons.Filled.TrackChanges, Icons.Filled.Settings)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("FinTrack", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                        Text("Welcome back, Joshua", fontSize = 14.sp, fontWeight = FontWeight.Normal)
                    }
                },
                actions = {
                    IconButton(onClick = { /* Handle Notifications */ }) {
                        Icon(Icons.Filled.Notifications, contentDescription = "Notifications")
                    }
                    IconButton(onClick = { /* Handle Profile/Settings */ }) {
                        Icon(Icons.Filled.AccountCircle, contentDescription = "Profile")
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
                        onClick = { selectedItem = index }
                    )
                }
            }
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { /* Quick Add */ }) {
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
                BalanceCard(balance = "UGX 2,450,000")
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    SummaryMiniCard("Income", "UGX 4.0M", Color(0xFF4CAF50), Modifier.weight(1f))
                    SummaryMiniCard("Expenses", "UGX 1.5M", Color(0xFFF44336), Modifier.weight(1f))
                    SummaryMiniCard("Savings", "UGX 900K", Color(0xFF2196F3), Modifier.weight(1f))
                }
            }

            // 3. Expense Analytics Section (Simple Placeholder)
            item {
                SectionHeader("Expense Analytics")
                AnalyticsPlaceholder()
            }

            // 4. Recent Transactions
            item {
                SectionHeader("Recent Transactions", viewAll = true)
            }
            items(getMockTransactions()) { transaction ->
                TransactionItem(transaction)
            }

            // 5. Savings Goals
            item {
                SectionHeader("Savings Goals")
            }
            item {
                SavingsGoalItem("Laptop Fund", 0.7f, "UGX 300,000 remaining")
            }

            // 6. Budget Alerts
            item {
                AlertCard("You have spent 85% of your food budget.")
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
fun SectionHeader(title: String, viewAll: Boolean = false) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(title, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        if (viewAll) {
            TextButton(onClick = { }) {
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
fun TransactionItem(transaction: Transaction) {
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
                (if (transaction.isExpense) "-" else "+") + transaction.amount,
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
fun AlertCard(message: String) {
    Card(
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

data class Transaction(val title: String, val category: String, val amount: String, val date: String, val isExpense: Boolean)

fun getMockTransactions() = listOf(
    Transaction("Airtime", "Utilities", "UGX 10,000", "May 22", true),
    Transaction("Salary", "Income", "UGX 2,000,000", "May 21", false),
    Transaction("Lunch", "Food", "UGX 15,000", "May 20", true),
    Transaction("Fuel", "Transport", "UGX 50,000", "May 19", true)
)
