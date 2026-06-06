package ug.ac.ndejje.myapp.screens

import ug.ac.ndejje.myapp.util.*
import ug.ac.ndejje.myapp.resources.*
import ug.ac.ndejje.myapp.theme.*

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditTransactionScreen(
    currency: String,
    onNavigateBack: () -> Unit,
    transactionRepository: TransactionRepository,
    categoryRepository: CategoryRepository,
    accountRepository: AccountRepository,
    utilityRepository: UtilityRepository,
    authManager: AuthManager
) {
    val scope = rememberCoroutineScope()
    val userId = authManager.getCurrentUserId()
    
    var transactionType by remember { mutableStateOf("Expense") }
    var amountString by remember { mutableStateOf("0.00") }
    
    val categories by categoryRepository.getAllCategories(userId).collectAsState(initial = emptyList())
    var selectedCategory by remember { mutableStateOf<Category?>(null) }
    
    val accounts by accountRepository.getAllAccounts(userId).collectAsState(initial = emptyList())
    var selectedAccount by remember { mutableStateOf<AccountEntity?>(null) }
    
    var title by remember { mutableStateOf("") }
    
    var showAddCategoryDialog by remember { mutableStateOf(false) }
    var showCategoryPicker by remember { mutableStateOf(false) }
    var showAccountPicker by remember { mutableStateOf(false) }
    
    var saveAsUtility by remember { mutableStateOf(false) }
    var providerName by remember { mutableStateOf("") }

    // Initialize selections
    LaunchedEffect(categories) {
        if (selectedCategory == null && categories.isNotEmpty()) {
            selectedCategory = categories.first()
        }
    }
    LaunchedEffect(accounts) {
        if (selectedAccount == null && accounts.isNotEmpty()) {
            selectedAccount = accounts.first()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add Transaction") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    TextButton(
                        onClick = {
                            scope.launch {
                                val amountValue = amountString.toDoubleOrNull() ?: 0.0
                                if (amountValue > 0 && selectedCategory != null) {
                                    val now = Calendar.getInstance()
                                    val dateStr = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(now.time)
                                    val timeStr = SimpleDateFormat("HH:mm", Locale.getDefault()).format(now.time)
                                    
                                    val transaction = Transaction(
                                        userId = userId,
                                        title = title.ifEmpty { selectedCategory!!.name },
                                        category = selectedCategory!!.name,
                                        amountValue = amountValue,
                                        date = dateStr,
                                        time = timeStr,
                                        isExpense = transactionType == "Expense",
                                        accountId = selectedAccount?.id ?: 0
                                    )
                                    transactionRepository.insert(transaction)

                                    if (saveAsUtility) {
                                        val utility = UtilityEntity(
                                            userId = userId,
                                            name = title.ifEmpty { selectedCategory!!.name },
                                            provider = providerName.ifEmpty { "Generic Provider" },
                                            accountNumber = null,
                                            defaultAmount = amountValue,
                                            categoryId = selectedCategory!!.id
                                        )
                                        utilityRepository.insert(utility)
                                    }

                                    onNavigateBack()
                                }
                            }
                        }
                    ) {
                        Text("Save", fontWeight = FontWeight.Bold)
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
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Transaction Type Selector
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                listOf("Expense", "Income").forEach { type ->
                    val isSelected = transactionType == type
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { transactionType = type }
                            .background(if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent)
                            .padding(vertical = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = type,
                            color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }
            }

            // Amount Display
            Column {
                Text("Amount", style = MaterialTheme.typography.labelLarge)
                Spacer(modifier = Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(8.dp))
                        .padding(16.dp),
                    contentAlignment = Alignment.CenterEnd
                ) {
                    Text(
                        text = "$currency $amountString",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Numeric Keypad
            NumericKeypad(
                onNumberClick = { num ->
                    if (amountString == "0.00") amountString = num
                    else amountString += num
                },
                onDeleteClick = {
                    if (amountString.length > 1) amountString = amountString.dropLast(1)
                    else amountString = "0.00"
                },
                onClearClick = { amountString = "0.00" }
            )

            // Title
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Title (Optional)") },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("e.g. Lunch at Starbucks") }
            )

            // Save as Utility Option
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = saveAsUtility, onCheckedChange = { saveAsUtility = it })
                    Text("Add to my Utilities (Recurring)")
                }
                if (saveAsUtility) {
                    OutlinedTextField(
                        value = providerName,
                        onValueChange = { providerName = it },
                        label = { Text("Provider Name") },
                        modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                        placeholder = { Text("e.g. Umeme, NWSC, Netflix") }
                    )
                }
            }

            // Category and Account
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Category", style = MaterialTheme.typography.labelLarge)
                    OutlinedCard(onClick = { showCategoryPicker = true }) {
                        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                            Text(selectedCategory?.name ?: "Select", modifier = Modifier.weight(1f))
                            Icon(Icons.Filled.ArrowDropDown, null)
                        }
                    }
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text("Account", style = MaterialTheme.typography.labelLarge)
                    OutlinedCard(onClick = { showAccountPicker = true }) {
                        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                            Text(selectedAccount?.name ?: "Select", modifier = Modifier.weight(1f))
                            Icon(Icons.Filled.ArrowDropDown, null)
                        }
                    }
                }
            }

            Button(
                onClick = {
                    scope.launch {
                        val amountValue = amountString.toDoubleOrNull() ?: 0.0
                        if (amountValue > 0 && selectedCategory != null) {
                            val now = Calendar.getInstance()
                            val dateStr = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(now.time)
                            val timeStr = SimpleDateFormat("HH:mm", Locale.getDefault()).format(now.time)
                            
                            val transaction = Transaction(
                                userId = userId,
                                title = title.ifEmpty { selectedCategory!!.name },
                                category = selectedCategory!!.name,
                                amountValue = amountValue,
                                date = dateStr,
                                time = timeStr,
                                isExpense = transactionType == "Expense",
                                accountId = selectedAccount?.id ?: 0
                            )
                            transactionRepository.insert(transaction)
                            onNavigateBack()
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("SAVE TRANSACTION", fontWeight = FontWeight.Bold)
            }
        }
    }

    if (showCategoryPicker) {
        AlertDialog(
            onDismissRequest = { showCategoryPicker = false },
            title = { Text("Select Category") },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    categories.forEach { cat ->
                        ListItem(
                            headlineContent = { Text(cat.name) },
                            modifier = Modifier.clickable {
                                selectedCategory = cat
                                showCategoryPicker = false
                            }
                        )
                    }
                    TextButton(onClick = { 
                        showCategoryPicker = false
                        showAddCategoryDialog = true 
                    }) {
                        Icon(Icons.Filled.Add, null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Add New Category")
                    }
                }
            },
            confirmButton = {}
        )
    }

    if (showAddCategoryDialog) {
        var newCatName by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { showAddCategoryDialog = false },
            title = { Text("Add New Category") },
            text = {
                OutlinedTextField(
                    value = newCatName,
                    onValueChange = { newCatName = it },
                    label = { Text("Category Name") },
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(onClick = {
                    if (newCatName.isNotEmpty()) {
                        scope.launch {
                            val newCat = Category(userId = userId, name = newCatName, icon = "💰", type = transactionType.lowercase())
                            categoryRepository.insert(newCat)
                            selectedCategory = newCat
                            showAddCategoryDialog = false
                        }
                    }
                }) {
                    Text("Add")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddCategoryDialog = false }) { Text("Cancel") }
            }
        )
    }

    if (showAccountPicker) {
        AlertDialog(
            onDismissRequest = { showAccountPicker = false },
            title = { Text("Select Account") },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    accounts.forEach { acc ->
                        ListItem(
                            headlineContent = { Text(acc.name) },
                            supportingContent = { Text("$currency ${acc.balance}") },
                            modifier = Modifier.clickable {
                                selectedAccount = acc
                                showAccountPicker = false
                            }
                        )
                    }
                }
            },
            confirmButton = {}
        )
    }
}

@Composable
fun NumericKeypad(
    onNumberClick: (String) -> Unit,
    onDeleteClick: () -> Unit,
    onClearClick: () -> Unit
) {
    val keys = listOf(
        listOf("1", "2", "3", "⌫"),
        listOf("4", "5", "6", "C"),
        listOf("7", "8", "9", "00"),
        listOf(".", "0", "000", "OK")
    )

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        keys.forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                row.forEach { key ->
                    Button(
                        onClick = {
                            when (key) {
                                "⌫" -> onDeleteClick()
                                "C" -> onClearClick()
                                "OK" -> { /* Confirm */ }
                                else -> onNumberClick(key)
                            }
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (key == "OK") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                            contentColor = if (key == "OK") MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    ) {
                        Text(key, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
