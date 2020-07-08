package com.joprovost.r8bemu.devices;

import com.joprovost.r8bemu.data.DataAccess;
import com.joprovost.r8bemu.data.DataAccessSubset;
import com.joprovost.r8bemu.data.DataOutput;
import com.joprovost.r8bemu.data.DataOutputComplement;
import com.joprovost.r8bemu.data.LogicAccess;
import com.joprovost.r8bemu.data.LogicInput;
import com.joprovost.r8bemu.port.ControlPort;
import com.joprovost.r8bemu.port.LogicOutputHandler;
import com.joprovost.r8bemu.port.DataPort;
import com.joprovost.r8bemu.data.Filter;
import com.joprovost.r8bemu.data.Variable;
import com.joprovost.r8bemu.memory.MemoryDevice;
import com.joprovost.r8bemu.port.DataInputProvider;
import com.joprovost.r8bemu.port.DataOutputHandler;

import java.util.ArrayList;
import java.util.List;

public class MC6821Port implements MemoryDevice, DataPort, ControlPort {

    // Control Register
    private final Variable CONTROL_REGISTER = Variable.ofMask(0xff);

    // TODO: Too long, see MC6821.pdf, page 10.
    public final DataAccess CA2_CONTROL = DataAccessSubset.of(CONTROL_REGISTER, 0b00111000);

    public final LogicAccess CA2_OUTPUT_MODE = DataAccessSubset.bit(CONTROL_REGISTER, 5);
    public final LogicAccess CA2_OUTPUT_STATE = DataAccessSubset.bit(CONTROL_REGISTER, 3);

    // IRQA2 Interrupt Flag (bit 6)
    // When CA2 is an input, IRQA goes high  on active transition CA2; Automatically cleared by MPQ
    // Read of Output Register A. May also be cleared by hardware Reset.
    // CA2 Established as Output (b5=1); IRQA2=0, not affected by CA2 transition.
    private final LogicAccess CA2_IRQ_FLAG = DataAccessSubset.bit(CONTROL_REGISTER, 6);

    public final LogicAccess CA2_IRQ_ENABLED = DataAccessSubset.bit(CONTROL_REGISTER, 3);

    // Determines if Data Direction Register Or Output Register is Addressed
    // b2=0: Data Direction Register selected.
    // b2=1: Output Register selected.
    private final LogicAccess DDR_ACCESS = DataAccessSubset.bit(CONTROL_REGISTER, 2);

    // IRQA1 Interrupt Flag (bit 7)
    // Goes high on active transition of CA1; Automatically cleared by MPU Read of Output Register A.
    // May also be cleared by hardware Reset.
    private final LogicAccess CA1_IRQ_FLAG = DataAccessSubset.bit(CONTROL_REGISTER, 7);

    private final LogicAccess CA1_IRQ_ENABLED = DataAccessSubset.bit(CONTROL_REGISTER, 0);

    // Peripheral Register A
    private final Variable PERIPHERAL_REGISTER = Variable.ofMask(0xff);

    // Data Direction Register A
    private final Variable DATA_DIRECTION_REGISTER = Variable.ofMask(0xff);

    private final List<DataInputProvider> inputProviders = new ArrayList<>();
    private final List<DataOutputHandler> outputHandlers = new ArrayList<>();
    private final List<LogicOutputHandler> controlHandlers = new ArrayList<>();
    private final Filter input = Filter.of(PERIPHERAL_REGISTER, DataOutputComplement.of(DATA_DIRECTION_REGISTER));
    private final Filter output = Filter.of(PERIPHERAL_REGISTER, DATA_DIRECTION_REGISTER);

    private final LogicInput irq;

    public MC6821Port(LogicInput irq) {
        this.irq = irq;
    }
    // 1=output 0=input

    @Override
    public DataAccess input() {
        return input;
    }

    @Override
    public DataOutput output() {
        return output;
    }

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

    @Override
    public void interrupt() {
        CA1_IRQ_FLAG.set();
        if (CA1_IRQ_ENABLED.isSet()) irq.set();
    }

    @Override
    public void control() {
        if (CA2_OUTPUT_MODE.isClear()) {
            CA2_IRQ_FLAG.set();
            if (CA2_IRQ_ENABLED.isSet()) irq.set();
        }
    }

    @Override
    public void inputFrom(DataInputProvider provider) {
        inputProviders.add(provider);
    }

    @Override
    public void outputTo(DataOutputHandler handler) {
        outputHandlers.add(handler);
    }

    @Override
    public void controlTo(LogicOutputHandler handler) {
        controlHandlers.add(handler);
    }
}
