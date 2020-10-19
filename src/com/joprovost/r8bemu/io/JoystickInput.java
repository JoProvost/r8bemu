package com.joprovost.r8bemu.io;

import com.joprovost.r8bemu.data.NumericRange;

import java.util.concurrent.Executor;

public interface JoystickInput {
    NumericRange AXIS_RANGE = new NumericRange(-1, 0, 1);

    double CENTER = AXIS_RANGE.zero;
    double MINIMUM = AXIS_RANGE.min;
    double MAXIMUM = AXIS_RANGE.max;

    static JoystickDispatcher dispatcher(Executor context) {
        return new JoystickDispatcher(context);
    }

    void horizontal(double value);

    void vertical(double value);

    void press();

    void release();
}
