package com.joprovost.r8bemu.coco.devices.gime;

import com.joprovost.r8bemu.clock.ClockDivider;
import com.joprovost.r8bemu.coco.devices.sam.ControlRegister;
import com.joprovost.r8bemu.coco.devices.sam.SAMVideoMemory;
import com.joprovost.r8bemu.data.binary.BinaryAccess;
import com.joprovost.r8bemu.data.binary.BinaryOutput;
import com.joprovost.r8bemu.data.binary.BinaryRegister;
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

    private final BinaryRegister verticalOffset = BinaryRegister.of(0x6c000, 0x7ffff);
    private final BinaryAccess videoOffsetLsb = BinaryAccessSubset.of(verticalOffset, 0b0000000011111111000);
    private final BinaryAccess videoOffsetMsb = BinaryAccessSubset.of(verticalOffset, 0b1111111100000000000);

    private final Addresses highRom = AddressSubset.mask(0x7c000, 0x3fff)
                                                   .exceptIf(sam.fullRam())
                                                   .excluding(MemoryMap.IO, MemoryMap.VECTOR.onlyIf(vectorRam));
    private final Addresses lowRom = AddressSubset.mask(0x78000, 0x3fff)
                                                  .exceptIf(sam.fullRam());
    private final Addresses rom = Addresses.of(lowRom.onlyIf(() -> romMode.value() <= 2),
                                               highRom.onlyIf(() -> romMode.value() == 2));
    private final Addresses cts = Addresses.of(lowRom.onlyIf(() -> romMode.value() == 3),
                                               highRom.onlyIf(() -> romMode.value() != 2));
    private final Addresses ram = AddressSubset.mask(0x00000, 0x7ffff)
                                               .excluding(MemoryMap.IO, lowRom, highRom);
    private final Addresses vector = AddressSubset.mask(0xfe00, 0xff).onlyIf(vectorRam);

    private final MemoryBank direct = new MemoryBank();
    private final MemoryBank task0 = new MemoryBank();
    private final MemoryBank task1 = new MemoryBank();
    private final Addressable memory;
    private final ClockDivider clockDivider;

    private int address;

    public MMU(Addressable memory, ClockDivider clockDivider) {
        this.memory = memory;
        this.clockDivider = clockDivider;
    }

    public DiscreteOutput rom() {
        return DiscreteOutput.of("S0 (ROM)", () -> rom.contains(address));
    }

    // Cartridge (ROM) Select Signal
    public DiscreteOutput cts() {
        return DiscreteOutput.of("S1 (CTS)", () -> cts.contains(address));
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
        init0.value(0x80);
        init1.clear();
        sam.clear();
    }

    @Override
    public int read(int cpuAddress) {
        address = extended(cpuAddress);
        if (MemoryMap.GIME.contains(address)) {
            if (address == 0x7ff90) return init0.value();
            if (address == 0x7ff91) return init1.value();
            if (address == 0x7ff9d) return videoOffsetMsb.value();
            if (address == 0x7ff9e) return videoOffsetLsb.value();
            if (MemoryMap.TASK1.contains(address)) return task1.get(MemoryMap.TASK1.offset(address)) >> 13;
            if (MemoryMap.TASK0.contains(address)) return task0.get(MemoryMap.TASK0.offset(address)) >> 13;
        }
        if (MemoryMap.INTERRUPT.contains(address)) return vectors[MemoryMap.INTERRUPT.offset(address)];
        if (ram.contains(address)) return memory.read(address);
        return 0;
    }

    @Override
    public void write(int cpuAddress, int data) {
        address = extended(cpuAddress);
        if (MemoryMap.SAM.contains(address)) {
            sam.write(MemoryMap.SAM.offset(address));
            clockDivider.divideBy(2 - sam.mpuRate().subset(0b10));
        }
        if (MemoryMap.GIME.contains(address)) {
            if (address == 0x7ff90) init0.value(data);
            if (address == 0x7ff91) init1.value(data);
            if (address == 0x7ff9d) videoOffsetMsb.value(data);
            if (address == 0x7ff9e) videoOffsetLsb.value(data);
            if (MemoryMap.TASK1.contains(address)) task1.set(MemoryMap.TASK1.offset(address), (data & 0x3f) << 13);
            if (MemoryMap.TASK0.contains(address)) task0.set(MemoryMap.TASK0.offset(address), (data & 0x3f) << 13);
        }
        if (ram.contains(address)) memory.write(address, data);
    }

    private int extended(int cpuAddress) {
        if (MemoryMap.CPU_IO.contains(cpuAddress)) return direct.translate(cpuAddress);
        if (vector.contains(cpuAddress)) return direct.translate(cpuAddress);
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
