package com.joprovost.r8bemu.devices.sam;

import com.joprovost.r8bemu.data.BitOutput;
import com.joprovost.r8bemu.data.link.LineOutputHandler;
import com.joprovost.r8bemu.memory.MemoryDevice;
import com.joprovost.r8bemu.memory.Subset;

public class MC6883 implements MemoryDevice {

    // FC00-FDFF
    // SAM Programmability
    private static final Subset SAM = Subset.mask(0xffc0, 0x1f);
    private final ControlRegister register = new ControlRegister();
    private final MemoryDevice ram;

    private int select = 0;
    private BitOutput SECOND_PAGE = BitOutput.and(BitOutput.not(register.fullRam()), register.pageSwitch32K());

    public MC6883(MemoryDevice ram) {
        this.ram = ram;
    }

    public VideoMemory video() {
        return new SAMVideoMemory(ram, register.videoAddressMode(), register.videoAddressOffset());
    }

    public BitOutput select(int number) {
        return BitOutput.of("S" + number, () -> select == number);
    }

    public LineOutputHandler reset() {
        return state -> {
            if (state.isSet()) register.clear();
        };
    }

    @Override
    public int read(int address) {
        if (address >= 0xfff2) select = 2;
        else if (address >= 0xffe0) select = 2;
        else if (address >= 0xffc0) select = 7;
        else if (address >= 0xff60) select = 7;
        else if (address >= 0xff40) select = 6;
        else if (address >= 0xff20) select = 5;
        else if (address >= 0xff00) select = 4;
        else if (register.fullRam().isClear() && address >= 0xc000) select = 3;
        else if (register.fullRam().isClear() && address >= 0xa000) select = 2;
        else if (register.fullRam().isClear() && address >= 0x8000) select = 1;
        else select = 0;

        if (select != 0 && select != 7) return 0;

        if (SECOND_PAGE.isSet()) address |= 0x8000;
        return ram.read(address);
    }

    @Override
    public void write(int address, int data) {
        if (address >= 0xfff2) select = 2;
        else if (address >= 0xffe0) select = 2;
        else if (address >= 0xffc0) select = 7;
        else if (address >= 0xff60) select = 7;
        else if (address >= 0xff40) select = 6;
        else if (address >= 0xff20) select = 5;
        else if (address >= 0xff00) select = 4;
        else if (register.fullRam().isClear() && address >= 0xc000) select = 3;
        else if (register.fullRam().isClear() && address >= 0xa000) select = 2;
        else if (register.fullRam().isClear() && address >= 0x8000) select = 1;
        else select = 7;

        if (select == 7) {
            if (SAM.contains(address)) {
                register.write(SAM.offset(address));
            } else {
                if (SECOND_PAGE.isSet()) address |= 0x8000;
                ram.write(address, data);
            }
        }
    }
}
