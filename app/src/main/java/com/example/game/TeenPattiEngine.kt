package com.example.game

import com.example.model.Card
import com.example.model.Rank

enum class TeenPattiHandRank(val weight: Int, val title: String) {
    TRAIL(6, "Trail (Trio / Teen)"),
    PURE_SEQUENCE(5, "Pure Sequence (Pakki Patti)"),
    SEQUENCE(4, "Sequence (Normal Patti)"),
    COLOR(3, "Color (Flush)"),
    PAIR(2, "Pair (Joda)"),
    HIGH_CARD(1, "High Card")
}

data class TeenPattiEvaluation(
    val rankType: TeenPattiHandRank,
    val primaryValue: Int,
    val secondaryValue: Int = 0,
    val tertiaryValue: Int = 0
) : Comparable<TeenPattiEvaluation> {
    override fun compareTo(other: TeenPattiEvaluation): Int {
        if (this.rankType.weight != other.rankType.weight) {
            return this.rankType.weight.compareTo(other.rankType.weight)
        }
        if (this.primaryValue != other.primaryValue) {
            return this.primaryValue.compareTo(other.primaryValue)
        }
        if (this.secondaryValue != other.secondaryValue) {
            return this.secondaryValue.compareTo(other.secondaryValue)
        }
        return this.tertiaryValue.compareTo(other.tertiaryValue)
    }
}

object TeenPattiEngine {

    fun evaluateHand(cards: List<Card>): TeenPattiEvaluation {
        require(cards.size == 3) { "Teen Patti requires exactly 3 cards" }
        val sorted = cards.sortedByDescending { it.rank.value }
        val r0 = sorted[0].rank.value
        val r1 = sorted[1].rank.value
        val r2 = sorted[2].rank.value

        val isSameSuit = cards.all { it.suit == cards[0].suit }

        // 1. Trail / Trio (e.g. A-A-A, K-K-K)
        if (r0 == r1 && r1 == r2) {
            return TeenPattiEvaluation(TeenPattiHandRank.TRAIL, r0)
        }

        // Check Sequence (A-K-Q is highest, A-2-3 is second highest in Teen Patti rules)
        val isA23 = (r0 == Rank.ACE.value && r1 == Rank.THREE.value && r2 == Rank.TWO.value)
        val isNormalSequence = (r0 - r1 == 1 && r1 - r2 == 1)

        if (isA23 || isNormalSequence) {
            val sequenceHigh = if (isA23) 13 else r0 // A23 ranks below AKQ (14) but above KQJ (13) or standard
            return if (isSameSuit) {
                TeenPattiEvaluation(TeenPattiHandRank.PURE_SEQUENCE, sequenceHigh)
            } else {
                TeenPattiEvaluation(TeenPattiHandRank.SEQUENCE, sequenceHigh)
            }
        }

        // 4. Color (Flush)
        if (isSameSuit) {
            return TeenPattiEvaluation(TeenPattiHandRank.COLOR, r0, r1, r2)
        }

        // 5. Pair
        if (r0 == r1) {
            return TeenPattiEvaluation(TeenPattiHandRank.PAIR, r0, r2)
        }
        if (r1 == r2) {
            return TeenPattiEvaluation(TeenPattiHandRank.PAIR, r1, r0)
        }
        if (r0 == r2) {
            return TeenPattiEvaluation(TeenPattiHandRank.PAIR, r0, r1)
        }

        // 6. High Card
        return TeenPattiEvaluation(TeenPattiHandRank.HIGH_CARD, r0, r1, r2)
    }

    fun findWinner(hands: Map<Int, List<Card>>): Int {
        return hands.mapValues { evaluateHand(it.value) }
            .maxByOrNull { it.value }!!.key
    }
}
