package com.example.audio

import android.content.Context
import com.example.model.ChatMessage
import com.example.model.ChatScope
import com.example.model.MessageType
import com.example.model.UserProfile
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID
import kotlin.random.Random

data class VoiceRecordingState(
    val isRecording: Boolean = false,
    val elapsedSeconds: Int = 0,
    val liveWaveform: List<Float> = emptyList()
)

data class VoicePlaybackState(
    val playingMessageId: String? = null,
    val progress: Float = 0f // 0f..1f
)

class VoiceMessageManager(private val context: Context) {

    private val _recordingState = MutableStateFlow(VoiceRecordingState())
    val recordingState: StateFlow<VoiceRecordingState> = _recordingState.asStateFlow()

    private val _playbackState = MutableStateFlow(VoicePlaybackState())
    val playbackState: StateFlow<VoicePlaybackState> = _playbackState.asStateFlow()

    private var recordJob: Job? = null
    private var playbackJob: Job? = null

    fun startRecording() {
        if (_recordingState.value.isRecording) return
        _recordingState.value = VoiceRecordingState(isRecording = true, elapsedSeconds = 0, liveWaveform = emptyList())

        recordJob = CoroutineScope(Dispatchers.Default).launch {
            val amplitudes = mutableListOf<Float>()
            var seconds = 0
            while (_recordingState.value.isRecording) {
                delay(100)
                // Generate natural voice amplitude fluctuation
                val amp = 0.2f + Random.nextFloat() * 0.75f
                amplitudes.add(amp)
                if (amplitudes.size % 10 == 0) {
                    seconds++
                }
                _recordingState.value = _recordingState.value.copy(
                    elapsedSeconds = seconds,
                    liveWaveform = amplitudes.takeLast(30)
                )
                if (seconds >= 60) {
                    // Max 60 seconds
                    break
                }
            }
        }
    }

    fun stopAndCreateVoiceMessage(
        sender: UserProfile,
        scope: ChatScope,
        roomId: String?
    ): ChatMessage? {
        val duration = _recordingState.value.elapsedSeconds.coerceAtLeast(1)
        val fullWaveform = if (_recordingState.value.liveWaveform.isNotEmpty()) {
            _recordingState.value.liveWaveform
        } else {
            List(15) { 0.3f + Random.nextFloat() * 0.5f }
        }

        recordJob?.cancel()
        _recordingState.value = VoiceRecordingState(isRecording = false)

        return ChatMessage(
            id = UUID.randomUUID().toString(),
            scope = scope,
            roomId = roomId,
            senderId = sender.id,
            senderName = sender.displayName,
            senderAvatar = sender.avatarEmoji,
            type = MessageType.VOICE,
            content = "Voice Message (${duration}s)",
            voiceDurationSeconds = duration,
            voiceWaveform = fullWaveform,
            timestamp = System.currentTimeMillis()
        )
    }

    fun cancelRecording() {
        recordJob?.cancel()
        _recordingState.value = VoiceRecordingState(isRecording = false)
    }

    fun playVoiceMessage(message: ChatMessage) {
        playbackJob?.cancel()
        if (_playbackState.value.playingMessageId == message.id) {
            // Pause/stop
            _playbackState.value = VoicePlaybackState(playingMessageId = null, progress = 0f)
            return
        }

        val totalMs = (message.voiceDurationSeconds.coerceAtLeast(2)) * 1000L
        _playbackState.value = VoicePlaybackState(playingMessageId = message.id, progress = 0f)

        playbackJob = CoroutineScope(Dispatchers.Default).launch {
            val steps = 30
            val stepDelay = totalMs / steps
            for (i in 1..steps) {
                delay(stepDelay)
                _playbackState.value = VoicePlaybackState(
                    playingMessageId = message.id,
                    progress = i.toFloat() / steps.toFloat()
                )
            }
            delay(150)
            _playbackState.value = VoicePlaybackState(playingMessageId = null, progress = 0f)
        }
    }
}
