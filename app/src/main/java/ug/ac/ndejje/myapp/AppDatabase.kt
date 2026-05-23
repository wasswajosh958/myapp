package ug.ac.ndejje.myapp

import android.content.Context
import androidx.room.*
import net.sqlcipher.database.SupportFactory
import net.sqlcipher.database.SQLiteDatabase

@Dao
interface TransactionDao {
    @Query("SELECT * FROM transactions ORDER BY id DESC")
    suspend fun getAll(): List<Transaction>

    @Insert
    suspend fun insert(transaction: Transaction)

    @Delete
    suspend fun delete(transaction: Transaction)
}

@Dao
interface CategoryDao {
    @Query("SELECT * FROM categories")
    suspend fun getAll(): List<Category>

    @Insert
    suspend fun insert(category: Category)
}

@Dao
interface AccountDao {
    @Query("SELECT * FROM accounts")
    suspend fun getAll(): List<AccountEntity>

    @Insert
    suspend fun insert(account: AccountEntity)

    @Update
    suspend fun update(account: AccountEntity)
}

@Dao
interface BudgetDao {
    @Query("SELECT * FROM budgets")
    suspend fun getAll(): List<BudgetEntity>

    @Insert
    suspend fun insert(budget: BudgetEntity)

    @Update
    suspend fun update(budget: BudgetEntity)
}

@Dao
interface CrashLogDao {
    @Insert
    suspend fun insert(log: CrashLog)
}

@Database(
    entities = [
        Transaction::class, 
        Category::class, 
        AccountEntity::class, 
        BudgetEntity::class, 
        RecurringTransaction::class, 
        AIConversation::class,
        CrashLog::class,
        AnalyticsEvent::class
    ],
    version = 1
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun transactionDao(): TransactionDao
    abstract fun categoryDao(): CategoryDao
    abstract fun accountDao(): AccountDao
    abstract fun budgetDao(): BudgetDao
    abstract fun crashLogDao(): CrashLogDao

    companion object {
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            if (INSTANCE == null) {
                synchronized(AppDatabase::class) {
                    // In a production app, we would get the passphrase from KeyStore
                    val passphrase = SQLiteDatabase.getBytes("secret_passphrase".toCharArray())
                    val factory = SupportFactory(passphrase)
                    
                    INSTANCE = Room.databaseBuilder(
                        context.applicationContext,
                        AppDatabase::class.java,
                        "fintrack.db"
                    )
                    .openHelperFactory(factory)
                    .build()
                }
            }
            return INSTANCE!!
        }
    }
}
