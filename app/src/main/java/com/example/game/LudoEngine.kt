package com.example.game

enum class LudoColor(val display: String, val startTile: Int) {
    RED("Red", 0),
    GREEN("Green", 13),
    YELLOW("Yellow", 26),
    BLUE("Blue", 39)
}

data class LudoToken(
    val id: Int,
    val color: LudoColor,
    val position: Int = -1 // -1 = Yard, 0..51 = track, 52..56 = home path, 57 = Home Palace
) {
    val isHome: Boolean get() = position == 57
    val isInYard: Boolean get() = position == -1
}

data class LudoState(
    val currentTurnColor: LudoColor = LudoColor.RED,
    val diceValue: Int = 1,
    val hasRolled: Boolean = false,
    val tokens: Map<LudoColor, List<LudoToken>> = LudoColor.values().associateWith { color ->
        (0..3).map { id -> LudoToken(id = id, color = color) }
    },
    val winner: LudoColor? = null
)

object LudoEngine {
    val SAFE_TILES = setOf(0, 8, 13, 21, 26, 34, 39, 47)

    fun canMoveToken(token: LudoToken, dice: Int): Boolean {
        if (token.isHome) return false
        if (token.isInYard) return dice == 6
        return token.position + dice <= 57
    }

    fun moveToken(state: LudoState, token: LudoToken): LudoState {
        val dice = state.diceValue
        if (!canMoveToken(token, dice)) return state

        val newPos = if (token.isInYard) {
            token.color.startTile
        } else {
            token.position + dice
        }

        val updatedTokens = state.tokens.toMutableMap()
        val colorTokens = updatedTokens[token.color]!!.map {
            if (it.id == token.id) it.copy(position = newPos) else it
        }
        updatedTokens[token.color] = colorTokens

        // Capture opponent token if landed on non-safe tile
        if (newPos in 0..51 && !SAFE_TILES.contains(newPos)) {
            LudoColor.values().filter { it != token.color }.forEach { oppColor ->
                val oppTokens = updatedTokens[oppColor]!!.map { oppToken ->
                    if (oppToken.position == newPos) oppToken.copy(position = -1) else oppToken
                }
                updatedTokens[oppColor] = oppTokens
            }
        }

        // Check if player won
        val won = colorTokens.all { it.isHome }
        val nextColor = if (won || dice == 6) state.currentTurnColor else getNextColor(state.currentTurnColor)

        return state.copy(
            tokens = updatedTokens,
            currentTurnColor = if (won) state.currentTurnColor else nextColor,
            hasRolled = false,
            winner = if (won) token.color else null
        )
    }

    private fun getNextColor(current: LudoColor): LudoColor {
        val values = LudoColor.values()
        return values[(current.ordinal + 1) % values.size]
    }
}
