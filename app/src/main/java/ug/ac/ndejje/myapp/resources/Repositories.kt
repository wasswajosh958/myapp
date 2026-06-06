package ug.ac.ndejje.myapp.resources

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import java.util.Date

class TransactionRepository(
    private val transactionDao: TransactionDao,
    private val accountDao: AccountDao,
    private val notificationDao: NotificationDao
) {
    fun getAllTransactions(userId: Int): Flow<List<Transaction>> = transactionDao.getAllTransactions(userId)

    suspend fun insert(transaction: Transaction) {
        transactionDao.insert(transaction)
        
        // Update account balance
        if (transaction.accountId != 0) {
            accountDao.getAccountById(transaction.accountId)?.let { account ->
                val newBalance = if (transaction.isExpense) {
                    account.balance - transaction.amountValue
                } else {
                    account.balance + transaction.amountValue
                }
                accountDao.update(account.copy(balance = newBalance))
            }
        }

        notificationDao.insert(
            NotificationEntity(
                userId = transaction.userId,
                type = "user_action",
                title = "Transaction Added",
                message = "New transaction '${transaction.title}' of Shs ${transaction.amountValue} added.",
                relatedId = transaction.id
            )
        )
    }

    suspend fun delete(transaction: Transaction) {
        transactionDao.delete(transaction)
        
        // Reverse account balance update
        if (transaction.accountId != 0) {
            accountDao.getAccountById(transaction.accountId)?.let { account ->
                val newBalance = if (transaction.isExpense) {
                    account.balance + transaction.amountValue
                } else {
                    account.balance - transaction.amountValue
                }
                accountDao.update(account.copy(balance = newBalance))
            }
        }

        notificationDao.insert(
            NotificationEntity(
                userId = transaction.userId,
                type = "user_action",
                title = "Transaction Deleted",
                message = "Transaction '${transaction.title}' has been deleted.",
                relatedId = transaction.id
            )
        )
    }
}

class AccountRepository(
    private val accountDao: AccountDao,
    private val notificationDao: NotificationDao
) {
    fun getAllAccounts(userId: Int): Flow<List<AccountEntity>> = accountDao.getAllAccounts(userId)

    suspend fun insert(account: AccountEntity) {
        accountDao.insert(account)
        notificationDao.insert(
            NotificationEntity(
                userId = account.userId,
                type = "user_action",
                title = "Account Added",
                message = "New account '${account.name}' added.",
                relatedId = account.id
            )
        )
    }

    suspend fun update(account: AccountEntity) {
        accountDao.update(account)
        notificationDao.insert(
            NotificationEntity(
                userId = account.userId,
                type = "user_action",
                title = "Account Updated",
                message = "Account '${account.name}' has been updated.",
                relatedId = account.id
            )
        )
    }

    suspend fun delete(account: AccountEntity) {
        accountDao.delete(account)
        notificationDao.insert(
            NotificationEntity(
                userId = account.userId,
                type = "user_action",
                title = "Account Deleted",
                message = "Account '${account.name}' has been deleted.",
                relatedId = account.id
            )
        )
    }

    suspend fun recalculateBalances(userId: Int, transactionDao: TransactionDao) {
        val accounts = accountDao.getAllAccounts(userId).first()
        if (accounts.isEmpty()) return

        val transactions = transactionDao.getAllTransactions(userId).first()
        val firstAccountId = accounts.first().id
        
        // Use a map to track running totals for each account
        val updatedBalances = mutableMapOf<Int, Double>()
        accounts.forEach { updatedBalances[it.id] = 0.0 }

        // Apply all transactions to the map
        transactions.forEach { tx ->
            // If accountId is 0 (orphaned), assign it to the first account
            val targetId = if (tx.accountId == 0) firstAccountId else tx.accountId
            
            // If for some reason we assign to an account that doesn't exist in the user's list
            if (updatedBalances.containsKey(targetId)) {
                val current = updatedBalances[targetId] ?: 0.0
                updatedBalances[targetId] = if (tx.isExpense) current - tx.amountValue else current + tx.amountValue
                
                // Also update the transaction in DB if it was orphaned
                if (tx.accountId == 0) {
                    transactionDao.insert(tx.copy(accountId = firstAccountId))
                }
            }
        }

        // Update the database for each account
        accounts.forEach { account ->
            val newBalance = updatedBalances[account.id] ?: 0.0
            accountDao.update(account.copy(balance = newBalance))
        }
    }
}

class CategoryRepository(private val dao: CategoryDao) {
    fun getAllCategories(userId: Int): Flow<List<Category>> = dao.getAllCategories(userId)
    suspend fun insert(category: Category) = dao.insert(category)
}

