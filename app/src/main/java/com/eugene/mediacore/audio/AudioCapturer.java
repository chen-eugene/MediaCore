package com.eugene.mediacore.audio;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class AudioCapturer {

    private static final String TAG = "AudioCapturer";

    /**
     * 该参数指的是音频采集的输入源，可选的值以常量的形式定义在 MediaRecorder.AudioSource 类中，
     * 常用的值包括：DEFAULT（默认），VOICE_RECOGNITION（用于语音识别，等同于DEFAULT），
     * MIC（由手机麦克风输入），VOICE_COMMUNICATION（用于VoIP应用）等等。
     */
    private static final int AUDIO_SOURCE = MediaRecorder.AudioSource.MIC;
    /**
     * AudioRecord内部缓冲区大小，不低于一帧的大小
     * 通过#getMinBufferSize方法获取
     */
    private int BUFFER_SIZE;
    /**
     * 采样率，注意，目前44100Hz是唯一可以保证兼容所有Android手机的采样率。
     */
    private static final int DEFAULT_SAMPLE_RATE = 44100;
    /**
     * 通道数的配置，可选的值以常量的形式定义在 AudioFormat 类中，
     * 常用的是 CHANNEL_IN_MONO（单通道），CHANNEL_IN_STEREO（双通道）
     * 其中CHANNEL_IN_MONO是可以保证在所有设备能够使用的。
     */
    public static final int CHANNEL_CONFIG = AudioFormat.CHANNEL_IN_MONO;
    /**
     * 这个参数是用来配置“数据位宽”的，可选的值也是以常量的形式定义在 AudioFormat 类中，
     * 常用的是 ENCODING_PCM_16BIT（16bit），ENCODING_PCM_8BIT（8bit），
     * 注意，前者是可以保证兼容所有Android手机的。
     */
    public static final int AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT;

    private AudioRecord audioRecord;

    private boolean isRecording = false;

    private OnAudioFrameCaptureListener onAudioFrameCaptureListener;

    private WavFileWriter wavFileWriter;

    private static final String PATH = Environment.getExternalStorageDirectory().getPath()
            + File.separator + "media" + File.separator;

    public void startRecord() {
        BUFFER_SIZE = AudioRecord.getMinBufferSize(DEFAULT_SAMPLE_RATE, CHANNEL_CONFIG, AUDIO_FORMAT);

        if (BUFFER_SIZE == AudioRecord.ERROR_BAD_VALUE) {
            Log.e(TAG, "Invalid parameter !");
            return;
        }
        Log.d(TAG, "getMinBufferSize = " + BUFFER_SIZE + " bytes !");

        audioRecord = new AudioRecord(AUDIO_SOURCE, DEFAULT_SAMPLE_RATE, CHANNEL_CONFIG, AUDIO_FORMAT, BUFFER_SIZE);

        if (audioRecord.getState() == AudioRecord.STATE_UNINITIALIZED) {
            Log.e(TAG, "AudioRecord initialize fail !");
            return;
        }

        wavFileWriter = new WavFileWriter();
        try {
            wavFileWriter.openFile(PATH + "audio.wav", DEFAULT_SAMPLE_RATE, CHANNEL_CONFIG, AUDIO_FORMAT);
        } catch (IOException e) {
            e.printStackTrace();
        }

        audioRecord.startRecording();
        new Thread(new AudioCaptureRunnable()).start();
        isRecording = true;
    }

    public void stopRecord() {
        isRecording = false;

        try {
            wavFileWriter.closeFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (audioRecord.getRecordingState() == AudioRecord.RECORDSTATE_RECORDING) {
            audioRecord.stop();
        }
        audioRecord.release();
        audioRecord = null;
    }

    private class AudioCaptureRunnable implements Runnable {

        @Override
        public void run() {
            final File file = new File(PATH + "audio.pcm");
            if (file.exists()) {
                file.delete();
            }
            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(file);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            byte[] buffer = new byte[BUFFER_SIZE];

            if (fos != null) {
                while (isRecording) {
                    int result = audioRecord.read(buffer, 0, BUFFER_SIZE);
                    if (result == AudioRecord.ERROR_INVALID_OPERATION) {
                        Log.e(TAG, "Error ERROR_INVALID_OPERATION");
                    } else if (result == AudioRecord.ERROR_BAD_VALUE) {
                        Log.e(TAG, "Error ERROR_BAD_VALUE");
                    } else {
                        wavFileWriter.writeData(buffer, 0, buffer.length);
                        if (onAudioFrameCaptureListener != null) {
                            onAudioFrameCaptureListener.onAudioFrameCapture(buffer);
                        }
                        try {
                            fos.write(buffer);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void setOnAudioFrameCaptureListener(OnAudioFrameCaptureListener onAudioFrameCaptureListener) {
        this.onAudioFrameCaptureListener = onAudioFrameCaptureListener;
    }

    public interface OnAudioFrameCaptureListener {
        void onAudioFrameCapture(byte[] audio);
    }


}
