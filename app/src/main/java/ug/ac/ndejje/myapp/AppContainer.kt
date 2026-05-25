package ug.ac.ndejje.myapp

import android.content.Context

/**
 * Dependency injection container to manage database and repositories.
 */
class AppContainer(context: Context) {
    // For demonstration, using a fixed passphrase. In a production app,
    // this should be securely retrieved (e.g., from Keystore or user session).
    private val passphrase = "secure_password".toByteArray()
    val database = AppDatabase.getInstance(context, passphrase)

    val transactionRepository: TransactionRepository = TransactionRepository(
        database.transactionDao(),
        database.notificationDao()
    )
    val categoryRepository: CategoryRepository = CategoryRepository(database.categoryDao())
    val accountRepository: AccountRepository = AccountRepository(
        database.accountDao(),
        database.notificationDao()
    )
    val budgetRepository: BudgetRepository = BudgetRepository(
        database.budgetDao(),
        database.notificationDao()
    )
    val userProfileRepository: UserProfileRepository = UserProfileRepository(database.userProfileDao())
    val notificationRepository: NotificationRepository = NotificationRepository(database.notificationDao())
    val aiConversationRepository: AIConversationRepository = AIConversationRepository(database.aiConversationDao())
}
