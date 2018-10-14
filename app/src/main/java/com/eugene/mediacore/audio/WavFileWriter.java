package com.eugene.mediacore.audio;

import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class WavFileWriter {

    private String filePath;
    private int dataSize = 0;
    private DataOutputStream dos;

    public boolean openFile(String filePath, int sampleRateInHz, int channelConfig, int audioFormat) throws IOException {
        if (dos != null) {
            return closeFile();
        }
        this.filePath = filePath;
        dataSize = 0;
        dos = new DataOutputStream(new FileOutputStream(filePath));
        return writeHeader(sampleRateInHz, channelConfig, audioFormat);
    }

    public boolean closeFile() throws IOException {
        boolean result = false;
        if (dos != null) {
            result = writeDataSize();
            dos.close();
            dos = null;
        }
        return result;
    }

    private boolean writeHeader(int sampleRateInHz, int channelConfig, int audioFormat) {
        if (dos == null) {
            return false;
        }

        WavFileHeader header = new WavFileHeader(sampleRateInHz, channelConfig, audioFormat);

        try {
            dos.writeBytes(header.mChunkID);
            dos.write(intToByteArray(header.mChunkSize), 0, 4);
            dos.writeBytes(header.mFormat);
            dos.writeBytes(header.mSubChunk1ID);
            dos.write(intToByteArray(header.mSubChunk1Size), 0, 4);
            dos.write(shortToByteArray(header.mAudioFormat), 0, 2);
            dos.write(shortToByteArray(header.mNumChannel), 0, 2);
            dos.write(intToByteArray(header.mSampleRate), 0, 4);
            dos.write(intToByteArray(header.mByteRate), 0, 4);
            dos.write(shortToByteArray(header.mBlockAlign), 0, 2);
            dos.write(shortToByteArray(header.mBitsPerSample), 0, 2);
            dos.writeBytes(header.mSubChunk2ID);
            dos.write(intToByteArray(header.mSubChunk2Size), 0, 4);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public boolean writeData(byte[] buffer, int offset, int count) {
        if (dos == null) {
            return false;
        }

        try {
            dos.write(buffer, offset, count);
            dataSize += count;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    private boolean writeDataSize() {
        if (dos == null) {
            return false;
        }

        try {
            RandomAccessFile wavFile = new RandomAccessFile(filePath, "rw");
            wavFile.seek(WavFileHeader.WAV_CHUNKSIZE_OFFSET);
            wavFile.write(intToByteArray((dataSize + WavFileHeader.WAV_CHUNKSIZE_EXCLUDE_DATA)), 0, 4);
            wavFile.seek(WavFileHeader.WAV_SUB_CHUNKSIZE2_OFFSET);
            wavFile.write(intToByteArray((dataSize)), 0, 4);
            wavFile.close();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private static byte[] intToByteArray(int data) {
        return ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(data).array();
    }

    private static byte[] shortToByteArray(short data) {
        return ByteBuffer.allocate(2).order(ByteOrder.LITTLE_ENDIAN).putShort(data).array();
    }

}
