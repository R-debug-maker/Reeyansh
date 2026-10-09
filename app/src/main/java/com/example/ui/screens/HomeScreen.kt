package com.example.ui.screens

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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Security
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ChatMessage
import com.example.model.GameRoom
import com.example.model.GameType
import com.example.model.MessageType
import com.example.model.RoomType
import com.example.model.UserProfile
import com.example.ui.AppScreen
import com.example.ui.MainViewModel
import com.example.ui.components.MainLobbyRoomsList
import com.example.ui.theme.EmeraldBorder
import com.example.ui.theme.EmeraldDark
import com.example.ui.theme.EmeraldFelt
import com.example.ui.theme.EmeraldTable
import com.example.ui.theme.GoldContainer
import com.example.ui.theme.RichBurgundy
import com.example.ui.theme.RoyalGold
import com.example.ui.theme.RoyalGoldLight
import com.example.ui.theme.StatusGreen
import com.example.ui.theme.SurfaceBorder
import com.example.ui.theme.SurfaceCard
import com.example.ui.theme.SurfaceCardElevated
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun HomeScreen(
    viewModel: MainViewModel,
    user: UserProfile,
    activeRooms: List<GameRoom>,
    lobbyMessages: List<ChatMessage>,
    onOpenLobbyChat: () -> Unit
) {
    var bonusClaimMessage by remember { mutableStateOf<String?>(null) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(SurfaceDark)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 90.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Royal Top Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { viewModel.navigateTo(AppScreen.PROFILE_SETTINGS) }
                ) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(SurfaceCardElevated)
                            .border(BorderStroke(1.5.dp, RoyalGold), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = user.avatarEmoji, fontSize = 24.sp)
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Royal Taas",
                                color = RoyalGold,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Black
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = "👑", fontSize = 14.sp)
                        }
                        Text(
                            text = "Namaste, ${user.displayName}",
                            color = TextSecondary,
                            fontSize = 12.sp
                        )
                    }
                }

                // Practice Balance Chip (clickable to Wallet)
                Surface(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .clickable { viewModel.navigateTo(AppScreen.WALLET) }
                        .testTag("home_wallet_chip"),
                    shape = RoundedCornerShape(20.dp),
                    color = GoldContainer,
                    border = BorderStroke(1.dp, RoyalGold)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(text = "🪙", fontSize = 14.sp)
                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "${user.practiceCoins}",
                                color = RoyalGoldLight,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Practice NPR",
                                color = TextMuted,
                                fontSize = 8.sp
                            )
                        }
                    }
                }
            }
        }

        // Daily Bonus & Quick Claim Banner
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = EmeraldFelt),
                border = BorderStroke(1.dp, EmeraldBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .padding(14.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(GoldContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "🎁", fontSize = 22.sp)
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Daily Royal Reward",
                                color = TextPrimary,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = bonusClaimMessage ?: "Claim 1,000 Free Practice Credits",
                                color = if (bonusClaimMessage != null) StatusGreen else RoyalGoldLight,
                                fontSize = 11.sp
                            )
                        }
                    }

                    Button(
                        onClick = {
                            val ok = viewModel.claimDailyBonus()
                            bonusClaimMessage = if (ok) "+1,000 Claimed!" else "Already claimed today"
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = RoyalGold),
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
                        modifier = Modifier.testTag("claim_daily_bonus_button")
                    ) {
                        Text(
                            text = "Claim",
                            color = SurfaceDark,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }

        // Main Action Buttons: Quick Play, Create Table, Browse Public Rooms
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Quick Play Button
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(14.dp))
                        .clickable {
                            viewModel.selectGame(GameType.CALL_BREAK)
                            viewModel.createAndEnterRoom(
                                name = "Quick Call Break Table",
                                gameType = GameType.CALL_BREAK,
                                roomType = RoomType.SOLO,
                                entryCoins = 100L
                            )
                        }
                        .testTag("quick_play_card"),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = RichBurgundy),
                    border = BorderStroke(1.dp, RoyalGold.copy(alpha = 0.6f))
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        horizontalAlignment = Alignment.Start
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(RoyalGold),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.PlayArrow, contentDescription = "Play", tint = SurfaceDark)
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "Quick Play",
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                        Text(
                            text = "Instant Solo Table",
                            color = TextSecondary,
                            fontSize = 11.sp
                        )
                    }
                }

                // Create Table Button
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(14.dp))
                        .clickable { viewModel.navigateTo(AppScreen.TABLE_CREATION) }
                        .testTag("create_table_card"),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = EmeraldTable),
                    border = BorderStroke(1.dp, EmeraldBorder)
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        horizontalAlignment = Alignment.Start
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(GoldContainer)
                                .border(BorderStroke(1.dp, RoyalGold), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "Create", tint = RoyalGoldLight)
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "Create Room",
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                        Text(
                            text = "Host Custom Rules",
                            color = TextSecondary,
                            fontSize = 11.sp
                        )
                    }
                }

                // Public Rooms Button
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(14.dp))
                        .clickable { viewModel.navigateTo(AppScreen.LOBBY_ROOMS) }
                        .testTag("browse_rooms_card"),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceCardElevated),
                    border = BorderStroke(1.dp, SurfaceBorder)
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        horizontalAlignment = Alignment.Start
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(SurfaceCard),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Groups, contentDescription = "Rooms", tint = RoyalGoldLight)
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "Public Rooms",
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                        Text(
                            text = "${activeRooms.size} Tables Open",
                            color = TextSecondary,
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }

        // Live Lobby Chat & Voice Messages Bar (Requested feature!)
        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                border = BorderStroke(1.dp, EmeraldBorder),
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .clickable { onOpenLobbyChat() }
                    .testTag("lobby_chat_preview_card")
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "🎙️", fontSize = 16.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Lobby Chat & Voice Lounge",
                                color = RoyalGoldLight,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                        Text(
                            text = "Tap to Talk 💬",
                            color = StatusGreen,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Latest message preview
                    val lastMsg = lobbyMessages.lastOrNull()
                    if (lastMsg != null) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .background(SurfaceCardElevated, RoundedCornerShape(8.dp))
                                .padding(horizontal = 8.dp, vertical = 6.dp)
                                .fillMaxWidth()
                        ) {
                            Text(text = lastMsg.senderAvatar, fontSize = 14.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "${lastMsg.senderName}: ",
                                color = RoyalGold,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                            if (lastMsg.type == MessageType.VOICE) {
                                Icon(
                                    Icons.Default.Mic,
                                    contentDescription = "Voice",
                                    tint = StatusGreen,
                                    modifier = Modifier.size(13.dp)
                                )
                                Text(
                                    text = " Voice Note (${lastMsg.voiceDurationSeconds}s)",
                                    color = TextPrimary,
                                    fontSize = 11.sp
                                )
                            } else {
                                Text(
                                    text = lastMsg.content,
                                    color = TextSecondary,
                                    fontSize = 11.sp,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }
            }
        }

        // Games Available Catalog Section
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Games Available",
                    color = TextPrimary,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "See All (7) →",
                    color = RoyalGold,
                    fontSize = 12.sp,
                    modifier = Modifier.clickable { viewModel.navigateTo(AppScreen.GAME_CATALOG) }
                )
            }
        }

        // Horizontal Game Carousel
        item {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(GameType.values()) { game ->
                    GameCatalogCard(
                        game = game,
                        onClick = {
                            viewModel.selectGame(game)
                            viewModel.navigateTo(AppScreen.GAME_CATALOG)
                        }
                    )
                }
            }
        }

        // Available Public Game Tables for Call Break, Marriage 21, Teen Patti, Kitti, Ludo, Chess
        item {
            MainLobbyRoomsList(
                rooms = activeRooms,
                onJoinRoom = { room -> viewModel.joinAndEnterRoom(room) },
                onCreateTable = { viewModel.navigateTo(AppScreen.TABLE_CREATION) },
                showSearchBar = false
            )
        }

        // Fair Play & Host Audit Log Preview
        item {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { viewModel.navigateTo(AppScreen.HOST_AUDIT) },
                shape = RoundedCornerShape(12.dp),
                color = SurfaceCardElevated,
                border = BorderStroke(0.5.dp, SurfaceBorder)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Security,
                            contentDescription = "Fair Play",
                            tint = RoyalGold,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "Fair Play & Cryptographic Shuffling",
                                color = TextPrimary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "Audited SHA-256 seed verification & host logs",
                                color = TextMuted,
                                fontSize = 10.sp
                            )
                        }
                    }
                    Text(
                        text = "Audit →",
                        color = RoyalGoldLight,
                        fontSize = 11.sp
                    )
                }
            }
        }
    }
}

