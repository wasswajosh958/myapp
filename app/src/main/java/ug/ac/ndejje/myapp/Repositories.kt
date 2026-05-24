package ug.ac.ndejje.myapp

import kotlinx.coroutines.flow.Flow

class TransactionRepository(private val dao: TransactionDao) {
    fun getAllTransactions(userId: Int): Flow<List<Transaction>> = dao.getAllTransactions(userId)
    suspend fun insert(transaction: Transaction) = dao.insert(transaction)
    suspend fun delete(transaction: Transaction) = dao.delete(transaction)
}

class AccountRepository(private val dao: AccountDao) {
    fun getAllAccounts(userId: Int): Flow<List<AccountEntity>> = dao.getAllAccounts(userId)
    suspend fun insert(account: AccountEntity) = dao.insert(account)
    suspend fun update(account: AccountEntity) = dao.update(account)
    suspend fun delete(account: AccountEntity) = dao.delete(account)
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

class BudgetRepository(private val dao: BudgetDao) {
    fun allBudgets(userId: Int): Flow<List<BudgetEntity>> = dao.getAllBudgets(userId)
    suspend fun insert(budget: BudgetEntity) = dao.insert(budget)
    suspend fun update(budget: BudgetEntity) = dao.update(budget)
}
