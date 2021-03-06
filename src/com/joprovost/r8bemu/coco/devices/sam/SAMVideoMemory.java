package com.joprovost.r8bemu.coco.devices.sam;

import com.joprovost.r8bemu.data.binary.BinaryAccess;
import com.joprovost.r8bemu.data.binary.BinaryOutput;
import com.joprovost.r8bemu.devices.memory.Addressable;

public class SAMVideoMemory implements Addressable {
    private final BinaryOutput mode;
    private Addressable ram;
    private BinaryAccess offset;

    public SAMVideoMemory(Addressable ram, BinaryOutput mode, BinaryAccess offset) {
        this.mode = mode;
        this.offset = offset;
        this.ram = ram;
    }

    @Override
    public int read(int address) {
        return ram.read(address(address) + ((offset.value()) << 9));
    }

    @Override
    public void write(int address, int data) {
        throw new UnsupportedOperationException();
    }

    public int address(int address) {
        switch (mode.value()) {
            case 0b000: // S4 S6
                return address(address, 32, 12);
            case 0b001: // CG1 (64x64) RG1 (128x64)
                return address(address, 16, 3);
            case 0b010: // S8 CG2 (128x64)
                return address(address, 32, 3);
            case 0b011: // RG2 (128x96)
                return address(address, 16, 2);
            case 0b100: // S12 CG3 (128x96)
                return address(address, 32, 2);
            case 0b101: // RG3 (128x192)
                return address(address, 16, 1);
            case 0b110: // S24 CG6(128x192) RG6
                return address(address, 32, 1);
            case 0b111: // direct memory access
            default:
                return address;
        }
    }

    public int address(int address, int bytesPerRow, int height) {
        return address % bytesPerRow + (address / bytesPerRow / height) * bytesPerRow;
    }
}
