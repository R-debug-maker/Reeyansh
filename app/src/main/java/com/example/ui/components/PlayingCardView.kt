package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Card
import com.example.ui.theme.CardBorder
import com.example.ui.theme.CardIvory
import com.example.ui.theme.CardSuitBlack
import com.example.ui.theme.CardSuitRed
import com.example.ui.theme.GoldContainer
import com.example.ui.theme.RichBurgundy
import com.example.ui.theme.RichBurgundyDark
import com.example.ui.theme.RoyalGold
import com.example.ui.theme.RoyalGoldLight

@Composable
fun PlayingCardView(
    card: Card,
    modifier: Modifier = Modifier,
    isSelected: Boolean = false,
    isFaceDown: Boolean = false,
    onClick: (() -> Unit)? = null
) {
    val cardWidth = 58.dp
    val cardHeight = 84.dp
    val elevation = if (isSelected) 10.dp else 4.dp
    val yOffset = if (isSelected) (-12).dp else 0.dp

    val suitColor = if (card.suit.isRed) CardSuitRed else CardSuitBlack

    Surface(
        modifier = modifier
            .offset(y = yOffset)
            .width(cardWidth)
            .height(cardHeight)
            .shadow(elevation, RoundedCornerShape(6.dp))
            .clip(RoundedCornerShape(6.dp))
            .border(
                BorderStroke(
                    width = if (isSelected) 2.dp else 1.dp,
                    color = if (isSelected) RoyalGold else CardBorder
                ),
                RoundedCornerShape(6.dp)
            )
            .testTag("playing_card_${card.id}")
            .then(
                if (onClick != null) Modifier.clickable { onClick() } else Modifier
            ),
        color = if (isFaceDown) RichBurgundy else CardIvory
    ) {
        if (isFaceDown) {
            // Elegant royal back design
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.linearGradient(
                            listOf(RichBurgundy, RichBurgundyDark)
                        )
                    )
                    .padding(4.dp),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .border(BorderStroke(1.dp, RoyalGold.copy(alpha = 0.6f)), RoundedCornerShape(4.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "👑",
                        fontSize = 18.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else {
            // Face-up ivory card
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(4.dp)
            ) {
                // Top-left rank and suit
                Column(
                    modifier = Modifier.align(Alignment.TopStart),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = card.rank.display,
                        color = suitColor,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        lineHeight = 13.sp
                    )
                    Text(
                        text = card.suit.symbol,
                        color = suitColor,
                        fontSize = 12.sp,
                        lineHeight = 12.sp
                    )
                }

                // Center main suit symbol
                Text(
                    text = card.suit.symbol,
                    color = suitColor.copy(alpha = 0.9f),
                    fontSize = 24.sp,
                    modifier = Modifier.align(Alignment.Center)
                )

                // Bottom-right rank and suit (upside down)
                Column(
                    modifier = Modifier.align(Alignment.BottomEnd),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = card.suit.symbol,
                        color = suitColor,
                        fontSize = 11.sp,
                        lineHeight = 11.sp
                    )
                    Text(
                        text = card.rank.display,
                        color = suitColor,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        lineHeight = 12.sp
                    )
                }

                if (card.isTiplu || card.isMal) {
                    // Marriage special badge
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .background(RoyalGold, RoundedCornerShape(3.dp))
                            .padding(horizontal = 2.dp, vertical = 1.dp)
                    ) {
                        Text(
                            text = if (card.isTiplu) "TIPLU" else "MAAL",
                            fontSize = 6.sp,
                            fontWeight = FontWeight.Black,
                            color = CardSuitBlack
                        )
                    }
                }
            }
        }
    }
}
