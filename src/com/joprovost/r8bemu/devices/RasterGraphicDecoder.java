package com.joprovost.r8bemu.devices;

import com.joprovost.r8bemu.devices.memory.Addressable;
import com.joprovost.r8bemu.graphic.Color;
import com.joprovost.r8bemu.graphic.Colors;

import java.util.function.IntSupplier;

public class RasterGraphicDecoder {
    private final Addressable memory;
    private final Colors colors;
    private final IntSupplier width;
    private final IntSupplier bits;

    private RasterGraphicDecoder(Addressable memory, Colors colors, IntSupplier width, IntSupplier bits) {
        this.memory = memory;
        this.colors = colors;
        this.width = width;
        this.bits = bits;
    }

    public static RasterGraphicDecoder of(Addressable memory, IntSupplier width, IntSupplier bits, Colors colors) {
        return new RasterGraphicDecoder(memory, colors, width, bits);
    }

    public Color pixel(int x, int y) {
        int bits = bits();
        int colorMask = (1 << bits) - 1;
        int pxPerByte = 8 / bits;
        int pixel = width() * y + x;
        int address = pixel / pxPerByte;
        int relative = pixel & (pxPerByte - 1);
        int shift = ((8 - bits) - bits * relative) & 7;
        return colors.color((memory.read(address) >> shift) & colorMask);
    }

    public Color[] line(int line) {
        Color[] pixels = new Color[width()];
        for (int ix = 0; ix < pixels.length; ix++) pixels[ix] = pixel(ix, line);
        return pixels;
    }

    public int width() {
        return width.getAsInt();
    }

    public int bits() {
        return bits.getAsInt();
    }
}
