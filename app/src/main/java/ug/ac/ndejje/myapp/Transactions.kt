package ug.ac.ndejje.myapp

import androidx.compose.foundation.background
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.lazy.itemsIndexed

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionsScreen(onNavigateBack: () -> Unit, onNavigateToAddTransaction: () -> Unit) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("All") }
    val filters = listOf("All", "Income", "Expense", "Pending")
    
    val transactions = remember { mutableStateListOf(*getFullMockTransactions().toTypedArray()) }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Transactions") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = onNavigateToAddTransaction) {
                        Icon(Icons.Filled.Add, contentDescription = "Add")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Search Bar
            SearchBar(
                query = searchQuery,
                onQueryChange = { searchQuery = it },
                onMicClick = { /* Handle Voice Search: "Show me all Uber rides from last month" */ },
                onCalendarClick = { /* Handle Date Filter */ }
            )

            // Filters
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                filters.forEach { filter ->
                    FilterChip(
                        selected = selectedFilter == filter,
                        onClick = { selectedFilter = filter },
                        label = { Text(filter) }
                    )
                }
            }

            // Transactions List
            val filteredTransactions = transactions.filter {
                (selectedFilter == "All" || 
                 (selectedFilter == "Income" && !it.isExpense) ||
                 (selectedFilter == "Expense" && it.isExpense) ||
                 (selectedFilter == "Pending" && it.isPending)) &&
                (searchQuery.isEmpty() || it.title.contains(searchQuery, ignoreCase = true))
            }

            val groupedTransactions = filteredTransactions.groupBy { it.date }

            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                groupedTransactions.forEach { (date, items) ->
                    item {
                        Text(
                            text = date.uppercase(),
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }
                    itemsIndexed(items) { index, transaction ->
                        SwipeableTransactionItem(
                            transaction = transaction,
                            onDelete = {
                                val removedTransaction = transaction
                                transactions.remove(transaction)
                                // Handle Undo
                            },
                            onEdit = { /* Handle Edit */ }
                        )
                    }
                }
            }

            // Footer
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "Showing ${filteredTransactions.size} of ${transactions.size} transactions",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }
        }
    }
}

@Composable
fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onMicClick: () -> Unit,
    onCalendarClick: () -> Unit
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        placeholder = { Text("Search transactions...") },
        leadingIcon = { Icon(Icons.Filled.Search, contentDescription = "Search") },
        trailingIcon = {
            Row {
                IconButton(onClick = onMicClick) {
                    Icon(Icons.Filled.Mic, contentDescription = "Voice Search")
                }
                IconButton(onClick = onCalendarClick) {
                    Icon(Icons.Filled.DateRange, contentDescription = "Calendar")
                }
            }
        },
        shape = RoundedCornerShape(28.dp),
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
            focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        )
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SwipeableTransactionItem(
    transaction: Transaction,
    onDelete: () -> Unit,
    onEdit: () -> Unit
) {
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = {
            when (it) {
                SwipeToDismissBoxValue.EndToStart -> {
                    onDelete()
                    false // Don't dismiss immediately, handle deletion in state
                }
                SwipeToDismissBoxValue.StartToEnd -> {
                    onEdit()
                    false
                }
                else -> false
            }
        }
    )

    SwipeToDismissBox(
        state = dismissState,
        backgroundContent = {
            val direction = dismissState.dismissDirection
            val color = when (direction) {
                SwipeToDismissBoxValue.StartToEnd -> Color(0xFF4CAF50) // Edit - Green
                SwipeToDismissBoxValue.EndToStart -> Color(0xFFF44336) // Delete - Red
                else -> Color.Transparent
            }
            val alignment = when (direction) {
                SwipeToDismissBoxValue.StartToEnd -> Alignment.CenterStart
                SwipeToDismissBoxValue.EndToStart -> Alignment.CenterEnd
                else -> Alignment.Center
            }
            val icon = when (direction) {
                SwipeToDismissBoxValue.StartToEnd -> Icons.Filled.Edit
                SwipeToDismissBoxValue.EndToStart -> Icons.Filled.Delete
                else -> null
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color, RoundedCornerShape(12.dp))
                    .padding(horizontal = 20.dp),
                contentAlignment = alignment
            ) {
                if (icon != null) {
                    Icon(icon, contentDescription = null, tint = Color.White)
                }
            }
        }
    ) {
        TransactionListItem(transaction)
    }
}

@Composable
fun TransactionListItem(transaction: Transaction) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                val icon = when (transaction.category) {
                    "Food" -> "☕️"
                    "Transport" -> "🚗"
                    "Groceries" -> "🛒"
                    "Subscription" -> "📺"
                    "Income" -> "💼"
                    else -> "💰"
                }
                Text(icon, fontSize = 24.sp)
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(transaction.title, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(transaction.time, fontSize = 12.sp, color = Color.Gray)
            }
            
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    (if (transaction.isExpense) "-" else "+") + transaction.amount,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = if (transaction.isExpense) Color.Red else Color(0xFF4CAF50)
                )
                Text(transaction.category, fontSize = 12.sp, color = Color.Gray)
            }
        }
    }
}

fun getFullMockTransactions() = listOf(
    Transaction(1, "Starbucks", "Food", "$5.25", "Today", "2:30 PM", true),
    Transaction(2, "Uber", "Transport", "$12.99", "Today", "8:15 AM", true),
    Transaction(3, "Salary", "Income", "$3,200", "Yesterday", "9:00 AM", false),
    Transaction(4, "Whole Foods", "Groceries", "$87.43", "Yesterday", "6:45 PM", true),
    Transaction(5, "Netflix", "Subscription", "$15.99", "Apr 10", "Recurring - Monthly", true),
    Transaction(6, "Rent", "Housing", "$1,200", "Apr 1", "Monthly", true, isPending = true)
)
