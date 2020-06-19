package com.joprovost.r8bemu.audio;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class WavFileFormat {

    public static final byte[] FILE_TYPE_BLOCK_ID = string("RIFF");
    public static final byte[] FILE_FORMAT_ID = string("WAVE");
    public static final byte[] FORMAT_BLOCK_ID = string("fmt ");
    public static final byte[] BLOCK_SIZE = le32(0x10);
    public static final byte[] AUDIO_FORMAT_PCM = le16(0x01);
    public static final byte[] CHANNELS_MONO = le16(0x01);
    public static final byte[] BYTES_PER_BLOC = le16(0x01);
    public static final byte[] BITS_PER_SAMPLE = le16(0x08);
    public static final byte[] DATA_BLOCK_ID = string("data");

    public static void save(String file, ByteArrayOutputStream recording, int frequency) throws IOException {
        FileOutputStream out = new FileOutputStream(file);

        out.write(FILE_TYPE_BLOCK_ID);
        out.write(fileSize(recording));
        out.write(FILE_FORMAT_ID);
        out.write(FORMAT_BLOCK_ID);
        out.write(BLOCK_SIZE);
        out.write(AUDIO_FORMAT_PCM);
        out.write(CHANNELS_MONO);
        out.write(sampleRate(frequency));
        out.write(bytesPerSec(frequency));
        out.write(BYTES_PER_BLOC);
        out.write(BITS_PER_SAMPLE);

        out.write(DATA_BLOCK_ID);
        out.write(dataSize(recording));
        recording.writeTo(out);

        out.close();
    }

    private static byte[] dataSize(ByteArrayOutputStream recording) {
        return le32(recording.size());
    }

    private static byte[] fileSize(ByteArrayOutputStream recording) {
        return le32(38 + recording.size());
    }

    private static byte[] sampleRate(int frequency) {
        return le32(frequency);
    }

    private static byte[] bytesPerSec(int frequency) {
        return le32(frequency);
    }

    private static byte[] le32(int value) {
        return new byte[] {
                (byte) (value & 0xff),
                (byte) (value >> 8 & 0xff),
                (byte) (value >> 16 & 0xff),
                (byte) (value >> 24 & 0xff),
        };
    }

    private static byte[] le16(int value) {
        return new byte[] {
                (byte) (value & 0xff),
                (byte) (value >> 8 & 0xff)
        };
    }

    private static byte[] string(String string) {
        return string.getBytes();
    }
}
