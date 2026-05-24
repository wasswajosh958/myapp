package ug.ac.ndejje.myapp

import android.content.Context
import androidx.work.*
import java.util.concurrent.TimeUnit

class RecurringTransactionWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        val passphrase = "secure_password".toByteArray()
        val database = AppDatabase.getInstance(applicationContext, passphrase)
        // logic to check and insert recurring transactions
        return Result.success()
    }
}

class BudgetCheckWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        val passphrase = "secure_password".toByteArray()
        val database = AppDatabase.getInstance(applicationContext, passphrase)
        // logic to check budget limits and send notifications
        return Result.success()
    }
}

fun scheduleWorkers(context: Context) {
    val workManager = WorkManager.getInstance(context)
    
    val recurringRequest = PeriodicWorkRequestBuilder<RecurringTransactionWorker>(1, TimeUnit.DAYS)
        .build()
    workManager.enqueueUniquePeriodicWork("recurring_check", ExistingPeriodicWorkPolicy.KEEP, recurringRequest)
    
    val budgetRequest = PeriodicWorkRequestBuilder<BudgetCheckWorker>(6, TimeUnit.HOURS)
        .build()
    workManager.enqueueUniquePeriodicWork("budget_check", ExistingPeriodicWorkPolicy.KEEP, budgetRequest)
}
