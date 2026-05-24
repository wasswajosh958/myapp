package ug.ac.ndejje.myapp

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {
    @Query("SELECT * FROM transactions WHERE userId = :userId ORDER BY date DESC, time DESC")
    fun getAllTransactions(userId: Int): Flow<List<Transaction>>

    @Insert
    suspend fun insert(transaction: Transaction)

    @Delete
    suspend fun delete(transaction: Transaction)

    @Query("DELETE FROM transactions WHERE userId = :userId")
    suspend fun deleteAll(userId: Int)
}

@Dao
interface AccountDao {
    @Query("SELECT * FROM accounts WHERE userId = :userId")
    fun getAllAccounts(userId: Int): Flow<List<AccountEntity>>

    @Insert
    suspend fun insert(account: AccountEntity)

    @Update
    suspend fun update(account: AccountEntity)

    @Delete
    suspend fun delete(account: AccountEntity)

    @Query("DELETE FROM accounts WHERE userId = :userId")
    suspend fun deleteAll(userId: Int)
}

@Dao
interface BudgetDao {
    @Query("SELECT * FROM budgets WHERE userId = :userId")
    fun getAllBudgets(userId: Int): Flow<List<BudgetEntity>>

    @Insert
    suspend fun insert(budget: BudgetEntity)

    @Update
    suspend fun update(budget: BudgetEntity)

    @Query("DELETE FROM budgets WHERE userId = :userId")
    suspend fun deleteAll(userId: Int)
}

@Dao
interface CategoryDao {
    @Query("SELECT * FROM categories WHERE userId = :userId")
    fun getAllCategories(userId: Int): Flow<List<Category>>

    @Insert
    suspend fun insert(category: Category)

    @Query("DELETE FROM categories WHERE userId = :userId")
    suspend fun deleteAll(userId: Int)
}

@Dao
interface UserProfileDao {
    @Query("SELECT * FROM user_profile WHERE id = :userId")
    fun getUserProfile(userId: Int): Flow<UserProfile?>

    @Query("SELECT * FROM user_profile WHERE username = :username LIMIT 1")
    suspend fun getUserByUsername(username: String): UserProfile?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(userProfile: UserProfile): Long

    @Update
    suspend fun update(userProfile: UserProfile)

    @Query("DELETE FROM user_profile WHERE id = :userId")
    suspend fun delete(userId: Int)
}

@Dao
interface AIConversationDao {
    @Query("SELECT * FROM ai_conversations WHERE userId = :userId ORDER BY timestamp ASC")
    fun getConversation(userId: Int): Flow<List<AIConversation>>

    @Insert
    suspend fun insert(message: AIConversation)

    @Query("DELETE FROM ai_conversations WHERE userId = :userId")
    suspend fun deleteAll(userId: Int)
}

@Dao
interface RecurringTransactionDao {
    @Query("SELECT * FROM recurring_transactions WHERE userId = :userId")
    fun getAll(userId: Int): Flow<List<RecurringTransaction>>

    @Insert
    suspend fun insert(item: RecurringTransaction)

    @Query("DELETE FROM recurring_transactions WHERE userId = :userId")
    suspend fun deleteAll(userId: Int)
}

@Dao
interface CrashLogDao {
    @Insert
    suspend fun insert(crashLog: CrashLog)
}

@Dao
interface NotificationDao {
    @Query("SELECT * FROM notifications WHERE userId = :userId AND isDeleted = 0 ORDER BY createdAt DESC")
    fun getAllActive(userId: Int): Flow<List<NotificationEntity>>

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

    @Query("DELETE FROM notifications WHERE userId = :userId")
    suspend fun deleteAll(userId: Int)
}
