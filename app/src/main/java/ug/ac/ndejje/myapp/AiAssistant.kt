package ug.ac.ndejje.myapp

import android.content.Context

/**
 * A Hybrid AI Assistant that handles both on-device financial queries 
 * and cloud-based general knowledge queries.
 */
class AiAssistant(context: Context, database: AppDatabase, userId: Int) {
    private val brain = AIBrain(database, userId)
    private val ttsHelper = TextToSpeechHelper(context)

    suspend fun getResponse(query: String): String {
        return brain.processQuery(query)
    }

    fun speak(text: String) {
        ttsHelper.speak(text)
    }

    fun onDestroy() {
        ttsHelper.shutdown()
    }
}
