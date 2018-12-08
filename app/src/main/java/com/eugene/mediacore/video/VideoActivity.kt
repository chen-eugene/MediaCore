package com.eugene.mediacore.video

import android.hardware.Camera
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.eugene.mediacore.R
import java.lang.StringBuilder

class VideoActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video)
        getCameraSupportSize()




    }

    private fun getCameraSupportSize() {
        val back = Camera.open(Camera.CameraInfo.CAMERA_FACING_BACK)
        val backSize = back.parameters.supportedPreviewSizes
        back.release()

        val front = Camera.open(Camera.CameraInfo.CAMERA_FACING_FRONT)
        val frontSize = front.parameters.supportedPreviewSizes
        front.release()

        val sb = StringBuilder()
        sb.append("后置摄像头：")
        backSize?.forEach {
            sb.append("${it.width} × ${it.height}").append("、")
        }

        sb.append("\n").append("前置摄像头：")
        frontSize?.forEach {
            sb.append("${it.width} × ${it.height}").append("、")
        }
    }


}