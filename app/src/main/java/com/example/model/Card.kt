package com.example.model

import java.security.SecureRandom

enum class Suit(val symbol: String, val isRed: Boolean, val suitName: String, val nepaliName: String) {
    SPADES("♠", false, "Spades", "हुकुम (Hukum)"),
    HEARTS("♥", true, "Hearts", "पान (Paan)"),
    CLUBS("♣", false, "Clubs", "चिडी (Chidi)"),
    DIAMONDS("♦", true, "Diamonds", "ईंट (Eent)")
}

enum class Rank(val value: Int, val display: String, val nepaliDisplay: String) {
    TWO(2, "2", "२"),
    THREE(3, "3", "३"),
    FOUR(4, "4", "४"),
    FIVE(5, "5", "५"),
    SIX(6, "6", "६"),
    SEVEN(7, "7", "७"),
    EIGHT(8, "8", "८"),
    NINE(9, "9", "९"),
    TEN(10, "10", "१०"),
    JACK(11, "J", "गुलाम (J)"),
    QUEEN(12, "Q", "मिस (Q)"),
    KING(13, "K", "बास्सा (K)"),
    ACE(14, "A", "एक्का (A)")
}

data class Card(
    val id: String,
    val suit: Suit,
    val rank: Rank,
    val isJoker: Boolean = false,
    val isTiplu: Boolean = false, // Marriage card concept
    val isMal: Boolean = false    // Marriage point-scoring card
) : Comparable<Card> {
    override fun compareTo(other: Card): Int {
        return if (this.suit == other.suit) {
            this.rank.value.compareTo(other.rank.value)
        } else {
            this.suit.ordinal.compareTo(other.suit.ordinal)
        }
    }

    val displayString: String
        get() = "${rank.display}${suit.symbol}"
}

object DeckFactory {
    fun createStandard52Deck(): List<Card> {
        val deck = mutableListOf<Card>()
        for (suit in Suit.values()) {
            for (rank in Rank.values()) {
                deck.add(Card(id = "${rank.name}_${suit.name}", suit = suit, rank = rank))
            }
        }
        return deck
    }

    /**
     * Marriage uses 3 standard decks (156 cards) + jokers.
     */
    fun createMarriageDeck(): List<Card> {
        val deck = mutableListOf<Card>()
        for (deckIndex in 1..3) {
            for (suit in Suit.values()) {
                for (rank in Rank.values()) {
                    deck.add(Card(id = "M_${deckIndex}_${rank.name}_${suit.name}", suit = suit, rank = rank))
                }
            }
        }
        return deck
    }

    fun shuffleDeck(deck: List<Card>, secureRandom: SecureRandom = SecureRandom()): List<Card> {
        val list = deck.toMutableList()
        for (i in list.indices.reversed()) {
            val j = secureRandom.nextInt(i + 1)
            val temp = list[i]
            list[i] = list[j]
            list[j] = temp
        }
        return list
    }
}
