package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Public
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.GameType
import com.example.model.RoomType
import com.example.ui.AppScreen
import com.example.ui.MainViewModel
import com.example.ui.theme.EmeraldBorder
import com.example.ui.theme.EmeraldFelt
import com.example.ui.theme.GoldContainer
import com.example.ui.theme.RichBurgundy
import com.example.ui.theme.RoyalGold
import com.example.ui.theme.RoyalGoldLight
import com.example.ui.theme.SurfaceCard
import com.example.ui.theme.SurfaceCardElevated
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun GameCatalogScreen(
    viewModel: MainViewModel,
    selectedGame: GameType,
    onBack: () -> Unit
) {
    var selectedMode by remember { mutableStateOf(RoomType.SOLO) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(SurfaceDark)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 90.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // App Bar with back button
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = RoyalGold)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = selectedGame.title,
                        color = TextPrimary,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = selectedGame.nepaliTitle,
                        color = RoyalGoldLight,
                        fontSize = 12.sp
                    )
                }
            }
        }

        // Game selector tabs
        item {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(GameType.values()) { g ->
                    val isSelected = g == selectedGame
                    Surface(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { viewModel.selectGame(g) }
                            .testTag("catalog_tab_${g.name}"),
                        shape = RoundedCornerShape(12.dp),
                        color = if (isSelected) RoyalGold else SurfaceCard,
                        border = BorderStroke(1.dp, if (isSelected) RoyalGold else EmeraldBorder)
                    ) {
                        Text(
                            text = g.title,
                            color = if (isSelected) SurfaceDark else TextPrimary,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                        )
                    }
                }
            }
        }

        // Game Detail Card
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                border = BorderStroke(1.dp, EmeraldBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Game Overview & Rules",
                        color = RoyalGoldLight,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = selectedGame.shortDescription,
                        color = TextSecondary,
                        fontSize = 13.sp,
                        lineHeight = 18.sp
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        DetailMetric("Players", "${selectedGame.minPlayers} - ${selectedGame.maxPlayers}")
                        DetailMetric("Est. Duration", "${selectedGame.estimatedMinutes} mins")
                        DetailMetric("Entry Stakes", "${selectedGame.defaultEntryCoins} Coins")
                    }
                }
            }
        }

        // Mode Selection: A. Solo Mode, B. Public Room, C. Private Room
        item {
            Text(
                text = "Select Playing Mode",
                color = TextPrimary,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // A. Solo Mode
                ModeSelectionCard(
                    title = "Solo Mode",
                    subtitle = "Practice vs AI Bots",
                    icon = Icons.Default.Person,
                    isSelected = selectedMode == RoomType.SOLO,
                    onClick = { selectedMode = RoomType.SOLO },
                    modifier = Modifier.weight(1f)
                )

                // B. Public Room
                ModeSelectionCard(
                    title = "Public Room",
                    subtitle = "Open Online Tables",
                    icon = Icons.Default.Public,
                    isSelected = selectedMode == RoomType.PUBLIC,
                    onClick = { selectedMode = RoomType.PUBLIC },
                    modifier = Modifier.weight(1f)
                )

                // C. Private Room
                ModeSelectionCard(
                    title = "Private Room",
                    subtitle = "Friends & Invite Code",
                    icon = Icons.Default.Lock,
                    isSelected = selectedMode == RoomType.PRIVATE,
                    onClick = { selectedMode = RoomType.PRIVATE },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Action Button: Start Playing or Configure Room
        item {
            Button(
                onClick = {
                    if (selectedMode == RoomType.PUBLIC) {
                        viewModel.navigateTo(AppScreen.LOBBY_ROOMS)
                    } else if (selectedMode == RoomType.PRIVATE) {
                        viewModel.navigateTo(AppScreen.TABLE_CREATION)
                    } else {
                        // Solo play
                        viewModel.createAndEnterRoom(
                            name = "Solo ${selectedGame.title} Table",
                            gameType = selectedGame,
                            roomType = RoomType.SOLO,
                            entryCoins = selectedGame.defaultEntryCoins
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("catalog_start_playing_button"),
                colors = ButtonDefaults.buttonColors(containerColor = RoyalGold),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text(
                    text = when (selectedMode) {
                        RoomType.SOLO -> "Start Solo Practice 👑"
                        RoomType.PUBLIC -> "Find Public Tables 🌐"
                        RoomType.PRIVATE -> "Configure Private Room 🔒"
                    },
                    color = SurfaceDark,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun DetailMetric(label: String, value: String) {
    Column {
        Text(text = label, color = TextMuted, fontSize = 10.sp)
        Text(text = value, color = RoyalGoldLight, fontSize = 13.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun ModeSelectionCard(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .testTag("mode_card_${title.replace(" ", "_")}"),
        shape = RoundedCornerShape(12.dp),
        color = if (isSelected) GoldContainer else SurfaceCard,
        border = BorderStroke(1.5.dp, if (isSelected) RoyalGold else EmeraldBorder)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = if (isSelected) RoyalGold else TextSecondary,
                modifier = Modifier.size(22.dp)
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = title,
                color = if (isSelected) RoyalGoldLight else TextPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp
            )
            Text(
                text = subtitle,
                color = TextMuted,
                fontSize = 9.sp
            )
        }
    }
}
