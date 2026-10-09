package com.example

import com.example.game.CallBreakEngine
import com.example.game.KittyEngine
import com.example.game.KittySets
import com.example.game.LudoColor
import com.example.game.LudoEngine
import com.example.game.LudoState
import com.example.game.LudoToken
import com.example.game.MarriageEngine
import com.example.game.TeenPattiEngine
import com.example.game.TeenPattiHandRank
import com.example.model.Card
import com.example.model.DeckFactory
import com.example.model.Rank
import com.example.model.Suit
import com.example.model.TransactionType
import com.example.repository.WalletRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class RoyalTaasGameEnginesTest {

    @Test
    fun testCallBreakTrickWinner_SpadeTrumpsLeadSuit() {
        // Player 0 leads King of Hearts
        // Player 1 plays 5 of Spades (trump)
        // Player 2 plays Ace of Hearts
        // Player 3 plays 2 of Hearts
        val trick = listOf(
            Pair(0, Card("KH", Suit.HEARTS, Rank.KING)),
            Pair(1, Card("5S", Suit.SPADES, Rank.FIVE)),
            Pair(2, Card("AH", Suit.HEARTS, Rank.ACE)),
            Pair(3, Card("2H", Suit.HEARTS, Rank.TWO))
        )
        val winnerSeat = CallBreakEngine.determineTrickWinner(trick)
        assertEquals("Spade trump must defeat Ace of Hearts", 1, winnerSeat)
    }

    @Test
    fun testCallBreakTrickWinner_HighestLeadSuitWinsWhenNoSpade() {
        val trick = listOf(
            Pair(0, Card("10D", Suit.DIAMONDS, Rank.TEN)),
            Pair(1, Card("KD", Suit.DIAMONDS, Rank.KING)),
            Pair(2, Card("QD", Suit.DIAMONDS, Rank.QUEEN)),
            Pair(3, Card("AD", Suit.DIAMONDS, Rank.ACE))
        )
        val winnerSeat = CallBreakEngine.determineTrickWinner(trick)
        assertEquals("Ace of Diamonds must win trick", 3, winnerSeat)
    }

    @Test
    fun testCallBreakScoring() {
        // 4 bid, 5 won -> 4.1
        val scoreMade = CallBreakEngine.calculateRoundScore(bid = 4, tricksWon = 5)
        assertEquals(4.1f, scoreMade, 0.01f)

        // 4 bid, 3 won -> -4.0
        val scoreFailed = CallBreakEngine.calculateRoundScore(bid = 4, tricksWon = 3)
        assertEquals(-4.0f, scoreFailed, 0.01f)
    }

    @Test
    fun testTeenPattiHandRanking_TrailBeatsPureSequence() {
        val trail = listOf(
            Card("AH", Suit.HEARTS, Rank.ACE),
            Card("AS", Suit.SPADES, Rank.ACE),
            Card("AC", Suit.CLUBS, Rank.ACE)
        )
        val pureSequence = listOf(
            Card("KD", Suit.DIAMONDS, Rank.KING),
            Card("QD", Suit.DIAMONDS, Rank.QUEEN),
            Card("JD", Suit.DIAMONDS, Rank.JACK)
        )

        val evalTrail = TeenPattiEngine.evaluateHand(trail)
        val evalPure = TeenPattiEngine.evaluateHand(pureSequence)

        assertEquals(TeenPattiHandRank.TRAIL, evalTrail.rankType)
        assertEquals(TeenPattiHandRank.PURE_SEQUENCE, evalPure.rankType)
        assertTrue(evalTrail > evalPure)
    }

    @Test
    fun testKittyMatchShowdown() {
        val p1Sets = KittySets(
            set1 = listOf(Card("AH", Suit.HEARTS, Rank.ACE), Card("AS", Suit.SPADES, Rank.ACE), Card("AC", Suit.CLUBS, Rank.ACE)), // Trail
            set2 = listOf(Card("KH", Suit.HEARTS, Rank.KING), Card("QH", Suit.HEARTS, Rank.QUEEN), Card("JH", Suit.HEARTS, Rank.JACK)), // Pure Seq
            set3 = listOf(Card("9D", Suit.DIAMONDS, Rank.NINE), Card("8D", Suit.DIAMONDS, Rank.EIGHT), Card("7D", Suit.DIAMONDS, Rank.SEVEN))
        )
        val p2Sets = KittySets(
            set1 = listOf(Card("2H", Suit.HEARTS, Rank.TWO), Card("3D", Suit.DIAMONDS, Rank.THREE), Card("7S", Suit.SPADES, Rank.SEVEN)),
            set2 = listOf(Card("5H", Suit.HEARTS, Rank.FIVE), Card("5D", Suit.DIAMONDS, Rank.FIVE), Card("2C", Suit.CLUBS, Rank.TWO)),
            set3 = listOf(Card("AD", Suit.DIAMONDS, Rank.ACE), Card("KD", Suit.DIAMONDS, Rank.KING), Card("QD", Suit.DIAMONDS, Rank.QUEEN))
        )

        val result = KittyEngine.evaluateMatch(mapOf(0 to p1Sets, 1 to p2Sets))
        assertEquals("Player 0 must win overall Kitty match", 0, result.overallWinnerSeat)
    }

    @Test
    fun testMarriageTunnelaAndPureSequence() {
        val tunnela = listOf(
            Card("1", Suit.HEARTS, Rank.TEN),
            Card("2", Suit.HEARTS, Rank.TEN),
            Card("3", Suit.HEARTS, Rank.TEN)
        )
        assertTrue("3 identical cards must be Tunnela", MarriageEngine.isTunnela(tunnela))

        val seq = listOf(
            Card("1", Suit.SPADES, Rank.SEVEN),
            Card("2", Suit.SPADES, Rank.EIGHT),
            Card("3", Suit.SPADES, Rank.NINE)
        )
        assertTrue("7-8-9 of Spades must be Pure Sequence", MarriageEngine.isPureSequence(seq))
    }

    @Test
    fun testLudoMoveOutOfYard() {
        val token = LudoToken(id = 0, color = LudoColor.RED, position = -1)
        assertFalse(LudoEngine.canMoveToken(token, 5))
        assertTrue(LudoEngine.canMoveToken(token, 6))

        val state = LudoState(diceValue = 6)
        val newState = LudoEngine.moveToken(state, token)
        val movedToken = newState.tokens[LudoColor.RED]?.find { it.id == 0 }
        assertNotNull(movedToken)
        assertEquals(0, movedToken?.position)
    }

    @Test
    fun testWalletLedgerInvariantAndIdempotency() {
        val walletRepo = WalletRepository()
        val initialBalance = walletRepo.walletState.value.balance

        val res1 = walletRepo.recordTransaction(
            type = TransactionType.TABLE_BUY_IN,
            amount = 100L,
            idempotencyKey = "TEST_TX_001",
            source = "USER",
            destination = "TABLE",
            notes = "Test buy in"
        )
        assertTrue(res1.isSuccess)
        assertEquals(initialBalance - 100L, walletRepo.walletState.value.balance)

        // Duplicate idempotency key must not double-debit
        val res2 = walletRepo.recordTransaction(
            type = TransactionType.TABLE_BUY_IN,
            amount = 100L,
            idempotencyKey = "TEST_TX_001",
            source = "USER",
            destination = "TABLE",
            notes = "Duplicate test"
        )
        assertEquals(initialBalance - 100L, walletRepo.walletState.value.balance)
    }

    @Test
    fun testPublicLobbyRoomsAvailabilityForAllSixGames() {
        val gameRepo = com.example.repository.GameRepository()
        val rooms = gameRepo.rooms.value.filter { it.roomType == com.example.model.RoomType.PUBLIC }

        val sixGames = listOf(
            com.example.model.GameType.CALL_BREAK,
            com.example.model.GameType.MARRIAGE_21,
            com.example.model.GameType.TEEN_PATTI,
            com.example.model.GameType.KITTY,
            com.example.model.GameType.LUDO,
            com.example.model.GameType.CHESS
        )

        for (game in sixGames) {
            val gameRooms = rooms.filter { it.gameType == game }
            assertTrue("Expected public tables for ${game.title}", gameRooms.isNotEmpty())
            for (room in gameRooms) {
                assertTrue("Player count must be within capacity", room.seats.size in 1..room.maxPlayers)
                assertTrue("Room status must be valid", room.status == com.example.model.RoomStatus.WAITING || room.status == com.example.model.RoomStatus.PLAYING)
            }
        }
    }
}
