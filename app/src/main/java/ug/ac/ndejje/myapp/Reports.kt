package ug.ac.ndejje.myapp

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
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
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun ReportsScreen(currency: String, onNavigateBack: () -> Unit) {
    val scrollState = rememberScrollState()
    var dateRange by remember { mutableStateOf("This month") }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Reports & Insights") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { 
                        scope.launch { snackbarHostState.showSnackbar("Exporting report...") }
                    }) {
                        Icon(Icons.Filled.FileUpload, contentDescription = "Export")
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
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Date Range Selector
            OutlinedCard(
                onClick = { /* Select Date Range */ },
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Date range: ", style = MaterialTheme.typography.bodyMedium)
                    Text(dateRange, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.weight(1f))
                    Icon(Icons.Filled.ArrowDropDown, null)
                }
            }

            // 1. Spending Trend Chart
            ChartCard(title = "Spending Trend") {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f), RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Filled.ShowChart, null, modifier = Modifier.size(48.dp), tint = MaterialTheme.colorScheme.primary)
                        Text("Daily Spending (Line Chart)", style = MaterialTheme.typography.labelLarge)
                    }
                }
            }

            // 2. Summary Totals
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        SummaryItem("Total spent", "$currency 2,660", Color.Red)
                        SummaryItem("Total income", "$currency 3,200", Color(0xFF4CAF50))
                    }
                    Divider(modifier = Modifier.padding(vertical = 12.dp), color = MaterialTheme.colorScheme.outlineVariant)
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        SummaryItem("Net", "+$currency 540", Color(0xFF2196F3))
                        SummaryItem("vs last month", "+8% ↑", Color.Gray)
                    }
                }
            }

            // 3. Category Breakdown (Pie/Donut Chart)
            ChartCard(title = "Category Breakdown") {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(150.dp)
                            .clip(RoundedCornerShape(75.dp))
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("📊 Pie Chart", fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        val categories = listOf(
                            "Food 32%" to Color(0xFFFF5722),
                            "Transport 12%" to Color(0xFFFFC107),
                            "Shopping 18%" to Color(0xFF2196F3),
                            "Bills 20%" to Color(0xFF9C27B0),
                            "Entertainment 8%" to Color(0xFFE91E63),
                            "Other 10%" to Color(0xFF607D8B)
                        )
                        categories.forEach { (label, color) ->
                            CategoryLegendItem(label, color) {
                                scope.launch {
                                    snackbarHostState.showSnackbar("Filtering transactions for ${label.split(" ")[0]}...")
                                }
                            }
                        }
                    }
                }
            }

            // 4. Top Spending Categories
            Column {
                Text("Top spending categories:", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                TopCategoryItem("1. Groceries", "$currency 450", "+5% vs last month", Color.Gray)
                TopCategoryItem("2. Dining out", "$currency 350", "+15% over budget", Color.Red, isOverBudget = true)
                TopCategoryItem("3. Transport", "$currency 320", "-2% ↓", Color(0xFF4CAF50))
            }

            // 5. AI Insight
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.4f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text("🤖", fontSize = 24.sp)
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            "AI Insight: Your dining out spending is up 15%. Consider reducing to meet your $currency 300 budget.",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        TextButton(onClick = { 
                            scope.launch { snackbarHostState.showSnackbar("Budget alert set!") }
                        }) {
                            Text("Set alert")
                        }
                    }
                }
            }

            // 6. Footer Actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(onClick = { 
                    scope.launch { snackbarHostState.showSnackbar("CSV Export started...") }
                }, modifier = Modifier.weight(1f)) {
                    Text("Export CSV", fontSize = 11.sp, maxLines = 1)
                }
                OutlinedButton(onClick = { 
                    scope.launch { snackbarHostState.showSnackbar("Opening share sheet...") }
                }, modifier = Modifier.weight(1f)) {
                    Text("Share", fontSize = 11.sp, maxLines = 1)
                }
                Button(onClick = { 
                    scope.launch { snackbarHostState.showSnackbar("AI Analysis: You spend most on Food.") }
                }, modifier = Modifier.weight(1.2f)) {
                    Text("Ask AI", fontSize = 11.sp, maxLines = 1)
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun ChartCard(title: String, content: @Composable () -> Unit) {
    Column {
        Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
        ) {
            Box(modifier = Modifier.padding(16.dp)) {
                content()
            }
        }
    }
}

@Composable
fun SummaryItem(label: String, value: String, color: Color) {
    Column {
        Text(label, style = MaterialTheme.typography.labelSmall, color = Color.Gray)
        Text(value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = color)
    }
}

@Composable
fun CategoryLegendItem(text: String, color: Color, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        color = Color.Transparent
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(modifier = Modifier.size(10.dp).background(color, CircleShape))
            Spacer(modifier = Modifier.width(4.dp))
            Text(text, fontSize = 12.sp)
        }
    }
}

@Composable
fun TopCategoryItem(name: String, amount: String, trend: String, trendColor: Color, isOverBudget: Boolean = false) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(name, modifier = Modifier.weight(1f))
        Text(amount, fontWeight = FontWeight.Bold, modifier = Modifier.width(80.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (isOverBudget) {
                Icon(Icons.Filled.Brightness1, null, modifier = Modifier.size(8.dp).padding(end = 4.dp), tint = Color.Red)
            }
            Text(trend, color = trendColor, fontSize = 12.sp)
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FlowRow(
    modifier: Modifier = Modifier,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.Start,
    verticalArrangement: Arrangement.Vertical = Arrangement.Top,
    content: @Composable FlowRowScope.() -> Unit
) {
    androidx.compose.foundation.layout.FlowRow(
        modifier = modifier,
        horizontalArrangement = horizontalArrangement,
        verticalArrangement = verticalArrangement,
        content = content
    )
}
