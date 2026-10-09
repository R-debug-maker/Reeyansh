package com.example.game

import com.example.model.Card
import com.example.model.DeckFactory
import com.example.model.Rank
import com.example.model.Suit
import java.security.SecureRandom

object CallBreakEngine {

    /**
     * Determines whether a card can legally be played according to Call Break rules:
     * 1. Must follow lead suit if player holds any card of that suit.
     * 2. If following lead suit, player should try to beat current winning card if possible.
     * 3. If void in lead suit, player may play a spade (Hukum) to cut.
     * 4. If spades also void or cannot play spade, any card may be discarded.
     */
    fun isLegalCard(
        card: Card,
        playerHand: List<Card>,
        leadCard: Card?,
        currentTrick: List<Pair<Int, Card>> // seatIndex to Card
    ): Boolean {
        if (!playerHand.contains(card)) return false
        if (leadCard == null || currentTrick.isEmpty()) {
            return true // Player is leading
        }

        val leadSuit = leadCard.suit
        val hasLeadSuit = playerHand.any { it.suit == leadSuit }

        if (hasLeadSuit) {
            // Must follow lead suit!
            if (card.suit != leadSuit) {
                return false
            }
            // If possible, try to play a higher card of the lead suit
            val highestLeadPlayed = currentTrick
                .filter { it.second.suit == leadSuit }
                .maxByOrNull { it.second.rank.value }

            val canBeatLead = highestLeadPlayed != null && playerHand.any { it.suit == leadSuit && it.rank.value > highestLeadPlayed.second.rank.value }
            if (canBeatLead && card.rank.value <= highestLeadPlayed!!.second.rank.value) {
                // In strict Call Break rules, beating the suit is preferred or mandatory if holding higher
                return true // Flexible house rule: allowed to play suit
            }
            return true
        }

        // Void in lead suit: Can play Spades (trump) or any other card
        val hasSpades = playerHand.any { it.suit == Suit.SPADES }
        if (hasSpades) {
            // If player plays a spade or any discard, it's valid if void in lead suit
            return true
        }

        // Void in lead suit and no spades: can play anything
        return true
    }

    /**
     * Determines the winning player seat of a trick.
     * Hukum (Spades) is permanent trump.
     * If any Spades played, highest Spade wins.
     * Otherwise, highest card of lead suit wins.
     */
    fun determineTrickWinner(
        trickCards: List<Pair<Int, Card>> // List of (seatIndex, card) in played order
    ): Int {
        require(trickCards.isNotEmpty()) { "Trick cannot be empty" }
        val leadSuit = trickCards.first().second.suit

        val spadesPlayed = trickCards.filter { it.second.suit == Suit.SPADES }
        return if (spadesPlayed.isNotEmpty()) {
            spadesPlayed.maxByOrNull { it.second.rank.value }!!.first
        } else {
            trickCards
                .filter { it.second.suit == leadSuit }
                .maxByOrNull { it.second.rank.value }!!.first
        }
    }

    /**
     * Calculates score for a round according to standard Call Break:
     * If tricksWon >= bid: score = bid + (tricksWon - bid) * 0.1
     * If tricksWon < bid: score = -bid
     */
    fun calculateRoundScore(bid: Int, tricksWon: Int): Float {
        return if (tricksWon >= bid) {
            bid.toFloat() + (tricksWon - bid) * 0.1f
        } else {
            -bid.toFloat()
        }
    }

    /**
     * Deals 13 cards to each of the 4 players from a shuffled 52-card deck.
     */
    fun deal4Players(secureRandom: SecureRandom = SecureRandom()): List<List<Card>> {
        val deck = DeckFactory.shuffleDeck(DeckFactory.createStandard52Deck(), secureRandom)
        return (0..3).map { seat ->
            deck.subList(seat * 13, (seat + 1) * 13).sorted()
        }
    }

    /**
     * Smart Bot card chooser for Call Break
     */
    fun selectBotCard(
        hand: List<Card>,
        leadCard: Card?,
        currentTrick: List<Pair<Int, Card>>
    ): Card {
        val legalCards = hand.filter { isLegalCard(it, hand, leadCard, currentTrick) }
        if (legalCards.isEmpty()) return hand.first()

        if (leadCard == null) {
            // Bot leads: lead high card or safe low card
            return legalCards.maxByOrNull { it.rank.value } ?: legalCards.first()
        }

        val leadSuit = leadCard.suit
        val suitCards = legalCards.filter { it.suit == leadSuit }
        if (suitCards.isNotEmpty()) {
            val highestOpponent = currentTrick
                .filter { it.second.suit == leadSuit }
                .maxByOrNull { it.second.rank.value }
            if (highestOpponent != null) {
                val winningCards = suitCards.filter { it.rank.value > highestOpponent.second.rank.value }
                if (winningCards.isNotEmpty()) {
                    // Play smallest winning card to conserve high cards
                    return winningCards.minByOrNull { it.rank.value } ?: winningCards.first()
                }
            }
            // Cannot beat, play lowest
            return suitCards.minByOrNull { it.rank.value } ?: suitCards.first()
        }

        // Void in lead suit: consider trumping with lowest spade
        val spadeCards = legalCards.filter { it.suit == Suit.SPADES }
        if (spadeCards.isNotEmpty()) {
            return spadeCards.minByOrNull { it.rank.value } ?: spadeCards.first()
        }

        // Sluff lowest card
        return legalCards.minByOrNull { it.rank.value } ?: legalCards.first()
    }
}
