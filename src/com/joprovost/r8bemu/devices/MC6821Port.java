package com.joprovost.r8bemu.devices;

import com.joprovost.r8bemu.data.DataAccess;
import com.joprovost.r8bemu.data.DataAccessSubset;
import com.joprovost.r8bemu.data.DataInput;
import com.joprovost.r8bemu.data.DataOutput;
import com.joprovost.r8bemu.data.DataOutputComplement;
import com.joprovost.r8bemu.data.DataPort;
import com.joprovost.r8bemu.data.Filter;
import com.joprovost.r8bemu.data.Variable;
import com.joprovost.r8bemu.memory.MemoryMapped;

import java.util.ArrayList;
import java.util.List;

public class MC6821Port implements MemoryMapped, DataPort {

    // Control Register
    private final Variable CONTROL_REGISTER = Variable.ofMask(0xff);

    // IRQA1 Interrupt Flag (bit 7)
    // Goes high on active transition of CA1; Automatically cleared by MPU Read of Output Register A.
    // May also be cleared by hardware Reset.
    private final DataAccess IRQA1_FLAG = DataAccessSubset.bit(CONTROL_REGISTER, 7);

    // IRQA2 Interrupt Flag (bit 6)
    // When CA2 is an input, IRQA goes high  on active transition CA2; Automatically cleared by MPQ
    // Read of Output Register A. May also be cleared by hardware Reset.
    // CA2 Established as Output (b5=1); IRQA2=0, not affected by CA2 transition.
    private final DataAccess IRQA2_FLAG = DataAccessSubset.bit(CONTROL_REGISTER, 6);

    // TODO: Too long, see MC6821.pdf, page 10.
    public final DataAccess CA2_CONTROL = DataAccessSubset.of(CONTROL_REGISTER, 0b00111000);

    // Determines if Data Direction Register Or Output Register is Addressed
    // b2=0: Data Direction Register selected.
    // b2=1: Output Register selected.
    private final DataAccess DDR_ACCESS = DataAccessSubset.bit(CONTROL_REGISTER, 2);

    // Interrupt Request Enable/Disable (b0)
    // b0=0: Disables IRQA MPU interrupt by CA1 active transition
    // b0=1: Enables IRQA MPU interrupt by CA1 active transition
    // Determine Active CA1 Transition for Setting Interrupt Flag IRQA1 - (bit 7)
    // b1=0: IRQA1 set by high-to-low transition on CA1
    // b1=1: IRQA1 set low-to-high transition on CA1
    private final DataAccess CA1_CONTROL = DataAccessSubset.of(CONTROL_REGISTER, 0b00000011);

    private final DataAccess CA1_IRQ_ENABLED = DataAccessSubset.bit(CA1_CONTROL, 0);

    // Peripheral Register A
    private final Variable PERIPHERAL_REGISTER = Variable.ofMask(0xff);

    // Data Direction Register A
    private final Variable DATA_DIRECTION_REGISTER = Variable.ofMask(0xff);

    private final List<DataFeeder> feeders = new ArrayList<>();
    private final List<DataConsumer> consumers = new ArrayList<>();
    private final Filter input = Filter.of(PERIPHERAL_REGISTER, DataOutputComplement.of(DATA_DIRECTION_REGISTER));
    private final Filter output = Filter.of(PERIPHERAL_REGISTER, DATA_DIRECTION_REGISTER);

    private final DataInput irq;

    public MC6821Port(DataInput irq) {
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
                IRQA1_FLAG.clear();
                if (CA1_IRQ_ENABLED.isSet()) irq.clear();
                feeders.forEach(it -> it.feed(input));
                return PERIPHERAL_REGISTER.unsigned();
            } else return DATA_DIRECTION_REGISTER.unsigned();
        } else {
            return CONTROL_REGISTER.unsigned();
        }
    }

    @Override
    public void write(int address, int data) {
        int rs0 = address & 0b1;
        if (rs0 == 0) {
            if (DDR_ACCESS.isSet()) {
                PERIPHERAL_REGISTER.set(data);
                consumers.forEach(it -> it.consume(output));
            }
            else DATA_DIRECTION_REGISTER.set(data);
        } else {
            CONTROL_REGISTER.set(data);
        }
    }

    @Override
    public void control() {
        IRQA1_FLAG.set();
        if (CA1_IRQ_ENABLED.isSet()) irq.set();
    }

    @Override
    public void feeder(DataFeeder feeder) {
        feeders.add(feeder);
    }

    @Override
    public void consumer(DataConsumer consumer) {
        consumers.add(consumer);
    }
}
