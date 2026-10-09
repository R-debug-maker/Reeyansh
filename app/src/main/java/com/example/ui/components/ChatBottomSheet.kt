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
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.VolumeMute
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audio.VoicePlaybackState
import com.example.audio.VoiceRecordingState
import com.example.model.ChatMessage
import com.example.model.ChatScope
import com.example.model.MessageType
import com.example.model.TableVoiceChannelState
import com.example.model.UserProfile
import com.example.ui.theme.EmeraldBorder
import com.example.ui.theme.EmeraldDark
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatBottomSheet(
    title: String,
    scope: ChatScope,
    roomId: String?,
    currentUser: UserProfile,
    messages: List<ChatMessage>,
    voiceChannelState: TableVoiceChannelState,
    recordingState: VoiceRecordingState,
    playbackState: VoicePlaybackState,
    onDismiss: () -> Unit,
    onSendMessage: (String) -> Unit,
    onSendEmoji: (String) -> Unit,
    onStartVoiceRecord: () -> Unit,
    onStopAndSendVoice: () -> Unit,
    onCancelVoiceRecord: () -> Unit,
    onPlayVoiceMessage: (ChatMessage) -> Unit,
    onToggleMic: () -> Unit,
    onToggleSpeaker: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var textInput by remember { mutableStateOf("") }
    val timeFormat = remember { SimpleDateFormat("HH:mm", Locale.getDefault()) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = SurfaceDark,
        tonalElevation = 8.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.85f)
                .padding(horizontal = 16.dp)
        ) {
            // Header with Title & Live Table Voice Toggles
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "💬 $title",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = RoyalGoldLight
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .background(GoldContainer, RoundedCornerShape(12.dp))
                            .border(BorderStroke(0.5.dp, RoyalGold), RoundedCornerShape(12.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "${messages.size} msgs",
                            fontSize = 10.sp,
                            color = RoyalGold
                        )
                    }
                }

                // Table Voice Controls: Mic & Speaker
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Speaker toggle
                    IconButton(
                        onClick = onToggleSpeaker,
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(if (voiceChannelState.isSpeakerMuted) RichBurgundy else SurfaceCardElevated)
                            .testTag("chat_speaker_toggle")
                    ) {
                        Icon(
                            imageVector = if (voiceChannelState.isSpeakerMuted) Icons.Default.VolumeMute else Icons.Default.VolumeUp,
                            contentDescription = "Toggle Speaker",
                            tint = if (voiceChannelState.isSpeakerMuted) StatusRed else RoyalGoldLight,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    // Mic toggle
                    IconButton(
                        onClick = onToggleMic,
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(if (voiceChannelState.isMicMuted) RichBurgundy else StatusGreen)
                            .testTag("chat_mic_toggle")
                    ) {
                        Icon(
                            imageVector = if (voiceChannelState.isMicMuted) Icons.Default.MicOff else Icons.Default.Mic,
                            contentDescription = "Toggle Mic",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Close Chat", tint = TextMuted)
                    }
                }
            }

            // Quick emoji reactions bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                listOf("👑", "🔥", "👏", "🃏", "💰", "😂", "🎯", "🤝").forEach { emoji ->
                    Surface(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { onSendEmoji(emoji) }
                            .padding(horizontal = 4.dp, vertical = 2.dp),
                        color = SurfaceCardElevated
                    ) {
                        Text(
                            text = emoji,
                            fontSize = 18.sp,
                            modifier = Modifier.padding(4.dp)
                        )
                    }
                }
            }

            // Message list
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                reverseLayout = false
            ) {
                items(messages, key = { it.id }) { msg ->
                    val isMe = msg.senderId == currentUser.id

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = if (isMe) Arrangement.End else Arrangement.Start
                    ) {
                        if (!isMe) {
                            Text(
                                text = msg.senderAvatar,
                                fontSize = 18.sp,
                                modifier = Modifier.padding(end = 6.dp, top = 4.dp)
                            )
                        }

                        Column(
                            horizontalAlignment = if (isMe) Alignment.End else Alignment.Start
                        ) {
                            if (!isMe) {
                                Text(
                                    text = msg.senderName,
                                    fontSize = 10.sp,
                                    color = RoyalGoldLight,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }

                            if (msg.type == MessageType.VOICE) {
                                // Voice message bubble with waveform and play button
                                val isPlaying = playbackState.playingMessageId == msg.id

                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = if (isMe) GoldContainer else SurfaceCard,
                                    border = BorderStroke(1.dp, if (isMe) RoyalGold else EmeraldBorder),
                                    modifier = Modifier.padding(top = 2.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        IconButton(
                                            onClick = { onPlayVoiceMessage(msg) },
                                            modifier = Modifier
                                                .size(32.dp)
                                                .clip(CircleShape)
                                                .background(RoyalGold)
                                        ) {
                                            Icon(
                                                imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                                contentDescription = "Play voice message",
                                                tint = SurfaceDark,
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }

                                        // Waveform visualization
                                        Row(
                                            modifier = Modifier
                                                .width(110.dp)
                                                .height(24.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(2.dp)
                                        ) {
                                            val bars = if (msg.voiceWaveform.isNotEmpty()) msg.voiceWaveform else List(12) { 0.5f }
                                            bars.take(16).forEachIndexed { index, amp ->
                                                val progressIndex = (playbackState.progress * bars.size).toInt()
                                                val barColor = if (isPlaying && index <= progressIndex) {
                                                    RoyalGoldLight
                                                } else {
                                                    TextMuted
                                                }
                                                val barHeight = (amp * 20).coerceIn(4f, 20f).dp

                                                Box(
                                                    modifier = Modifier
                                                        .width(3.dp)
                                                        .height(barHeight)
                                                        .clip(RoundedCornerShape(1.dp))
                                                        .background(barColor)
                                                )
                                            }
                                        }

                                        Text(
                                            text = "${msg.voiceDurationSeconds}s",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = RoyalGoldLight
                                        )
                                    }
                                }
                            } else if (msg.type == MessageType.EMOJI) {
                                Text(
                                    text = msg.content,
                                    fontSize = 28.sp,
                                    modifier = Modifier.padding(2.dp)
                                )
                            } else {
                                // Text message bubble
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = if (isMe) EmeraldFelt else SurfaceCard,
                                    border = BorderStroke(0.5.dp, if (isMe) EmeraldBorder else SurfaceCardElevated),
                                    modifier = Modifier.padding(top = 2.dp)
                                ) {
                                    Text(
                                        text = msg.content,
                                        color = TextPrimary,
                                        fontSize = 13.sp,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                    )
                                }
                            }

                            Text(
                                text = timeFormat.format(Date(msg.timestamp)),
                                fontSize = 9.sp,
                                color = TextMuted,
                                modifier = Modifier.padding(top = 1.dp)
                            )
                        }
                    }
                }
            }

            // Voice recording indicator bar (active when recording)
            AnimatedVisibility(visible = recordingState.isRecording) {
                val infiniteTransition = rememberInfiniteTransition(label = "recPulse")
                val recScale by infiniteTransition.animateFloat(
                    initialValue = 0.9f,
                    targetValue = 1.15f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(500),
                        repeatMode = RepeatMode.Reverse
                    ),
                    label = "recScale"
                )

                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    shape = RoundedCornerShape(12.dp),
                    color = RichBurgundy.copy(alpha = 0.3f),
                    border = BorderStroke(1.dp, RichBurgundy)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(12.dp)
                                    .scale(recScale)
                                    .clip(CircleShape)
                                    .background(StatusRed)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Recording Voice Note... 0:${recordingState.elapsedSeconds.toString().padStart(2, '0')}",
                                color = TextPrimary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            IconButton(
                                onClick = onCancelVoiceRecord,
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(Icons.Default.Close, contentDescription = "Cancel Recording", tint = TextMuted)
                            }

                            IconButton(
                                onClick = onStopAndSendVoice,
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(RoyalGold)
                            ) {
                                Icon(Icons.Default.Send, contentDescription = "Send Voice Message", tint = SurfaceDark)
                            }
                        }
                    }
                }
            }

            // Input Bar: Text input + Voice Note Record Button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = textInput,
                    onValueChange = { textInput = it },
                    placeholder = { Text("Message or voice in table...", color = TextMuted, fontSize = 13.sp) },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("chat_input_field"),
                    shape = RoundedCornerShape(20.dp),
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

                if (textInput.isNotBlank()) {
                    IconButton(
                        onClick = {
                            onSendMessage(textInput)
                            textInput = ""
                        },
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(RoyalGold)
                            .testTag("chat_send_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Send,
                            contentDescription = "Send Text",
                            tint = SurfaceDark,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                } else {
                    // Voice Record Action Button (Hold or tap to record voice message)
                    IconButton(
                        onClick = {
                            if (recordingState.isRecording) {
                                onStopAndSendVoice()
                            } else {
                                onStartVoiceRecord()
                            }
                        },
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(if (recordingState.isRecording) StatusRed else RoyalGold)
                            .testTag("chat_voice_record_button")
                    ) {
                        Icon(
                            imageVector = if (recordingState.isRecording) Icons.Default.Stop else Icons.Default.Mic,
                            contentDescription = if (recordingState.isRecording) "Stop & Send Voice" else "Record Voice Note",
                            tint = SurfaceDark,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
            }
        }
    }
}
