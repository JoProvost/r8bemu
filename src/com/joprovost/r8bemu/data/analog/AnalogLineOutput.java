package com.joprovost.r8bemu.data.analog;

public interface AnalogLineOutput extends AnalogOutput {
    void to(AnalogOutputHandler handler);
}
