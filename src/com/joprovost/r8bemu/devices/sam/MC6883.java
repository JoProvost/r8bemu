package com.joprovost.r8bemu.devices.sam;

import com.joprovost.r8bemu.data.BitOutput;
import com.joprovost.r8bemu.data.DataAccess;
import com.joprovost.r8bemu.data.Variable;
import com.joprovost.r8bemu.data.link.LineOutputHandler;
import com.joprovost.r8bemu.data.transform.Addition;
import com.joprovost.r8bemu.data.transform.DataAccessSubset;
import com.joprovost.r8bemu.data.transform.Subtraction;
import com.joprovost.r8bemu.io.DisplayPage;
import com.joprovost.r8bemu.memory.Memory;
import com.joprovost.r8bemu.memory.MemoryDevice;

public class MC6883 implements MemoryDevice, DisplayPage {

    // FC00-FDFF
    // SAM Programmability
    private static final int REGISTER_ADDRESS = 0xffc0;
    private final Variable REGISTER = Variable.ofMask(0xffff);

    // @formatter:off
    private final DataAccess VDG_ADDRESS_MODE   = DataAccessSubset.of(REGISTER, 0b0000000000000111);
    private final DataAccess VDG_ADDRESS_OFFSET = DataAccessSubset.of(REGISTER, 0b0000001111111000);
    private final DataAccess PAGE_SWITCH_32K    = DataAccessSubset.of(REGISTER, 0b0000010000000000);
    private final DataAccess MPU_RATE           = DataAccessSubset.of(REGISTER, 0b0001100000000000);
    private final DataAccess MEMORY_SIZE        = DataAccessSubset.of(REGISTER, 0b0110000000000000);
    private final DataAccess FULL_RAM           = DataAccessSubset.of(REGISTER, 0b1000000000000000);
    private final int MEMORY_SIZE_4K_BANK = 0b00;
    private final int MEMORY_SIZE_16K_BANK = 0b01;
    private final int MEMORY_SIZE_64K_BANK_DYNAMIC = 0b10;
    private final int MEMORY_SIZE_64K_BANK_STATIC = 0b11;private final Memory ram;
    // @formatter:on

    private int select = 0;
    private BitOutput SECOND_PAGE = BitOutput.and(BitOutput.not(FULL_RAM), PAGE_SWITCH_32K);

    public MC6883(Memory ram) {
        this.ram = ram;
    }

    public MemoryDevice video() {
        return new VideoMemory(ram, VDG_ADDRESS_MODE, VDG_ADDRESS_OFFSET);
    }

    public BitOutput select(int number) {
        return BitOutput.of("S" + number, () -> select == number);
    }

    public LineOutputHandler reset() {
        return state -> {
            if (state.isSet()) REGISTER.clear();
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
        else if (FULL_RAM.isClear() && address >= 0xc000) select = 3;
        else if (FULL_RAM.isClear() && address >= 0xa000) select = 2;
        else if (FULL_RAM.isClear() && address >= 0x8000) select = 1;
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
        else if (FULL_RAM.isClear() && address >= 0xc000) select = 3;
        else if (FULL_RAM.isClear() && address >= 0xa000) select = 2;
        else if (FULL_RAM.isClear() && address >= 0x8000) select = 1;
        else select = 7;

        if (select == 7) {
            // Writing to th SAM Control Register
            // Any bit in the control register (CR) may be set by writing to a specific unique address. Each bit has to unique
            // address... writing to the even # address clears the bit and writing to the odd # address sets the bit. (Data on
            // the data bus is irrelevant in this procedure.
            if (address >= REGISTER_ADDRESS) {
                int bit = (address - REGISTER_ADDRESS) / 2;
                if ((address - REGISTER_ADDRESS) % 2 == 1) {
                    REGISTER.set(1 << bit);
                } else {
                    REGISTER.clear(1 << bit);
                }
            } else {
                if (SECOND_PAGE.isSet()) address |= 0x8000;
                ram.write(address, data);
            }
        }
    }

    @Override
    public void previous() {
        VDG_ADDRESS_OFFSET.update(Subtraction.decrement());
    }

    @Override
    public void next() {
        VDG_ADDRESS_OFFSET.update(Addition.increment());
    }
}
