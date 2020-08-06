package com.joprovost.r8bemu.devices;

import com.joprovost.r8bemu.data.BitAccess;
import com.joprovost.r8bemu.data.BitInput;
import com.joprovost.r8bemu.data.DataAccess;
import com.joprovost.r8bemu.data.DataOutput;
import com.joprovost.r8bemu.data.Variable;
import com.joprovost.r8bemu.data.link.LineOutputHandler;
import com.joprovost.r8bemu.data.link.LinePort;
import com.joprovost.r8bemu.data.link.ParallelInputProvider;
import com.joprovost.r8bemu.data.link.ParallelOutputHandler;
import com.joprovost.r8bemu.data.link.ParallelPort;
import com.joprovost.r8bemu.data.transform.DataOutputComplement;
import com.joprovost.r8bemu.data.transform.Filter;
import com.joprovost.r8bemu.memory.MemoryDevice;

import java.util.ArrayList;
import java.util.List;

import static com.joprovost.r8bemu.data.transform.DataAccessSubset.bit;

public class MC6821Port implements MemoryDevice {

    // Control Register
    private final Variable CONTROL_REGISTER = Variable.ofMask(0xff);

    public final BitAccess CA2_OUTPUT_MODE = (bit(CONTROL_REGISTER, 5));
    public final BitAccess CA2_OUTPUT_STATE = (bit(CONTROL_REGISTER, 3));

    // IRQA2 Interrupt Flag (bit 6)
    // When CA2 is an input, IRQA goes high  on active transition CA2; Automatically cleared by MPQ
    // Read of Output Register A. May also be cleared by hardware Reset.
    // CA2 Established as Output (b5=1); IRQA2=0, not affected by CA2 transition.
    private final BitAccess CA2_IRQ_FLAG = (bit(CONTROL_REGISTER, 6));

    public final BitAccess CA2_IRQ_ENABLED = (bit(CONTROL_REGISTER, 3));

    // Determines if Data Direction Register Or Output Register is Addressed
    // b2=0: Data Direction Register selected.
    // b2=1: Output Register selected.
    private final BitAccess DDR_ACCESS = (bit(CONTROL_REGISTER, 2));

    // IRQA1 Interrupt Flag (bit 7)
    // Goes high on active transition of CA1; Automatically cleared by MPU Read of Output Register A.
    // May also be cleared by hardware Reset.
    private final BitAccess CA1_IRQ_FLAG = (bit(CONTROL_REGISTER, 7));

    private final BitAccess CA1_IRQ_ENABLED = (bit(CONTROL_REGISTER, 0));

    // Peripheral Register A
    private final Variable PERIPHERAL_REGISTER = Variable.ofMask(0xff);

    // Data Direction Register A
    private final Variable DATA_DIRECTION_REGISTER = Variable.ofMask(0xff);

    private final List<ParallelInputProvider> inputProviders = new ArrayList<>();
    private final List<ParallelOutputHandler> outputHandlers = new ArrayList<>();
    private final List<LineOutputHandler> controlHandlers = new ArrayList<>();
    private final Filter input = Filter.of(PERIPHERAL_REGISTER, DataOutputComplement.of(DATA_DIRECTION_REGISTER));
    private final Filter output = Filter.of(PERIPHERAL_REGISTER, DATA_DIRECTION_REGISTER);

    private final BitInput irq;

    public MC6821Port(BitInput irq) {
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

    public void interrupt() {
        CA1_IRQ_FLAG.set();
        if (CA1_IRQ_ENABLED.isSet()) irq.set();
    }

    public ParallelPort port() {
        return new ParallelPort() {
            @Override
            public void from(ParallelInputProvider provider) {
                inputProviders.add(provider);
            }

            @Override
            public DataAccess input() {
                return input;
            }

            @Override
            public void to(ParallelOutputHandler handler) {
                outputHandlers.add(handler);
            }

            @Override
            public DataOutput output() {
                return output;
            }
        };
    }

    public LinePort control() {
        return new LinePort() {
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
            public boolean isClear() {
                return CA2_OUTPUT_STATE.isClear();
            }

            @Override
            public void to(LineOutputHandler handler) {
                controlHandlers.add(handler);
            }
        };
    }
}
