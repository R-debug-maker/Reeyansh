package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.VolumeMute
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.game.ChessState
import com.example.game.LudoColor
import com.example.game.LudoState
import com.example.model.Card
import com.example.model.ChatMessage
import com.example.model.GameRoom
import com.example.model.GameType
import com.example.model.PlayerSeat
import com.example.model.RoomStatus
import com.example.model.UserProfile
import com.example.ui.MainViewModel
import com.example.ui.components.PlayerSeatView
import com.example.ui.components.PlayingCardView
import com.example.ui.theme.CardSuitBlack
import com.example.ui.theme.EmeraldBorder
import com.example.ui.theme.EmeraldDark
import com.example.ui.theme.EmeraldFelt
import com.example.ui.theme.EmeraldTable
import com.example.ui.theme.GoldContainer
import com.example.ui.theme.RichBurgundy
import com.example.ui.theme.RoyalGold
import com.example.ui.theme.RoyalGoldLight
import com.example.ui.theme.StatusGreen
import com.example.ui.theme.StatusRed
import com.example.ui.theme.SurfaceBorder
import com.example.ui.theme.SurfaceCard
import com.example.ui.theme.SurfaceCardElevated
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun TableGameScreen(
    viewModel: MainViewModel,
    room: GameRoom?,
    currentUser: UserProfile,
    ludoState: LudoState,
    chessState: ChessState,
    snakePositions: Map<Int, Int>,
    onBack: () -> Unit,
    onOpenChat: () -> Unit
) {
    if (room == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No active table", color = TextPrimary)
        }
        return
    }

    var selectedCard by remember { mutableStateOf<Card?>(null) }
    var lastBoardMessage by remember { mutableStateOf<String?>(null) }

    val mySeat = room.seats.find { it.player.id == currentUser.id } ?: room.seats.getOrNull(0)
    val opponents = room.seats.filter { it != mySeat }

    val isMyTurn = mySeat != null && room.currentTurnSeat == mySeat.seatIndex

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SurfaceDark)
    ) {
        // Top Table Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(SurfaceCardElevated)
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack, modifier = Modifier.size(36.dp)) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Leave Table", tint = RoyalGold)
                }
                Spacer(modifier = Modifier.width(6.dp))
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = room.name,
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "● Live",
                            color = StatusGreen,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Text(
                        text = "${room.gameType.title} • Rnd ${room.roundNumber} • Pot: 🪙 ${room.potAmount}",
                        color = RoyalGoldLight,
                        fontSize = 11.sp
                    )
                }
            }

            // Quick In-Game Voice & Chat Icons
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // Table Mic Toggle
                val voiceState by viewModel.tableVoiceState.collectAsState()
                IconButton(
                    onClick = { viewModel.toggleTableMic() },
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(if (voiceState.isMicMuted) RichBurgundy else StatusGreen)
                        .testTag("table_mic_toggle_button")
                ) {
                    Icon(
                        imageVector = if (voiceState.isMicMuted) Icons.Default.MicOff else Icons.Default.Mic,
                        contentDescription = "Mic",
                        tint = Color.White,
                        modifier = Modifier.size(17.dp)
                    )
                }

                // Table Chat Button (opens voice & text messages)
                IconButton(
                    onClick = onOpenChat,
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(GoldContainer)
                        .border(BorderStroke(1.dp, RoyalGold), CircleShape)
                        .testTag("table_chat_open_button")
                ) {
                    Icon(
                        Icons.Default.Chat,
                        contentDescription = "Chat",
                        tint = RoyalGoldLight,
                        modifier = Modifier.size(17.dp)
                    )
                }
            }
        }

        // The Emerald Felt Card Table
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(8.dp)
                .shadow(12.dp, RoundedCornerShape(24.dp))
                .clip(RoundedCornerShape(24.dp))
                .background(
                    Brush.radialGradient(
                        listOf(EmeraldTable, EmeraldFelt, EmeraldDark)
                    )
                )
                .border(BorderStroke(3.dp, EmeraldBorder), RoundedCornerShape(24.dp))
        ) {
            // Gold border inlay
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(6.dp)
                    .border(BorderStroke(1.dp, RoyalGold.copy(alpha = 0.35f)), RoundedCornerShape(18.dp))
            )

            // Table Header info inside felt
            Row(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = SurfaceDark.copy(alpha = 0.6f),
                    border = BorderStroke(0.5.dp, RoyalGold.copy(alpha = 0.5f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(text = "👑 ${room.gameType.title}", fontSize = 11.sp, color = RoyalGoldLight, fontWeight = FontWeight.Bold)
                        Text(text = "• Code: ${room.roomCode}", fontSize = 10.sp, color = TextSecondary)
                    }
                }
            }

            // Opponent Seats around the Table
            // Top Opponent (Seat 2)
            opponents.getOrNull(1)?.let { opp ->
                Box(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 34.dp)
                ) {
                    PlayerSeatView(seat = opp, isCurrentTurn = room.currentTurnSeat == opp.seatIndex)
                }
            }

            // Left Opponent (Seat 1)
            opponents.getOrNull(0)?.let { opp ->
                Box(
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .padding(start = 12.dp)
                ) {
                    PlayerSeatView(seat = opp, isCurrentTurn = room.currentTurnSeat == opp.seatIndex)
                }
            }

            // Right Opponent (Seat 3)
            opponents.getOrNull(2)?.let { opp ->
                Box(
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .padding(end = 12.dp)
                ) {
                    PlayerSeatView(seat = opp, isCurrentTurn = room.currentTurnSeat == opp.seatIndex)
                }
            }

            // Center Table Play Area
            Box(
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(horizontal = 60.dp),
                contentAlignment = Alignment.Center
            ) {
                if (room.gameType.isCardGame) {
                    // Card Game Central Trick / Showdown Area
                    CardPlayCenterArea(room = room)
                } else {
                    // Board Games (Ludo, Chess, Snake & Ladder)
                    BoardGameCenterArea(
                        gameType = room.gameType,
                        ludoState = ludoState,
                        chessState = chessState,
                        snakePositions = snakePositions,
                        onRollLudo = { viewModel.rollLudo() },
                        onMoveLudo = { viewModel.moveLudo(it) },
                        onRollSnake = {
                            viewModel.rollSnakeLadder()
                            lastBoardMessage = "Dice rolled!"
                        },
                        onSelectChess = { r, c -> viewModel.selectChess(r, c) }
                    )
                }
            }

            // Match Start or Ready Button for Host if Waiting
            if (room.status == RoomStatus.WAITING) {
                Surface(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .clip(RoundedCornerShape(14.dp)),
                    color = SurfaceDark.copy(alpha = 0.9f),
                    border = BorderStroke(1.dp, RoyalGold)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = "Waiting for match to begin...", color = TextSecondary, fontSize = 12.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = { viewModel.startCurrentMatch() },
                            colors = ButtonDefaults.buttonColors(containerColor = RoyalGold),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.testTag("start_match_button")
                        ) {
                            Text("Deal Cards & Start Match 🃏", color = SurfaceDark, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                    }
                }
            }
        }

        // Bottom User Hand & Action Controls Bar
        if (mySeat != null && room.status == RoomStatus.PLAYING) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = SurfaceCard,
                tonalElevation = 8.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    // Turn prompt & Action Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = if (isMyTurn) "🎯 Your Turn!" else "⏳ Waiting for opponent...",
                                color = if (isMyTurn) RoyalGoldLight else TextMuted,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                            if (mySeat.currentBid > 0) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Bid: ${mySeat.currentBid} (${mySeat.tricksWon} won)",
                                    color = TextSecondary,
                                    fontSize = 11.sp
                                )
                            }
                        }

                        // Action buttons based on game
                        if (room.gameType == GameType.CALL_BREAK) {
                            Button(
                                onClick = {
                                    selectedCard?.let { card ->
                                        viewModel.playCard(card)
                                        selectedCard = null
                                    }
                                },
                                enabled = isMyTurn && selectedCard != null,
                                colors = ButtonDefaults.buttonColors(containerColor = RoyalGold),
                                shape = RoundedCornerShape(10.dp),
                                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                                modifier = Modifier.testTag("play_card_button")
                            ) {
                                Text("Play Card", color = SurfaceDark, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                        } else if (room.gameType == GameType.TEEN_PATTI) {
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Button(
                                    onClick = { viewModel.teenPattiAction("FOLD") },
                                    colors = ButtonDefaults.buttonColors(containerColor = RichBurgundy),
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                                ) {
                                    Text("Fold", color = TextPrimary, fontSize = 11.sp)
                                }
                                Button(
                                    onClick = { viewModel.teenPattiAction("CHAAL", 20L) },
                                    colors = ButtonDefaults.buttonColors(containerColor = RoyalGold),
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                                ) {
                                    Text("Chaal 🪙20", color = SurfaceDark, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                }
                                Button(
                                    onClick = { viewModel.teenPattiAction("SHOW") },
                                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldTable),
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                                ) {
                                    Text("Show", color = TextPrimary, fontSize = 11.sp)
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    // Player's Cards in Hand
                    if (mySeat.hand.isNotEmpty()) {
                        val scrollState = rememberScrollState()
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(scrollState)
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            mySeat.hand.forEach { card ->
                                PlayingCardView(
                                    card = card,
                                    isSelected = selectedCard == card,
                                    onClick = {
                                        if (isMyTurn) {
                                            selectedCard = if (selectedCard == card) null else card
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CardPlayCenterArea(room: GameRoom) {
    if (room.currentTrickCards.isNotEmpty()) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(8.dp)
        ) {
            Text(
                text = "Current Trick",
                color = RoyalGoldLight,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                room.currentTrickCards.forEach { (seat, card) ->
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "P$seat",
                            color = TextMuted,
                            fontSize = 9.sp
                        )
                        PlayingCardView(card = card)
                    }
                }
            }
        }
    } else {
        // Table center pot or royal logo
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(12.dp)
        ) {
            Text(text = "👑", fontSize = 32.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Royal Table",
                color = RoyalGold.copy(alpha = 0.8f),
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Lead any card to start trick",
                color = TextSecondary,
                fontSize = 11.sp
            )
        }
    }
}

@Composable
fun BoardGameCenterArea(
    gameType: GameType,
    ludoState: LudoState,
    chessState: ChessState,
    snakePositions: Map<Int, Int>,
    onRollLudo: () -> Unit,
    onMoveLudo: (Int) -> Unit,
    onRollSnake: () -> Unit,
    onSelectChess: (Int, Int) -> Unit
) {
    when (gameType) {
        GameType.LUDO -> {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Royal Ludo — Turn: ${ludoState.currentTurnColor.name}",
                    color = RoyalGoldLight,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(6.dp))

                // Dice Roll
                Button(
                    onClick = onRollLudo,
                    colors = ButtonDefaults.buttonColors(containerColor = RoyalGold),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(
                        text = "🎲 Roll Dice: [ ${ludoState.diceValue} ]",
                        color = SurfaceDark,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Tokens Row
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    ludoState.tokens[ludoState.currentTurnColor]?.forEachIndexed { idx, token ->
                        Button(
                            onClick = { onMoveLudo(idx) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = when (token.color) {
                                    LudoColor.RED -> StatusRed
                                    LudoColor.GREEN -> StatusGreen
                                    LudoColor.YELLOW -> RoyalGold
                                    LudoColor.BLUE -> Color(0xFF2196F3)
                                }
                            ),
                            shape = CircleShape,
                            modifier = Modifier.size(36.dp),
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Text(
                                text = if (token.isInYard) "Base" else "${token.position}",
                                fontSize = 9.sp,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
        GameType.CHESS -> {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Chess — Turn: ${chessState.currentTurn}",
                    color = RoyalGoldLight,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                // 8x8 Mini Grid
                Column(
                    modifier = Modifier
                        .border(BorderStroke(1.dp, RoyalGold))
                        .padding(2.dp)
                ) {
                    for (r in 0..7) {
                        Row {
                            for (c in 0..7) {
                                val piece = chessState.board[r][c]
                                val isWhiteSq = (r + c) % 2 == 0
                                val isSelected = chessState.selectedSquare == Pair(r, c)
                                val isValidMove = chessState.validMovesForSelected.contains(Pair(r, c))

                                Box(
                                    modifier = Modifier
                                        .size(24.dp)
                                        .background(
                                            if (isSelected) RoyalGold
                                            else if (isValidMove) StatusGreen.copy(alpha = 0.5f)
                                            else if (isWhiteSq) Color(0xFFEEEED2)
                                            else Color(0xFF769656)
                                        )
                                        .clickable { onSelectChess(r, c) },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = piece?.type?.symbolWhite ?: "",
                                        fontSize = 12.sp,
                                        color = if (piece?.color == com.example.game.PieceColor.WHITE) Color.White else Color.Black
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
        GameType.SNAKE_LADDER -> {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Snake & Ladder",
                    color = RoyalGoldLight,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Player 1 at tile: ${snakePositions[0] ?: 1} / 100",
                    color = TextPrimary,
                    fontSize = 12.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = onRollSnake,
                    colors = ButtonDefaults.buttonColors(containerColor = RoyalGold),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("🎲 Roll Dice & Climb", color = SurfaceDark, fontWeight = FontWeight.Bold)
                }
            }
        }
        else -> {}
    }
}
