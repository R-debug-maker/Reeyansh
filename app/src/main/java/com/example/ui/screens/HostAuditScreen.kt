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
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Security
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.HostAuditEntry
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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HostAuditScreen(
    viewModel: MainViewModel,
    auditLogs: List<HostAuditEntry>,
    onBack: () -> Unit
) {
    val dateFormat = remember { SimpleDateFormat("HH:mm:ss", Locale.getDefault()) }

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
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack, modifier = Modifier.size(36.dp)) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = RoyalGold)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = "Host & Fair Play Audit Log",
                        color = TextPrimary,
                        fontSize = 19.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Cryptographic Fairness & Anti-Manipulation",
                        color = StatusGreen,
                        fontSize = 11.sp
                    )
                }
            }
        }

        // Transparency statement
        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = GoldContainer),
                border = BorderStroke(1.dp, RoyalGold),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Lock, contentDescription = "Security", tint = RoyalGold, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Immutable Table State Architecture",
                            color = RoyalGoldLight,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Hosts cannot alter cards, rig deals, or modify player balances. All deals are locked with SHA-256 seed proofs before hands are dealt.",
                        color = TextSecondary,
                        fontSize = 11.sp,
                        lineHeight = 16.sp
                    )
                }
            }
        }

        items(auditLogs, key = { it.id }) { log ->
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                border = BorderStroke(1.dp, EmeraldBorder),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("audit_entry_${log.id}")
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = log.actionType.name.replace("_", " "),
                            color = RoyalGoldLight,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = dateFormat.format(Date(log.timestamp)),
                            color = TextMuted,
                            fontSize = 10.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = log.description,
                        color = TextPrimary,
                        fontSize = 12.sp
                    )

                    if (log.deckVerificationHash != null) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = SurfaceDark
                        ) {
                            Text(
                                text = "Verification Seed: ${log.deckVerificationHash}",
                                color = StatusGreen,
                                fontSize = 9.sp,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
