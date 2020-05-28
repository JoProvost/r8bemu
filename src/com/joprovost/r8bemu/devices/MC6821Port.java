package com.joprovost.r8bemu.devices;

import com.joprovost.r8bemu.arithmetic.Complement;
import com.joprovost.r8bemu.data.DataAccess;
import com.joprovost.r8bemu.data.DataOutput;
import com.joprovost.r8bemu.data.Filter;
import com.joprovost.r8bemu.data.Subset;
import com.joprovost.r8bemu.data.Variable;
import com.joprovost.r8bemu.data.DataPort;
import com.joprovost.r8bemu.memory.MemoryMapped;

public class MC6821Port implements MemoryMapped, DataPort {

    // Control Register
    public final Variable CONTROL_REGISTER = Variable.ofMask(0xff);

    // IRQA1 Interrupt Flag (bit 7)
    // Goes high on active transition of CA1; Automatically cleared by MPU Read of Output Register A.
    // May also be cleared by hardware Reset.
    public final Subset IRQA1_FLAG = Subset.bit(CONTROL_REGISTER, 7);

    // IRQA2 Interrupt Flag (bit 6)
    // When CA2 is an input, IRQA goes high  on active transition CA2; Automatically cleared by MPQ
    // Read of Output Register A. May also be cleared by hardware Reset.
    // CA2 Established as Output (b5=1); IRQA2=0, not affected by CA2 transition.
    public final Subset IRQA2_FLAG = Subset.bit(CONTROL_REGISTER, 6);

    // TODO: Too long, see MC6821.pdf, page 10.
    public final Subset CA2_CONTROL = Subset.of(CONTROL_REGISTER, 0b00111000);

    // Determines Wether Data Direction Register Or Output Register is Addressed
    // b2=0: Data Direction Register selected.
    // b2=1: Output Register selected.
    public final Subset DDR_ACCESS = Subset.bit(CONTROL_REGISTER, 2);

    // Interrupt Request Enable/Disable (b0)
    // b0=0: Disables IRQA MPU interrupt by CA1 active transition
    // b0=0: Enables IRQA MPU interrupt by CA1 active transition
    // Determine Active CA1 Transition for Setting Interrupt Flag IRQA1 - (bit 7)
    // b1=0: IRQA1 set by high-to-low transition on CA1
    // b1=1: IRQA1 set low-to-high transition on CA1
    public final Subset CA1_CONTROL = Subset.of(CONTROL_REGISTER, 0b00000011);

    // Peripheral Register A
    public final Variable PERIPHERAL_REGISTER = Variable.ofMask(0xff);

    // Data Direction Register A
    public final Variable DATA_DIRECTION_REGISTER = Variable.ofMask(0xff);
    // 1=output 0=input

    @Override
    public DataAccess in() {
        return Filter.of(PERIPHERAL_REGISTER, Complement.of(DATA_DIRECTION_REGISTER));
    }

    @Override
    public DataOutput out() {
        return Filter.of(PERIPHERAL_REGISTER, DATA_DIRECTION_REGISTER);
    }

    @Override
    public int read(int address) {
        int rs0 = address & 0b1;
        if (rs0 == 0) {
            if (DDR_ACCESS.isSet()) {
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
            if (DDR_ACCESS.isSet()) PERIPHERAL_REGISTER.set(data);
            else DATA_DIRECTION_REGISTER.set(data);
        } else {
            CONTROL_REGISTER.set(data);
        }
    }
}
