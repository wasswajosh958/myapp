package ug.ac.ndejje.myapp

import androidx.room.*
import java.math.BigDecimal
import java.util.Date

@Entity(tableName = "transactions")
data class Transaction(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val category: String,
    val amountValue: Double,
    val date: String,
    val time: String,
    val isExpense: Boolean,
    val isPending: Boolean = false,
    val accountId: Int = 0,
    val modeId: Int = 1
) {
    fun getFormattedAmount(currency: String): String {
        return "$currency ${String.format("%.2f", amountValue)}"
    }
}

@Entity(tableName = "categories")
data class Category(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val icon: String,
    val type: String, // "income", "expense"
    val modeId: Int = 1
)

@Entity(tableName = "accounts")
data class AccountEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val type: String,
    val balance: Double,
    val currency: String = "Shs",
    val lastFour: String? = null,
    val modeId: Int = 1
)

@Entity(tableName = "budgets")
data class BudgetEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val category: String,
    val limit: Double,
    val spent: Double,
    val modeId: Int = 1
)

@Entity(tableName = "recurring_transactions")
data class RecurringTransaction(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val amount: Double,
    val frequency: String,
    val nextDueDate: Long,
    val modeId: Int = 1
)

@Entity(tableName = "ai_conversations")
data class AIConversation(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val role: String, // "user", "assistant"
    val content: String,
    val timestamp: Long = System.currentTimeMillis(),
    val modeId: Int = 1
)

@Entity(tableName = "crash_logs")
data class CrashLog(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val exception: String,
    val message: String,
    val stackTrace: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "analytics_events")
data class AnalyticsEvent(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val eventName: String,
    val params: String, // JSON
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "user_profile")
data class UserProfile(
    @PrimaryKey val id: Int = 1, // We only have one local user
    val name: String,
    val email: String,
    val currency: String = "Shs",
    val photoUri: String? = null
)

data class ChatMessage(
    val text: String,
    val isUser: Boolean
)
