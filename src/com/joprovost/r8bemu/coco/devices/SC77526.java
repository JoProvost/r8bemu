package com.joprovost.r8bemu.coco.devices;

import com.joprovost.r8bemu.data.NumericRange;
import com.joprovost.r8bemu.data.analog.AnalogInput;
import com.joprovost.r8bemu.data.analog.AnalogLine;
import com.joprovost.r8bemu.data.analog.AnalogLineInput;
import com.joprovost.r8bemu.data.binary.BinaryInputProvider;
import com.joprovost.r8bemu.data.binary.BinaryOutputHandler;
import com.joprovost.r8bemu.data.discrete.DiscteteOutputHandler;

public class SC77526 {
    private final NumericRange DAC_RANGE = new NumericRange(0, 32, 63);

    private final AnalogInput audioSink;

    private boolean soundOutput;
    private boolean selA;
    private boolean selB;

    double analog;

    private final AnalogLine[] joy = {
            AnalogLine.named("joy0"), // left / h
            AnalogLine.named("joy1"), // left / v
            AnalogLine.named("joy2"), // right / h
            AnalogLine.named("joy3")  // right / v
    };

    public SC77526(AnalogInput audioSink) {
        this.audioSink = audioSink;
    }

    public BinaryOutputHandler dac() {
        return output -> {
            analog = DAC_RANGE.normalize(output.value());
            if (soundOutput) audioSink.value(analog);
        };
    }

    public BinaryInputProvider cmp() {
        return input -> {
            if (analog <= joy[(selB ? 2 : 0) | (selA ? 1 : 0)].value()) {
                input.set();
            } else {
                input.clear();
            }
        };
    }

    public DiscteteOutputHandler sndEn() {
        return state -> {
            soundOutput = state.isSet();
            if (soundOutput) audioSink.value(analog);
        };
    }

    public AnalogLineInput joy(int number) {
        return joy[number];
    }

    public DiscteteOutputHandler selA() {
        return state -> selA = state.isSet();
    }

    public DiscteteOutputHandler selB() {
        return state -> selB = state.isSet();
    }
}
