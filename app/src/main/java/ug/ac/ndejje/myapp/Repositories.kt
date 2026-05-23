package ug.ac.ndejje.myapp

import kotlinx.coroutines.flow.Flow

/**
 * Repositories for managing data operations across different sources.
 */

class TransactionRepository(private val transactionDao: TransactionDao) {
    suspend fun getAllTransactions(): List<Transaction> = transactionDao.getAll()
    suspend fun insertTransaction(transaction: Transaction) = transactionDao.insert(transaction)
    suspend fun deleteTransaction(transaction: Transaction) = transactionDao.delete(transaction)
}

class CategoryRepository(private val categoryDao: CategoryDao) {
    suspend fun getAllCategories(): List<Category> = categoryDao.getAll()
    suspend fun insertCategory(category: Category) = categoryDao.insert(category)
}

class AccountRepository(private val accountDao: AccountDao) {
    suspend fun getAllAccounts(): List<AccountEntity> = accountDao.getAll()
    suspend fun insertAccount(account: AccountEntity) = accountDao.insert(account)
    suspend fun updateAccount(account: AccountEntity) = accountDao.update(account)
}

class BudgetRepository(private val budgetDao: BudgetDao) {
    suspend fun getAllBudgets(): List<BudgetEntity> = budgetDao.getAll()
    suspend fun insertBudget(budget: BudgetEntity) = budgetDao.insert(budget)
    suspend fun updateBudget(budget: BudgetEntity) = budgetDao.update(budget)
}

class UserProfileRepository(private val userProfileDao: UserProfileDao) {
    suspend fun getUserProfile(): UserProfile? = userProfileDao.getUser()
    suspend fun saveUserProfile(profile: UserProfile) = userProfileDao.saveUser(profile)
}
