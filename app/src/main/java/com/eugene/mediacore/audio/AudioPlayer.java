package com.eugene.mediacore.audio;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.util.Log;

public class AudioPlayer {
    private static final String TAG = "AudioPlayer";

    /**
     * 这个参数代表着当前应用使用的哪一种音频管理策略，当系统有多个进程需要播放音频时，
     * 这个管理策略会决定最终的展现效果，该参数的可选的值以常量的形式定义在 AudioManager 类中
     */
    private static final int DEF_STREAM_TYPE = AudioManager.STREAM_MUSIC;
    private static final int DEF_SAMPLE_RATE = 44100;
    private static final int DEF_CHANNEL_CONFIG = AudioFormat.CHANNEL_IN_MONO;
    private static final int DEF_AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT;
    /**
     * AudioTrack 提供了两种播放模式，一种是 static 方式，一种是 streaming 方式，
     * 前者需要一次性将所有的数据都写入播放缓冲区，简单高效，通常用于播放铃声、系统提醒的音频片段;
     * 后者则是按照一定的时间间隔不间断地写入音频数据，理论上它可用于任何音频播放的场景。
     * <p>
     * 可选的值以常量的形式定义在 AudioTrack 类中，一个是 MODE_STATIC，另一个是 MODE_STREAM
     */
    private static final int DEF_MODE = AudioTrack.MODE_STREAM;

    private int bufferSize = 0;

    private AudioTrack audioTrack;

    private boolean isPlayerStarted = false;
    private boolean isPlaying = false;

    public boolean startPlayer() {
        if (isPlayerStarted) {
            Log.e(TAG, "Player already started !");
            return false;
        }
        bufferSize = AudioTrack.getMinBufferSize(DEF_SAMPLE_RATE, DEF_CHANNEL_CONFIG, DEF_AUDIO_FORMAT);
        if (bufferSize == AudioTrack.ERROR_BAD_VALUE) {
            Log.e(TAG, "Invalid parameter !");
            return false;
        }

        audioTrack = new AudioTrack(DEF_STREAM_TYPE, DEF_SAMPLE_RATE,
                DEF_CHANNEL_CONFIG, DEF_AUDIO_FORMAT, bufferSize, DEF_MODE);

        if (audioTrack.getState() == AudioTrack.STATE_UNINITIALIZED) {
            Log.e(TAG, "AudioTrack initialize fail !");
            return false;
        }
        isPlayerStarted = true;

        Log.i(TAG, "Start audio player success !");

        return true;
    }

    public void stopPlayer() {
        if (!isPlayerStarted) {
            return;
        }

        if (audioTrack.getPlayState() == AudioTrack.PLAYSTATE_PLAYING) {
            audioTrack.stop();
        }

        isPlayerStarted = false;
        audioTrack.release();
        audioTrack = null;
    }

    public boolean play(byte[] audioData, int offset, int size) {
        if (!isPlayerStarted){
            Log.e(TAG, "Player not started !");
            return false;
        }

        if (audioTrack.write(audioData,offset,size)!=size){
            Log.e(TAG, "Could not write all the samples to the audio device !");
        }

        audioTrack.play();

        return true;
    }

}
