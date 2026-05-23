package ug.ac.ndejje.myapp

import android.content.Context

/**
 * Dependency injection container to manage database and repositories.
 */
class AppContainer(context: Context) {
    private val database = AppDatabase.getInstance(context)

    val transactionRepository: TransactionRepository = TransactionRepository(database.transactionDao())
    val categoryRepository: CategoryRepository = CategoryRepository(database.categoryDao())
    val accountRepository: AccountRepository = AccountRepository(database.accountDao())
    val budgetRepository: BudgetRepository = BudgetRepository(database.budgetDao())
    val userProfileRepository: UserProfileRepository = UserProfileRepository(database.userProfileDao())
}
