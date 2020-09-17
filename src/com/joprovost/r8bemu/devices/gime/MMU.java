package com.joprovost.r8bemu.devices.gime;

import com.joprovost.r8bemu.data.BitOutput;
import com.joprovost.r8bemu.data.DataOutput;
import com.joprovost.r8bemu.data.Variable;
import com.joprovost.r8bemu.data.transform.DataOutputSubset;
import com.joprovost.r8bemu.devices.sam.ControlRegister;
import com.joprovost.r8bemu.devices.sam.SAMVideoMemory;
import com.joprovost.r8bemu.devices.sam.VideoMemory;
import com.joprovost.r8bemu.memory.MemoryDevice;
import com.joprovost.r8bemu.memory.Range;
import com.joprovost.r8bemu.memory.Subset;

public class MMU implements MemoryDevice {

    private final int[] vectors = {
            0xfe, 0xee, // 0xfff2
            0xfe, 0xf1, // 0xfff4
            0xfe, 0xf4, // 0xfff6
            0xfe, 0xf7, // 0xfff8
            0xfe, 0xfa, // 0xfffa
            0xfe, 0xfd, // 0xfffc
            0x8c, 0x1b, // 0xfffe
    };

    private final ControlRegister sam = new ControlRegister();

    private final Variable init0 = Variable.ofMask(0xff);
    private final BitOutput legacy = DataOutputSubset.of(init0, 0x80);
    private final BitOutput mmu = DataOutputSubset.of(init0, 0x40);
    private final BitOutput vectorRam = DataOutputSubset.of(init0, 0x08);
    private final DataOutput romMode = DataOutputSubset.of(init0, 0x03);

    private final Variable init1 = Variable.ofMask(0xff);
    private final DataOutput task = DataOutputSubset.of(init1, 0x01);

    private final Subset CPU_IO = Subset.mask(0xff00, 0xff);
    private final Subset CPU_VECTOR = Subset.mask(0xfe00, 0xff);

    private final Subset INTERRUPT = Subset.range(0x7fff2, 0x7ffff);
    private final Subset RESERVED = Subset.range(0x7ffe0, 0x7fff1);
    private final Subset SAM = Subset.mask(0x7ffc0, 0x1f);
    private final Subset GIME = Subset.range(0x7ff90, 0x7ffbf);
    private final Subset TASK0 = Subset.mask(0x7ffa0, 0x07);
    private final Subset TASK1 = Subset.mask(0x7ffa8, 0x07);
    private final Subset UNUSED = Subset.mask(0x7ff80, 0x0f);
    private final Subset SCS = Subset.mask(0x7ff40, 0x3f);
    private final Subset PIA = Subset.mask(0x7ff00, 0x3f);
    private final Range IO = Range.of(INTERRUPT, RESERVED, SAM, GIME, UNUSED, SCS, PIA);

    private final Subset VECTOR = Subset.mask(0x7fe00, 0xff);
    private final Range ROM_HIGH = Subset.mask(0x7c000, 0x3fff)
                                         .exceptIf(sam.fullRam())
                                         .excluding(IO, VECTOR.onlyIf(vectorRam));
    private final Range ROM_LOW = Subset.mask(0x78000, 0x3fff)
                                        .exceptIf(sam.fullRam());
    private final Range RAM = Subset.mask(0x00000, 0x7ffff)
                                    .excluding(IO, ROM_LOW, ROM_HIGH);

    private final MemoryBank direct = new MemoryBank();
    private final MemoryBank task0 = new MemoryBank();
    private final MemoryBank task1 = new MemoryBank();
    private final MemoryDevice memory;

    private int address;

    public MMU(MemoryDevice memory) {
        this.memory = memory;
    }

    public BitOutput rom() {
        return BitOutput.of("S0 (ROM)", () -> Range.of(ROM_LOW.onlyIf(() -> romMode.value() <= 2),
                                                       ROM_HIGH.onlyIf(() -> romMode.value() == 2))
                                                   .contains(address));
    }

    // Cartridge (ROM) Select Signal
    public BitOutput cts() {
        return BitOutput.of("S1 (CTS)", () -> Range.of(ROM_LOW.onlyIf(() -> romMode.value() == 3),
                                                       ROM_HIGH.onlyIf(() -> romMode.value() != 2))
                                                   .contains(address));
    }

    public BitOutput pia() {
        return BitOutput.of("S2 (PIA)", () -> PIA.contains(address));
    }

    // Spare Cartridge (DISK) Select Signal
    public BitOutput scs() {
        return BitOutput.of("S6 (SCS)", () -> SCS.contains(address));
    }

    public VideoMemory lowResVideo() {
        return new SAMVideoMemory(MemoryDevice.mask(0x70000, memory), sam.videoAddressMode(), sam.videoAddressOffset());
    }

    @Override
    public int read(int cpuAddress) {
        address = extended(cpuAddress);
        if (GIME.contains(address)) {
            if (address == 0x7ff90) return init0.value();
            if (address == 0x7ff91) return init1.value();
            if (TASK1.contains(address)) return task1.get(TASK1.offset(address)) >> 13;
            if (TASK0.contains(address)) return task0.get(TASK0.offset(address)) >> 13;
        }
        if (INTERRUPT.contains(address)) return vectors[INTERRUPT.offset(address)];
        if (RAM.contains(address)) return memory.read(address);
        return 0;
    }

    @Override
    public void write(int cpuAddress, int data) {
        address = extended(cpuAddress);
        if (SAM.contains(address)) sam.write(SAM.offset(address));
        if (GIME.contains(address)) {
            if (address == 0x7ff90) init0.value(data);
            if (address == 0x7ff91) init1.value(data);
            if (TASK1.contains(address)) task1.set(TASK1.offset(address), (data & 0x3f) << 13);
            if (TASK0.contains(address)) task0.set(TASK0.offset(address), (data & 0x3f) << 13);
        }
        if (RAM.contains(address)) memory.write(address, data);
    }

    private int extended(int cpuAddress) {
        if (CPU_IO.contains(cpuAddress)) return direct.translate(cpuAddress);
        if (CPU_VECTOR.onlyIf(vectorRam).contains(cpuAddress)) return direct.translate(cpuAddress);
        return bank().translate(cpuAddress);
    }

    private MemoryBank bank() {
        if (mmu.isClear()) return direct;
        switch (task.value()) {
            case 0: return task0;
            case 1: return task1;
            default: throw new IllegalStateException("task : " + task);
        }
    }
}
