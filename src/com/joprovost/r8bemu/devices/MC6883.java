package com.joprovost.r8bemu.devices;

import com.joprovost.r8bemu.data.DataAccess;
import com.joprovost.r8bemu.data.DataAccessSubset;
import com.joprovost.r8bemu.data.Variable;
import com.joprovost.r8bemu.memory.AddressRange;
import com.joprovost.r8bemu.memory.ChipSelect;
import com.joprovost.r8bemu.memory.InvalidDevice;
import com.joprovost.r8bemu.memory.MemoryBus;
import com.joprovost.r8bemu.memory.MemoryMapped;

public class MC6883 implements MemoryMapped {

    public static final int REGISTER_ADDRESS = 0xffc0;

    // Writing to th SAM Control Register
    // Any bit in the control register (CR) may be set by writing to a specific unique address. Each bit has to unique
    // address... writing to the even # address clears the bit and writing to the odd # address sets the bit. (Data on
    // the data bus is irrelevant in this procedure.

    // FC00-FDFF
    // SAM Programmability
    private final Variable SAM_CONTROL_REGISTER = Variable.ofMask(0xffff);
    // VDG Address Mode (3 bits) (V2, V1, V0)
    private final DataAccess VDG_ADDRESS_MODE = DataAccessSubset.of(SAM_CONTROL_REGISTER, 0b0000000000000111);
    // VDG Address Offset (7 bits)
    private final DataAccess VDG_ADDRESS_OFFSET = DataAccessSubset.of(SAM_CONTROL_REGISTER, 0b0000001111111000);
    // 32 K Page Switch (1 bit)
    private final DataAccess PAGE_SWITCH_32K = DataAccessSubset.of(SAM_CONTROL_REGISTER, 0b0000010000000000);
    // MPU Rate (2 bits)
    private final DataAccess MPU_RATE = DataAccessSubset.of(SAM_CONTROL_REGISTER, 0b0001100000000000);
    // Memory size (2 bits)
    private final DataAccess MEMORY_SIZE = DataAccessSubset.of(SAM_CONTROL_REGISTER, 0b0110000000000000);
    private final int MEMORY_SIZE_4K_BANK = 0b00;
    private final int MEMORY_SIZE_16K_BANK = 0b01;
    private final int MEMORY_SIZE_64K_BANK_DYNAMIC = 0b10;
    private final int MEMORY_SIZE_64K_BANK_STATIC = 0b11;


    // Map type (1 bit)
    public final DataAccess MAP_TYPE = DataAccessSubset.bit(SAM_CONTROL_REGISTER, 15);

    private final MemoryMapped bus;

    final MemoryMapped ram;
    final MemoryMapped rom0;
    final MemoryMapped rom1;
    final MemoryMapped rom2;
    final MemoryMapped cs4;
    final MemoryMapped cs5;
    final MemoryMapped cs6;
    final MemoryMapped display;

    private final MemoryMapped sam = new SamRegister();

    public static MC6883 ofRam(MemoryMapped ram) {
        return new MC6883(
                ram,
                InvalidDevice.invalid("rom0"),
                InvalidDevice.invalid("rom1"),
                InvalidDevice.invalid("rom2"),
                InvalidDevice.invalid("cs4"),
                InvalidDevice.invalid("cs5"),
                InvalidDevice.invalid("cs6"),
                MemoryMapped.none()
        );
    }

    public MC6883 withRom0(MemoryMapped rom0) {
        return new MC6883(ram, rom0, rom1, rom2, cs4, cs5, cs6, display);
    }

    public MC6883 withRom1(MemoryMapped rom1) {
        return new MC6883(ram, rom0, rom1, rom2, cs4, cs5, cs6, display);
    }

    public MC6883 withRom2(MemoryMapped rom2) {
        return new MC6883(ram, rom0, rom1, rom2, cs4, cs5, cs6, display);
    }

    public MC6883 withCS4(MemoryMapped cs4) {
        return new MC6883(ram, rom0, rom1, rom2, cs4, cs5, cs6, display);
    }

