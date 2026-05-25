package ug.ac.ndejje.myapp

import kotlinx.coroutines.flow.Flow
import java.util.Date

class TransactionRepository(
    private val transactionDao: TransactionDao,
    private val notificationDao: NotificationDao
) {
    fun getAllTransactions(userId: Int): Flow<List<Transaction>> = transactionDao.getAllTransactions(userId)

    suspend fun insert(transaction: Transaction) {
        transactionDao.insert(transaction)
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
