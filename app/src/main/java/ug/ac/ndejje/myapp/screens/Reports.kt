package ug.ac.ndejje.myapp.screens

import ug.ac.ndejje.myapp.util.*
import ug.ac.ndejje.myapp.resources.*
import ug.ac.ndejje.myapp.theme.*
import ug.ac.ndejje.myapp.util.AiAssistant

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
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
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import java.util.Calendar
import java.util.Locale
import java.text.SimpleDateFormat
import kotlin.math.min

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun ReportsScreen(
    currency: String,
    userId: Int,
    transactionRepository: TransactionRepository,
    savingsGoalRepository: SavingsGoalRepository,
    aiConversationRepository: AIConversationRepository,
    settingsDataStore: SettingsDataStore,
    onNavigateBack: () -> Unit
) {
    val scrollState = rememberScrollState()
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    val geminiKey by settingsDataStore.geminiApiKeyFlow.collectAsState(initial = "")

    // Date Range State
    val calendar = remember { Calendar.getInstance() }
    var selectedMonth by remember { mutableIntStateOf(calendar.get(Calendar.MONTH)) }
    var selectedYear by remember { mutableIntStateOf(calendar.get(Calendar.YEAR)) }
    var showMenu by remember { mutableStateOf(false) }

    // AI Chat State
    var isChatExpanded by remember { mutableStateOf(false) }
    var chatQuery by remember { mutableStateOf("") }
    val savedConversation by aiConversationRepository.conversation(userId).collectAsState(initial = emptyList())
    val listState = rememberLazyListState()
    var isAITyping by remember { mutableStateOf(false) }
    var showChartsInChat by remember { mutableStateOf(true) }

    // Scroll to bottom when new message arrives
    LaunchedEffect(savedConversation.size) {
        if (savedConversation.isNotEmpty()) {
            listState.animateScrollToItem(savedConversation.size - 1)
        }
    }

    val monthNames = listOf("January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December")
    val availableRanges = remember {
        val list = mutableListOf<Pair<Int, Int>>()
        val cal = Calendar.getInstance()
        for (i in 0 until 12) {
            list.add(cal.get(Calendar.MONTH) to cal.get(Calendar.YEAR))
            cal.add(Calendar.MONTH, -1)
        }
        list
    }

    // 1. Collect real transaction data
    val allTransactions by transactionRepository.getAllTransactions(userId).collectAsState(initial = emptyList())
    val allGoals by savingsGoalRepository.allGoals(userId).collectAsState(initial = emptyList())

    // 2. Filter data by selected Month and Year
    val filteredTransactions = remember(allTransactions, selectedMonth, selectedYear) {
        allTransactions.filter { tx ->
            try {
                val sdf = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
                val date = sdf.parse(tx.date)
                if (date != null) {
                    val txCal = Calendar.getInstance()
                    txCal.time = date
                    txCal.get(Calendar.MONTH) == selectedMonth && txCal.get(Calendar.YEAR) == selectedYear
                } else false
            } catch (e: Exception) {
                false
            }
        }
    }

    val incomes = filteredTransactions.filter { !it.isExpense }
    val expenses = filteredTransactions.filter { it.isExpense }

    val totalIncome = incomes.sumOf { it.amountValue }
    val totalExpense = expenses.sumOf { it.amountValue }
    val net = totalIncome - totalExpense

    val breakdownData = filteredTransactions.groupBy { it.category to it.isExpense }
        .mapValues { entry -> entry.value.sumOf { it.amountValue } }
        .toList()
        .sortedByDescending { it.second }

    val totalMovement = breakdownData.sumOf { it.second }.coerceAtLeast(1.0)

    // AI Brain Instance
    val aiAssistant = remember(geminiKey) { 
        AiAssistant(context, AppDatabase.getInstance(context, "secure_password".toByteArray()), userId, geminiKey)
    }

    val aiInsight = remember(totalIncome, totalExpense, breakdownData, filteredTransactions.size) {
        when {
            filteredTransactions.isEmpty() -> "🤖 AI Insight: No data for this month. Start tracking to get insights!"
            totalExpense > totalIncome -> "🤖 AI Insight: You are spending more than you earn! Look for ways to save."
            else -> "🤖 AI Insight: Your finances look balanced. Consider setting a new savings goal!"
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text(if(isChatExpanded) "AI Financial Assistant" else "Reports & Insights") },
                navigationIcon = {
                    IconButton(onClick = { if(isChatExpanded) isChatExpanded = false else onNavigateBack() }) {
                        Icon(if(isChatExpanded) Icons.Filled.Close else Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if(!isChatExpanded) {
                        IconButton(onClick = { 
                            scope.launch { snackbarHostState.showSnackbar("Exporting report...") }
                        }) {
                            Icon(Icons.Filled.FileUpload, contentDescription = "Export")
                        }
                    } else {
                        IconButton(onClick = { showChartsInChat = !showChartsInChat }) {
                            Icon(if(showChartsInChat) Icons.Filled.BarChart else Icons.Filled.ShowChart, contentDescription = "Toggle Charts", tint = if(showChartsInChat) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface)
                        }
                    }
                }
            )
        },
        bottomBar = {
            if (isChatExpanded) {
                Surface(
                    tonalElevation = 8.dp,
                    modifier = Modifier.imePadding()
                ) {
                    Row(
                        modifier = Modifier
                            .padding(8.dp)
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = chatQuery,
                            onValueChange = { chatQuery = it },
                            modifier = Modifier.weight(1f),
                            placeholder = { Text("Ask about your finances...") },
                            shape = RoundedCornerShape(24.dp),
                            maxLines = 3
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        IconButton(
                            onClick = {
                                if (chatQuery.isNotBlank()) {
                                    val userMsg = chatQuery
                                    chatQuery = ""
                                    isAITyping = true
                                    scope.launch {
                                        // Save user message
                                        aiConversationRepository.insert(
                                            AIConversation(userId = userId, role = "user", content = userMsg)
                                        )
                                        
                                        // Get and save AI response
                                        val response = aiAssistant.getResponse(userMsg)
                                        aiConversationRepository.insert(
                                            AIConversation(userId = userId, role = "assistant", content = response)
                                        )
                                        isAITyping = false
                                    }
                                }
                            },
                            enabled = !isAITyping && chatQuery.isNotBlank(),
                            colors = IconButtonDefaults.iconButtonColors(containerColor = MaterialTheme.colorScheme.primary, contentColor = MaterialTheme.colorScheme.onPrimary)
                        ) {
                            Icon(Icons.Filled.Send, contentDescription = "Send")
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        if (isChatExpanded) {
            Column(modifier = Modifier.padding(innerPadding).fillMaxSize()) {
                if (showChartsInChat) {
                    Box(modifier = Modifier.height(250.dp).fillMaxWidth().padding(horizontal = 16.dp).verticalScroll(rememberScrollState())) {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            ChartCard(title = "Ref: ${monthNames[selectedMonth]} Movement") {
                                Box(modifier = Modifier.height(100.dp).fillMaxWidth()) {
                                    ReportPieChart(breakdownData, totalMovement)
                                }
                            }
                            Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))) {
                                Row(modifier = Modifier.padding(8.dp).fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text("In", style = MaterialTheme.typography.labelSmall)
                                        Text("$currency ${String.format("%,.0f", totalIncome)}", color = Color(0xFF4CAF50), fontWeight = FontWeight.Bold)
                                    }
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text("Out", style = MaterialTheme.typography.labelSmall)
                                        Text("$currency ${String.format("%,.0f", totalExpense)}", color = Color.Red, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                    Divider()
                }
                
                LazyColumn(
                    modifier = Modifier.weight(1f).fillMaxWidth(),
                    state = listState,
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(savedConversation) { message ->
                        ChatBubble(message.content, message.role == "user")
                    }
                    if (isAITyping) {
                        item {
                            Text("AI is analyzing...", style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(8.dp))
                        }
                    }
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(scrollState)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // Date Range Selector
                Box {
                    OutlinedCard(
                        onClick = { showMenu = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Date range: ", style = MaterialTheme.typography.bodyMedium)
                            Text("${monthNames[selectedMonth]} $selectedYear", fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.weight(1f))
                            Icon(Icons.Filled.ArrowDropDown, null)
                        }
                    }
                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false },
                        modifier = Modifier.fillMaxWidth(0.9f)
                    ) {
                        availableRanges.forEach { (m, y) ->
                            DropdownMenuItem(
                                text = { Text("${monthNames[m]} $y") },
                                onClick = {
                                    selectedMonth = m
                                    selectedYear = y
                                    showMenu = false
                                }
                            )
                        }
                    }
                }

                // 1. Transaction Trend Chart
                ChartCard(title = "Transaction Trend (${monthNames[selectedMonth]})") {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp)
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f), RoundedCornerShape(8.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        if (filteredTransactions.isEmpty()) {
                            Text("No transaction data", color = Color.Gray)
                        } else {
                            Canvas(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                                val dataPoints = filteredTransactions.map { if(it.isExpense) -it.amountValue else it.amountValue }
                                if (dataPoints.size > 1) {
                                    val maxVal = filteredTransactions.maxOf { it.amountValue }.coerceAtLeast(1.0)
                                    val widthStep = size.width / (dataPoints.size - 1)
                                    val centerY = size.height / 2
                                    for (i in 0 until dataPoints.size - 1) {
                                        val startY = centerY - (dataPoints[i] / maxVal * (size.height/2)).toFloat()
                                        val endY = centerY - (dataPoints[i+1] / maxVal * (size.height/2)).toFloat()
                                        drawLine(
                                            color = if(dataPoints[i+1] >= 0) Color(0xFF4CAF50) else Color.Red,
                                            start = androidx.compose.ui.geometry.Offset(i * widthStep, startY),
                                            end = androidx.compose.ui.geometry.Offset((i + 1) * widthStep, endY),
                                            strokeWidth = 3.dp.toPx()
                                        )
                                    }
                                } else {
                                    drawCircle(color = if(filteredTransactions[0].isExpense) Color.Red else Color(0xFF4CAF50), radius = 5.dp.toPx(), center = androidx.compose.ui.geometry.Offset(size.width/2, size.height/2))
                                }
                            }
                        }
                    }
                }

                // 2. Summary Totals
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            SummaryItem("Total outflow", "$currency ${String.format("%,.0f", totalExpense)}", Color.Red)
                            SummaryItem("Total inflow", "$currency ${String.format("%,.0f", totalIncome)}", Color(0xFF4CAF50))
                        }
                        Divider(modifier = Modifier.padding(vertical = 12.dp), color = MaterialTheme.colorScheme.outlineVariant)
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            SummaryItem("Net Balance", (if(net >=0) "+" else "") + "$currency ${String.format("%,.0f", net)}", if(net >= 0) Color(0xFF2196F3) else Color.Red)
                            SummaryItem("Transactions", "${filteredTransactions.size} total", Color.Gray)
                        }
                    }
                }

                // 3. Category Breakdown
                ChartCard(title = "Inflow & Outflow Breakdown") {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        if (breakdownData.isEmpty()) {
                            Text("No data", color = Color.Gray)
                        } else {
                            ReportPieChart(breakdownData, totalMovement)
                            Spacer(modifier = Modifier.height(24.dp))
                            FlowRow(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center
                            ) {
                                val colors = listOf(Color(0xFF4CAF50), Color(0xFFF44336), Color(0xFF2196F3), Color(0xFFFFC107), Color(0xFF9C27B0), Color(0xFF00BCD4))
                                breakdownData.forEachIndexed { index, (key, amount) ->
                                    val (category, isExpense) = key
                                    val percentage = (amount / totalMovement * 100).toInt()
                                    CategoryLegendItem("${if(isExpense) category+"(Out)" else category+"(In)"} $percentage%", colors[index % colors.size]) {
                                        scope.launch { snackbarHostState.showSnackbar("$category: $currency ${String.format("%,.0f", amount)}") }
                                    }
                                }
                            }
                        }
                    }
                }

                // 4. AI Insight Summary & Ask Button
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.4f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("🤖", fontSize = 24.sp)
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(aiInsight, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(1f))
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = { isChatExpanded = true },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Filled.Chat, null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Ask AI Personal Assistant")
                        }
                    }
                }

                // 5. Savings Progress
                if (allGoals.isNotEmpty()) {
                    Text("Savings Progress", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    allGoals.forEach { goal ->
                        val progress = (goal.currentAmount / goal.targetAmount.coerceAtLeast(1.0)).toFloat()
                        Column(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(goal.name, style = MaterialTheme.typography.bodySmall)
                                Text("${(progress * 100).toInt()}%", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                            }
                            LinearProgressIndicator(
                                progress = progress,
                                modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
                                color = Color(0xFF4CAF50),
                                trackColor = Color.Gray.copy(alpha = 0.2f)
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
fun ReportPieChart(breakdownData: List<Pair<Pair<String, Boolean>, Double>>, totalMovement: Double) {
    Box(
        modifier = Modifier.size(180.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            var startAngle = -90f
            val colors = listOf(Color(0xFF4CAF50), Color(0xFFF44336), Color(0xFF2196F3), Color(0xFFFFC107), Color(0xFF9C27B0), Color(0xFF00BCD4))
            breakdownData.forEachIndexed { index, pair ->
                val sweep = (pair.second.toFloat() / totalMovement.toFloat()) * 360f
                drawArc(
                    color = colors[index % colors.size],
                    startAngle = startAngle,
                    sweepAngle = sweep,
                    useCenter = true
                )
                startAngle += sweep
            }
        }
    }
}

@Composable
fun ChatBubble(content: String, isUser: Boolean) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = if (isUser) Alignment.End else Alignment.Start
    ) {
        Surface(
            color = if (isUser) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
            shape = RoundedCornerShape(
                topStart = 16.dp,
                topEnd = 16.dp,
                bottomStart = if (isUser) 16.dp else 0.dp,
                bottomEnd = if (isUser) 0.dp else 16.dp
            )
        ) {
            Text(
                text = content,
                modifier = Modifier.padding(12.dp),
                color = if (isUser) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodyMedium
            )
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
