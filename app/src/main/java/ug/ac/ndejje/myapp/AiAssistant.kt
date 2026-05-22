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

    private var tts: TextToSpeech? = null
    
    init {
        tts = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                tts?.language = Locale.US
            }
        }
    }

    private val financialKeywords = setOf(
        "spent", "spend", "budget", "balance", "transaction", "income", "expense",
        "paid", "cost", "price", "category", "save", "saving", "ugx"
    )

    fun classifyQuery(query: String): QueryIntent {
        val lower = query.lowercase()
        return if (financialKeywords.any { it in lower }) {
            QueryIntent.FINANCIAL
        } else {
            QueryIntent.GENERAL
        }
    }

    suspend fun getResponse(query: String, contextData: String): String = withContext(Dispatchers.IO) {
        val intent = classifyQuery(query)
        
        if (intent == QueryIntent.FINANCIAL) {
            // Placeholder for MediaPipe GenAI On-Device Inference
            // In a real app, you'd call LlmInference here.
            return@withContext "Based on your records: $contextData\nYou asked: \"$query\""
        } else {
            // Placeholder for Cloud Fallback (e.g., GPT-4o-mini or DeepSeek)
            return@withContext "I'm answering your general question about \"$query\" using my cloud brain."
        }
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
