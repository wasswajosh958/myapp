package ug.ac.ndejje.myapp

data class Transaction(
    val id: Int,
    val title: String, 
    val category: String, 
    val amountValue: Double, 
    val date: String, 
    val time: String,
    val isExpense: Boolean,
    val isPending: Boolean = false
) {
    fun getFormattedAmount(currency: String): String {
        return "$currency ${String.format("%.2f", amountValue)}"
    }
}

data class ChatMessage(val text: String, val isUser: Boolean)
