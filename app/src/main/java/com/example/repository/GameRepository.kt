package com.example.repository

import com.example.game.CallBreakEngine
import com.example.game.ChessEngine
import com.example.game.ChessState
import com.example.game.KittyEngine
import com.example.game.LudoEngine
import com.example.game.LudoState
import com.example.game.MarriageEngine
import com.example.game.SnakeLadderEngine
import com.example.game.TeenPattiEngine
import com.example.model.Card
import com.example.model.DeckFactory
import com.example.model.GameRoom
import com.example.model.GameType
import com.example.model.HostActionType
import com.example.model.HostAuditEntry
import com.example.model.PlayerSeat
import com.example.model.PlayerStatus
import com.example.model.RoomStatus
import com.example.model.RoomType
import com.example.model.RulePreset
import com.example.model.UserProfile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.security.MessageDigest
import java.util.UUID
import kotlin.random.Random

class GameRepository {

    private val _rooms = MutableStateFlow<List<GameRoom>>(emptyList())
    val rooms: StateFlow<List<GameRoom>> = _rooms.asStateFlow()

    private val _currentActiveRoom = MutableStateFlow<GameRoom?>(null)
    val currentActiveRoom: StateFlow<GameRoom?> = _currentActiveRoom.asStateFlow()

    private val _auditLogs = MutableStateFlow<List<HostAuditEntry>>(emptyList())
    val auditLogs: StateFlow<List<HostAuditEntry>> = _auditLogs.asStateFlow()

    // Board game states
    private val _ludoState = MutableStateFlow(LudoState())
    val ludoState: StateFlow<LudoState> = _ludoState.asStateFlow()

    private val _chessState = MutableStateFlow(ChessState(board = ChessEngine.createInitialBoard()))
    val chessState: StateFlow<ChessState> = _chessState.asStateFlow()

    private val _snakeLadderPositions = MutableStateFlow<Map<Int, Int>>(mapOf(0 to 1, 1 to 1, 2 to 1, 3 to 1))
    val snakeLadderPositions: StateFlow<Map<Int, Int>> = _snakeLadderPositions.asStateFlow()