    public MC6883 withCS5(MemoryMapped cs5) {
        return new MC6883(ram, rom0, rom1, rom2, cs4, cs5, cs6, display);
    }

    public MC6883 withCS6(MemoryMapped cs6) {
        return new MC6883(ram, rom0, rom1, rom2, cs4, cs5, cs6, display);
    }

    public MC6883 withDisplay(MemoryMapped display) {
        return new MC6883(ram, rom0, rom1, rom2, cs4, cs5, cs6, display);
    }

    public MC6883(MemoryMapped ram,
                  MemoryMapped rom0,
                  MemoryMapped rom1,
                  MemoryMapped rom2,
                  MemoryMapped keyboard,
                  MemoryMapped cs5,
                  MemoryMapped cs6,
                  MemoryMapped display) {
        this.ram = ram;
        this.rom0 = rom0;
        this.rom1 = rom1;
        this.rom2 = rom2;
        this.cs4 = keyboard;
        this.cs5 = cs5;
        this.cs6 = cs6;
        this.display = display;

        this.bus = MemoryBus.bus(
                ChipSelect.mapping(
                        display,
                        address -> address >= videoStart() && address <= videoEnd(),
                        address -> address - videoStart()
                ), // Display
                ChipSelect.mapping(this.rom1, AddressRange.of(0xffe0, 0xffff)), // S=2
                ChipSelect.mapping(this.sam, AddressRange.of(0xff60, 0xffdf)),  // S=7
                ChipSelect.mapping(this.cs6, AddressRange.of(0xff40, 0xff5f)),  // S=6
                ChipSelect.mapping(this.cs5, AddressRange.of(0xff20, 0xff3f)),  // S=5
                ChipSelect.mapping(this.cs4, AddressRange.of(0xff00, 0xff1f)),  // S=4
                ChipSelect.mapping(this.rom2, AddressRange.of(0xc000, 0xfeff)), // S=3
                ChipSelect.mapping(this.rom1, AddressRange.of(0xa000, 0xbfff)), // S=2
                ChipSelect.mapping(this.rom0, AddressRange.of(0x8000, 0x9fff)), // S=1
                ChipSelect.mapping(new RamMultiplexer(), AddressRange.of(0x0000, 0x7fff))
        );
    }

    public int videoStart() {
        return VDG_ADDRESS_OFFSET.value() << 9;
    }

    public int videoEnd() {
        switch (VDG_ADDRESS_MODE.value()) {
            case 0: return videoStart() + 0x01ff;
            case 1: return videoStart() + 0x03ff;
            case 2: return videoStart() + 0x07ff;
            case 3: return videoStart() + 0x05ff;
            case 4: return videoStart() + 0x0bff;
            case 5: return videoStart() + 0x0bff;
            case 6: return videoStart() + 0x17ff;
        }
        return videoStart() + 0x01ff;
    }

    @Override
    public int read(int address) {
        return bus.read(address);
    }

    @Override
    public void write(int address, int data) {
        bus.write(address, data);
    }

    private class SamRegister implements MemoryMapped {
        @Override
        public int read(int address) {
            return 0;
        }

        @Override
        public void write(int address, int data) {
            if (address >= REGISTER_ADDRESS && address < REGISTER_ADDRESS + 32) {
                int bit = (address - REGISTER_ADDRESS) / 2;
                if ((address - REGISTER_ADDRESS) % 2 == 1) {
                    SAM_CONTROL_REGISTER.set(1 << bit);
                } else {
                    SAM_CONTROL_REGISTER.clear(1 << bit);
                }
            }
        }
    }

    private class RamMultiplexer implements MemoryMapped {
        @Override
        public int read(int address) {
            return MC6883.this.ram.read(address);
        }

        @Override
        public void write(int address, int data) {
            switch (MEMORY_SIZE.value()) {
                case MEMORY_SIZE_4K_BANK:
                case MEMORY_SIZE_16K_BANK:
                    sam.write(0x8000 | address, data);

                case MEMORY_SIZE_64K_BANK_DYNAMIC:
                case MEMORY_SIZE_64K_BANK_STATIC:
                default:
            }
            MC6883.this.ram.write(address, data);
        }
    }
}
