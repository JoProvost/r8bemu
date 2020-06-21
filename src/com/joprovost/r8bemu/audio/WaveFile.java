package com.joprovost.r8bemu.audio;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class WaveFile {
    public static final int BLOCK_SIZE = 0x10;
    public static final int AUDIO_FORMAT_PCM = 0x01;
    public static final int CHANNELS_MONO = 0x01;
    public static final int BYTES_PER_BLOC = 0x01;
    public static final int BITS_PER_SAMPLE = 0x08;
    public final int sampleRate;
    public final byte[] data;

    private WaveFile(int sampleRate, byte[] data) {
        this.sampleRate = sampleRate;
        this.data = data;
    }

    public static WaveFile empty() {
        return new WaveFile(44100, new byte[0]);
    }

    public static WaveFile load(String file) throws IOException {

        try (FileInputStream in = new FileInputStream(file)) {
            if (!string(in, 4).equals("RIFF")) throw new IOException();
            int fileSize = le32(in);
            if (!string(in, 4).equals("WAVE")) throw new IOException();
            if (!string(in, 4).equals("fmt ")) throw new IOException();

            int blockSize = le32(in);
            if (blockSize != BLOCK_SIZE) throw new IOException();

            int audioFormat = le16(in);
            int channels = le16(in);
            int sampleRate = le32(in);
            int bytesPerSec = le32(in);
            int bytesPerBloc = le16(in);
            int bitsPerSample = le16(in);

            // TODO support other audio formats
            if (audioFormat != AUDIO_FORMAT_PCM) throw new IOException();

            // TODO support other resolutions
            if (channels != CHANNELS_MONO) throw new IOException();
            if (bytesPerSec != sampleRate) throw new IOException();
            if (bytesPerBloc != BYTES_PER_BLOC) throw new IOException();
            if (bitsPerSample != BITS_PER_SAMPLE) throw new IOException();

            if (!string(in, 4).equals("data")) throw new IOException();
            int dataSize = le32(in);

            return new WaveFile(sampleRate, in.readNBytes(dataSize));
        }
    }

    public static void save(String file, ByteArrayOutputStream recording, int frequency) throws IOException {
        FileOutputStream out = new FileOutputStream(file);

        out.write(string("RIFF"));
        out.write(fileSize(recording));
        out.write(string("WAVE"));
        out.write(string("fmt "));
        out.write(le32(BLOCK_SIZE));
        out.write(le16(AUDIO_FORMAT_PCM));
        out.write(le16(CHANNELS_MONO));
        out.write(sampleRate(frequency));
        out.write(bytesPerSec(frequency));
        out.write(le16(BYTES_PER_BLOC));
        out.write(le16(BITS_PER_SAMPLE));

        out.write(string("data"));
        out.write(dataSize(recording));
        recording.writeTo(out);

        out.close();
    }

    private static int le16(InputStream inputStream) throws IOException {
        return inputStream.read() | inputStream.read() << 8;
    }

    private static int le32(InputStream inputStream) throws IOException {
        return le16(inputStream) | le16(inputStream) << 16;
    }

    private static String string(InputStream inputStream, int len) throws IOException {
        return new String(inputStream.readNBytes(len));
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