    init {
        // Pre-populate realistic active rooms in Public Lobby for all 6 games
        val initialRooms = listOf(
            createPresetRoom(
                id = "ROOM_PUB_01",
                name = "Everest Call Break Club",
                code = "RT-1088",
                game = GameType.CALL_BREAK,
                roomType = RoomType.PUBLIC,
                host = UserProfile("HOST_01", "Bikash_Hukum", "+977-9801122334", "👑", PlayerStatus.IN_GAME, 12000L),
                entryCoins = 100L,
                initialPlayerCount = 3,
                initialStatus = RoomStatus.WAITING
            ),
            createPresetRoom(
                id = "ROOM_PUB_02",
                name = "Kathmandu Marriage 21 Lounge",
                code = "RT-7711",
                game = GameType.MARRIAGE_21,
                roomType = RoomType.PUBLIC,
                host = UserProfile("HOST_03", "Rajan_Taas", "+977-9812445566", "🃏", PlayerStatus.IN_GAME, 18000L),
                entryCoins = 300L,
                initialPlayerCount = 2,
                initialStatus = RoomStatus.WAITING
            ),
            createPresetRoom(
                id = "ROOM_PUB_03",
                name = "Royal Teen Patti High Rollers",
                code = "RT-3392",
                game = GameType.TEEN_PATTI,
                roomType = RoomType.PUBLIC,
                host = UserProfile("HOST_02", "Pooja_Sharma", "+977-9841998877", "💎", PlayerStatus.IN_GAME, 25000L),
                entryCoins = 200L,
                initialPlayerCount = 4,
                initialStatus = RoomStatus.PLAYING
            ),
            createPresetRoom(
                id = "ROOM_PUB_04",
                name = "Pokhara Kitty Masters",
                code = "RT-4420",
                game = GameType.KITTY,
                roomType = RoomType.PUBLIC,
                host = UserProfile("HOST_04", "Sunil_Kitty", "+977-9851001122", "🐯", PlayerStatus.IN_GAME, 15000L),
                entryCoins = 150L,
                initialPlayerCount = 2,
                initialStatus = RoomStatus.WAITING
            ),
            createPresetRoom(
                id = "ROOM_PUB_05",
                name = "Royal Ludo Palace",
                code = "RT-9901",
                game = GameType.LUDO,
                roomType = RoomType.PUBLIC,
                host = UserProfile("HOST_05", "Anita_Rani", "+977-9861334455", "🎲", PlayerStatus.IN_GAME, 8000L),
                entryCoins = 100L,
                initialPlayerCount = 3,
                initialStatus = RoomStatus.WAITING
            ),
            createPresetRoom(
                id = "ROOM_PUB_06",
                name = "Grandmaster Chess Arena",
                code = "RT-2045",
                game = GameType.CHESS,
                roomType = RoomType.PUBLIC,
                host = UserProfile("HOST_06", "Suman_Grandmaster", "+977-9841556677", "♟️", PlayerStatus.IN_GAME, 30000L),
                entryCoins = 250L,
                initialPlayerCount = 1,
                initialStatus = RoomStatus.WAITING
            ),
            createPresetRoom(
                id = "ROOM_PUB_07",
                name = "Hukum Champions League",
                code = "RT-5512",
                game = GameType.CALL_BREAK,
                roomType = RoomType.PUBLIC,
                host = UserProfile("HOST_07", "Dipen_Spade", "+977-9811889900", "♠️", PlayerStatus.IN_GAME, 14000L),
                entryCoins = 200L,
                initialPlayerCount = 4,
                initialStatus = RoomStatus.PLAYING
            ),
            createPresetRoom(
                id = "ROOM_PUB_08",
                name = "Patan Dublee 21 Salon",
                code = "RT-8823",
                game = GameType.MARRIAGE_21,
                roomType = RoomType.PUBLIC,
                host = UserProfile("HOST_08", "Manish_Taas", "+977-9822334455", "💍", PlayerStatus.IN_GAME, 22000L),
                entryCoins = 300L,
                initialPlayerCount = 3,
                initialStatus = RoomStatus.WAITING
            ),
            createPresetRoom(
                id = "ROOM_PUB_09",
                name = "Chaal & Blind Diamond Table",
                code = "RT-6677",
                game = GameType.TEEN_PATTI,
                roomType = RoomType.PUBLIC,
                host = UserProfile("HOST_09", "Kiran_Gold", "+977-9833445566", "✨", PlayerStatus.IN_GAME, 19000L),
                entryCoins = 100L,
                initialPlayerCount = 2,
                initialStatus = RoomStatus.WAITING
            )
        )
        _rooms.value = initialRooms
    }

    private fun createPresetRoom(
        id: String,
        name: String,
        code: String,
        game: GameType,
        roomType: RoomType,
        host: UserProfile,
        entryCoins: Long,
        initialPlayerCount: Int = 1,
        initialStatus: RoomStatus = RoomStatus.WAITING
    ): GameRoom {
        val opponentNames = listOf("Aarav_Ace", "Pooja_Rani", "Bikram_Pro", "Sita_Diamond", "Kiran_Gold")
        val opponentAvatars = listOf("💎", "🌸", "🦁", "⚡", "🦅")

        val seats = mutableListOf<PlayerSeat>()
        seats.add(
            PlayerSeat(
                seatIndex = 0,
                player = host,
                isHost = true,
                isReady = true,
                isMicOn = true
            )
        )

        for (i in 1 until initialPlayerCount.coerceAtMost(game.maxPlayers)) {
            val opp = UserProfile(
                id = "SEED_P_${i}_${id}",
                displayName = opponentNames[(i - 1) % opponentNames.size],
                phoneNumber = "+977-981000$i$i",
                avatarEmoji = opponentAvatars[(i - 1) % opponentAvatars.size],
                status = PlayerStatus.IN_GAME,
                practiceCoins = 10000L
            )
            seats.add(
                PlayerSeat(
                    seatIndex = i,
                    player = opp,
                    isHost = false,
                    isReady = true,
                    isMicOn = (i % 2 == 0)
                )
            )
        }

        return GameRoom(
            id = id,
            name = name,
            roomCode = code,
            gameType = game,
            roomType = roomType,
            hostId = host.id,
            hostName = host.displayName,
            maxPlayers = game.maxPlayers,
            minPlayers = game.minPlayers,
            entryCoins = entryCoins,
            status = initialStatus,
            seats = seats
        )
    }

