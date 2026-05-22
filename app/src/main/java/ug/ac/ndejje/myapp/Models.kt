package ug.ac.ndejje.myapp

data class Transaction(
    val id: Int,
    val title: String, 
    val category: String, 
    val amount: String, 
    val date: String, 
    val time: String,
    val isExpense: Boolean,
    val isPending: Boolean = false
)

data class ChatMessage(val text: String, val isUser: Boolean)
