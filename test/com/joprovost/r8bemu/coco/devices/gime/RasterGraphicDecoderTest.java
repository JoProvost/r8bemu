package com.joprovost.r8bemu.coco.devices.gime;

import com.joprovost.r8bemu.Assert;
import com.joprovost.r8bemu.devices.RasterGraphicDecoder;
import com.joprovost.r8bemu.devices.memory.Addressable;
import com.joprovost.r8bemu.devices.memory.Memory;
import com.joprovost.r8bemu.graphic.Colors;
import org.junit.jupiter.api.Test;

import java.awt.*;

class RasterGraphicDecoderTest {

    private final Addressable memory = new Memory(0xffff);
    private final Colors black = value -> new Color(0, 0, 0, value);

    @Test
    void readPixelsWhenOnePixelPerByte() {
        RasterGraphicDecoder decoder = RasterGraphicDecoder.of(memory, () -> 4, () -> 8, black);
        memory.write(
                0,
                0x10, 0x20, 0x30, 0x40,
                0x50, 0x60, 0x70, 0x80
        );
        Assert.assertEquals(0x10, decoder.pixel(0, 0).getAlpha());
        Assert.assertEquals(0x20, decoder.pixel(1, 0).getAlpha());
        Assert.assertEquals(0x50, decoder.pixel(0, 1).getAlpha());
        Assert.assertEquals(0x60, decoder.pixel(1, 1).getAlpha());
    }

    @Test
    void readPixelsWhenTwoPixelsPerByte() {
        RasterGraphicDecoder decoder = RasterGraphicDecoder.of(memory, () -> 4, () -> 4, black);
        memory.write(
                0,
                0x12, 0x34,
                0x56, 0x78
        );
        Assert.assertEquals(0x1, decoder.pixel(0, 0).getAlpha());
        Assert.assertEquals(0x2, decoder.pixel(1, 0).getAlpha());
        Assert.assertEquals(0x5, decoder.pixel(0, 1).getAlpha());
        Assert.assertEquals(0x6, decoder.pixel(1, 1).getAlpha());
    }

    @Test
    void readPixelsWhenFourPixelsPerByte() {
        RasterGraphicDecoder decoder = RasterGraphicDecoder.of(memory, () -> 4, () -> 2, black);
        memory.write(
                0,
                0b11_01_10_00,
                0b00_10_01_11
        );
        Assert.assertEquals(0b11, decoder.pixel(0, 0).getAlpha());
        Assert.assertEquals(0b01, decoder.pixel(1, 0).getAlpha());
        Assert.assertEquals(0b00, decoder.pixel(0, 1).getAlpha());
        Assert.assertEquals(0b10, decoder.pixel(1, 1).getAlpha());
    }
}
