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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.UserProfile
import com.example.ui.AppLanguage
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
fun ProfileSettingsScreen(
    viewModel: MainViewModel,
    user: UserProfile,
    currentLanguage: AppLanguage,
    onBack: () -> Unit
) {
    var displayNameInput by remember { mutableStateOf(user.displayName) }
    var selectedAvatar by remember { mutableStateOf(user.avatarEmoji) }
    var soundEnabled by remember { mutableStateOf(viewModel.soundManager.isSoundEnabled) }
    var hapticsEnabled by remember { mutableStateOf(viewModel.soundManager.isHapticsEnabled) }

    val avatarOptions = listOf("👑", "💎", "🃏", "🦁", "🐅", "🦅", "⚔️", "🏔️", "🌸")

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
                Text(
                    text = "Profile & Settings",
                    color = TextPrimary,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Profile Avatar & Badges Hero
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                border = BorderStroke(1.dp, EmeraldBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(68.dp)
                            .clip(CircleShape)
                            .background(GoldContainer)
                            .border(BorderStroke(2.dp, RoyalGold), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = selectedAvatar, fontSize = 36.sp)
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = user.displayName,
                        color = TextPrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Phone: ${user.maskedPhone}",
                        color = TextMuted,
                        fontSize = 12.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Avatar selector row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        avatarOptions.forEach { emoji ->
                            val isSel = emoji == selectedAvatar
                            Surface(
                                modifier = Modifier
                                    .size(34.dp)
                                    .clip(CircleShape)
                                    .clickable { selectedAvatar = emoji },
                                shape = CircleShape,
                                color = if (isSel) RoyalGold else SurfaceCardElevated,
                                border = BorderStroke(1.dp, if (isSel) RoyalGold else SurfaceCardElevated)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(text = emoji, fontSize = 16.sp)
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = displayNameInput,
                        onValueChange = { displayNameInput = it },
                        label = { Text("Display Name", color = TextSecondary) },
                        modifier = Modifier.fillMaxWidth().testTag("profile_name_input"),
                        shape = RoundedCornerShape(12.dp),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = SurfaceDark,
                            unfocusedContainerColor = SurfaceDark,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            focusedIndicatorColor = RoyalGold,
                            unfocusedIndicatorColor = EmeraldBorder
                        ),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Button(
                        onClick = {
                            viewModel.updateProfile(displayNameInput.ifBlank { user.displayName }, selectedAvatar)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = RoyalGold),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth().testTag("save_profile_button")
                    ) {
                        Text("Save Profile Changes", color = SurfaceDark, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Language Selector (English, Nepali, Hindi)
        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                border = BorderStroke(1.dp, EmeraldBorder)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "Language / भाषा",
                        color = RoyalGoldLight,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    AppLanguage.values().forEach { lang ->
                        val isSel = lang == currentLanguage
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .clickable { viewModel.setLanguage(lang) }
                                .padding(vertical = 8.dp, horizontal = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = lang.title,
                                color = if (isSel) RoyalGoldLight else TextPrimary,
                                fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 13.sp
                            )
                            if (isSel) {
                                Icon(Icons.Default.Check, contentDescription = "Selected", tint = RoyalGold, modifier = Modifier.size(18.dp))
                            }
                        }
                    }
                }
            }
        }

        // Audio & Tactile Haptics Toggles
        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                border = BorderStroke(1.dp, EmeraldBorder)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "Game Sound & Tactile Haptics",
                        color = RoyalGoldLight,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Sound Effects & Chimes", color = TextPrimary, fontSize = 13.sp)
                            Text("Card deals, flips, dice rolls, victory fanfare", color = TextMuted, fontSize = 10.sp)
                        }
                        Switch(
                            checked = soundEnabled,
                            onCheckedChange = {
                                soundEnabled = it
                                viewModel.soundManager.isSoundEnabled = it
                            },
                            colors = SwitchDefaults.colors(checkedThumbColor = RoyalGold)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Vibration & Haptics", color = TextPrimary, fontSize = 13.sp)
                            Text("Tactile feedback on card selection & turns", color = TextMuted, fontSize = 10.sp)
                        }
                        Switch(
                            checked = hapticsEnabled,
                            onCheckedChange = {
                                hapticsEnabled = it
                                viewModel.soundManager.isHapticsEnabled = it
                            },
                            colors = SwitchDefaults.colors(checkedThumbColor = RoyalGold)
                        )
                    }
                }
            }
        }
    }
}
