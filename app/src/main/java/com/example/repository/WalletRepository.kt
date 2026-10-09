package com.example.repository

import com.example.model.LedgerTransaction
import com.example.model.TransactionStatus
import com.example.model.TransactionType
import com.example.model.WalletAccount
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.security.MessageDigest
import java.util.UUID

class WalletRepository {

    private val _walletState = MutableStateFlow(
        WalletAccount(
            userId = "USER_ROYAL_01",
            balance = 10000L,
            pendingBalance = 0L,
            currency = "NPR (Practice)",
            transactions = listOf(
                LedgerTransaction(
                    id = "TX_INIT_001",
                    idempotencyKey = "IDEMP_INIT_001",
                    timestamp = System.currentTimeMillis() - 86400000L * 2,
                    type = TransactionType.PRACTICE_GRANT,
                    amount = 10000L,
                    currency = "NPR (Practice)",
                    sourceAccount = "SYSTEM_TREASURY",
                    destinationAccount = "USER_ROYAL_01",
                    balanceAfter = 10000L,
                    status = TransactionStatus.CONFIRMED,
                    auditHash = "SHA256:4a79b20e1f3d82",
                    notes = "Welcome Royal Taas Practice Grant"
                ),
                LedgerTransaction(
                    id = "TX_DAILY_002",
                    idempotencyKey = "IDEMP_DAILY_002",
                    timestamp = System.currentTimeMillis() - 3600000L * 5,
                    type = TransactionType.DAILY_BONUS,
                    amount = 500L,
                    currency = "NPR (Practice)",
                    sourceAccount = "SYSTEM_REWARDS",
                    destinationAccount = "USER_ROYAL_01",
                    balanceAfter = 10500L,
                    status = TransactionStatus.CONFIRMED,
                    auditHash = "SHA256:9b12c40e3f71a0",
                    notes = "Day 1 Consecutive Login Reward"
                )
            )
        )
    )
    val walletState: StateFlow<WalletAccount> = _walletState.asStateFlow()

    private val processedIdempotencyKeys = mutableSetOf("IDEMP_INIT_001", "IDEMP_DAILY_002")

    @Synchronized
    fun recordTransaction(
        type: TransactionType,
        amount: Long,
        idempotencyKey: String,
        source: String,
        destination: String,
        notes: String
    ): Result<LedgerTransaction> {
        if (processedIdempotencyKeys.contains(idempotencyKey)) {
            // Idempotent: return existing
            val existing = _walletState.value.transactions.find { it.idempotencyKey == idempotencyKey }
            return if (existing != null) Result.success(existing) else Result.failure(IllegalStateException("Duplicate transaction key"))
        }

        val currentBalance = _walletState.value.balance
        if (!type.isCredit && currentBalance < amount) {
            return Result.failure(IllegalStateException("Insufficient balance ($currentBalance < $amount)"))
        }

        val newBalance = if (type.isCredit) currentBalance + amount else currentBalance - amount
        val txId = "TX_" + UUID.randomUUID().toString().take(8).uppercase()
        val timestamp = System.currentTimeMillis()

        // Generate SHA-256 audit digest
        val hashData = "$txId:$idempotencyKey:$amount:$source:$destination:$newBalance:$timestamp"
        val auditHash = "SHA256:" + MessageDigest.getInstance("SHA-256")
            .digest(hashData.toByteArray())
            .joinToString("") { "%02x".format(it) }.take(16)

        val tx = LedgerTransaction(
            id = txId,
            idempotencyKey = idempotencyKey,
            timestamp = timestamp,
            type = type,
            amount = amount,
            currency = _walletState.value.currency,
            sourceAccount = source,
            destinationAccount = destination,
            balanceAfter = newBalance,
            status = TransactionStatus.CONFIRMED,
            auditHash = auditHash,
            notes = notes
        )

        processedIdempotencyKeys.add(idempotencyKey)
        val updatedList = listOf(tx) + _walletState.value.transactions
        _walletState.value = _walletState.value.copy(
            balance = newBalance,
            transactions = updatedList
        )

        return Result.success(tx)
    }

    fun claimDailyBonus(): Boolean {
        val key = "DAILY_" + (System.currentTimeMillis() / (1000 * 60 * 60 * 24))
        val res = recordTransaction(
            type = TransactionType.DAILY_BONUS,
            amount = 1000L,
            idempotencyKey = key,
            source = "SYSTEM_REWARDS",
            destination = _walletState.value.userId,
            notes = "Daily Royal Taas Bonus Claim"
        )
        return res.isSuccess
    }
}
