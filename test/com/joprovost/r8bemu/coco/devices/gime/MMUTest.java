package com.joprovost.r8bemu.coco.devices.gime;

import com.joprovost.r8bemu.Assert;
import com.joprovost.r8bemu.devices.memory.Addressable;
import com.joprovost.r8bemu.devices.memory.Memory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static com.joprovost.r8bemu.data.binary.BinaryOutput.hex;
import static com.joprovost.r8bemu.devices.memory.Addressable.bus;
import static com.joprovost.r8bemu.devices.memory.Addressable.readOnly;
import static com.joprovost.r8bemu.devices.memory.Addressable.select;
import static com.joprovost.r8bemu.devices.memory.Addressable.when;

class MMUTest {
    public static final int ENABLE_MMU = 0x40;
    public static final int ROM_32K_MODE = 0x02;
    Addressable ram = new Memory(0x7ffff) {{
        for (int addr = 0; addr < 0x7ffff; addr += 0x2000) {
            write(addr, ("RAM AT " + hex(addr, 0x7ffff)).getBytes());
        }
        write(0x7fe00, "RAM VECTOR".getBytes());
    }};

    MMU mmu = new MMU(ram);

    Addressable rom = new Memory(0x7fff) {{
        write(0x8000, "EXTENDED BASIC".getBytes());
        write(0xa000, "BASIC".getBytes());
        write(0xe000, "SUPER BASIC".getBytes());
        write(0xfe00, "ROM VECTOR".getBytes());
    }};

    Addressable cart = new Memory(0x1fff) {{
        write(0xc000, "DISK BASIC".getBytes());
    }};

    Addressable pia0 = new Memory(0x1f) {{
        write(0xff00, "PIA0".getBytes());
    }};
    Addressable pia1 = new Memory(0x1f) {{
        write(0xff20, "PIA1".getBytes());
    }};
    Addressable disk = new Memory(0x3f) {{
        write(0xff40, "SCS".getBytes());
    }};

    Addressable bus = bus(
            mmu,
            when(mmu.rom(), readOnly(rom)),
            when(mmu.cts(), readOnly(cart)),
            when(mmu.pia(),
                 select(0x00, 0x20, pia0),
                 select(0x20, 0x20, pia1)
            ),
            when(mmu.scs(), disk)
    );

    @BeforeEach
    void setupMMU() {
        bus.write(0xff90, ENABLE_MMU | ROM_32K_MODE);
    }

    @Test
    void readFromPeripherals() {
        pia0.write(0x00, "PIA0".getBytes());
        pia1.write(0x00, "PIA1".getBytes());
        disk.write(0x00, "SCS".getBytes());

        Assert.assertRead("PIA0", bus, 0xff00);
        Assert.assertRead("PIA1", bus, 0xff20);
        Assert.assertRead("SCS", bus, 0xff40);
    }

    @Test
    void interruptVectors() {
        Assert.assertReadWord(0x8c1b, bus, 0xfffe);
        Assert.assertReadWord(0xfefd, bus, 0xfffc);
        Assert.assertReadWord(0xfefa, bus, 0xfffa);
        Assert.assertReadWord(0xfef7, bus, 0xfff8);
        Assert.assertReadWord(0xfef4, bus, 0xfff6);
        Assert.assertReadWord(0xfef1, bus, 0xfff4);
        Assert.assertReadWord(0xfeee, bus, 0xfff2);
    }

    @Test
    void secondaryVectorsNormallyMapped() {
        Assert.assertRead("ROM VECTOR", bus, 0xfe00);
    }

    @Test
    void secondaryVectorsInRam() {
        bus.write(0xff90, 0x08);
        Assert.assertRead("RAM VECTOR", bus, 0xfe00);
    }

    @Nested
    class CompatibilityMemoryMapping {
        @BeforeEach
        void setup() {
            bus.write(0xff90, ENABLE_MMU);
        }

