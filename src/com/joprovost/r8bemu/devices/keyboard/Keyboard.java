package com.joprovost.r8bemu.devices.keyboard;

import com.joprovost.r8bemu.clock.ClockAware;
import com.joprovost.r8bemu.devices.MC6821;

import java.util.List;

public class Keyboard implements ClockAware {
    private final MC6821 pia;

    private List<KeyStroke> keyStroke = List.of();

    public Keyboard(MC6821 pia) {
        this.pia = pia;
    }

    @Override
    public void tick(long tick) {
        if (keyStroke.isEmpty()) {
            pia.a.in().set(0xff);
        } else {
            int portA = 0xff;
            var portB = pia.b.out().unsigned();
            for (var key : keyStroke) {
                portA &= key.portA(portB);
            }
            pia.a.in().set(portA);
        }
    }

    public void press(List<KeyStroke> keyStroke) {
        this.keyStroke = keyStroke;
    }

    public void release() {
        this.keyStroke = List.of();
    }
}
