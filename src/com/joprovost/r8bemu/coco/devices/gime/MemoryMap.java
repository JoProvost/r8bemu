package com.joprovost.r8bemu.coco.devices.gime;

import com.joprovost.r8bemu.devices.memory.AddressSubset;

public class MemoryMap {
    public static final AddressSubset VECTOR = AddressSubset.mask(0x7fe00, 0xff);
    public static final AddressSubset IO = AddressSubset.mask(0x7ff00, 0xff);
    public static final AddressSubset PIA = AddressSubset.mask(0x7ff00, 0x3f);
    public static final AddressSubset SCS = AddressSubset.mask(0x7ff40, 0x3f);
    public static final AddressSubset GIME = AddressSubset.range(0x7ff90, 0x7ffbf);
    public static final AddressSubset TASK0 = AddressSubset.mask(0x7ffa0, 0x07);
    public static final AddressSubset TASK1 = AddressSubset.mask(0x7ffa8, 0x07);
    public static final AddressSubset PALETTE = AddressSubset.mask(0x7ffb0, 0x0f);
    public static final AddressSubset SAM = AddressSubset.mask(0x7ffc0, 0x1f);
    public static final AddressSubset INTERRUPT = AddressSubset.range(0x7fff2, 0x7ffff);
}
