package com.joprovost.r8bemu.devices.mc6809;

import com.joprovost.r8bemu.data.discrete.DiscreteLine;

/**
 * Input signal pins of the Motorola 6809
 * Note: Those signals are usually low when set... let's just say they are set when they are set().
 */
public final class Signal extends DiscreteLine {
    public static final Signal REBOOT = new Signal("REBOOT");
    public static final Signal RESET = new Signal("RESET");
    public static final Signal IRQ = new Signal("IRQ");
    public static final Signal FIRQ = new Signal("FIRQ");
    public static final Signal NMI = new Signal("NMI");
    public static final Signal HALT = new Signal("HALT");

    private Signal(String description) {
        super(description);
    }

    public static void reset() {
        REBOOT.clear();
        RESET.clear();
        IRQ.clear();
        FIRQ.clear();
        NMI.clear();
        HALT.clear();
    }
}