class UserProfileRepository(private val dao: UserProfileDao) {
    fun getUserProfile(userId: Int): Flow<UserProfile?> = dao.getUserProfile(userId)
    suspend fun getUserByUsername(username: String): UserProfile? = dao.getUserByUsername(username)
    suspend fun insert(profile: UserProfile): Long = dao.insert(profile)
    suspend fun update(profile: UserProfile) = dao.update(profile)
}

class NotificationRepository(private val dao: NotificationDao) {
    fun activeNotifications(userId: Int): Flow<List<NotificationEntity>> = dao.getAllActive(userId)
    suspend fun getById(id: Long) = dao.getById(id)
    suspend fun markAsRead(id: Long) = dao.markAsRead(id)
    suspend fun delete(id: Long) = dao.softDelete(id)
    suspend fun insert(notification: NotificationEntity) = dao.insert(notification)
}

class AIConversationRepository(private val dao: AIConversationDao) {
    fun conversation(userId: Int): Flow<List<AIConversation>> = dao.getConversation(userId)
    suspend fun insert(message: AIConversation) = dao.insert(message)
}

class UtilityRepository(private val dao: UtilityDao) {
    fun getAllUtilities(userId: Int): Flow<List<UtilityEntity>> = dao.getAllUtilities(userId)
    suspend fun insert(utility: UtilityEntity) = dao.insert(utility)
    suspend fun update(utility: UtilityEntity) = dao.update(utility)
    suspend fun delete(utility: UtilityEntity) = dao.delete(utility)
}

class BudgetRepository(
    private val budgetDao: BudgetDao,
    private val notificationDao: NotificationDao
) {
    fun allBudgets(userId: Int): Flow<List<BudgetEntity>> = budgetDao.getAllBudgets(userId)

    suspend fun insert(budget: BudgetEntity) {
        budgetDao.insert(budget)
        notificationDao.insert(
            NotificationEntity(
                userId = budget.userId,
                type = "user_action",
                title = "Budget Added",
                message = "New budget for category '${budget.category}' added.",
                relatedId = budget.id
            )
        )
    }

    suspend fun update(budget: BudgetEntity) {
        budgetDao.update(budget)
        notificationDao.insert(
            NotificationEntity(
                userId = budget.userId,
                type = "user_action",
                title = "Budget Updated",
                message = "Budget for category '${budget.category}' updated.",
                relatedId = budget.id
            )
        )
    }
}

class SavingsGoalRepository(
    private val goalDao: SavingsGoalDao,
    private val accountDao: AccountDao,
    private val transactionDao: TransactionDao,
    private val notificationDao: NotificationDao
) {
    fun allGoals(userId: Int): Flow<List<SavingsGoal>> = goalDao.getAllGoals(userId)

    suspend fun insert(goal: SavingsGoal) {
        goalDao.insert(goal)
        notificationDao.insert(
            NotificationEntity(
                userId = goal.userId,
                type = "user_action",
                title = "Savings Goal Added",
                message = "New savings goal '${goal.name}' added.",
                relatedId = goal.id
            )
        )
    }

    suspend fun contribute(goal: SavingsGoal, amount: Double) {
        // 1. Update goal amount
        val updatedGoal = goal.copy(currentAmount = (goal.currentAmount + amount).coerceAtMost(goal.targetAmount))
        goalDao.update(updatedGoal)

        // 2. Find an account to deduct from (default to first one)
        val accounts = accountDao.getAllAccounts(goal.userId).first()
        if (accounts.isNotEmpty()) {
            val account = accounts.first()
            val newBalance = account.balance - amount
            accountDao.update(account.copy(balance = newBalance))

            // 3. Create a transaction record for tracking
            val now = java.util.Calendar.getInstance()
            val dateStr = java.text.SimpleDateFormat("MMM dd, yyyy", java.util.Locale.getDefault()).format(now.time)
            val timeStr = java.text.SimpleDateFormat("HH:mm", java.util.Locale.getDefault()).format(now.time)

            transactionDao.insert(Transaction(
                userId = goal.userId,
                title = "Savings: ${goal.name}",
                category = "Savings",
                amountValue = amount,
                date = dateStr,
                time = timeStr,
                isExpense = true, 
                accountId = account.id
            ))
        }

        notificationDao.insert(
            NotificationEntity(
                userId = goal.userId,
                type = "user_action",
                title = "Goal Contribution",
                message = "Added Shs ${amount} to goal '${goal.name}'.",
                relatedId = goal.id
            )
        )
    }

    suspend fun update(goal: SavingsGoal) {
        goalDao.update(goal)
    }

    suspend fun delete(goal: SavingsGoal) {
        goalDao.delete(goal)
    }
}

