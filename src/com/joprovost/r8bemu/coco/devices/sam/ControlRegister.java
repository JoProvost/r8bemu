package com.joprovost.r8bemu.coco.devices.sam;

import com.joprovost.r8bemu.data.binary.BinaryAccess;
import com.joprovost.r8bemu.data.binary.BinaryOutput;
import com.joprovost.r8bemu.data.binary.BinaryRegister;
import com.joprovost.r8bemu.data.transform.BinaryAccessSubset;

public class ControlRegister {

    // FC00-FDFF
    // SAM Programmability
    private final BinaryRegister controlRegister = BinaryRegister.ofMask(0xffff);

    // @formatter:off
    private final BinaryOutput videoAddressMode   = BinaryAccessSubset.of(controlRegister, 0b0000000000000111);
    private final BinaryAccess videoAddressOffset = BinaryAccessSubset.of(controlRegister, 0b0000001111111000);
    private final BinaryOutput pageSwitch32K      = BinaryAccessSubset.of(controlRegister, 0b0000010000000000);
    private final BinaryOutput mpuRate            = BinaryAccessSubset.of(controlRegister, 0b0001100000000000);
    private final BinaryOutput memorySize         = BinaryAccessSubset.of(controlRegister, 0b0110000000000000);
    private final BinaryOutput fullRam            = BinaryAccessSubset.of(controlRegister, 0b1000000000000000);
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

    public BinaryOutput videoAddressMode() {
        return videoAddressMode;
    }

    public BinaryAccess videoAddressOffset() {
        return videoAddressOffset;
    }

    public BinaryOutput pageSwitch32K() {
        return pageSwitch32K;
    }

    public BinaryOutput mpuRate() {
        return mpuRate;
    }

    public BinaryOutput memorySize() {
        return memorySize;
    }

    public BinaryOutput fullRam() {
        return fullRam;
    }
}