        @Test
        void read() {
            Assert.assertRead("RAM AT 70000", bus, 0x0000);
            Assert.assertRead("RAM AT 72000", bus, 0x2000);
            Assert.assertRead("RAM AT 74000", bus, 0x4000);
            Assert.assertRead("RAM AT 76000", bus, 0x6000);

            Assert.assertRead("EXTENDED BASIC", bus, 0x8000);
            Assert.assertRead("BASIC", bus, 0xa000);
            Assert.assertRead("DISK BASIC", bus, 0xc000);

            Assert.assertRead("PIA0", bus, 0xff00);
            Assert.assertRead("PIA1", bus, 0xff20);
            Assert.assertRead("SCS", bus, 0xff40);
        }

        @Test
        void write() {
            bus.write(0x0000, "WRITE AT 0x0000".getBytes());
            bus.write(0x2000, "WRITE AT 0x2000".getBytes());
            bus.write(0x4000, "WRITE AT 0x4000".getBytes());
            bus.write(0x6000, "WRITE AT 0x6000".getBytes());
            bus.write(0x8000, "WRITE AT 0x8000".getBytes());

            bus.write(0xa000, "WRITE AT 0xa000".getBytes());
            bus.write(0xc000, "WRITE AT 0xc000".getBytes());
            bus.write(0xe000, "WRITE AT 0xe000".getBytes());

            bus.write(0xff00, "WRITE AT PIA0".getBytes());
            bus.write(0xff20, "WRITE AT PIA1".getBytes());
            bus.write(0xff40, "WRITE AT SCS".getBytes());

            Assert.assertRead("WRITE AT 0x0000", bus, 0x0000);
            Assert.assertRead("WRITE AT 0x2000", bus, 0x2000);
            Assert.assertRead("WRITE AT 0x4000", bus, 0x4000);
            Assert.assertRead("WRITE AT 0x6000", bus, 0x6000);

            Assert.assertRead("EXTENDED BASIC", bus, 0x8000);
            Assert.assertRead("BASIC", bus, 0xa000);
            Assert.assertRead("DISK BASIC", bus, 0xc000);

            Assert.assertRead("WRITE AT PIA0", bus, 0xff00);
            Assert.assertRead("WRITE AT PIA1", bus, 0xff20);
            Assert.assertRead("WRITE AT SCS", bus, 0xff40);
        }
    }

    @Nested
    class CustomMemoryMapping {
        @BeforeEach
        void configureBanks() {
            // bank 0
            bus.write(0xffa0, 0);
            bus.write(0xffa1, 1);
            bus.write(0xffa2, 2);
            bus.write(0xffa3, 3);
            bus.write(0xffa4, 4);
            bus.write(0xffa5, 5);
            bus.write(0xffa6, 6);
            bus.write(0xffa7, 7);

            // bank 1
            bus.write(0xffa8, 7);
            bus.write(0xffa9, 6);
            bus.write(0xffaa, 5);
            bus.write(0xffab, 4);
            bus.write(0xffac, 3);
            bus.write(0xffad, 2);
            bus.write(0xffae, 1);
            bus.write(0xffaf, 0);
        }

        @Nested
        class FullMemory512K {

            @Nested
            class FirstBank {
                @BeforeEach
                void configureBanks() {
                    bus.write(0xff91, 0x00);
                }

                @Test
                void read() {
                    Assert.assertRead("RAM AT 00000", bus, 0x0000);
                    Assert.assertRead("RAM AT 02000", bus, 0x2000);
                    Assert.assertRead("RAM AT 04000", bus, 0x4000);
                    Assert.assertRead("RAM AT 06000", bus, 0x6000);
                    Assert.assertRead("RAM AT 08000", bus, 0x8000);
                    Assert.assertRead("RAM AT 0a000", bus, 0xa000);
                    Assert.assertRead("RAM AT 0c000", bus, 0xc000);
                    Assert.assertRead("RAM AT 0e000", bus, 0xe000);

                    Assert.assertRead("PIA0", bus, 0xff00);
                    Assert.assertRead("PIA1", bus, 0xff20);
                    Assert.assertRead("SCS", bus, 0xff40);
                }

