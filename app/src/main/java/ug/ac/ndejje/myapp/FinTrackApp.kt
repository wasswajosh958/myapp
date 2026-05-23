package ug.ac.ndejje.myapp

import android.app.Application
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class FinTrackApp : Application() {

    override fun onCreate() {
        super.onCreate()
        
        val database = AppDatabase.getInstance(this)
        
        // Setup Custom Crash Handler
        val defaultHandler = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            val crashLog = CrashLog(
                exception = throwable.javaClass.simpleName,
                message = throwable.message ?: "No message",
                stackTrace = throwable.stackTraceToString()
            )
            
            GlobalScope.launch {
                database.crashLogDao().insert(crashLog)
            }
            
            defaultHandler?.uncaughtException(thread, throwable)
        }

        scheduleWorkers(this)
    }
}
