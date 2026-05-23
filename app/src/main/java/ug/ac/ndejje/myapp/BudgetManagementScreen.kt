package ug.ac.ndejje.myapp

import androidx.compose.foundation.background
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

data class Budget(
    val id: Int,
    val category: String,
    val limit: Double,
    val spent: Double
) {
    val progress: Float get() = (spent / limit).toFloat().coerceIn(0f, 1f)
    val remaining: Double get() = (limit - spent).coerceAtLeast(0.0)
    val isOverspent: Boolean get() = spent > limit
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BudgetManagementScreen(currency: String, onNavigateBack: () -> Unit) {
    var totalMonthlyBudget by remember { mutableDoubleStateOf(5000000.0) }
    val budgets = remember {
        mutableStateListOf(
            Budget(1, "Food", 500000.0, 450000.0),
            Budget(2, "Transport", 300000.0, 150000.0),
            Budget(3, "Rent", 1200000.0, 1200000.0),
            Budget(4, "Entertainment", 200000.0, 250000.0),
            Budget(5, "Utilities", 400000.0, 100000.0)
        )
    }

    var showAddBudgetDialog by remember { mutableStateOf(false) }
    var editingBudget by remember { mutableStateOf<Budget?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Budget Management") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { showAddBudgetDialog = true }) {
                        Icon(Icons.Filled.Add, contentDescription = "Add Budget")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddBudgetDialog = true }) {
                Icon(Icons.Filled.Add, contentDescription = "Add Budget")
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                TotalBudgetCard(totalMonthlyBudget, budgets.sumOf { it.spent }, currency)
            }

            item {
                Text("Category Budgets", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }

            items(budgets) { budget ->
                BudgetCategoryItem(budget, currency, 
                    onDelete = { budgets.remove(budget) },
                    onEdit = { editingBudget = budget }
                )
            }
        }
    }

    if (showAddBudgetDialog) {
        AddBudgetDialog(
            onDismiss = { showAddBudgetDialog = false },
            onConfirm = { category, limit ->
                budgets.add(Budget(budgets.size + 1, category, limit, 0.0))
                showAddBudgetDialog = false
            }
        )
    }

    if (editingBudget != null) {
        AddBudgetDialog(
            title = "Edit Budget",
            initialCategory = editingBudget!!.category,
            initialLimit = editingBudget!!.limit.toString(),
            onDismiss = { editingBudget = null },
            onConfirm = { category, limit ->
                val index = budgets.indexOfFirst { it.id == editingBudget!!.id }
                if (index != -1) {
                    budgets[index] = budgets[index].copy(category = category, limit = limit)
                }
                editingBudget = null
            }
        )
    }
}

@Composable
fun TotalBudgetCard(limit: Double, spent: Double, currency: String) {
    val progress = (spent / limit).toFloat().coerceIn(0f, 1f)
    val remaining = (limit - spent).coerceAtLeast(0.0)
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text("Total Monthly Budget", fontSize = 16.sp)
            Text("$currency ${String.format("%,.0f", limit)}", fontSize = 24.sp, fontWeight = FontWeight.Bold)
            
            Spacer(modifier = Modifier.height(16.dp))
            
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
                color = if (progress > 0.9f) Color.Red else MaterialTheme.colorScheme.primary
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Spent: $currency ${String.format("%,.0f", spent)}", fontSize = 12.sp)
                Text("Remaining: $currency ${String.format("%,.0f", remaining)}", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
            
            if (spent > limit) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Warning, contentDescription = null, tint = Color.Red, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Overspending Alert!", color = Color.Red, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun BudgetCategoryItem(budget: Budget, currency: String, onDelete: () -> Unit, onEdit: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(budget.category, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Row {
                    IconButton(onClick = onEdit, modifier = Modifier.size(24.dp)) {
                        Icon(Icons.Filled.Edit, contentDescription = "Edit", modifier = Modifier.size(16.dp))
                    }
                    IconButton(onClick = onDelete, modifier = Modifier.size(24.dp)) {
                        Icon(Icons.Filled.Delete, contentDescription = "Delete", modifier = Modifier.size(16.dp), tint = Color.Red)
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            LinearProgressIndicator(
                progress = { budget.progress },
                modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
                color = if (budget.isOverspent) Color.Red else MaterialTheme.colorScheme.secondary
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("$currency ${String.format("%,.0f", budget.spent)} / $currency ${String.format("%,.0f", budget.limit)}", fontSize = 12.sp)
                if (budget.isOverspent) {
                    Text("Exceeded by $currency ${String.format("%,.0f", budget.spent - budget.limit)}", color = Color.Red, fontSize = 12.sp)
                } else {
                    Text("${(budget.progress * 100).toInt()}% used", fontSize = 12.sp)
                }
            }
        }
    }
}

@Composable
fun AddBudgetDialog(
    title: String = "Add Category Budget",
    initialCategory: String = "",
    initialLimit: String = "",
    onDismiss: () -> Unit, 
    onConfirm: (String, Double) -> Unit
) {
    var category by remember { mutableStateOf(initialCategory) }
    var limit by remember { mutableStateOf(initialLimit) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = category,
                    onValueChange = { category = it },
                    label = { Text("Category (e.g. Food, Rent)") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = limit,
                    onValueChange = { limit = it },
                    label = { Text("Monthly Limit") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { 
                    val limitVal = limit.toDoubleOrNull() ?: 0.0
                    if (category.isNotEmpty() && limitVal > 0) {
                        onConfirm(category, limitVal)
                    }
                }
            ) {
                Text(if (title.contains("Add")) "Add" else "Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
