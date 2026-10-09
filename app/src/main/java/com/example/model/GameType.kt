package com.example.model

enum class GameType(
    val title: String,
    val nepaliTitle: String,
    val shortDescription: String,
    val minPlayers: Int,
    val maxPlayers: Int,
    val estimatedMinutes: Int,
    val isCardGame: Boolean,
    val defaultEntryCoins: Long
) {
    CALL_BREAK(
        title = "Call Break",
        nepaliTitle = "कल ब्रेक",
        shortDescription = "4-player strategic trick-taking game. Hukum (Spades) is permanent trump. Bid your hands and capture tricks!",
        minPlayers = 4,
        maxPlayers = 4,
        estimatedMinutes = 15,
        isCardGame = true,
        defaultEntryCoins = 100
    ),
    TEEN_PATTI(
        title = "Teen Patti",
        nepaliTitle = "तीन पत्ती",
        shortDescription = "Royal 3-card showdown. Blind and Chaal bets, Trail (Trio), Pure Sequence, Sequence, and High Card.",
        minPlayers = 2,
        maxPlayers = 6,
        estimatedMinutes = 10,
        isCardGame = true,
        defaultEntryCoins = 200
    ),
    KITTY(
        title = "Kitty / Kitti",
        nepaliTitle = "कित्ती (Kitty)",
        shortDescription = "Popular Nepali 9-card game. Organize cards into 3 winning sets and triumph across 3 showdown rounds.",
        minPlayers = 2,
        maxPlayers = 5,
        estimatedMinutes = 12,
        isCardGame = true,
        defaultEntryCoins = 150
    ),
    MARRIAGE_21(
        title = "Marriage 21",
        nepaliTitle = "म्यारिज (२१ पत्ती)",
        shortDescription = "Legendary Nepali 21-card rummy game with 3 decks, Tiplu, Maal points, Tunnelas, and Marriage declarations.",
        minPlayers = 2,
        maxPlayers = 5,
        estimatedMinutes = 20,
        isCardGame = true,
        defaultEntryCoins = 300
    ),
    LUDO(
        title = "Royal Ludo",
        nepaliTitle = "लुडो",
        shortDescription = "Classic 4-color board game. Roll the royal dice, escape home base, capture opponents, and reach the palace.",
        minPlayers = 2,
        maxPlayers = 4,
        estimatedMinutes = 18,
        isCardGame = false,
        defaultEntryCoins = 100
    ),
    CHESS(
        title = "Royal Chess",
        nepaliTitle = "चेस (बुद्धिचाल)",
        shortDescription = "Timeless 64-square grandmaster battle. Checkmate your opponent's King with precise tactical calculation.",
        minPlayers = 2,
        maxPlayers = 2,
        estimatedMinutes = 25,
        isCardGame = false,
        defaultEntryCoins = 250
    ),
    SNAKE_LADDER(
        title = "Snake & Ladder",
        nepaliTitle = "साँप र भर्‍याङ",
        shortDescription = "Classic 100-step royal race. Climb lucky ladders, avoid dangerous cobras, and reach tile 100 first.",
        minPlayers = 2,
        maxPlayers = 4,
        estimatedMinutes = 10,
        isCardGame = false,
        defaultEntryCoins = 50
    )
}
