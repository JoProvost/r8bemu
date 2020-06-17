package com.joprovost.r8bemu.audio;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import java.io.IOException;
import java.io.UncheckedIOException;

public class Speaker implements AudioSink, Runnable {

    public static final AudioFormat AUDIO_FORMAT = new AudioFormat(44100, 8, 1, true, false);
    public static final int BUFFER_LENGTH = (int) (AUDIO_FORMAT.getSampleRate() / 10);
    private final Buffer buffer = new Buffer(BUFFER_LENGTH, 10);
    private long last = 0;

    private long position(long nanoTime) {
        return (nanoTime * (long) AUDIO_FORMAT.getSampleRate() / 1000000000);
    }

    @Override
    public void sample(int amplitude, long atNanoTime) {
        try {
            var pos = position(atNanoTime);
            int count = (int) (pos - last);
            if (count > 0) {
                buffer.skip(count - 1);
                buffer.write(amplitude);
            }
            last = pos;
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public void run() {
        try {
            SourceDataLine audio = AudioSystem.getSourceDataLine(AUDIO_FORMAT);
            audio.open(AUDIO_FORMAT, BUFFER_LENGTH / 2);
            while (audio.isOpen()) {
                var bytes = buffer.readNBytes(audio.available());
                audio.write(bytes, 0, bytes.length);
                audio.start();
            }
        } catch (LineUnavailableException e) {
            throw new RuntimeException(e);
        } catch (IOException ignored) {
            // Read simply ended (the thread is stopping)
        }
    }
}