    fun createRoom(
        name: String,
        gameType: GameType,
        roomType: RoomType,
        host: UserProfile,
        entryCoins: Long,
        rulePreset: RulePreset = RulePreset()
    ): GameRoom {
        val roomId = "ROOM_" + UUID.randomUUID().toString().take(8).uppercase()
        val roomCode = "RT-" + Random.nextInt(1000, 9999)

        val hostSeat = PlayerSeat(
            seatIndex = 0,
            player = host,
            isHost = true,
            isReady = true
        )

        // If Solo mode, instantly create computer bot seats
        val seats = if (roomType == RoomType.SOLO) {
            val botNames = listOf("Royal Bot Alpha", "Taas Bot Guru", "Hukum Bot Pro", "Chidi Bot")
            val botEmojis = listOf("🤖", "🧙", "🦁", "⚡")
            val botSeats = (1 until gameType.maxPlayers).map { idx ->
                val botUser = UserProfile(
                    id = "BOT_$idx",
                    displayName = botNames[(idx - 1) % botNames.size],
                    phoneNumber = "+977-BOT-00$idx",
                    avatarEmoji = botEmojis[(idx - 1) % botEmojis.size],
                    status = PlayerStatus.IN_GAME,
                    practiceCoins = 10000L
                )
                PlayerSeat(
                    seatIndex = idx,
                    player = botUser,
                    isHost = false,
                    isBot = true,
                    isReady = true
                )
            }
            listOf(hostSeat) + botSeats
        } else {
            listOf(hostSeat)
        }

        val fairHash = generateDeckHash(roomId)
        val room = GameRoom(
            id = roomId,
            name = name,
            roomCode = roomCode,
            gameType = gameType,
            roomType = roomType,
            hostId = host.id,
            hostName = host.displayName,
            maxPlayers = gameType.maxPlayers,
            minPlayers = gameType.minPlayers,
            entryCoins = entryCoins,
            status = if (roomType == RoomType.SOLO) RoomStatus.PLAYING else RoomStatus.WAITING,
            rulePreset = rulePreset,
            seats = seats,
            fairShuffleHash = fairHash
        )

        _rooms.value = listOf(room) + _rooms.value
        _currentActiveRoom.value = room

        recordAuditLog(
            roomId = roomId,
            hostId = host.id,
            hostName = host.displayName,
            action = HostActionType.ROOM_CREATED,
            desc = "Created ${roomType.name} table '$name' for ${gameType.title} with entry $entryCoins coins",
            hash = fairHash
        )

        if (roomType == RoomType.SOLO) {
            startMatch(roomId, host.id)
        }

        return room
    }

    fun joinRoom(room: GameRoom, user: UserProfile): GameRoom {
        val existingSeat = room.seats.find { it.player.id == user.id }
        if (existingSeat != null) {
            _currentActiveRoom.value = room
            return room
        }

        if (room.seats.size >= room.maxPlayers) {
            // Room is full, spectate
            _currentActiveRoom.value = room.copy(spectatorCount = room.spectatorCount + 1)
            return _currentActiveRoom.value!!
        }

        val newSeat = PlayerSeat(
            seatIndex = room.seats.size,
            player = user,
            isHost = false,
            isReady = true
        )
        val updatedSeats = room.seats + newSeat
        val updatedRoom = room.copy(seats = updatedSeats)

        updateRoomState(updatedRoom)
        _currentActiveRoom.value = updatedRoom
        return updatedRoom
    }

