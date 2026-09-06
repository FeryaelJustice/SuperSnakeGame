package com.feryaeljustice.supersnakegame.data.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.feryaeljustice.supersnakegame.R

class SoundEffectManager(
    context: Context,
) : DefaultLifecycleObserver {
    private val audioAttributes =
        AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_GAME)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()

    private val soundPool: SoundPool =
        SoundPool.Builder()
            .setMaxStreams(3)
            .setAudioAttributes(audioAttributes)
            .build()

    private var eatSoundId: Int = 0
    private var isLoaded: Boolean = false

    init {
        soundPool.setOnLoadCompleteListener { _, sampleId, status ->
            if (status == 0 && sampleId == eatSoundId) {
                isLoaded = true
            }
        }
        eatSoundId = soundPool.load(context.applicationContext, R.raw.eat_apple, 1)
    }

    fun playEatSound(volume: Float) {
        val clamped = volume.coerceIn(0.0f, 1.0f)
        if (clamped > 0.0f && isLoaded && eatSoundId != 0) {
            soundPool.play(eatSoundId, clamped, clamped, 1, 0, 1.0f)
        }
    }

    fun pause() {
        soundPool.autoPause()
    }

    fun resume() {
        soundPool.autoResume()
    }

    fun release() {
        soundPool.release()
    }

    override fun onResume(owner: LifecycleOwner) {
        resume()
    }

    override fun onPause(owner: LifecycleOwner) {
        pause()
    }

    override fun onDestroy(owner: LifecycleOwner) {
        release()
    }
}
