package com.joprovost.r8bemu.devices;

import com.joprovost.r8bemu.audio.AudioSink;
import com.joprovost.r8bemu.data.DataOutput;
import com.joprovost.r8bemu.data.DataOutputSubset;
import com.joprovost.r8bemu.data.Variable;
import com.joprovost.r8bemu.port.DataOutputHandler;

public class SC77526 implements DataOutputHandler {
    private final Variable CS5_PA_OUT = Variable.ofMask(0xff);
    private final DataOutput DAC = DataOutputSubset.of(CS5_PA_OUT, 0b11111100);

    private final AudioSink audioSink;

    public SC77526(AudioSink audioSink) {
        this.audioSink = audioSink;
    }

    @Override
    public void handle(DataOutput output) {
        CS5_PA_OUT.value(output.value());
        audioSink.sample(DAC.value() * 4);
    }
}
