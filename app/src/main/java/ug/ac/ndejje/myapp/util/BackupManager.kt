package ug.ac.ndejje.myapp.util

import ug.ac.ndejje.myapp.resources.*

import android.content.Context
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import java.io.File

data class AppDataBackup(
    val transactions: List<Transaction>,
    val accounts: List<AccountEntity>,
    val budgets: List<BudgetEntity>,
    val categories: List<Category>,
    val utilities: List<UtilityEntity>,
    val userProfile: UserProfile?
)

class BackupManager(
    private val context: Context,
    private val database: AppDatabase,
    private val userId: Int
) {
    private val gson = Gson()

    suspend fun exportData(): File? = withContext(Dispatchers.IO) {
        try {
            val backup = AppDataBackup(
                transactions = database.transactionDao().getAllTransactions(userId).first(),
                accounts = database.accountDao().getAllAccounts(userId).first(),
                budgets = database.budgetDao().getAllBudgets(userId).first(),
                categories = database.categoryDao().getAllCategories(userId).first(),
                utilities = database.utilityDao().getAllUtilities(userId).first(),
                userProfile = database.userProfileDao().getUserProfile(userId).first()
            )
            
            val json = gson.toJson(backup)
            val file = File(context.filesDir, "fintrack_backup_${userId}_${System.currentTimeMillis()}.json")
            file.writeText(json)
            file
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
