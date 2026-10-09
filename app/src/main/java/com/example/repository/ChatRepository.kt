package com.example.repository

import com.example.model.ChatMessage
import com.example.model.ChatScope
import com.example.model.MessageType
import com.example.model.UserProfile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

class ChatRepository {

    private val _lobbyMessages = MutableStateFlow<List<ChatMessage>>(
        listOf(
            ChatMessage(
                id = "MSG_LOBBY_1",
                scope = ChatScope.GLOBAL_LOBBY,
                senderId = "USER_POKHARA_99",
                senderName = "PokharaKing",
                senderAvatar = "🏔️",
                type = MessageType.TEXT,
                content = "Namaste sathi haru! Call Break table RT-492 is waiting for 1 more player!",
                timestamp = System.currentTimeMillis() - 180000L
            ),
            ChatMessage(
                id = "MSG_LOBBY_2",
                scope = ChatScope.GLOBAL_LOBBY,
                senderId = "USER_KATHMANDU_07",
                senderName = "Aarav_Taas",
                senderAvatar = "👑",
                type = MessageType.VOICE,
                content = "Voice Note (4s)",
                voiceDurationSeconds = 4,
                voiceWaveform = listOf(0.2f, 0.5f, 0.8f, 0.9f, 0.6f, 0.4f, 0.7f, 0.3f),
                timestamp = System.currentTimeMillis() - 120000L
            ),
            ChatMessage(
                id = "MSG_LOBBY_3",
                scope = ChatScope.GLOBAL_LOBBY,
                senderId = "USER_DHARAN_11",
                senderName = "Shreeya_NP",
                senderAvatar = "🌸",
                type = MessageType.TEXT,
                content = "Who wants to play 21-card Marriage with Tiplu? Creating private room soon!",
                timestamp = System.currentTimeMillis() - 60000L
            )
        )
    )
    val lobbyMessages: StateFlow<List<ChatMessage>> = _lobbyMessages.asStateFlow()

    // Table messages keyed by roomId
    private val _tableMessages = MutableStateFlow<Map<String, List<ChatMessage>>>(emptyMap())
    val tableMessages: StateFlow<Map<String, List<ChatMessage>>> = _tableMessages.asStateFlow()

    fun sendLobbyTextMessage(user: UserProfile, text: String) {
        val msg = ChatMessage(
            id = UUID.randomUUID().toString(),
            scope = ChatScope.GLOBAL_LOBBY,
            senderId = user.id,
            senderName = user.displayName,
            senderAvatar = user.avatarEmoji,
            type = MessageType.TEXT,
            content = text,
            timestamp = System.currentTimeMillis()
        )
        _lobbyMessages.value = _lobbyMessages.value + msg
    }

    fun sendLobbyVoiceMessage(voiceMsg: ChatMessage) {
        _lobbyMessages.value = _lobbyMessages.value + voiceMsg
    }

    fun sendTableTextMessage(roomId: String, user: UserProfile, text: String) {
        val msg = ChatMessage(
            id = UUID.randomUUID().toString(),
            scope = ChatScope.GAME_TABLE,
            roomId = roomId,
            senderId = user.id,
            senderName = user.displayName,
            senderAvatar = user.avatarEmoji,
            type = MessageType.TEXT,
            content = text,
            timestamp = System.currentTimeMillis()
        )
        val currentList = _tableMessages.value[roomId] ?: emptyList()
        val updatedMap = _tableMessages.value.toMutableMap()
        updatedMap[roomId] = currentList + msg
        _tableMessages.value = updatedMap
    }

    fun sendTableVoiceMessage(roomId: String, voiceMsg: ChatMessage) {
        val currentList = _tableMessages.value[roomId] ?: emptyList()
        val updatedMap = _tableMessages.value.toMutableMap()
        updatedMap[roomId] = currentList + voiceMsg
        _tableMessages.value = updatedMap
    }

    fun sendTableEmojiReaction(roomId: String, user: UserProfile, emoji: String) {
        val msg = ChatMessage(
            id = UUID.randomUUID().toString(),
            scope = ChatScope.GAME_TABLE,
            roomId = roomId,
            senderId = user.id,
            senderName = user.displayName,
            senderAvatar = user.avatarEmoji,
            type = MessageType.EMOJI,
            content = emoji,
            timestamp = System.currentTimeMillis()
        )
        val currentList = _tableMessages.value[roomId] ?: emptyList()
        val updatedMap = _tableMessages.value.toMutableMap()
        updatedMap[roomId] = currentList + msg
        _tableMessages.value = updatedMap
    }
}
