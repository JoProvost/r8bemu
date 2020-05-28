package com.joprovost.r8bemu.devices;

import com.joprovost.r8bemu.memory.AddressRange;
import com.joprovost.r8bemu.data.Constant;
import com.joprovost.r8bemu.data.Subset;
import com.joprovost.r8bemu.data.Variable;
import com.joprovost.r8bemu.memory.ChipSelect;
import com.joprovost.r8bemu.memory.InvalidDevice;
import com.joprovost.r8bemu.memory.MemoryBus;
import com.joprovost.r8bemu.memory.MemoryMapped;

public class MC6883 implements MemoryMapped {

    public static final AddressRange DISPLAY_RANGE = AddressRange.of(0x0400, 0x05ff);

    public static final int VDG_ADDRESS_PREFIX_MASK = 0b1111111000000000;
    public static final int VDG_ADDRESS_MASK = 0b0000000111111111;

    public static final int REGISTER_ADDRESS = 0xffc0;
    public static final int REGISTER_ADDRESS_PREFIX_MASK = 0b1111111111100000;
    public static final int REGISTER_ADDRESS_BIT_MASK = 0b0000000000011110;
    public static final int REGISTER_ADDRESS_VALUE_MASK = 0b0000000000000001;

    // Writing to th SAM Control Register
    // Any bit in the control register (CR) may be set by writing to a specific unicque address. Each bit has to unique
    // address... writing to the even # address clears the bit and writing to the odd # address sets the bit. (Data on
    // the data bus is irrelevant in this procedure.

    // FC00-FDFF
    // SAM Programmability
    private final Variable SAM_CONTROL_REGISTER = Variable.ofMask(0xffff);
    // VDG Address Mode (3 bits) (V2, V1, V0)
    private final Subset VDG_ADDRESS_MODE = Subset.of(SAM_CONTROL_REGISTER, 0b1110000000000000);
    // VDG Address Offset (7 bits)
    private final Subset VDG_ADDRESS_OFFSET = Subset.of(SAM_CONTROL_REGISTER, 0b0001111111000000);
    // 32 K Page Switch (1 bit)
    private final Subset PAGE_SWITCH_32K = Subset.of(SAM_CONTROL_REGISTER, 0b0000000000100000);
    // MPU Rate (2 bits)
    private final Subset MPU_RATE = Subset.of(SAM_CONTROL_REGISTER, 0b0000000000011000);
    // Memory size (2 bits)
    private final Subset MEMORY_SIZE = Subset.of(SAM_CONTROL_REGISTER, 0b0000000000000110);
    private final int MEMORY_SIZE_4K_BANK = 0b00;
    private final int MEMORY_SIZE_16K_BANK = 0b01;
    private final int MEMORY_SIZE_64K_BANK_DYNAMIC = 0b10;
    private final int MEMORY_SIZE_64K_BANK_STATIC = 0b11;


    // Map type (1 bit)
    public final Subset MAP_TYPE = Subset.of(SAM_CONTROL_REGISTER, 0b0000000000000001);

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
                InvalidDevice.invalid("display")
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
                ChipSelect.mapping(DISPLAY_RANGE, display), // Display
                ChipSelect.mapping(AddressRange.of(0xffe0, 0xffff), this.rom1), // S=2
                ChipSelect.mapping(AddressRange.of(0xff60, 0xffdf), this.sam),  // S=7
                ChipSelect.mapping(AddressRange.of(0xff40, 0xff5f), this.cs6),  // S=6
                ChipSelect.mapping(AddressRange.of(0xff20, 0xff3f), this.cs5),  // S=5
                ChipSelect.mapping(AddressRange.of(0xff00, 0xff1f), this.cs4),  // S=4
                ChipSelect.mapping(AddressRange.of(0xc000, 0xfeff), this.rom2), // S=3
                ChipSelect.mapping(AddressRange.of(0xa000, 0xbfff), this.rom1), // S=2
                ChipSelect.mapping(AddressRange.of(0x8000, 0x9fff), this.rom0), // S=1
                ChipSelect.mapping(AddressRange.of(0x0000, 0x7fff), new RamMultiplexer())
        );
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
            Constant addressRegister = Constant.of(address, 0xffff);
            if (Subset.of(addressRegister, REGISTER_ADDRESS_PREFIX_MASK).matches(REGISTER_ADDRESS)) {
                Subset.bit(SAM_CONTROL_REGISTER, Subset.of(addressRegister, REGISTER_ADDRESS_BIT_MASK).unsigned())
                      .set(Subset.of(addressRegister, REGISTER_ADDRESS_VALUE_MASK));
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
            switch (MEMORY_SIZE.unsigned()) {
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
