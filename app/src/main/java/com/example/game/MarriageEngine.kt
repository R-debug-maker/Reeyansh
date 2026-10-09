package com.example.game

import com.example.model.Card
import com.example.model.DeckFactory
import com.example.model.Rank
import com.example.model.Suit
import java.security.SecureRandom

data class MarriageMelds(
    val pureSequences: List<List<Card>>,
    val tunnelas: List<List<Card>>,
    val dublees: List<Pair<Card, Card>>,
    val rawCardsRemaining: List<Card>
)

object MarriageEngine {

    /**
     * Determines if 3 cards form a valid Tunnela (3 identical cards in rank and suit)
     */
    fun isTunnela(cards: List<Card>): Boolean {
        if (cards.size != 3) return false
        val first = cards[0]
        return cards.all { it.suit == first.suit && it.rank == first.rank }
    }

    /**
     * Determines if cards form a valid Pure Sequence (same suit, sequential rank)
     */
    fun isPureSequence(cards: List<Card>): Boolean {
        if (cards.size < 3) return false
        val suit = cards[0].suit
        if (!cards.all { it.suit == suit }) return false
        val sortedRanks = cards.map { it.rank.value }.sorted()
        for (i in 0 until sortedRanks.size - 1) {
            if (sortedRanks[i + 1] - sortedRanks[i] != 1) {
                return false
            }
        }
        return true
    }

    /**
     * Checks if 2 cards form a Dublee (2 identical cards)
     */
    fun isDublee(c1: Card, c2: Card): Boolean {
        return c1.suit == c2.suit && c1.rank == c2.rank
    }

    /**
     * Checks if player is eligible to open Tiplu (must have at least 3 pure sequences OR 7 dublees)
     */
    fun canOpenTiplu(melds: MarriageMelds): Boolean {
        val sequenceCount = melds.pureSequences.size + melds.tunnelas.size
        val dubleeCount = melds.dublees.size
        return sequenceCount >= 3 || dubleeCount >= 7
    }

    /**
     * Deal 21 cards to each of the players from the 3-deck Marriage shoe
     */
    fun deal21Cards(playerCount: Int = 4, secureRandom: SecureRandom = SecureRandom()): Pair<List<List<Card>>, Card> {
        val deck = DeckFactory.shuffleDeck(DeckFactory.createMarriageDeck(), secureRandom)
        val hands = (0 until playerCount).map { p ->
            deck.subList(p * 21, (p + 1) * 21).sorted()
        }
        // Cut card for Tiplu
        val tipluCard = deck[playerCount * 21]
        return Pair(hands, tipluCard)
    }

    /**
     * Calculates Maal points for a player given the Tiplu card
     */
    fun calculateMaalPoints(hand: List<Card>, tiplu: Card): Int {
        var points = 0
        hand.forEach { card ->
            if (card.suit == tiplu.suit && card.rank == tiplu.rank) {
                points += 3 // Tiplu is worth 3 points
            } else if (card.suit == tiplu.suit && card.rank.value == tiplu.rank.value + 1) {
                points += 2 // Poplu
            } else if (card.suit == tiplu.suit && card.rank.value == tiplu.rank.value - 1) {
                points += 2 // Jhiplu
            } else if (card.rank == tiplu.rank && card.suit != tiplu.suit) {
                points += 5 // Alter Tiplu
            }
        }
        return points
    }
}
