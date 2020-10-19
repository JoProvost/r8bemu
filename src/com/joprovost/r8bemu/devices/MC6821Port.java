package com.joprovost.r8bemu.devices;

import com.joprovost.r8bemu.data.binary.BinaryAccess;
import com.joprovost.r8bemu.data.binary.BinaryInputProvider;
import com.joprovost.r8bemu.data.binary.BinaryOutput;
import com.joprovost.r8bemu.data.binary.BinaryOutputHandler;
import com.joprovost.r8bemu.data.binary.BinaryPort;
import com.joprovost.r8bemu.data.binary.BinaryRegister;
import com.joprovost.r8bemu.data.discrete.DiscreteAccess;
import com.joprovost.r8bemu.data.discrete.DiscreteInput;
import com.joprovost.r8bemu.data.discrete.DiscreteLineInput;
import com.joprovost.r8bemu.data.discrete.DiscretePort;
import com.joprovost.r8bemu.data.discrete.DiscteteOutputHandler;
import com.joprovost.r8bemu.data.transform.BinaryAccessSubset;
import com.joprovost.r8bemu.data.transform.BinaryOutputComplement;
import com.joprovost.r8bemu.data.transform.BinaryOutputSubset;
import com.joprovost.r8bemu.data.transform.Filter;
import com.joprovost.r8bemu.devices.memory.Addressable;

import java.util.ArrayList;
import java.util.List;

import static com.joprovost.r8bemu.data.transform.BinaryAccessSubset.bit;

public class MC6821Port implements Addressable {

    // Control Register
    private final BinaryRegister CONTROL_REGISTER = BinaryRegister.ofMask(0xff);

    public final DiscreteAccess CA2_OUTPUT_MODE = (bit(CONTROL_REGISTER, 5));
    public final DiscreteAccess CA2_OUTPUT_STATE = (bit(CONTROL_REGISTER, 3));

    // IRQA2 Interrupt Flag (bit 6)
    // When CA2 is an input, IRQA goes high  on active transition CA2; Automatically cleared by MPQ
    // Read of Output Register A. May also be cleared by hardware Reset.
    // CA2 Established as Output (b5=1); IRQA2=0, not affected by CA2 transition.
    private final DiscreteAccess CA2_IRQ_FLAG = (bit(CONTROL_REGISTER, 6));

    public final DiscreteAccess CA2_IRQ_ENABLED = (bit(CONTROL_REGISTER, 3));

    // Determines if Data Direction Register Or Output Register is Addressed
    // b2=0: Data Direction Register selected.
    // b2=1: Output Register selected.
    private final DiscreteAccess DDR_ACCESS = (bit(CONTROL_REGISTER, 2));

    // IRQA1 Interrupt Flag (bit 7)
    // Goes high on active transition of CA1; Automatically cleared by MPU Read of Output Register A.
    // May also be cleared by hardware Reset.
    private final DiscreteAccess CA1_IRQ_FLAG = (bit(CONTROL_REGISTER, 7));

    private final DiscreteAccess CA1_IRQ_ENABLED = (bit(CONTROL_REGISTER, 0));

    // Peripheral Register A
    private final BinaryRegister PERIPHERAL_REGISTER = BinaryRegister.ofMask(0xff);

    // Data Direction Register A
    private final BinaryRegister DATA_DIRECTION_REGISTER = BinaryRegister.ofMask(0xff);

    private final List<BinaryInputProvider> inputProviders = new ArrayList<>();
    private final List<BinaryOutputHandler> outputHandlers = new ArrayList<>();
    private final List<DiscteteOutputHandler> controlHandlers = new ArrayList<>();
    private final Filter input = Filter.of(PERIPHERAL_REGISTER, BinaryOutputComplement.of(DATA_DIRECTION_REGISTER));
    private final Filter output = Filter.of(PERIPHERAL_REGISTER, DATA_DIRECTION_REGISTER);

    private final DiscreteInput irq;

    public MC6821Port(DiscreteInput irq) {
        this.irq = irq;
    }
    // 1=output 0=input

    @Override
    public int read(int address) {
        int rs0 = address & 0b1;
        if (rs0 == 0) {
            if (DDR_ACCESS.isSet()) {
                CA1_IRQ_FLAG.clear();
                CA2_IRQ_FLAG.clear();
                if (CA1_IRQ_ENABLED.isSet() || CA2_IRQ_ENABLED.isSet()) irq.clear();
                inputProviders.forEach(it -> it.provide(input));
                return PERIPHERAL_REGISTER.value();
            } else return DATA_DIRECTION_REGISTER.value();
        } else {
            return CONTROL_REGISTER.value();
        }
    }

    @Override
    public void write(int address, int data) {
        int rs0 = address & 0b1;
        if (rs0 == 0) {
            if (DDR_ACCESS.isSet()) {
                PERIPHERAL_REGISTER.value(data);
                outputHandlers.forEach(it -> it.handle(output));
            }
            else DATA_DIRECTION_REGISTER.value(data);
        } else {
            CONTROL_REGISTER.value(data);
            if (CA2_OUTPUT_MODE.isSet()) {
                controlHandlers.forEach(it -> it.handle(CA2_OUTPUT_STATE));
            }
        }
    }

    public DiscreteLineInput interrupt() {
        return value -> {
            if (!value) return; // TODO: Only interrupt on low to high is supported
            CA1_IRQ_FLAG.set();
            if (CA1_IRQ_ENABLED.isSet()) irq.set();
        };
    }

    public BinaryPort port() {
        return new BinaryPort() {
            @Override
            public void from(BinaryInputProvider provider) {
                inputProviders.add(provider);
            }

            @Override
            public BinaryAccess input() {
                return input;
            }

            @Override
            public void to(BinaryOutputHandler handler) {
                outputHandlers.add(handler);
            }

            @Override
            public BinaryOutput output() {
                return output;
            }
        };
    }

    public BinaryPort port(int mask) {
        BinaryAccessSubset input = BinaryAccessSubset.of(this.input, mask);
        BinaryOutputSubset output = BinaryOutputSubset.of(this.output, mask);
        return new BinaryPort() {
            @Override
            public void from(BinaryInputProvider provider) {
                inputProviders.add(unused -> provider.provide(input));
            }

            @Override
            public BinaryAccess input() {
                return input;
            }

            @Override
            public void to(BinaryOutputHandler handler) {
                outputHandlers.add(unused -> handler.handle(output));
            }

            @Override
            public BinaryOutput output() {
                return output;
            }
        };
    }

    public DiscretePort control() {
        return new DiscretePort() {
            @Override
            public void set(boolean value) {
                if (!value) return; // TODO: Only interrupt on low to high is supported
                if (CA2_OUTPUT_MODE.isClear()) {
                    CA2_IRQ_FLAG.set();
                    if (CA2_IRQ_ENABLED.isSet()) irq.set();
                }
            }

            @Override
            public String description() {
                return CA2_OUTPUT_STATE.description();
            }

            @Override
            public boolean isSet() {
                return CA2_OUTPUT_STATE.isSet();
            }

            @Override
            public void to(DiscteteOutputHandler handler) {
                controlHandlers.add(handler);
            }
        };
    }
}
