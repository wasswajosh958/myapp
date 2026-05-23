package ug.ac.ndejje.myapp

import android.content.Context
import android.speech.tts.TextToSpeech
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.*

/**
 * A Hybrid AI Assistant that handles both on-device financial queries 
 * and cloud-based general knowledge queries.
 */
class AiAssistant(private val context: Context) {
    private val brain = AIBrain(context)
    private var tts: TextToSpeech? = null
    
    init {
        tts = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                tts?.language = Locale.US
            }
        }
    }

    suspend fun getResponse(query: String, contextData: String): String {
        return brain.processQuery(query)
    }

    fun speak(text: String) {
        tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
    }

    fun onDestroy() {
        tts?.stop()
        tts?.shutdown()
    }
}

enum class QueryIntent {
    FINANCIAL, GENERAL
}