                @Test
                void write() {
                    bus.write(0x0000, "WRITE AT 0x0000".getBytes());
                    bus.write(0x2000, "WRITE AT 0x2000".getBytes());
                    bus.write(0x4000, "WRITE AT 0x4000".getBytes());
                    bus.write(0x6000, "WRITE AT 0x6000".getBytes());
                    bus.write(0x8000, "WRITE AT 0x8000".getBytes());
                    bus.write(0xa000, "WRITE AT 0xa000".getBytes());
                    bus.write(0xc000, "WRITE AT 0xc000".getBytes());
                    bus.write(0xe000, "WRITE AT 0xe000".getBytes());

                    bus.write(0xff00, "WRITE AT PIA0".getBytes());
                    bus.write(0xff20, "WRITE AT PIA1".getBytes());
                    bus.write(0xff40, "WRITE AT SCS".getBytes());

                    Assert.assertRead("WRITE AT 0x0000", bus, 0x0000);
                    Assert.assertRead("WRITE AT 0x2000", bus, 0x2000);
                    Assert.assertRead("WRITE AT 0x4000", bus, 0x4000);
                    Assert.assertRead("WRITE AT 0x6000", bus, 0x6000);
                    Assert.assertRead("WRITE AT 0x8000", bus, 0x8000);
                    Assert.assertRead("WRITE AT 0xa000", bus, 0xa000);
                    Assert.assertRead("WRITE AT 0xc000", bus, 0xc000);
                    Assert.assertRead("WRITE AT 0xe000", bus, 0xe000);

                    Assert.assertRead("WRITE AT PIA0", bus, 0xff00);
                    Assert.assertRead("WRITE AT PIA1", bus, 0xff20);
                    Assert.assertRead("WRITE AT SCS", bus, 0xff40);
                }
            }

            @Nested
            class SecondBank {
                @BeforeEach
                void configureBanks() {
                    bus.write(0xff91, 0x01);
                }

                @Test
                void read() {
                    Assert.assertRead("RAM AT 0e000", bus, 0x0000);
                    Assert.assertRead("RAM AT 0c000", bus, 0x2000);
                    Assert.assertRead("RAM AT 0a000", bus, 0x4000);
                    Assert.assertRead("RAM AT 08000", bus, 0x6000);
                    Assert.assertRead("RAM AT 06000", bus, 0x8000);
                    Assert.assertRead("RAM AT 04000", bus, 0xa000);
                    Assert.assertRead("RAM AT 02000", bus, 0xc000);
                    Assert.assertRead("RAM AT 00000", bus, 0xe000);

                    Assert.assertRead("PIA0", bus, 0xff00);
                    Assert.assertRead("PIA1", bus, 0xff20);
                    Assert.assertRead("SCS", bus, 0xff40);
                }

                @Test
                void write() {
                    bus.write(0x0000, "WRITE AT 0x0000".getBytes());
                    bus.write(0x2000, "WRITE AT 0x2000".getBytes());
                    bus.write(0x4000, "WRITE AT 0x4000".getBytes());
                    bus.write(0x6000, "WRITE AT 0x6000".getBytes());
                    bus.write(0x8000, "WRITE AT 0x8000".getBytes());
                    bus.write(0xa000, "WRITE AT 0xa000".getBytes());
                    bus.write(0xc000, "WRITE AT 0xc000".getBytes());
                    bus.write(0xe000, "WRITE AT 0xe000".getBytes());

                    bus.write(0xff00, "WRITE AT PIA0".getBytes());
                    bus.write(0xff20, "WRITE AT PIA1".getBytes());
                    bus.write(0xff40, "WRITE AT SCS".getBytes());

                    Assert.assertRead("WRITE AT 0x0000", bus, 0x0000);
                    Assert.assertRead("WRITE AT 0x2000", bus, 0x2000);
                    Assert.assertRead("WRITE AT 0x4000", bus, 0x4000);
                    Assert.assertRead("WRITE AT 0x6000", bus, 0x6000);
                    Assert.assertRead("WRITE AT 0x8000", bus, 0x8000);
                    Assert.assertRead("WRITE AT 0xa000", bus, 0xa000);
                    Assert.assertRead("WRITE AT 0xc000", bus, 0xc000);
                    Assert.assertRead("WRITE AT 0xe000", bus, 0xe000);

                    Assert.assertRead("WRITE AT PIA0", bus, 0xff00);
                    Assert.assertRead("WRITE AT PIA1", bus, 0xff20);
                    Assert.assertRead("WRITE AT SCS", bus, 0xff40);
                }
            }
        }
    }
}
