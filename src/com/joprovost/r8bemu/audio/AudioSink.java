package com.joprovost.r8bemu.audio;

public interface AudioSink {
    void sample(int amplitude, long atNanoTime);
}