    fun startMatch(roomId: String, callerId: String): Boolean {
        val room = _rooms.value.find { it.id == roomId } ?: _currentActiveRoom.value ?: return false
        if (room.hostId != callerId && room.roomType != RoomType.SOLO) return false

        // Fill remaining empty seats with bots if needed to reach minPlayers
        val currentSeats = room.seats.toMutableList()
        val needed = room.minPlayers - currentSeats.size
        if (needed > 0) {
            val botNames = listOf("Sagarmatha Bot", "Lumbini Bot", "Gorkha Bot", "Mustang Bot")
            val botAvatars = listOf("🦅", "🔥", "🛡️", "🏔️")
            for (i in 0 until needed) {
                val idx = currentSeats.size
                currentSeats.add(
                    PlayerSeat(
                        seatIndex = idx,
                        player = UserProfile(
                            id = "BOT_$idx",
                            displayName = botNames[i % botNames.size],
                            phoneNumber = "+977-BOT-0$idx",
                            avatarEmoji = botAvatars[i % botAvatars.size],
                            practiceCoins = 10000L
                        ),
                        isHost = false,
                        isBot = true,
                        isReady = true
                    )
                )
            }
        }

        // Deal cards based on game type
        val dealtSeats = when (room.gameType) {
            GameType.CALL_BREAK -> {
                val hands = CallBreakEngine.deal4Players()
                currentSeats.mapIndexed { idx, seat ->
                    seat.copy(
                        hand = hands.getOrElse(idx) { emptyList() },
                        currentBid = if (seat.isBot) Random.nextInt(2, 5) else 3,
                        tricksWon = 0,
                        hasTurn = idx == 0
                    )
                }
            }
            GameType.TEEN_PATTI -> {
                val deck = DeckFactory.shuffleDeck(DeckFactory.createStandard52Deck())
                currentSeats.mapIndexed { idx, seat ->
                    seat.copy(
                        hand = deck.subList(idx * 3, (idx + 1) * 3).sorted(),
                        currentBet = room.rulePreset.bootAmount,
                        hasTurn = idx == 0
                    )
                }
            }
            GameType.KITTY -> {
                val deck = DeckFactory.shuffleDeck(DeckFactory.createStandard52Deck())
                currentSeats.mapIndexed { idx, seat ->
                    seat.copy(
                        hand = deck.subList(idx * 9, (idx + 1) * 9).sorted(),
                        hasTurn = idx == 0
                    )
                }
            }
            GameType.MARRIAGE_21 -> {
                val (hands, _) = MarriageEngine.deal21Cards(playerCount = currentSeats.size)
                currentSeats.mapIndexed { idx, seat ->
                    seat.copy(
                        hand = hands.getOrElse(idx) { emptyList() },
                        hasTurn = idx == 0
                    )
                }
            }
            else -> {
                currentSeats.mapIndexed { idx, seat -> seat.copy(hasTurn = idx == 0) }
            }
        }

        val startedRoom = room.copy(
            status = RoomStatus.PLAYING,
            seats = dealtSeats,
            potAmount = if (room.gameType == GameType.TEEN_PATTI) dealtSeats.size * room.rulePreset.bootAmount else room.entryCoins * dealtSeats.size,
            currentTurnSeat = 0,
            currentTrickCards = emptyList()
        )

        updateRoomState(startedRoom)
        _currentActiveRoom.value = startedRoom

        recordAuditLog(
            roomId = roomId,
            hostId = room.hostId,
            hostName = room.hostName,
            action = HostActionType.MATCH_STARTED,
            desc = "Match started for ${room.gameType.title}. Dealt authoritative deck to ${dealtSeats.size} seated players.",
            hash = startedRoom.fairShuffleHash
        )
        return true
    }

