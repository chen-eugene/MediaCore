package com.eugene.mediacore

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.eugene.mediacore.audio.AudioCapturer
import com.eugene.mediacore.audio.AudioCapturer.PATH
import com.eugene.mediacore.audio.AudioPlayer
import com.eugene.mediacore.audio.WavFileReader
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val audioCapture = AudioCapturer()
    private val audioPlayer = AudioPlayer()
    val wavFileReader = WavFileReader()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Example of a call to a native method
//        sample_text.text = stringFromJNI()

        btn_audio_capture_start?.setOnClickListener {
            audioCapture()
        }

        btn_audio_capture_stop?.setOnClickListener {
            stopCapture()
        }


        btn_audio_play_start?.setOnClickListener {
            wavFileReader.openFile(PATH + "audio.wav")

            audioPlayer.startPlayer()

            Thread {
                val buffer = ByteArray(1024 * 2)
                while (wavFileReader.readData(buffer, 0, buffer.size) > 0) {
                    audioPlayer.play(buffer, 0, buffer.size)
                }
                audioPlayer.stopPlayer()

                wavFileReader.closeFile()
            }.start()
        }
        btn_audio_play_stop?.setOnClickListener {
            audioPlayer.stopPlayer()
        }
    }


    private fun audioCapture() {
//        wavFileWriter.openFile()
//        audioCapture.setOnAudioFrameCaptureListener {
//            wavFileWriter.writeData(it,0,it.size)
//        }
        audioCapture.startRecord()
    }

    private fun stopCapture() {
        audioCapture.stopRecord()
    }


    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    external fun stringFromJNI(): String

    companion object {

        // Used to load the 'native-lib' library on application startup.
        init {
            System.loadLibrary("native-lib")
        }
    }
}
