package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.GameRoom
import com.example.model.GameType
import com.example.model.RoomStatus
import com.example.model.RoomType
import com.example.ui.theme.EmeraldBorder
import com.example.ui.theme.EmeraldDark
import com.example.ui.theme.EmeraldFelt
import com.example.ui.theme.EmeraldTable
import com.example.ui.theme.GoldContainer
import com.example.ui.theme.RichBurgundy
import com.example.ui.theme.RoyalGold
import com.example.ui.theme.RoyalGoldLight
import com.example.ui.theme.StatusGreen
import com.example.ui.theme.StatusOrange
import com.example.ui.theme.SurfaceBorder
import com.example.ui.theme.SurfaceCard
import com.example.ui.theme.SurfaceCardElevated
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

/**
 * Main Lobby UI Component listing available public game rooms for
 * Call Break, Marriage 21, Teen Patti, Kitti, Ludo, and Chess
 * with their real-time player counts, status, and join controls.
 */
@Composable
fun MainLobbyRoomsList(
    rooms: List<GameRoom>,
    onJoinRoom: (GameRoom) -> Unit,
    onCreateTable: () -> Unit,
    modifier: Modifier = Modifier,
    initialFilter: GameType? = null,
    showSearchBar: Boolean = true
) {
    var selectedGameFilter by remember { mutableStateOf<GameType?>(initialFilter) }
    var selectedStatusFilter by remember { mutableStateOf<RoomStatus?>(null) }
    var searchKeyword by remember { mutableStateOf("") }

    val supportedGames = listOf(
        GameType.CALL_BREAK,
        GameType.MARRIAGE_21,
        GameType.TEEN_PATTI,
        GameType.KITTY,
        GameType.LUDO,
        GameType.CHESS
    )

    // Filter public rooms
    val publicRooms = rooms.filter { it.roomType == RoomType.PUBLIC }
    val filteredRooms = publicRooms.filter { room ->
        val matchesGame = selectedGameFilter == null || room.gameType == selectedGameFilter
        val matchesStatus = selectedStatusFilter == null || room.status == selectedStatusFilter
        val matchesSearch = searchKeyword.isBlank() ||
                room.name.contains(searchKeyword.trim(), ignoreCase = true) ||
                room.roomCode.contains(searchKeyword.trim(), ignoreCase = true) ||
                room.hostName.contains(searchKeyword.trim(), ignoreCase = true)
        matchesGame && matchesStatus && matchesSearch
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .testTag("main_lobby_rooms_list_component"),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Section Header with Title & Table Count Badge
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Public Game Tables",
                        color = TextPrimary,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = GoldContainer,
                        border = BorderStroke(1.dp, RoyalGold.copy(alpha = 0.6f))
                    ) {
                        Text(
                            text = "${filteredRooms.size} Available",
                            color = RoyalGoldLight,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
                Text(
                    text = "Live tables for Call Break, Marriage, Teen Patti, Kitti, Ludo & Chess",
                    color = TextSecondary,
                    fontSize = 11.sp
                )
            }

            Button(
                onClick = onCreateTable,
                colors = ButtonDefaults.buttonColors(containerColor = RoyalGold),
                shape = RoundedCornerShape(10.dp),
                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                modifier = Modifier.testTag("lobby_create_table_shortcut_btn")
            ) {
                Icon(Icons.Default.Add, contentDescription = "Create", tint = SurfaceDark, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(2.dp))
                Text("Create Table", color = SurfaceDark, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }

        // Search Bar (optional)
        if (showSearchBar) {
            OutlinedTextField(
                value = searchKeyword,
                onValueChange = { searchKeyword = it },
                placeholder = { Text("Search by table code (e.g. RT-1088), game, or host...", color = TextMuted, fontSize = 12.sp) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search", tint = RoyalGold, modifier = Modifier.size(18.dp)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("lobby_search_field"),
                shape = RoundedCornerShape(12.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = SurfaceCard,
                    unfocusedContainerColor = SurfaceCard,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary,
                    focusedIndicatorColor = RoyalGold,
                    unfocusedIndicatorColor = EmeraldBorder
                ),
                singleLine = true
            )
        }

        // Game Category Filter Tabs for the 6 Games
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            item {
                val isAllSelected = selectedGameFilter == null
                Surface(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .clickable { selectedGameFilter = null }
                        .testTag("filter_tab_all_games"),
                    shape = RoundedCornerShape(10.dp),
                    color = if (isAllSelected) RoyalGold else SurfaceCard,
                    border = BorderStroke(1.dp, if (isAllSelected) RoyalGold else EmeraldBorder)
                ) {
                    Text(
                        text = "All Games (${publicRooms.size})",
                        color = if (isAllSelected) SurfaceDark else TextPrimary,
                        fontSize = 11.sp,
                        fontWeight = if (isAllSelected) FontWeight.Bold else FontWeight.Medium,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    )
                }
            }

            items(supportedGames) { game ->
                val isSelected = selectedGameFilter == game
                val countForGame = publicRooms.count { it.gameType == game }

                Surface(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .clickable { selectedGameFilter = game }
                        .testTag("filter_tab_${game.name}"),
                    shape = RoundedCornerShape(10.dp),
                    color = if (isSelected) RoyalGold else SurfaceCard,
                    border = BorderStroke(1.dp, if (isSelected) RoyalGold else EmeraldBorder)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = when (game) {
                                GameType.CALL_BREAK -> "♠️ "
                                GameType.MARRIAGE_21 -> "💍 "
                                GameType.TEEN_PATTI -> "👑 "
                                GameType.KITTY -> "🐯 "
                                GameType.LUDO -> "🎲 "
                                GameType.CHESS -> "♟️ "
                                else -> "🃏 "
                            },
                            fontSize = 11.sp
                        )
                        Text(
                            text = "${game.title} ($countForGame)",
                            color = if (isSelected) SurfaceDark else TextPrimary,
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                        )
                    }
                }
            }
        }

        // Status Quick Filters (All, Waiting, Playing)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            StatusFilterChip(
                label = "All Statuses",
                isSelected = selectedStatusFilter == null,
                onClick = { selectedStatusFilter = null }
            )
            StatusFilterChip(
                label = "● Waiting for Players",
                isSelected = selectedStatusFilter == RoomStatus.WAITING,
                onClick = { selectedStatusFilter = RoomStatus.WAITING },
                activeColor = StatusGreen
            )
            StatusFilterChip(
                label = "● In Progress",
                isSelected = selectedStatusFilter == RoomStatus.PLAYING,
                onClick = { selectedStatusFilter = RoomStatus.PLAYING },
                activeColor = StatusOrange
            )
        }

        // Public Rooms List Cards
        if (filteredRooms.isEmpty()) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                color = SurfaceCard,
                border = BorderStroke(1.dp, SurfaceBorder)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = "🃏", fontSize = 32.sp)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "No public tables match current filters",
                        color = TextPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Be the first to host a table for this game!",
                        color = TextMuted,
                        fontSize = 11.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Button(
                        onClick = onCreateTable,
                        colors = ButtonDefaults.buttonColors(containerColor = RoyalGold),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Create Table Now 👑", color = SurfaceDark, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                filteredRooms.forEach { room ->
                    PublicGameRoomCard(
                        room = room,
                        onJoin = { onJoinRoom(room) }
                    )
                }
            }
        }
    }
}

