package com.joprovost.r8bemu.devices.sam;

import com.joprovost.r8bemu.data.DataAccess;
import com.joprovost.r8bemu.data.DataOutput;
import com.joprovost.r8bemu.data.Variable;
import com.joprovost.r8bemu.data.transform.DataAccessSubset;

public class ControlRegister {

    // FC00-FDFF
    // SAM Programmability
    private final Variable controlRegister = Variable.ofMask(0xffff);

    // @formatter:off
    private final DataOutput videoAddressMode   = DataAccessSubset.of(controlRegister, 0b0000000000000111);
    private final DataAccess videoAddressOffset = DataAccessSubset.of(controlRegister, 0b0000001111111000);
    private final DataOutput pageSwitch32K      = DataAccessSubset.of(controlRegister, 0b0000010000000000);
    private final DataOutput mpuRate            = DataAccessSubset.of(controlRegister, 0b0001100000000000);
    private final DataOutput memorySize         = DataAccessSubset.of(controlRegister, 0b0110000000000000);
    private final DataOutput fullRam            = DataAccessSubset.of(controlRegister, 0b1000000000000000);
    // @formatter:on

    public void write(int address) {
        int bit = (address & 0x1f) / 2;
        if ((address & 0x1) == 1) {
            controlRegister.set(1 << bit);
        } else {
            controlRegister.clear(1 << bit);
        }
    }

    public void clear() {
        controlRegister.clear();
    }

    public DataOutput videoAddressMode() {
        return videoAddressMode;
    }

    public DataAccess videoAddressOffset() {
        return videoAddressOffset;
    }

    public DataOutput pageSwitch32K() {
        return pageSwitch32K;
    }

    public DataOutput mpuRate() {
        return mpuRate;
    }

    public DataOutput memorySize() {
        return memorySize;
    }

    public DataOutput fullRam() {
        return fullRam;
    }
}
