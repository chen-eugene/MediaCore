package com.eugene.mediademo.audio

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.eugene.mediacore.R
import com.eugene.mediacore.audio.AudioRecorder
import kotlinx.android.synthetic.main.activity_audio.*

class AudioActivity : AppCompatActivity() {

    private var isRecording = false

    private val audioRecorder by lazy {
        AudioRecorder()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_audio)
        setListener()


    }

    private fun setListener() {
        btn_record?.setOnClickListener {
            if (!isRecording) {
                audioRecorder.startRecord()
                btn_record?.text = "停止录制"
            } else {
                audioRecorder.stopRecord()
                btn_record?.text = "开始录制"
            }
        }

        audioRecorder.setAudioFrameRecordListener {

        }


    }


}