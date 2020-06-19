package com.joprovost.r8bemu.audio;

public interface AudioSink {
    void sample(int amplitude);

    static AudioSink broadcast(AudioSink... sinks) {
        return (amplitude) -> {
            for (var sink : sinks) {
                sink.sample(amplitude);
            }
        };
    }
}
