package com.eugene.mediacore

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.eugene.mediacore.audio.AudioCapturer
import com.eugene.mediacore.audio.WavFileWriter
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val audioCapture = AudioCapturer()

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

    }

    private fun audioCapture(){
        val wavFileWriter = WavFileWriter()
//        wavFileWriter.openFile()
        audioCapture.setOnAudioFrameCaptureListener {
            wavFileWriter.writeData(it,0,it.size)
        }
        audioCapture.startRecord()
    }

    private fun stopCapture(){
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
