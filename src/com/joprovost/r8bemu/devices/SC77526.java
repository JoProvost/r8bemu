package com.joprovost.r8bemu.devices;

import com.joprovost.r8bemu.audio.AudioSink;
import com.joprovost.r8bemu.clock.Uptime;
import com.joprovost.r8bemu.data.DataOutput;
import com.joprovost.r8bemu.data.DataOutputSubset;
import com.joprovost.r8bemu.data.DataPort;
import com.joprovost.r8bemu.data.Variable;

public class SC77526 implements DataPort.DataConsumer {
    private final Variable CS5_PA_OUT = Variable.ofMask(0xff);
    private final DataOutput DAC = DataOutputSubset.of(CS5_PA_OUT, 0b11111100);

    private final Uptime uptime;
    private final AudioSink audioSink;

    public SC77526(Uptime uptime, AudioSink audioSink) {
        this.uptime = uptime;
        this.audioSink = audioSink;
    }

    @Override
    public void consume(DataOutput output) {
        CS5_PA_OUT.set(output.unsigned());
        audioSink.sample(DAC.unsigned(), uptime.nanoTime());
    }
}
