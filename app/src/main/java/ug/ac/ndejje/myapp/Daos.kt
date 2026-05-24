package ug.ac.ndejje.myapp

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {
    @Query("SELECT * FROM transactions ORDER BY date DESC, time DESC")
    fun getAllTransactions(): Flow<List<Transaction>>

    @Insert
    suspend fun insert(transaction: Transaction)

    @Delete
    suspend fun delete(transaction: Transaction)

    @Query("DELETE FROM transactions")
    suspend fun deleteAll()
}

@Dao
interface AccountDao {
    @Query("SELECT * FROM accounts")
    fun getAllAccounts(): Flow<List<AccountEntity>>

    @Insert
    suspend fun insert(account: AccountEntity)

    @Update
    suspend fun update(account: AccountEntity)

    @Delete
    suspend fun delete(account: AccountEntity)

    @Query("DELETE FROM accounts")
    suspend fun deleteAll()
}

@Dao
interface BudgetDao {
    @Query("SELECT * FROM budgets")
    fun getAllBudgets(): Flow<List<BudgetEntity>>

    @Insert
    suspend fun insert(budget: BudgetEntity)

    @Update
    suspend fun update(budget: BudgetEntity)

    @Query("DELETE FROM budgets")
    suspend fun deleteAll()
}

@Dao
interface CategoryDao {
    @Query("SELECT * FROM categories")
    fun getAllCategories(): Flow<List<Category>>

    @Insert
    suspend fun insert(category: Category)

    @Query("DELETE FROM categories")
    suspend fun deleteAll()
}

@Dao
interface UserProfileDao {
    @Query("SELECT * FROM user_profile WHERE id = 1")
    fun getUserProfile(): Flow<UserProfile?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(userProfile: UserProfile)

    @Update
    suspend fun update(userProfile: UserProfile)

    @Query("DELETE FROM user_profile")
    suspend fun deleteAll()
}

@Dao
interface AIConversationDao {
    @Query("SELECT * FROM ai_conversations ORDER BY timestamp ASC")
    fun getConversation(): Flow<List<AIConversation>>

    @Insert
    suspend fun insert(message: AIConversation)

    @Query("DELETE FROM ai_conversations")
    suspend fun deleteAll()
}

@Dao
interface RecurringTransactionDao {
    @Query("SELECT * FROM recurring_transactions")
    fun getAll(): Flow<List<RecurringTransaction>>

    @Insert
    suspend fun insert(item: RecurringTransaction)

    @Query("DELETE FROM recurring_transactions")
    suspend fun deleteAll()
}

@Dao
interface CrashLogDao {
    @Insert
    suspend fun insert(crashLog: CrashLog)
}

@Dao
interface NotificationDao {
    @Query("SELECT * FROM notifications WHERE isDeleted = 0 ORDER BY createdAt DESC")
    fun getAllActive(): Flow<List<NotificationEntity>>

    @Query("SELECT * FROM notifications WHERE id = :id AND isDeleted = 0")
    suspend fun getById(id: Long): NotificationEntity?

    @Query("UPDATE notifications SET isRead = 1 WHERE id = :id")
    suspend fun markAsRead(id: Long)

    @Query("UPDATE notifications SET isDeleted = 1 WHERE id = :id")
    suspend fun softDelete(id: Long)

    @Query("DELETE FROM notifications WHERE id = :id")
    suspend fun delete(id: Long)

    @Insert
    suspend fun insert(notification: NotificationEntity)

    @Query("DELETE FROM notifications")
    suspend fun deleteAll()
}
