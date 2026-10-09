package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.LedgerTransaction
import com.example.model.WalletAccount
import com.example.ui.MainViewModel
import com.example.ui.theme.EmeraldBorder
import com.example.ui.theme.EmeraldFelt
import com.example.ui.theme.GoldContainer
import com.example.ui.theme.RichBurgundy
import com.example.ui.theme.RoyalGold
import com.example.ui.theme.RoyalGoldLight
import com.example.ui.theme.StatusGreen
import com.example.ui.theme.StatusRed
import com.example.ui.theme.SurfaceCard
import com.example.ui.theme.SurfaceCardElevated
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun WalletScreen(
    viewModel: MainViewModel,
    wallet: WalletAccount,
    onBack: () -> Unit
) {
    val dateFormat = remember { SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault()) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(SurfaceDark)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 90.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // App Bar
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack, modifier = Modifier.size(36.dp)) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = RoyalGold)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = "Royal Ledger & Wallet",
                        color = TextPrimary,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Double-Entry Practice Ledger",
                        color = RoyalGoldLight,
                        fontSize = 11.sp
                    )
                }
            }
        }

        // Available Balance Hero Card
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = GoldContainer),
                border = BorderStroke(1.5.dp, RoyalGold),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(
                        text = "TOTAL AVAILABLE PRACTICE BALANCE",
                        color = TextMuted,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "🪙 ${wallet.balance}",
                            color = RoyalGoldLight,
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Black
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "NPR (Practice)",
                            color = TextSecondary,
                            fontSize = 13.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = SurfaceDark.copy(alpha = 0.6f)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.CheckCircle, contentDescription = "Verified", tint = StatusGreen, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Audited Invariant", color = TextSecondary, fontSize = 10.sp)
                            }
                        }

                        Button(
                            onClick = { viewModel.claimDailyBonus() },
                            colors = ButtonDefaults.buttonColors(containerColor = RoyalGold),
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                            modifier = Modifier.testTag("wallet_daily_bonus_button")
                        ) {
                            Text("+1,000 Bonus", color = SurfaceDark, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Compliant Practice Notice Card
        item {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = SurfaceCard,
                border = BorderStroke(0.5.dp, EmeraldBorder)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Security, contentDescription = "Notice", tint = RoyalGold, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Royal Taas operates exclusively in Free Practice Mode. Credits have zero real-world monetary redemption value and cannot be withdrawn.",
                        color = TextSecondary,
                        fontSize = 11.sp,
                        lineHeight = 15.sp
                    )
                }
            }
        }

        // Ledger History Title
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Immutable Ledger Transactions",
                    color = TextPrimary,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${wallet.transactions.size} records",
                    color = TextMuted,
                    fontSize = 11.sp
                )
            }
        }

        // Transaction items
        items(wallet.transactions, key = { it.id }) { tx ->
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                border = BorderStroke(1.dp, SurfaceCardElevated),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("tx_${tx.id}")
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = tx.type.title,
                                color = TextPrimary,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = dateFormat.format(Date(tx.timestamp)),
                                color = TextMuted,
                                fontSize = 10.sp
                            )
                        }

                        Text(
                            text = if (tx.type.isCredit) "+${tx.amount}" else "-${tx.amount}",
                            color = if (tx.type.isCredit) StatusGreen else StatusRed,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Bal after: 🪙 ${tx.balanceAfter}",
                            color = TextSecondary,
                            fontSize = 10.sp
                        )
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = SurfaceDark
                        ) {
                            Text(
                                text = tx.auditHash,
                                color = RoyalGoldLight,
                                fontSize = 9.sp,
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
