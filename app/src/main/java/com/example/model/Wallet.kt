package com.example.model

enum class TransactionType(val title: String, val isCredit: Boolean) {
    PRACTICE_GRANT("Initial Practice Grant", true),
    DAILY_BONUS("Daily Royal Bonus", true),
    TABLE_BUY_IN("Table Entry Buy-In", false),
    MATCH_WIN_SETTLEMENT("Match Victory Payout", true),
    MATCH_REFUND("Table Cancelled Refund", true),
    HOST_TIP("Host Appreciation", false)
}

enum class TransactionStatus {
    CONFIRMED,
    PENDING,
    REJECTED
}

data class LedgerTransaction(
    val id: String,
    val idempotencyKey: String,
    val timestamp: Long,
    val type: TransactionType,
    val amount: Long,
    val currency: String = "NPR (Practice)",
    val sourceAccount: String,
    val destinationAccount: String,
    val balanceAfter: Long,
    val status: TransactionStatus = TransactionStatus.CONFIRMED,
    val auditHash: String,
    val notes: String = ""
)

data class WalletAccount(
    val userId: String,
    val balance: Long,
    val pendingBalance: Long = 0L,
    val currency: String = "NPR (Practice)",
    val transactions: List<LedgerTransaction> = emptyList()
)
