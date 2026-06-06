package ug.ac.ndejje.myapp.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import kotlinx.coroutines.launch
import ug.ac.ndejje.myapp.resources.SavingsGoal
import ug.ac.ndejje.myapp.resources.SavingsGoalRepository

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SavingsGoalScreen(
    currency: String,
    currentUserId: Int,
    savingsGoalRepository: SavingsGoalRepository,
    onNavigateBack: () -> Unit
) {
    val savingsGoals by savingsGoalRepository.allGoals(currentUserId).collectAsState(initial = emptyList())
    var showAddGoalDialog by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Savings Goals") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { showAddGoalDialog = true }) {
                        Icon(Icons.Filled.Add, contentDescription = "Add Goal")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddGoalDialog = true }) {
                Icon(Icons.Filled.Add, contentDescription = "Add Goal")
            }
        }
    ) { innerPadding ->
        if (savingsGoals.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Filled.AccountBalance,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("No savings goals yet.", color = Color.Gray)
                    Button(onClick = { showAddGoalDialog = true }, modifier = Modifier.padding(top = 16.dp)) {
                        Text("Create Your First Goal")
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(vertical = 16.dp)
            ) {
                items(savingsGoals) { goal ->
                    val progress = if (goal.targetAmount > 0) (goal.currentAmount / goal.targetAmount).toFloat() else 0f
                    val remaining = goal.targetAmount - goal.currentAmount
                    
                    SavingsGoalItem(
                        name = goal.name,
                        progress = progress,
                        remaining = "$currency ${String.format("%,.0f", remaining)} remaining",
                        currentAmount = "$currency ${String.format("%,.0f", goal.currentAmount)}",
                        targetAmount = "$currency ${String.format("%,.0f", goal.targetAmount)}",
                        onDelete = {
                            scope.launch {
                                savingsGoalRepository.delete(goal)
                            }
                        },
                        onAddFunds = {
                            // Deduct from income/account while adding to goal
                            scope.launch {
                                val increment = goal.targetAmount * 0.1
                                savingsGoalRepository.contribute(goal, increment)
                            }
                        }
                    )
                }
            }
        }

        if (showAddGoalDialog) {
            AddGoalDialog(
                onDismiss = { showAddGoalDialog = false },
                onConfirm = { name: String, target: Double ->
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
fun SavingsGoalItem(
    name: String, 
    progress: Float, 
    remaining: String,
    currentAmount: String,
    targetAmount: String,
    onDelete: () -> Unit,
    onAddFunds: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(), 
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(name, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Text("Target: $targetAmount", fontSize = 12.sp, color = Color.Gray)
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Filled.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(currentAmount, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.primary)
                Text("${(progress * 100).toInt()}%")
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(10.dp)
                    .clip(RoundedCornerShape(5.dp)),
                color = if (progress >= 1f) Color(0xFF4CAF50) else MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(remaining, fontSize = 12.sp, color = Color.Gray)
                if (progress < 1f) {
                    TextButton(onClick = onAddFunds) {
                        Icon(Icons.Filled.AddCircleOutline, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Add Funds", fontSize = 12.sp)
                    }
                } else {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.CheckCircle, contentDescription = null, tint = Color(0xFF4CAF50), modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Completed", fontSize = 12.sp, color = Color(0xFF4CAF50), fontWeight = FontWeight.Bold)
                    }
                }
            }
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
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Goal Name (e.g. New Car)") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
                OutlinedTextField(
                    value = targetAmount,
                    onValueChange = { targetAmount = it },
                    label = { Text("Target Amount") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                        keyboardType = androidx.compose.ui.text.input.KeyboardType.Number
                    )
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
                },
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Create Goal")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
