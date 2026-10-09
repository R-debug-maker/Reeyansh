package com.example.game

data class SnakeLadderPlayer(
    val id: Int,
    val name: String,
    val colorHex: Long,
    val position: Int = 1
)

object SnakeLadderEngine {
    val LADDERS = mapOf(
        1 to 38,
        4 to 14,
        9 to 31,
        21 to 42,
        28 to 84,
        36 to 44,
        51 to 67,
        71 to 91,
        80 to 100
    )

    val SNAKES = mapOf(
        16 to 6,
        47 to 26,
        49 to 11,
        56 to 53,
        62 to 19,
        64 to 60,
        87 to 24,
        93 to 73,
        95 to 75,
        98 to 78
    )

    fun calculateNewPosition(currentPos: Int, dice: Int): Pair<Int, String> {
        val candidate = currentPos + dice
        if (candidate > 100) {
            return Pair(currentPos, "Need exact roll to reach 100!")
        }
        if (LADDERS.containsKey(candidate)) {
            val destination = LADDERS[candidate]!!
            return Pair(destination, "Lucky Ladder! Climbed from $candidate to $destination!")
        }
        if (SNAKES.containsKey(candidate)) {
            val destination = SNAKES[candidate]!!
            return Pair(destination, "Ouch! Bitten by Cobra! Slid from $candidate down to $destination!")
        }
        return Pair(candidate, if (candidate == 100) "Royal Champion! Reached 100!" else "Moved to $candidate")
    }
}
