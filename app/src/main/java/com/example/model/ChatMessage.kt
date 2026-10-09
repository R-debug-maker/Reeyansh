package com.example.model

enum class MessageType {
    TEXT,
    VOICE,
    EMOJI,
    SYSTEM
}

enum class ChatScope {
    GLOBAL_LOBBY,
    GAME_TABLE
}

data class ChatMessage(
    val id: String,
    val scope: ChatScope,
    val roomId: String? = null,
    val senderId: String,
    val senderName: String,
    val senderAvatar: String,
    val type: MessageType,
    val content: String = "", // Text content or emoji
    val voiceDurationSeconds: Int = 0,
    val voiceWaveform: List<Float> = emptyList(), // Normalized amplitudes 0f..1f for visual waveform
    val voiceAudioUri: String? = null,
    val timestamp: Long = System.currentTimeMillis()
)

data class TableVoiceChannelState(
    val isMicMuted: Boolean = false,
    val isSpeakerMuted: Boolean = false,
    val activeSpeakerSeatIndex: Int? = null,
    val isRecordingVoiceNote: Boolean = false,
    val recordingDurationSec: Int = 0
)
