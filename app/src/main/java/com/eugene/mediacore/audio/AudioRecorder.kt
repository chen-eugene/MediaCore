package com.eugene.mediacore.audio

import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.util.Log
import com.eugene.mediacore.AUDIO_FORMAT
import com.eugene.mediacore.CHANNEL
import com.eugene.mediacore.SAMPLE_RATE_HZ
import java.io.File
import java.io.FileOutputStream

class AudioRecorder {

    private val TAG = this::class.java.name

    private var audioRecord: AudioRecord

    private var bufferSize = 0

    private lateinit var file: File

    private var isRecording = false

    private var recordThread: Thread? = null

    private var audioFrameRecordListener: ((ByteArray) -> Unit)? = null

    init {

        bufferSize = AudioRecord.getMinBufferSize(SAMPLE_RATE_HZ, AudioFormat.CHANNEL_IN_MONO, AUDIO_FORMAT)

        audioRecord = AudioRecord(MediaRecorder.AudioSource.MIC, SAMPLE_RATE_HZ,
                CHANNEL, AUDIO_FORMAT, bufferSize * 4)

    }

    fun openFile(path: String, fileName: String) {
        val path = File(path)
        file = File(path, fileName)

        if (!path.exists()) {
            path.mkdirs()
        }

        if (file.exists()) {
            file.delete()
        }
    }

    fun startRecord() {
        audioRecord.startRecording()

        recordThread = Thread(RecordingRunnable())

        isRecording = true

        recordThread?.start()
    }

    fun stopRecord() {
        recordThread?.join(1000)

        isRecording = false

        if (audioRecord.state == AudioRecord.RECORDSTATE_RECORDING)
            audioRecord.stop()

        audioRecord.release()

        audioFrameRecordListener = null
    }

    private inner class RecordingRunnable : Runnable {

        override fun run() {

            var fos = FileOutputStream(file)

            val buffer = ByteArray(bufferSize)

            while (isRecording) {

                val result = audioRecord.read(buffer, 0, buffer.size)

                if (result == AudioRecord.ERROR_INVALID_OPERATION)
                    Log.e(TAG, "Error ERROR_INVALID_OPERATION")
                else if (result == AudioRecord.ERROR_BAD_VALUE)
                    Log.e(TAG, "Error ERROR_BAD_VALUE")
                else {
                    fos.write(buffer)
                    audioFrameRecordListener?.invoke(buffer)
                }

            }

            fos.close()

        }

    }

    fun setAudioFrameRecordListener(listener: (ByteArray) -> Unit) {
        this.audioFrameRecordListener = listener
    }


}