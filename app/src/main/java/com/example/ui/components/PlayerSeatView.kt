package com.example.ui.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.PlayerSeat
import com.example.ui.theme.EmeraldBorder
import com.example.ui.theme.EmeraldFelt
import com.example.ui.theme.GoldContainer
import com.example.ui.theme.RichBurgundy
import com.example.ui.theme.RoyalGold
import com.example.ui.theme.RoyalGoldLight
import com.example.ui.theme.StatusGreen
import com.example.ui.theme.StatusRed
import com.example.ui.theme.SurfaceCard
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun PlayerSeatView(
    seat: PlayerSeat,
    modifier: Modifier = Modifier,
    isCurrentTurn: Boolean = false,
    showDetailedScore: Boolean = true
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(800),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    Column(
        modifier = modifier
            .testTag("player_seat_${seat.seatIndex}")
            .width(84.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Avatar circle with royal border & turn indicator
        Box(
            modifier = Modifier
                .size(48.dp)
                .then(
                    if (isCurrentTurn) Modifier.scale(pulseScale) else Modifier
                ),
            contentAlignment = Alignment.Center
        ) {
            // Glow halo if current turn
            if (isCurrentTurn) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                listOf(RoyalGold.copy(alpha = 0.5f), Color.Transparent)
                            )
                        )
                )
            }

            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(SurfaceCard)
                    .border(
                        BorderStroke(
                            width = if (isCurrentTurn) 2.dp else 1.dp,
                            color = if (isCurrentTurn) RoyalGold else EmeraldBorder
                        ),
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = seat.player.avatarEmoji,
                    fontSize = 20.sp
                )
            }

            // Host badge
            if (seat.isHost) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .size(16.dp)
                        .clip(CircleShape)
                        .background(RoyalGold),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "👑",
                        fontSize = 10.sp
                    )
                }
            }

            // Voice Mic status icon
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .size(15.dp)
                    .clip(CircleShape)
                    .background(if (seat.isMicOn) StatusGreen else RichBurgundy),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (seat.isMicOn) Icons.Default.Mic else Icons.Default.MicOff,
                    contentDescription = if (seat.isMicOn) "Mic Active" else "Mic Off",
                    tint = Color.White,
                    modifier = Modifier.size(10.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(2.dp))

        // Name
        Text(
            text = seat.player.displayName,
            color = if (isCurrentTurn) RoyalGoldLight else TextPrimary,
            fontSize = 11.sp,
            fontWeight = if (isCurrentTurn) FontWeight.Bold else FontWeight.Medium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        // Cards left or bid/tricks badge
        if (showDetailedScore) {
            Surface(
                modifier = Modifier.padding(top = 2.dp),
                shape = RoundedCornerShape(4.dp),
                color = if (isCurrentTurn) GoldContainer else SurfaceCard,
                border = BorderStroke(0.5.dp, if (isCurrentTurn) RoyalGold else EmeraldBorder)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    if (seat.currentBid > 0) {
                        Text(
                            text = "Bid:${seat.currentBid} (${seat.tricksWon})",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (seat.tricksWon >= seat.currentBid) StatusGreen else RoyalGoldLight
                        )
                    } else if (seat.currentBet > 0) {
                        Text(
                            text = "🪙 ${seat.currentBet}",
                            fontSize = 9.sp,
                            color = RoyalGoldLight
                        )
                    } else {
                        Text(
                            text = "${seat.hand.size} cards",
                            fontSize = 9.sp,
                            color = TextMuted
                        )
                    }
                }
            }
        }
    }
}