/**
 * Individual Public Game Room Card with rich indicators:
 * Game Icon, Table Name, Room Code, Host, Player Count with visual dots,
 * Room Status Pill with live pulse, and Join/Spectate Button.
 */
@Composable
fun PublicGameRoomCard(
    room: GameRoom,
    onJoin: () -> Unit
) {
    val isFull = room.seats.size >= room.maxPlayers
    val isPlaying = room.status == RoomStatus.PLAYING
    val isWaiting = room.status == RoomStatus.WAITING

    val infiniteTransition = rememberInfiniteTransition(label = "pulseWait")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(900),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseAlpha"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .clickable { onJoin() }
            .testTag("public_room_card_${room.roomCode}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceCard),
        border = BorderStroke(1.dp, if (isWaiting) EmeraldBorder else SurfaceBorder)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Top Row: Game Badge, Room Name, Room Code & Status Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    // Game Symbol Avatar
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(
                                Brush.verticalGradient(
                                    listOf(EmeraldTable, EmeraldDark)
                                )
                            )
                            .border(BorderStroke(1.dp, RoyalGold.copy(alpha = 0.5f)), RoundedCornerShape(10.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = when (room.gameType) {
                                GameType.CALL_BREAK -> "♠️"
                                GameType.MARRIAGE_21 -> "💍"
                                GameType.TEEN_PATTI -> "👑"
                                GameType.KITTY -> "🐯"
                                GameType.LUDO -> "🎲"
                                GameType.CHESS -> "♟️"
                                else -> "🃏"
                            },
                            fontSize = 18.sp
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = room.name,
                                color = TextPrimary,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = room.gameType.title,
                                color = RoyalGoldLight,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(text = "•", color = TextMuted, fontSize = 10.sp)
                            Text(
                                text = room.roomCode,
                                color = TextSecondary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Status Badge with live status indicator
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = when {
                        isWaiting -> StatusGreen.copy(alpha = 0.15f)
                        isPlaying -> StatusOrange.copy(alpha = 0.15f)
                        else -> SurfaceCardElevated
                    },
                    border = BorderStroke(
                        width = 1.dp,
                        color = when {
                            isWaiting -> StatusGreen.copy(alpha = 0.6f)
                            isPlaying -> StatusOrange.copy(alpha = 0.6f)
                            else -> SurfaceBorder
                        }
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        // Pulse circle for active tables
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(
                                    when {
                                        isWaiting -> StatusGreen.copy(alpha = pulseAlpha)
                                        isPlaying -> StatusOrange
                                        else -> TextMuted
                                    }
                                )
                        )
                        Text(
                            text = when {
                                isWaiting && !isFull -> "Accepting Players"
                                isWaiting && isFull -> "Ready to Deal"
                                isPlaying -> "In Progress"
                                else -> "Completed"
                            },
                            color = when {
                                isWaiting -> StatusGreen
                                isPlaying -> StatusOrange
                                else -> TextMuted
                            },
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Middle Row: Player Count Visual Dots, Host Info & Entry Fee
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(SurfaceCardElevated, RoundedCornerShape(10.dp))
                    .padding(horizontal = 10.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Player Capacity with Seat Dots
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(Icons.Default.People, contentDescription = "Players", tint = RoyalGold, modifier = Modifier.size(14.dp))
                        Text(
                            text = "${room.seats.size} / ${room.maxPlayers} Players",
                            color = TextPrimary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        if (isWaiting && !isFull) {
                            Text(
                                text = "(${room.maxPlayers - room.seats.size} left)",
                                color = StatusGreen,
                                fontSize = 10.sp
                            )
                        }
                    }

                    // Visual Seat indicators (Green for occupied, Gray for open)
                    Row(
                        modifier = Modifier.padding(top = 3.dp),
                        horizontalArrangement = Arrangement.spacedBy(3.dp)
                    ) {
                        for (s in 0 until room.maxPlayers) {
                            val isOccupied = s < room.seats.size
                            Box(
                                modifier = Modifier
                                    .size(width = 14.dp, height = 4.dp)
                                    .clip(RoundedCornerShape(2.dp))
                                    .background(if (isOccupied) RoyalGold else SurfaceBorder)
                            )
                        }
                    }
                }

                // Entry Fee & Stakes
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(text = "🪙", fontSize = 12.sp)
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "${room.entryCoins} Coins",
                            color = RoyalGoldLight,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Practice Stakes",
                            color = TextMuted,
                            fontSize = 9.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Bottom Action Row: Host & Voice Tag + Join Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "Host: ${room.hostName}",
                        color = TextSecondary,
                        fontSize = 11.sp
                    )

                    // Live Voice Chat badge if mic is active in room
                    val hasVoice = room.seats.any { it.isMicOn }
                    if (hasVoice) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = StatusGreen.copy(alpha = 0.15f),
                            border = BorderStroke(0.5.dp, StatusGreen)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Mic, contentDescription = "Voice", tint = StatusGreen, modifier = Modifier.size(10.dp))
                                Spacer(modifier = Modifier.width(2.dp))
                                Text("Voice Active", color = StatusGreen, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                // Interactive Join / Spectate Button
                Button(
                    onClick = onJoin,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isWaiting && !isFull) RoyalGold else SurfaceCardElevated
                    ),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                    modifier = Modifier.testTag("join_table_btn_${room.roomCode}")
                ) {
                    Text(
                        text = when {
                            isWaiting && !isFull -> "Join Table 👑"
                            isWaiting && isFull -> "Spectate Table"
                            isPlaying -> "Spectate Game 👁️"
                            else -> "View Table"
                        },
                        color = if (isWaiting && !isFull) SurfaceDark else TextPrimary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun StatusFilterChip(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    activeColor: Color = RoyalGold
) {
    Surface(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(8.dp),
        color = if (isSelected) activeColor.copy(alpha = 0.2f) else SurfaceCard,
        border = BorderStroke(1.dp, if (isSelected) activeColor else SurfaceBorder)
    ) {
        Text(
            text = label,
            color = if (isSelected) activeColor else TextMuted,
            fontSize = 10.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}