    fun playCallBreakCard(seatIndex: Int, card: Card): GameRoom? {
        val room = _currentActiveRoom.value ?: return null
        if (room.gameType != GameType.CALL_BREAK) return null
        val seat = room.seats.find { it.seatIndex == seatIndex } ?: return null
        if (!seat.hand.contains(card)) return null

        val currentTrick = room.currentTrickCards.toMutableList()
        currentTrick.add(Pair(seatIndex, card))

        val updatedHand = seat.hand.filter { it.id != card.id }
        var updatedSeats = room.seats.map {
            if (it.seatIndex == seatIndex) it.copy(hand = updatedHand, selectedPlayCard = null) else it
        }

        if (currentTrick.size == 4) {
            // Trick complete!
            val trickWinnerSeat = CallBreakEngine.determineTrickWinner(currentTrick)
            updatedSeats = updatedSeats.map {
                if (it.seatIndex == trickWinnerSeat) {
                    it.copy(tricksWon = it.tricksWon + 1)
                } else it
            }

            // Check if 13 tricks complete
            val roundDone = updatedSeats[0].hand.isEmpty()
            val nextStatus = if (roundDone) RoomStatus.ROUND_OVER else RoomStatus.PLAYING

            if (roundDone) {
                // Calculate round scores
                updatedSeats = updatedSeats.map {
                    val rScore = CallBreakEngine.calculateRoundScore(it.currentBid, it.tricksWon)
                    it.copy(
                        roundScore = rScore,
                        totalScore = it.totalScore + rScore
                    )
                }
            }

            val updatedRoom = room.copy(
                seats = updatedSeats,
                currentTrickCards = emptyList(),
                currentTurnSeat = trickWinnerSeat,
                status = nextStatus
            )
            updateRoomState(updatedRoom)
            _currentActiveRoom.value = updatedRoom
            return updatedRoom
        } else {
            // Next player's turn
            val nextTurn = (seatIndex + 1) % 4
            val updatedRoom = room.copy(
                seats = updatedSeats,
                currentTrickCards = currentTrick,
                currentTurnSeat = nextTurn
            )
            updateRoomState(updatedRoom)
            _currentActiveRoom.value = updatedRoom

            // If next turn is a bot, play automatically
            val nextSeat = updatedSeats.find { it.seatIndex == nextTurn }
            if (nextSeat != null && nextSeat.isBot && nextSeat.hand.isNotEmpty()) {
                val leadCard = currentTrick.firstOrNull()?.second
                val botCard = CallBreakEngine.selectBotCard(nextSeat.hand, leadCard, currentTrick)
                return playCallBreakCard(nextTurn, botCard)
            }
            return updatedRoom
        }
    }

    fun makeTeenPattiAction(seatIndex: Int, action: String, amount: Long = 10L): GameRoom? {
        val room = _currentActiveRoom.value ?: return null
        if (room.gameType != GameType.TEEN_PATTI) return null

        var updatedSeats = room.seats
        var pot = room.potAmount

        when (action) {
            "FOLD" -> {
                updatedSeats = updatedSeats.map {
                    if (it.seatIndex == seatIndex) it.copy(isFolded = true) else it
                }
            }
            "CHAAL", "BET" -> {
                pot += amount
                updatedSeats = updatedSeats.map {
                    if (it.seatIndex == seatIndex) it.copy(currentBet = it.currentBet + amount) else it
                }
            }
            "SEEN" -> {
                updatedSeats = updatedSeats.map {
                    if (it.seatIndex == seatIndex) it.copy(isSeen = true) else it
                }
            }
            "SHOW" -> {
                // Determine winner
                val activeHands = updatedSeats.filter { !it.isFolded }.associate { it.seatIndex to it.hand }
                if (activeHands.isNotEmpty()) {
                    val winnerSeat = TeenPattiEngine.findWinner(activeHands)
                    val finishedRoom = room.copy(
                        status = RoomStatus.ROUND_OVER,
                        currentTurnSeat = winnerSeat,
                        potAmount = pot
                    )
                    updateRoomState(finishedRoom)
                    _currentActiveRoom.value = finishedRoom
                    return finishedRoom
                }
            }
        }

        val nextTurn = (seatIndex + 1) % room.seats.size
        val updatedRoom = room.copy(
            seats = updatedSeats,
            potAmount = pot,
            currentTurnSeat = nextTurn
        )
        updateRoomState(updatedRoom)
        _currentActiveRoom.value = updatedRoom
        return updatedRoom
    }

