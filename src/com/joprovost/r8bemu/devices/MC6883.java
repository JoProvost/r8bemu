package com.joprovost.r8bemu.devices;

import com.joprovost.r8bemu.data.DataAccess;
import com.joprovost.r8bemu.data.DataAccessSubset;
import com.joprovost.r8bemu.data.Variable;
import com.joprovost.r8bemu.memory.Memory;
import com.joprovost.r8bemu.memory.MemoryDevice;

public class MC6883 implements MemoryDevice {

    // FC00-FDFF
    // SAM Programmability
    private static final int REGISTER_ADDRESS = 0xffc0;
    private final Variable REGISTER = Variable.ofMask(0xffff);

    private final DataAccess MAP_TYPE = DataAccessSubset.bit(REGISTER, 15);
    private final DataAccess VDG_ADDRESS_MODE = DataAccessSubset.of(REGISTER, 0b0000000000000111);
    private final DataAccess VDG_ADDRESS_OFFSET = DataAccessSubset.of(REGISTER, 0b0000001111111000);
    private final DataAccess PAGE_SWITCH_32K = DataAccessSubset.of(REGISTER, 0b0000010000000000);
    private final DataAccess MPU_RATE = DataAccessSubset.of(REGISTER, 0b0001100000000000);

    private final DataAccess MEMORY_SIZE = DataAccessSubset.of(REGISTER, 0b0110000000000000);
    private final int MEMORY_SIZE_4K_BANK = 0b00;
    private final int MEMORY_SIZE_16K_BANK = 0b01;
    private final int MEMORY_SIZE_64K_BANK_DYNAMIC = 0b10;
    private final int MEMORY_SIZE_64K_BANK_STATIC = 0b11;

    public MemoryDevice videoMemory(Memory ram) {
        return new VideoMemory(ram, VDG_ADDRESS_MODE, VDG_ADDRESS_OFFSET);
    }

    @Override
    public int read(int address) {
        return 0;
    }

    @Override
    public void write(int address, int data) {
        switch (MEMORY_SIZE.value()) {
            case MEMORY_SIZE_4K_BANK:
            case MEMORY_SIZE_16K_BANK:
                address |= 0x8000;
        }

        // Writing to th SAM Control Register
        // Any bit in the control register (CR) may be set by writing to a specific unique address. Each bit has to unique
        // address... writing to the even # address clears the bit and writing to the odd # address sets the bit. (Data on
        // the data bus is irrelevant in this procedure.
        if (address >= REGISTER_ADDRESS && address < REGISTER_ADDRESS + 32) {
            int bit = (address - REGISTER_ADDRESS) / 2;
            if ((address - REGISTER_ADDRESS) % 2 == 1) {
                REGISTER.set(1 << bit);
            } else {
                REGISTER.clear(1 << bit);
            }
        }
    }
}
