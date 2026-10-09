package com.example.model

enum class HostActionType {
    ROOM_CREATED,
    RULES_LOCKED,
    MATCH_STARTED,
    CARD_DEAL_VERIFIED,
    PLAYER_READY_STATUS_CHANGED,
    ROUND_CONCLUDED,
    DISPUTE_SETTLED,
    SPECTATOR_ALLOWED
}

data class HostAuditEntry(
    val id: String,
    val roomId: String,
    val hostId: String,
    val hostName: String,
    val timestamp: Long = System.currentTimeMillis(),
    val actionType: HostActionType,
    val description: String,
    val deckVerificationHash: String? = null,
    val isAuditImmutable: Boolean = true
)
