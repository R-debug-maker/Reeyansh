package com.example.model

enum class RoomType {
    SOLO,
    PUBLIC,
    PRIVATE
}

enum class RoomStatus {
    WAITING,
    PLAYING,
    ROUND_OVER,
    COMPLETED
}

data class PlayerSeat(
    val seatIndex: Int,
    val player: UserProfile,
    val isHost: Boolean = false,
    val isBot: Boolean = false,
    val hand: List<Card> = emptyList(),
    val currentBid: Int = 0,
    val tricksWon: Int = 0,
    val roundScore: Float = 0f,
    val totalScore: Float = 0f,
    val currentBet: Long = 0L,
    val isFolded: Boolean = false,
    val isSeen: Boolean = false, // Teen Patti
    val isReady: Boolean = true,
    val hasTurn: Boolean = false,
    val isMicOn: Boolean = false,
    val isMuted: Boolean = false,
    val selectedPlayCard: Card? = null
)

data class RulePreset(
    val id: String = "default",
    val name: String = "Standard Rules",
    val roundsCount: Int = 5,
    val turnTimeoutSeconds: Int = 20,
    val spadesCutRequired: Boolean = true, // Call Break
    val bootAmount: Long = 10L,           // Teen Patti
    val maxBetMultiplier: Int = 4,        // Teen Patti
    val marriageDubleeCount: Int = 7,     // Marriage 21
    val ludoSafeSpacesActive: Boolean = true,
    val customHouseRules: String = "Standard Tournament Rulebook"
)

data class GameRoom(
    val id: String,
    val name: String,
    val roomCode: String, // e.g. "RT-8492"
    val gameType: GameType,
    val roomType: RoomType,
    val hostId: String,
    val hostName: String,
    val maxPlayers: Int,
    val minPlayers: Int,
    val entryCoins: Long,
    val status: RoomStatus = RoomStatus.WAITING,
    val roundNumber: Int = 1,
    val currentTurnSeat: Int = 0,
    val potAmount: Long = 0L,
    val currentTrickCards: List<Pair<Int, Card>> = emptyList(), // seatIndex to Card
    val rulePreset: RulePreset = RulePreset(),
    val seats: List<PlayerSeat> = emptyList(),
    val spectatorCount: Int = 0,
    val fairShuffleHash: String = "SHA256:7e8a91c0e3b429188a",
    val createdAt: Long = System.currentTimeMillis()
)
