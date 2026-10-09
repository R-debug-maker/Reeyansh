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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Public
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
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
import com.example.model.RulePreset
import com.example.ui.MainViewModel
import com.example.ui.theme.EmeraldBorder
import com.example.ui.theme.EmeraldDark
import com.example.ui.theme.EmeraldFelt
import com.example.ui.theme.GoldContainer
import com.example.ui.theme.RoyalGold
import com.example.ui.theme.RoyalGoldLight
import com.example.ui.theme.StatusGreen
import com.example.ui.theme.SurfaceCard
import com.example.ui.theme.SurfaceCardElevated
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun TableCreationScreen(
    viewModel: MainViewModel,
    initialGameType: GameType,
    onBack: () -> Unit
) {
    var tableName by remember { mutableStateOf("Royal ${initialGameType.title} Table") }
    var selectedGame by remember { mutableStateOf(initialGameType) }
    var isPrivate by remember { mutableStateOf(false) }
    var entryCoins by remember { mutableStateOf(initialGameType.defaultEntryCoins.toString()) }
    var roundsCount by remember { mutableFloatStateOf(5f) }
    var turnTimeout by remember { mutableFloatStateOf(20f) }
    var spadesCutRule by remember { mutableStateOf(true) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(SurfaceDark)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 90.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Top Header
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
                        text = "Create Royal Table",
                        color = TextPrimary,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "You will occupy Seat #1 as Host",
                        color = RoyalGoldLight,
                        fontSize = 12.sp
                    )
                }
            }
        }

        // Table Name Input
        item {
            OutlinedTextField(
                value = tableName,
                onValueChange = { tableName = it },
                label = { Text("Table Name", color = TextSecondary) },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("table_name_input"),
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

        // Game Type Selection Chips
        item {
            Text(
                text = "Select Game",
                color = TextPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(GameType.CALL_BREAK, GameType.TEEN_PATTI, GameType.MARRIAGE_21, GameType.KITTY).forEach { g ->
                    val isSel = g == selectedGame
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .clickable {
                                selectedGame = g
                                entryCoins = g.defaultEntryCoins.toString()
                            },
                        shape = RoundedCornerShape(8.dp),
                        color = if (isSel) RoyalGold else SurfaceCard,
                        border = BorderStroke(1.dp, if (isSel) RoyalGold else EmeraldBorder)
                    ) {
                        Text(
                            text = g.title,
                            color = if (isSel) SurfaceDark else TextPrimary,
                            fontSize = 11.sp,
                            fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                            modifier = Modifier.padding(vertical = 8.dp),
                            maxLines = 1
                        )
                    }
                }
            }
        }

        // Table Visibility (Public vs Private)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { isPrivate = false },
                    shape = RoundedCornerShape(12.dp),
                    color = if (!isPrivate) GoldContainer else SurfaceCard,
                    border = BorderStroke(1.5.dp, if (!isPrivate) RoyalGold else EmeraldBorder)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Public, contentDescription = "Public", tint = if (!isPrivate) RoyalGold else TextSecondary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text("Public Table", color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            Text("Anyone can join", color = TextMuted, fontSize = 10.sp)
                        }
                    }
                }

                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { isPrivate = true },
                    shape = RoundedCornerShape(12.dp),
                    color = if (isPrivate) GoldContainer else SurfaceCard,
                    border = BorderStroke(1.5.dp, if (isPrivate) RoyalGold else EmeraldBorder)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Lock, contentDescription = "Private", tint = if (isPrivate) RoyalGold else TextSecondary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text("Private Table", color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            Text("Code invitation only", color = TextMuted, fontSize = 10.sp)
                        }
                    }
                }
            }
        }

        // Entry Fee & Stakes
        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                border = BorderStroke(1.dp, EmeraldBorder)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "Practice Stakes (Entry Coins)",
                        color = RoyalGoldLight,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf(50L, 100L, 200L, 500L, 1000L).forEach { amount ->
                            val isSel = entryCoins == amount.toString()
                            Surface(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable { entryCoins = amount.toString() },
                                shape = RoundedCornerShape(8.dp),
                                color = if (isSel) RoyalGold else SurfaceCardElevated,
                                border = BorderStroke(1.dp, if (isSel) RoyalGold else EmeraldBorder)
                            ) {
                                Text(
                                    text = "$amount",
                                    color = if (isSel) SurfaceDark else TextPrimary,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Host Configurable Rules: Turn Timer & Rounds
        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                border = BorderStroke(1.dp, EmeraldBorder)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "Rule Configuration",
                        color = RoyalGoldLight,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Turn Timer", color = TextPrimary, fontSize = 12.sp)
                        Text("${turnTimeout.toInt()} seconds", color = RoyalGold, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                    Slider(
                        value = turnTimeout,
                        onValueChange = { turnTimeout = it },
                        valueRange = 10f..45f,
                        steps = 6,
                        colors = SliderDefaults.colors(
                            thumbColor = RoyalGold,
                            activeTrackColor = RoyalGold
                        )
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Match Length", color = TextPrimary, fontSize = 12.sp)
                        Text("${roundsCount.toInt()} Rounds", color = RoyalGold, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                    Slider(
                        value = roundsCount,
                        onValueChange = { roundsCount = it },
                        valueRange = 1f..10f,
                        steps = 8,
                        colors = SliderDefaults.colors(
                            thumbColor = RoyalGold,
                            activeTrackColor = RoyalGold
                        )
                    )

                    if (selectedGame == GameType.CALL_BREAK) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Must Cut with Spades (Hukum)", color = TextPrimary, fontSize = 12.sp)
                            Switch(
                                checked = spadesCutRule,
                                onCheckedChange = { spadesCutRule = it },
                                colors = SwitchDefaults.colors(checkedThumbColor = RoyalGold)
                            )
                        }
                    }
                }
            }
        }

        // Create Table Final Action
        item {
            Button(
                onClick = {
                    val fee = entryCoins.toLongOrNull() ?: selectedGame.defaultEntryCoins
                    val roomType = if (isPrivate) RoomType.PRIVATE else RoomType.PUBLIC
                    val preset = RulePreset(
                        roundsCount = roundsCount.toInt(),
                        turnTimeoutSeconds = turnTimeout.toInt(),
                        spadesCutRequired = spadesCutRule
                    )
                    viewModel.createAndEnterRoom(
                        name = tableName.ifBlank { "Royal ${selectedGame.title} Table" },
                        gameType = selectedGame,
                        roomType = roomType,
                        entryCoins = fee,
                        rulePreset = preset
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("submit_create_table_button"),
                colors = ButtonDefaults.buttonColors(containerColor = RoyalGold),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text(
                    text = "Launch & Seat at Table 👑",
                    color = SurfaceDark,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