    fun rollLudoDice(): Int {
        val dice = Random.nextInt(1, 7)
        _ludoState.value = _ludoState.value.copy(diceValue = dice, hasRolled = true)
        return dice
    }

    fun moveLudoToken(tokenIndex: Int) {
        val state = _ludoState.value
        val color = state.currentTurnColor
        val token = state.tokens[color]?.getOrNull(tokenIndex) ?: return
        val newState = LudoEngine.moveToken(state, token)
        _ludoState.value = newState
    }

    fun rollSnakeLadderDice(playerIndex: Int): Pair<Int, String> {
        val dice = Random.nextInt(1, 7)
        val currentPos = _snakeLadderPositions.value[playerIndex] ?: 1
        val (newPos, message) = SnakeLadderEngine.calculateNewPosition(currentPos, dice)
        val updated = _snakeLadderPositions.value.toMutableMap()
        updated[playerIndex] = newPos
        _snakeLadderPositions.value = updated
        return Pair(dice, message)
    }

    fun selectChessSquare(r: Int, c: Int) {
        val state = _chessState.value
        if (state.selectedSquare == null) {
            val piece = state.board[r][c]
            if (piece != null && piece.color == state.currentTurn) {
                val validMoves = ChessEngine.getValidMoves(state.board, r, c)
                _chessState.value = state.copy(
                    selectedSquare = Pair(r, c),
                    validMovesForSelected = validMoves
                )
            }
        } else {
            val from = state.selectedSquare
            if (state.validMovesForSelected.contains(Pair(r, c))) {
                // Move piece
                val newBoard = state.board.map { it.clone() }.toTypedArray()
                newBoard[r][c] = newBoard[from.first][from.second]
                newBoard[from.first][from.second] = null
                val nextTurn = if (state.currentTurn == com.example.game.PieceColor.WHITE) com.example.game.PieceColor.BLACK else com.example.game.PieceColor.WHITE
                _chessState.value = state.copy(
                    board = newBoard,
                    currentTurn = nextTurn,
                    selectedSquare = null,
                    validMovesForSelected = emptyList()
                )
            } else {
                _chessState.value = state.copy(selectedSquare = null, validMovesForSelected = emptyList())
            }
        }
    }

    private fun updateRoomState(updated: GameRoom) {
        _rooms.value = _rooms.value.map { if (it.id == updated.id) updated else it }
    }

    private fun recordAuditLog(
        roomId: String,
        hostId: String,
        hostName: String,
        action: HostActionType,
        desc: String,
        hash: String?
    ) {
        val entry = HostAuditEntry(
            id = "AUDIT_" + UUID.randomUUID().toString().take(6),
            roomId = roomId,
            hostId = hostId,
            hostName = hostName,
            actionType = action,
            description = desc,
            deckVerificationHash = hash
        )
        _auditLogs.value = listOf(entry) + _auditLogs.value
    }

    private fun generateDeckHash(roomId: String): String {
        val text = "$roomId:${System.currentTimeMillis()}:${Random.nextLong()}"
        return "SHA256:" + MessageDigest.getInstance("SHA-256")
            .digest(text.toByteArray())
            .joinToString("") { "%02x".format(it) }.take(16)
    }
}
