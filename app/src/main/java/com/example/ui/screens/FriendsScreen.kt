package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Send
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
import com.example.model.PlayerStatus
import com.example.model.UserProfile
import com.example.ui.MainViewModel
import com.example.ui.theme.EmeraldBorder
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
fun FriendsScreen(
    viewModel: MainViewModel,
    onBack: () -> Unit
) {
    val sampleFriends = remember {
        listOf(
            UserProfile("F_1", "PokharaKing", "+977-9801112233", "🏔️", PlayerStatus.ONLINE, 15000L, 56, 38),
            UserProfile("F_2", "Aarav_Taas", "+977-9841223344", "👑", PlayerStatus.IN_GAME, 19200L, 72, 51),
            UserProfile("F_3", "Shreeya_NP", "+977-9811334455", "🌸", PlayerStatus.ONLINE, 11400L, 34, 22),
            UserProfile("F_4", "Gorkha_Warrior", "+977-9851445566", "⚔️", PlayerStatus.OFFLINE, 8900L, 29, 18),
            UserProfile("F_5", "MustangAce", "+977-9861556677", "🦅", PlayerStatus.ONLINE, 24000L, 88, 64)
        )
    }

    var inviteSentTo by remember { mutableStateOf<String?>(null) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(SurfaceDark)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 90.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onBack, modifier = Modifier.size(36.dp)) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = RoyalGold)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "Royal Friends & Club",
                            color = TextPrimary,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${sampleFriends.count { it.status == PlayerStatus.ONLINE }} Online Friends",
                            color = StatusGreen,
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }

        items(sampleFriends, key = { it.id }) { friend ->
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceCard),
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
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(SurfaceCardElevated)
                                .border(BorderStroke(1.dp, RoyalGold), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = friend.avatarEmoji, fontSize = 22.sp)
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = friend.displayName,
                                    color = TextPrimary,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(if (friend.status == PlayerStatus.ONLINE) StatusGreen else TextMuted)
                                )
                            }
                            Text(
                                text = "Win Rate: ${friend.winRate}% (${friend.gamesWon} wins)",
                                color = TextMuted,
                                fontSize = 11.sp
                            )
                        }
                    }

                    Button(
                        onClick = {
                            viewModel.soundManager.triggerHaptic(20)
                            inviteSentTo = friend.displayName
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = RoyalGold),
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                        modifier = Modifier.testTag("invite_friend_${friend.id}")
                    ) {
                        Text(
                            text = if (inviteSentTo == friend.displayName) "Invited ✓" else "Invite",
                            color = SurfaceDark,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
