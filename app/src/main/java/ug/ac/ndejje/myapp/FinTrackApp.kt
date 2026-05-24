package ug.ac.ndejje.myapp

import android.app.Application
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class FinTrackApp : Application() {
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        
        container = AppContainer(this)
        
        // Setup Custom Crash Handler
        val defaultHandler = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            val crashLog = CrashLog(
                exception = throwable.javaClass.simpleName,
                message = throwable.message ?: "No message",
                stackTrace = throwable.stackTraceToString()
            )
            
            // Note: We need a direct DB reference or a way to access the DAO
            // For simplicity in crash handling, we can use the container's database if we make it public
            // or just use the repositories if they handle it.
            // Let's assume we want to save this crash log.
            
            // To avoid passing passphrase everywhere, AppDatabase should ideally manage its instance
            // with the passphrase internally after the first call.
            
            // For now, let's skip the crash log insert here to get the app running, 
            // or better, implement it properly in AppContainer.
            
            defaultHandler?.uncaughtException(thread, throwable)
        }

        scheduleWorkers(this)
    }
}
