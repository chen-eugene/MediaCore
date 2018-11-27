**Android SDK提供了两套音频采集API**
 
- MediaRecorder：可以直接把手机麦克风录入的音频数据进行编码压缩（如AMR、MP3等）并存成文件，需要设置编码器。并且录制的音频文件可以用系统自带的Music播放器播放。MediaRecorder 底层也是调用了 AudioRecord 与 Android Framework 层的 AudioFlinger 进行交互的。  
 
- AudioRecord：后者则更接近底层，录制的是一帧一帧PCM格式的音频文件，需要用AudioTrack来播放，AudioTrack更接近底层。
  
    ```
    public AudioRecord(int audioSource, int sampleRateInHz, int channelConfig, int audioFormat,
            int bufferSizeInBytes)
    ```
    
   - audioSource：该参数指的是音频采集的输入源，可选的值以常量的形式定义在 MediaRecorder.AudioSource 类中，常用的值包括：DEFAULT（默认），VOICE_RECOGNITION（用于语音识别，等同于DEFAULT），MIC（由手机麦克风输入），VOICE_COMMUNICATION（用于VoIP应用）等等。
   
   - sampleRateInHz：采样率，注意，目前44100Hz是唯一可以保证兼容所有Android手机的采样率。
   
   - channelConfig：通道数的配置，可选的值以常量的形式定义在 AudioFormat 类中，常用的是 CHANNEL_IN_MONO（单通道），CHANNEL_IN_STEREO（双通道）。
   
   - audioFormat：这个参数是用来配置“数据位宽”的，可选的值也是以常量的形式定义在 AudioFormat 类中，常用的是 ENCODING_PCM_16BIT（16bit），ENCODING_PCM_8BIT（8bit），注意，前者是可以保证兼容所有Android手机的。
   
   - bufferSizeInBytes：它配置的是 AudioRecord 内部的音频缓冲区的大小，该缓冲区的值不能低于一帧“音频帧”（Frame）的大小，而前一篇文章介绍过，一帧音频帧的大小计算如下：
   
   $ int size = 采样率 x 位宽 x 采样时间 x 通道数 $
   
   采样时间一般取 2.5ms~120ms 之间，由厂商或者具体的应用决定，我们其实可以推断，每一帧的采样时间取得越短，产生的延时就应该会越小，当然，碎片化的数据也就会越多。不同的厂商的底层实现是不一样的，但无外乎就是根据上面的计算公式得到一帧的大小，音频缓冲区的大小则必须是一帧大小的2～N倍。
   
   Android中通过一下函数获取：
   
   ```
   int getMinBufferSize(int sampleRateInHz, int channelConfig, int audioFormat);
   ```

**如果想简单地做一个录音机，录制成音频文件，则推荐使用 MediaRecorder，而如果需要对音频做进一步的算法处理、或者采用第三方的编码库进行压缩、以及网络传输等应用，则建议使用 AudioRecord，直播中实时采集音频自然是要用AudioRecord了。**
  
**Android SDK 提供了3套音频播放的API**
 
   - SoundPool：SoundPool 则适合播放比较短的音频片段，比如游戏声音、按键声、铃声片段等等，它可以同时播放多个音频。
 
   由于SoundPool对载入声音文件大小有所限制，这就导致了如果SoundPool没有载入完成，而不能安全调用play方法。好在Android SDK提供了一个SoundPool.OnLoadCompleteListener类来帮助我们了解声音文件是否载入完成。
    
   - MediaPlayer：而 AudioTrack 则更接近底层，提供了非常强大的控制能力，支持低延迟播放，适合流媒体和VoIP语音电话等场景。
 
   - AudioTrack：使用AudioTrack播放的音频必须是解码后的PCM数据。
 
   - MediaExtractor：对容器文件进行读取控制，将音频和视频的数据进行分离，内部方法均为native方法。
  
   - MediaCodec：对数据进行编解码，负责媒体文件的编码和解码工作，内部方法均为native方法。
  
   - MediaMuxer：生成音频或视频文件；还可以把音频与视频混合成一个音视频文件。
