package com.joprovost.r8bemu.coco.devices.gime;

public class MemoryBank {
    public static final int PAGE_SIZE = 0x2000;
    private final int[] pages = {0x70000, 0x72000, 0x74000, 0x76000, 0x78000, 0x7a000, 0x7c000, 0x7e000};

    public int translate(int address) {
        return page(address) | (address & 0x1fff);
    }

    public int page(int address) {
        return pages[bloc(address)];
    }

    public int get(int bloc) {
        return pages[bloc];
    }

    public void set(int bloc, int page) {
        pages[bloc] = page;
    }

    private int bloc(int address) {
        return (address & 0xffff) / PAGE_SIZE;
    }
}
