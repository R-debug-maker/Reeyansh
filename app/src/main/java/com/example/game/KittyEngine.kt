package com.example.game

import com.example.model.Card

data class KittySets(
    val set1: List<Card>, // 3 cards
    val set2: List<Card>, // 3 cards
    val set3: List<Card>  // 3 cards
)

data class KittyRoundResult(
    val round1WinnerSeat: Int,
    val round2WinnerSeat: Int,
    val round3WinnerSeat: Int,
    val overallWinnerSeat: Int,
    val round1Evals: Map<Int, TeenPattiEvaluation>,
    val round2Evals: Map<Int, TeenPattiEvaluation>,
    val round3Evals: Map<Int, TeenPattiEvaluation>
)

object KittyEngine {

    /**
     * Splits 9 cards automatically into the best 3 sets of 3 cards for smart bot play
     */
    fun autoArrange9Cards(cards: List<Card>): KittySets {
        require(cards.size == 9) { "Kitty requires 9 cards" }
        // Simple strategic arrangement: sort by rank value, group into 3 sets
        val sorted = cards.sortedByDescending { it.rank.value }
        return KittySets(
            set1 = listOf(sorted[0], sorted[1], sorted[2]),
            set2 = listOf(sorted[3], sorted[4], sorted[5]),
            set3 = listOf(sorted[6], sorted[7], sorted[8])
        )
    }

    /**
     * Compare Kitty sets across 3 rounds and determine the match winner
     */
    fun evaluateMatch(playerSets: Map<Int, KittySets>): KittyRoundResult {
        val r1Evals = playerSets.mapValues { TeenPattiEngine.evaluateHand(it.value.set1) }
        val r2Evals = playerSets.mapValues { TeenPattiEngine.evaluateHand(it.value.set2) }
        val r3Evals = playerSets.mapValues { TeenPattiEngine.evaluateHand(it.value.set3) }

        val r1Winner = r1Evals.maxByOrNull { it.value }!!.key
        val r2Winner = r2Evals.maxByOrNull { it.value }!!.key
        val r3Winner = r3Evals.maxByOrNull { it.value }!!.key

        val winsCount = mutableMapOf<Int, Int>()
        winsCount[r1Winner] = (winsCount[r1Winner] ?: 0) + 1
        winsCount[r2Winner] = (winsCount[r2Winner] ?: 0) + 1
        winsCount[r3Winner] = (winsCount[r3Winner] ?: 0) + 1

        val overallWinner = winsCount.maxByOrNull { it.value }!!.key

        return KittyRoundResult(
            round1WinnerSeat = r1Winner,
            round2WinnerSeat = r2Winner,
            round3WinnerSeat = r3Winner,
            overallWinnerSeat = overallWinner,
            round1Evals = r1Evals,
            round2Evals = r2Evals,
            round3Evals = r3Evals
        )
    }
}
