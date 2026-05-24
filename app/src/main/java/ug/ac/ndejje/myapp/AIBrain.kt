package ug.ac.ndejje.myapp

import android.content.Context
import kotlinx.coroutines.flow.first
import java.math.BigDecimal

class AIBrain(private val database: AppDatabase, private val userId: Int) {

    suspend fun processQuery(query: String): String {
        val lowerQuery = query.lowercase()
        
        return when {
            lowerQuery.contains("balance") -> getBalanceInsight()
            lowerQuery.contains("spent") || lowerQuery.contains("expense") -> getSpendingInsight()
            lowerQuery.contains("budget") -> getBudgetInsight()
            else -> "I'm your AI assistant. You can ask me about your balance, spending, or budgets."
        }
    }

    private suspend fun getBalanceInsight(): String {
        val accounts = database.accountDao().getAllAccounts(userId).first()
        val total = accounts.sumOf { it.balance }
        return "Your total balance across ${accounts.size} accounts is Shs ${String.format("%,.0f", total)}."
    }

    private suspend fun getSpendingInsight(): String {
        val transactions = database.transactionDao().getAllTransactions(userId).first()
        val totalSpent = transactions.filter { it.isExpense }.sumOf { it.amountValue }
        return "You have recorded total expenses of Shs ${String.format("%,.0f", totalSpent)}."
    }

    private suspend fun getBudgetInsight(): String {
        val budgets = database.budgetDao().getAllBudgets(userId).first()
        if (budgets.isEmpty()) return "You haven't set any budgets yet."
        
        val totalLimit = budgets.sumOf { it.limit }
        val totalSpent = budgets.sumOf { it.spent }
        val percent = if (totalLimit > 0) (totalSpent / totalLimit * 100).toInt() else 0
        
        return "You've used $percent% of your total budget (Shs ${String.format("%,.0f", totalSpent)} out of Shs ${String.format("%,.0f", totalLimit)})."
    }
}
