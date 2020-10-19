package com.joprovost.r8bemu.coco.devices.gime;

import com.joprovost.r8bemu.devices.memory.Addressable;
import com.joprovost.r8bemu.graphic.Colors;

import java.awt.*;

public class ColorPalette implements Addressable, Colors {
    private final Colors colors;

    private final int[] palette = new int[16];

    public ColorPalette(Colors colors) {
        this.colors = colors;
    }

    @Override
    public int read(int address) {
        if (MemoryMap.PALETTE.contains(address | 0x70000))
            return palette[MemoryMap.PALETTE.offset(address | 0x70000)];
        return 0;
    }

    @Override
    public void write(int address, int data) {
        if (MemoryMap.PALETTE.contains(address | 0x70000))
            palette[MemoryMap.PALETTE.offset(address | 0x70000)] = data;
    }

    @Override
    public Color color(int value) {
        return colors.color(palette[value % palette.length]);
    }
}
