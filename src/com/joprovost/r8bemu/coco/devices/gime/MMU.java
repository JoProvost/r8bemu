package com.joprovost.r8bemu.coco.devices.gime;

import com.joprovost.r8bemu.coco.devices.sam.ControlRegister;
import com.joprovost.r8bemu.coco.devices.sam.SAMVideoMemory;
import com.joprovost.r8bemu.data.binary.BinaryAccess;
import com.joprovost.r8bemu.data.binary.BinaryOutput;
import com.joprovost.r8bemu.data.binary.BinaryRegister;
import com.joprovost.r8bemu.data.discrete.DiscreteAccess;
import com.joprovost.r8bemu.data.discrete.DiscreteOutput;
import com.joprovost.r8bemu.data.transform.BinaryAccessSubset;
import com.joprovost.r8bemu.data.transform.BinaryOutputSubset;
import com.joprovost.r8bemu.devices.memory.AddressSubset;
import com.joprovost.r8bemu.devices.memory.Addressable;
import com.joprovost.r8bemu.devices.memory.Addresses;

public class MMU implements Addressable {

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

    private final BinaryRegister init0 = BinaryRegister.ofMask(0xff);
    private final DiscreteOutput legacy = BinaryOutputSubset.of(init0, 0x80);
    private final DiscreteOutput mmu = BinaryOutputSubset.of(init0, 0x40);
    private final DiscreteOutput vectorRam = BinaryOutputSubset.of(init0, 0x08);
    private final BinaryOutput romMode = BinaryOutputSubset.of(init0, 0x03);

    private final BinaryRegister init1 = BinaryRegister.ofMask(0xff);
    private final BinaryOutput task = BinaryOutputSubset.of(init1, 0x01);
    private final DiscreteAccess memory512k = BinaryAccessSubset.of(init1, 0x40);

    private final BinaryRegister verticalOffset = BinaryRegister.of(0x6c000, 0x7ffff);
    private final BinaryAccess videoOffsetLsb = BinaryAccessSubset.of(verticalOffset, 0b0000000011111111000);
    private final BinaryAccess videoOffsetMsb = BinaryAccessSubset.of(verticalOffset, 0b1111111100000000000);

    private final AddressSubset CPU_IO = AddressSubset.mask(0xff00, 0xff);
    private final AddressSubset CPU_VECTOR = AddressSubset.mask(0xfe00, 0xff);

    private final Addresses ROM_HIGH = AddressSubset.mask(0x7c000, 0x3fff)
                                                    .exceptIf(sam.fullRam())
                                                    .excluding(MemoryMap.IO, MemoryMap.VECTOR.onlyIf(vectorRam));
    private final Addresses ROM_LOW = AddressSubset.mask(0x78000, 0x3fff)
                                                   .exceptIf(sam.fullRam());
    private final Addresses RAM = AddressSubset.mask(0x00000, 0x7ffff)
                                               .excluding(MemoryMap.IO, ROM_LOW, ROM_HIGH);

    private final MemoryBank direct = new MemoryBank();
    private final MemoryBank task0 = new MemoryBank();
    private final MemoryBank task1 = new MemoryBank();
    private final Addressable memory;

    private int address;

    public MMU(Addressable memory) {
        this.memory = memory;
    }

    public DiscreteOutput rom() {
        return DiscreteOutput.of("S0 (ROM)", () -> Addresses.of(ROM_LOW.onlyIf(() -> romMode.value() <= 2),
                                                                ROM_HIGH.onlyIf(() -> romMode.value() == 2))
                                                            .contains(address));
    }

    // Cartridge (ROM) Select Signal
    public DiscreteOutput cts() {
        return DiscreteOutput.of("S1 (CTS)", () -> Addresses.of(ROM_LOW.onlyIf(() -> romMode.value() == 3),
                                                                ROM_HIGH.onlyIf(() -> romMode.value() != 2))
                                                            .contains(address));
    }

    public DiscreteOutput pia() {
        return DiscreteOutput.of("S2 (PIA)", () -> MemoryMap.PIA.contains(address));
    }

    // Spare Cartridge (DISK) Select Signal
    public DiscreteOutput scs() {
        return DiscreteOutput.of("S6 (SCS)", () -> MemoryMap.SCS.contains(address));
    }

    public Addressable lowResVideo() {
        return new SAMVideoMemory(Addressable.mask(0x70000, memory), sam.videoAddressMode(), sam.videoAddressOffset());
    }

    public Addressable video() {
        return Addressable.offset(memory, verticalOffset);
    }

    public DiscreteOutput legacy() {
        return legacy;
    }

    public void clear() {
        init0.clear();
        init1.clear();
        sam.clear();
    }

    @Override
    public int read(int cpuAddress) {
        address = extended(cpuAddress);
        if (address ==0x01f5b5) {
            var a=true;
        }
        if (MemoryMap.GIME.contains(address)) {
            if (address == 0x7ff90) return init0.value();
            if (address == 0x7ff91) return init1.value();
            if (address == 0x7ff9d) return videoOffsetMsb.value();
            if (address == 0x7ff9e) return videoOffsetLsb.value();
            if (MemoryMap.TASK1.contains(address)) return task1.get(MemoryMap.TASK1.offset(address)) >> 13;
            if (MemoryMap.TASK0.contains(address)) return task0.get(MemoryMap.TASK0.offset(address)) >> 13;
        }
        if (MemoryMap.INTERRUPT.contains(address)) return vectors[MemoryMap.INTERRUPT.offset(address)];
        if (RAM.contains(address)) return memory.read(address);
        return 0;
    }

    @Override
    public void write(int cpuAddress, int data) {
        address = extended(cpuAddress);
        if (MemoryMap.SAM.contains(address)) sam.write(MemoryMap.SAM.offset(address));
        if (MemoryMap.GIME.contains(address)) {
            if (address == 0x7ff90) init0.value(data);
            if (address == 0x7ff91) init1.value(data);
            if (address == 0x7ff9d) videoOffsetMsb.value(data);
            if (address == 0x7ff9e) videoOffsetLsb.value(data);
            if (MemoryMap.TASK1.contains(address)) task1.set(MemoryMap.TASK1.offset(address), (data & 0x3f) << 13);
            if (MemoryMap.TASK0.contains(address)) task0.set(MemoryMap.TASK0.offset(address), (data & 0x3f) << 13);
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
