package ug.ac.ndejje.myapp

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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditTransactionScreen(currency: String, onNavigateBack: () -> Unit) {
    var transactionType by remember { mutableStateOf("Expense") }
    var amountValue by remember { mutableStateOf(0.0) }
    var amountString by remember { mutableStateOf("0.00") }
    var category by remember { mutableStateOf("Food") }
    var account by remember { mutableStateOf("Cash") }
    var note by remember { mutableStateOf("") }
    var isRecurring by remember { mutableStateOf(false) }
    var needsReceipt by remember { mutableStateOf(false) }
    
    val scrollState = rememberScrollState()

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
                    TextButton(onClick = { /* Save Transaction */ }) {
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
                .verticalScroll(scrollState)
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
                listOf("Expense", "Income", "Transfer").forEach { type ->
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

            // Category and Account
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Category", style = MaterialTheme.typography.labelLarge)
                    OutlinedCard(onClick = { /* Open Category Picker */ }) {
                        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                            Text(category, modifier = Modifier.weight(1f))
                            Icon(Icons.Filled.ArrowDropDown, null)
                        }
                    }
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text("Account", style = MaterialTheme.typography.labelLarge)
                    OutlinedCard(onClick = { /* Open Account Picker */ }) {
                        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                            Text(account, modifier = Modifier.weight(1f))
                            Icon(Icons.Filled.ArrowDropDown, null)
                        }
                    }
                }
            }

            // Note
            Column {
                Text("Note (optional)", style = MaterialTheme.typography.labelLarge)
                OutlinedTextField(
                    value = note,
                    onValueChange = { note = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("e.g. Lunch with team") }
                )
            }

            // Options
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = isRecurring, onCheckedChange = { isRecurring = it })
                    Text("Recurring (monthly)")
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = needsReceipt, onCheckedChange = { needsReceipt = it })
                    Text("Needs receipt")
                    Spacer(modifier = Modifier.weight(1f))
                    IconButton(onClick = { /* Attach Receipt */ }) {
                        Icon(Icons.Filled.AttachFile, contentDescription = "Attach")
                    }
                }
            }

            // AI Suggestion Box
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.4f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text("🤖", fontSize = 24.sp)
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            "AI: I see you often spend $12-15 at Starbucks. Use quick-add?",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                    TextButton(onClick = { /* Apply Quick Add */ }) {
                        Text("Yes")
                    }
                }
            }
        }
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
        listOf(".", "0", "←", "✓")
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
                                "✓" -> { /* Confirm */ }
                                "←" -> { /* Back */ }
                                else -> onNumberClick(key)
                            }
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (key == "✓") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                            contentColor = if (key == "✓") MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    ) {
                        Text(key, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
