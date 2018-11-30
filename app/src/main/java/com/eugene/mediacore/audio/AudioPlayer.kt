package com.eugene.mediacore.audio

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioTrack
import android.util.Log
import com.eugene.mediacore.AUDIO_FORMAT
import com.eugene.mediacore.CHANNEL
import com.eugene.mediacore.SAMPLE_RATE_HZ
import java.io.File
import java.io.FileInputStream

class AudioPlayer {

    private val TAG = this::class.java.name

    private var audioTrack: AudioTrack

    private var bufferSize = 0

    private var isPlaying = false

    private lateinit var file: File

    init {

        val attr = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .build()

        val audioFormat = AudioFormat.Builder()
                .setEncoding(AUDIO_FORMAT)
                .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                .build()

        bufferSize = AudioTrack.getMinBufferSize(SAMPLE_RATE_HZ, CHANNEL, AUDIO_FORMAT)

        if (bufferSize == AudioTrack.ERROR_BAD_VALUE) {
            Log.e(TAG, "Invalid parameter !")
        }

        audioTrack = AudioTrack(attr, audioFormat, bufferSize, AudioTrack.MODE_STREAM, AudioManager.AUDIO_SESSION_ID_GENERATE)
        if (audioTrack.state == AudioTrack.STATE_UNINITIALIZED) {
            Log.e(TAG, "AudioTrack initialize fail !")
        }

    }

    fun play(path: String) {
        val file = File(path)
        if (!file.exists()) {
            throw IllegalAccessError("The file dose not exist!")
        }

        this.file = file

        isPlaying = true

        Thread(PlayerRunnable()).start()
    }

    fun stop() {
        if (!isPlaying)
            return

        if (audioTrack.state == AudioTrack.PLAYSTATE_PLAYING)
            audioTrack.stop()

        audioTrack.release()

        isPlaying = false

    }

    private inner class PlayerRunnable : Runnable {
        override fun run() {
            val fis = FileInputStream(file)

            val buffer = ByteArray(bufferSize)

            while (fis.available() > 0) {
                val ret = fis.read(buffer)
                if (ret == AudioTrack.ERROR_INVALID_OPERATION ||
                        ret == AudioTrack.ERROR_BAD_VALUE) {
                    continue
                }

                if (ret != 0 && ret != -1) {
                    audioTrack.write(buffer, 0, ret)
                }
            }

            fis.close()
        }
    }


}



