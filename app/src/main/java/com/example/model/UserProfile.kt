package com.example.model

enum class PlayerStatus {
    ONLINE,
    IN_LOBBY,
    IN_GAME,
    OFFLINE
}

data class UserProfile(
    val id: String,
    val displayName: String,
    val phoneNumber: String, // Masked in public views e.g. +977-98****1234
    val avatarEmoji: String = "👑",
    val status: PlayerStatus = PlayerStatus.ONLINE,
    val practiceCoins: Long = 10000L,
    val gamesPlayed: Int = 42,
    val gamesWon: Int = 29,
    val winRate: Float = 69.0f,
    val badges: List<String> = listOf("Royal Pioneer", "Hukum Master", "Table King"),
    val isVerified: Boolean = true
) {
    val maskedPhone: String
        get() = if (phoneNumber.length > 7) {
            phoneNumber.take(5) + "••••" + phoneNumber.takeLast(2)
        } else {
            phoneNumber
        }
}