@Composable
fun GameCatalogCard(
    game: GameType,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(170.dp)
            .clip(RoundedCornerShape(14.dp))
            .clickable { onClick() }
            .testTag("game_card_${game.name}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceCard),
        border = BorderStroke(1.dp, EmeraldBorder)
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(72.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(
                        Brush.verticalGradient(
                            listOf(EmeraldTable, EmeraldDark)
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = when (game) {
                        GameType.CALL_BREAK -> "♠️ 🂡"
                        GameType.TEEN_PATTI -> "👑 🂮"
                        GameType.KITTY -> "🐯 🂠"
                        GameType.MARRIAGE_21 -> "💍 🂱"
                        GameType.LUDO -> "🎲 🏰"
                        GameType.CHESS -> "♟️ ♔"
                        GameType.SNAKE_LADDER -> "🐍 🪜"
                    },
                    fontSize = 28.sp
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = game.title,
                color = TextPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = game.nepaliTitle,
                color = RoyalGoldLight,
                fontSize = 11.sp
            )

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "${game.minPlayers}-${game.maxPlayers} Players",
                    color = TextMuted,
                    fontSize = 10.sp
                )
                Text(
                    text = "~${game.estimatedMinutes}m",
                    color = TextMuted,
                    fontSize = 10.sp
                )
            }
        }
    }
}
