package ug.ac.ndejje.myapp

import kotlinx.coroutines.flow.Flow

class TransactionRepository(private val dao: TransactionDao) {
    val allTransactions: Flow<List<Transaction>> = dao.getAllTransactions()
    suspend fun insert(transaction: Transaction) = dao.insert(transaction)
    suspend fun delete(transaction: Transaction) = dao.delete(transaction)
}

class AccountRepository(private val dao: AccountDao) {
    val allAccounts: Flow<List<AccountEntity>> = dao.getAllAccounts()
    suspend fun insert(account: AccountEntity) = dao.insert(account)
    suspend fun update(account: AccountEntity) = dao.update(account)
    suspend fun delete(account: AccountEntity) = dao.delete(account)
}

class UserProfileRepository(private val dao: UserProfileDao) {
    val userProfile: Flow<UserProfile?> = dao.getUserProfile()
    suspend fun insert(profile: UserProfile) = dao.insert(profile)
    suspend fun update(profile: UserProfile) = dao.update(profile)
}

class NotificationRepository(private val dao: NotificationDao) {
    val activeNotifications: Flow<List<NotificationEntity>> = dao.getAllActive()
    suspend fun getById(id: Long) = dao.getById(id)
    suspend fun markAsRead(id: Long) = dao.markAsRead(id)
    suspend fun delete(id: Long) = dao.softDelete(id)
    suspend fun insert(notification: NotificationEntity) = dao.insert(notification)
}

class AIConversationRepository(private val dao: AIConversationDao) {
    val conversation: Flow<List<AIConversation>> = dao.getConversation()
    suspend fun insert(message: AIConversation) = dao.insert(message)
}

class BudgetRepository(private val dao: BudgetDao) {
    val allBudgets: Flow<List<BudgetEntity>> = dao.getAllBudgets()
    suspend fun insert(budget: BudgetEntity) = dao.insert(budget)
    suspend fun update(budget: BudgetEntity) = dao.update(budget)
}
