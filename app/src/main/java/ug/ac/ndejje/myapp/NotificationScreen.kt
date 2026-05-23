package ug.ac.ndejje.myapp

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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

enum class NotificationType {
    TRANSACTION, BUDGET_ALERT, SAVINGS_GOAL, AI_INSIGHT, SECURITY, REMINDER
}

data class AppNotification(
    val id: Int,
    val type: NotificationType,
    val title: String,
    val message: String,
    val timestamp: String,
    var isRead: Boolean = false
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationScreen(onNavigateBack: () -> Unit, onNavigateToSettings: () -> Unit) {
    val notifications = remember {
        mutableStateListOf(
            AppNotification(1, NotificationType.BUDGET_ALERT, "Budget Alert", "Food budget is 90% used", "2 mins ago"),
            AppNotification(2, NotificationType.AI_INSIGHT, "AI Insight", "Transport spending increased 20%", "10 mins ago"),
            AppNotification(3, NotificationType.TRANSACTION, "Transaction", "Expense SHS 15,000 added", "Today 3:45 PM"),
            AppNotification(4, NotificationType.SAVINGS_GOAL, "Savings Goal", "Laptop fund reached 75%", "1 hour ago"),
            AppNotification(5, NotificationType.REMINDER, "Bill Reminder", "Electricity bill due tomorrow", "2 hours ago"),
            AppNotification(6, NotificationType.SECURITY, "Security", "New login detected from Oppo A57", "Yesterday")
        )
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
                        notifications.forEachIndexed { index, notif -> 
                            notifications[index] = notif.copy(isRead = true)
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
        if (notifications.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(innerPadding), contentAlignment = Alignment.Center) {
                Text("No notifications yet", color = Color.Gray)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(notifications) { notification ->
                    NotificationItem(
                        notification = notification,
                        onClick = { 
                            val index = notifications.indexOf(notification)
                            if (index != -1) notifications[index] = notification.copy(isRead = true)
                        },
                        onDelete = { notifications.remove(notification) }
                    )
                }
            }
        }
    }
}

@Composable
fun NotificationItem(notification: AppNotification, onClick: () -> Unit, onDelete: () -> Unit) {
    val (icon, color) = when (notification.type) {
        NotificationType.TRANSACTION -> Icons.Filled.Payments to Color.Gray
        NotificationType.BUDGET_ALERT -> Icons.Filled.Warning to Color.Red
        NotificationType.SAVINGS_GOAL -> Icons.Filled.TrackChanges to Color(0xFF4CAF50)
        NotificationType.AI_INSIGHT -> Icons.Filled.AutoAwesome to Color(0xFF2196F3)
        NotificationType.SECURITY -> Icons.Filled.Lock to Color(0xFFB71C1C)
        NotificationType.REMINDER -> Icons.Filled.NotificationsActive to Color(0xFFFFA000)
    }

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
                Text(notification.message, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(notification.timestamp, style = MaterialTheme.typography.labelSmall, color = Color.Gray, modifier = Modifier.padding(top = 4.dp))
            }

            IconButton(onClick = onDelete, modifier = Modifier.size(24.dp)) {
                Icon(Icons.Filled.Close, contentDescription = "Delete", modifier = Modifier.size(16.dp), tint = Color.Gray)
            }
        }
    }
}
