package com.example.sound

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.sin

class SoundAndHapticManager(private val context: Context) {

    private val vibrator: Vibrator? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val vm = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
        vm?.defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
    }

    var isSoundEnabled: Boolean = true
    var isHapticsEnabled: Boolean = true

    fun playCardDealSound() {
        if (!isSoundEnabled) return
        generateTone(freq = 600.0, durationMs = 45, amplitude = 0.4f)
        triggerHaptic(durationMs = 20, amplitude = 60)
    }

    fun playCardFlipSound() {
        if (!isSoundEnabled) return
        generateTone(freq = 750.0, durationMs = 35, amplitude = 0.35f)
        triggerHaptic(durationMs = 15, amplitude = 50)
    }

    fun playChipSound() {
        if (!isSoundEnabled) return
        generateTone(freq = 1100.0, durationMs = 50, amplitude = 0.5f)
        triggerHaptic(durationMs = 25, amplitude = 80)
    }

    fun playDiceRollSound() {
        if (!isSoundEnabled) return
        CoroutineScope(Dispatchers.Default).launch {
            for (f in listOf(400.0, 520.0, 680.0, 850.0)) {
                generateTone(freq = f, durationMs = 40, amplitude = 0.35f)
                kotlinx.coroutines.delay(45)
            }
        }
        triggerHaptic(durationMs = 80, amplitude = 90)
    }

    fun playTurnChime() {
        if (!isSoundEnabled) return
        CoroutineScope(Dispatchers.Default).launch {
            generateTone(freq = 587.33, durationMs = 90, amplitude = 0.4f) // D5
            kotlinx.coroutines.delay(100)
            generateTone(freq = 880.0, durationMs = 140, amplitude = 0.5f) // A5
        }
        triggerHaptic(durationMs = 40, amplitude = 70)
    }

    fun playVictoryFanfare() {
        if (!isSoundEnabled) return
        CoroutineScope(Dispatchers.Default).launch {
            val notes = listOf(523.25, 659.25, 783.99, 1046.50) // C5, E5, G5, C6
            for (n in notes) {
                generateTone(freq = n, durationMs = 120, amplitude = 0.5f)
                kotlinx.coroutines.delay(130)
            }
        }
        triggerHaptic(durationMs = 200, amplitude = 120)
    }

    fun triggerHaptic(durationMs: Long, amplitude: Int = 100) {
        if (!isHapticsEnabled || vibrator == null) return
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(
                    VibrationEffect.createOneShot(
                        durationMs.coerceAtLeast(10),
                        amplitude.coerceIn(1, 255)
                    )
                )
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(durationMs)
            }
        } catch (_: Exception) {}
    }

    private fun generateTone(freq: Double, durationMs: Int, amplitude: Float) {
        CoroutineScope(Dispatchers.Default).launch {
            try {
                val sampleRate = 22050
                val numSamples = (sampleRate * (durationMs / 1000.0)).toInt()
                val buffer = ShortArray(numSamples)
                for (i in 0 until numSamples) {
                    val angle = 2.0 * Math.PI * i / (sampleRate / freq)
                    // Apply fade-out envelope to avoid audio clicks
                    val envelope = 1.0f - (i.toFloat() / numSamples.toFloat())
                    buffer[i] = (sin(angle) * Short.MAX_VALUE * amplitude * envelope).toInt().toShort()
                }

                val audioTrack = AudioTrack.Builder()
                    .setAudioAttributes(
                        AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_GAME)
                            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                            .build()
                    )
                    .setAudioFormat(
                        AudioFormat.Builder()
                            .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                            .setSampleRate(sampleRate)
                            .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                            .build()
                    )
                    .setBufferSizeInBytes(buffer.size * 2)
                    .setTransferMode(AudioTrack.MODE_STATIC)
                    .build()

                audioTrack.write(buffer, 0, buffer.size)
                audioTrack.play()
                kotlinx.coroutines.delay(durationMs.toLong() + 50)
                audioTrack.stop()
                audioTrack.release()
            } catch (_: Exception) {}
        }
    }
}
