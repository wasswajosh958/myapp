package ug.ac.ndejje.myapp.util

import ug.ac.ndejje.myapp.resources.*
import com.google.ai.client.generativeai.GenerativeModel
import kotlinx.coroutines.flow.first
import java.util.*

class AIBrain(private val database: AppDatabase, private val userId: Int, apiKey: String) {

    private val generativeModel = GenerativeModel(
        modelName = "gemini-1.5-flash",
        apiKey = if (apiKey.isNotBlank()) apiKey else Config.GLOBAL_GEMINI_API_KEY
    )

    suspend fun processQuery(query: String): String {
        val lowerQuery = query.lowercase()
        
        val allTransactions = database.transactionDao().getAllTransactions(userId).first()
        val accounts = database.accountDao().getAllAccounts(userId).first()
        val budgets = database.budgetDao().getAllBudgets(userId).first()

        val localResponse = when {
            lowerQuery.contains("balance") -> getDetailedBalance(accounts)
            lowerQuery.contains("spent") || lowerQuery.contains("expense") -> getDetailedSpending(allTransactions)
            lowerQuery.contains("income") -> getDetailedIncome(allTransactions)
            lowerQuery.contains("budget") -> getDetailedBudget(budgets)
            else -> null
        }

        if (localResponse != null) return localResponse

        return try {
            val contextPrompt = """
                You are FinTrack AI, a personal financial advisor. 
                Data for User ID $userId:
                - Total Balance: Shs ${accounts.sumOf { it.balance }}
                - Last 5 Transactions: ${allTransactions.take(5).joinToString { "${it.title}: ${it.amountValue}" }}
                - Number of Active Budgets: ${budgets.size}
                
                Question: $query
                Answer helpfully and concisely.
            """.trimIndent()

            val response = generativeModel.generateContent(contextPrompt)
            response.text ?: "I'm thinking... but I couldn't find the words. Try asking again?"
        } catch (e: Exception) {
            "I can't reach my cloud brain right now, but locally I know your total balance is Shs ${String.format("%,.0f", accounts.sumOf { it.balance })}."
        }
    }

    private fun getDetailedBalance(accounts: List<AccountEntity>): String {
        if (accounts.isEmpty()) return "You haven't added any accounts yet."
        val total = accounts.sumOf { it.balance }
        return "Your total balance is Shs ${String.format("%,.0f", total)}."
    }

    private fun getDetailedSpending(transactions: List<Transaction>): String {
        val expenses = transactions.filter { it.isExpense }
        if (expenses.isEmpty()) return "No expenses recorded yet."
        val totalSpent = expenses.sumOf { it.amountValue }
        return "You've spent Shs ${String.format("%,.0f", totalSpent)} in total."
    }

    private fun getDetailedIncome(transactions: List<Transaction>): String {
        val incomes = transactions.filter { !it.isExpense }
        if (incomes.isEmpty()) return "No income recorded yet."
        val total = incomes.sumOf { it.amountValue }
        return "Your total recorded income is Shs ${String.format("%,.0f", total)}."
    }

    private fun getDetailedBudget(budgets: List<BudgetEntity>): String {
        if (budgets.isEmpty()) return "No active budgets set."
        val totalLimit = budgets.sumOf { it.limit }
        val totalSpent = budgets.sumOf { it.spent }
        val limitVal = totalLimit.toDouble()
        return if (limitVal > 0.0) {
            "You've used ${((totalSpent/limitVal)*100).toInt()}% of your total budget."
        } else "Budget limit is zero."
    }
}
