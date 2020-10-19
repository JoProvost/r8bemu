package com.joprovost.r8bemu.coco.devices.gime;

import com.joprovost.r8bemu.devices.memory.Addressable;
import com.joprovost.r8bemu.graphic.Colors;

import java.awt.*;
import java.util.function.Consumer;

public class ColorPalette implements Addressable, Colors {
    private final Colors colors;

    private final Consumer<int[]> changed;

    private final int[] palette = new int[16];

    public ColorPalette(Colors colors, Consumer<int[]> changed) {
        this.colors = colors;
        this.changed = changed;
    }

    @Override
    public int read(int address) {
        if (MemoryMap.PALETTE.contains(address | 0x70000))
            return palette[MemoryMap.PALETTE.offset(address | 0x70000)];
        return 0;
    }

    @Override
    public void write(int address, int data) {
        if (MemoryMap.PALETTE.contains(address | 0x70000)) {
            palette[MemoryMap.PALETTE.offset(address | 0x70000)] = data;
            changed.accept(palette);
        }
    }

    @Override
    public Color color(int value) {
        return colors.color(palette[value % palette.length]);
    }
}
