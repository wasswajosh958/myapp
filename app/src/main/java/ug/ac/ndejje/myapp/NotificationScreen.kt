package ug.ac.ndejje.myapp

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationScreen(
    onNavigateBack: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToDetail: (Long) -> Unit,
    notificationRepository: NotificationRepository,
    userId: Int
) {
    var selectedPeriod by remember { mutableStateOf("All") }
    val periods = listOf("All", "Today", "This Week", "This Month")
    
    val notifications by notificationRepository.activeNotifications(userId).collectAsState(initial = emptyList())
    val scope = rememberCoroutineScope()

    val filteredNotifications = remember(notifications, selectedPeriod) {
        val now = System.currentTimeMillis()
        val calendar = Calendar.getInstance()
        
        notifications.filter { notif ->
            when (selectedPeriod) {
                "Today" -> {
                    val notifDate = Calendar.getInstance().apply { timeInMillis = notif.createdAt }
                    val nowDate = Calendar.getInstance().apply { timeInMillis = now }
                    notifDate.get(Calendar.YEAR) == nowDate.get(Calendar.YEAR) &&
                    notifDate.get(Calendar.DAY_OF_YEAR) == nowDate.get(Calendar.DAY_OF_YEAR)
                }
                "This Week" -> {
                    notif.createdAt > now - (7 * 24 * 60 * 60 * 1000L)
                }
                "This Month" -> {
                    val notifDate = Calendar.getInstance().apply { timeInMillis = notif.createdAt }
                    val nowDate = Calendar.getInstance().apply { timeInMillis = now }
                    notifDate.get(Calendar.YEAR) == nowDate.get(Calendar.YEAR) &&
                    notifDate.get(Calendar.MONTH) == nowDate.get(Calendar.MONTH)
                }
                else -> true
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Notifications") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { 
                        scope.launch {
                            notifications.forEach { if (!it.isRead) notificationRepository.markAsRead(it.id) }
                        }
                    }) {
                        Icon(Icons.Filled.DoneAll, contentDescription = "Mark all as read")
                    }
                    IconButton(onClick = onNavigateToSettings) {
                        Icon(Icons.Filled.Settings, contentDescription = "Notification Settings")
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
            // Period Filter
            ScrollableTabRow(
                selectedTabIndex = periods.indexOf(selectedPeriod),
                edgePadding = 16.dp,
                containerColor = Color.Transparent,
                divider = {}
            ) {
                periods.forEach { period ->
                    Tab(
                        selected = selectedPeriod == period,
                        onClick = { selectedPeriod = period },
                        text = { Text(period) }
                    )
                }
            }

            if (filteredNotifications.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No notifications for this period", color = Color.Gray)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filteredNotifications, key = { it.id }) { notification ->
                        NotificationItem(
                            notification = notification,
                            onClick = { onNavigateToDetail(notification.id) },
                            onDelete = { 
                                scope.launch {
                                    notificationRepository.delete(notification.id)
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun NotificationItem(
    notification: NotificationEntity,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    val (icon, color) = when (notification.type) {
        "transaction" -> Icons.Filled.Payments to Color.Gray
        "budget_alert" -> Icons.Filled.Warning to Color.Red
        "ai_insight" -> Icons.Filled.AutoAwesome to Color(0xFF2196F3)
        "user_action" -> Icons.Filled.History to Color.DarkGray
        else -> Icons.Filled.Notifications to Color(0xFFFFA000)
    }

    val dateFormat = remember { SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault()) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (notification.isRead) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, null, tint = color, modifier = Modifier.size(20.dp))
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = notification.title,
                        fontWeight = if (notification.isRead) FontWeight.Medium else FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    if (!notification.isRead) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Box(modifier = Modifier.size(8.dp).background(MaterialTheme.colorScheme.primary, CircleShape))
                    }
                }
                Text(
                    text = notification.message,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = dateFormat.format(Date(notification.createdAt)),
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.Gray,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            IconButton(onClick = onDelete, modifier = Modifier.size(24.dp)) {
                Icon(Icons.Filled.Close, contentDescription = "Delete", modifier = Modifier.size(16.dp), tint = Color.Gray)
            }
        }
    }
}
