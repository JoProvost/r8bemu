package com.joprovost.r8bemu.graphic;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SpriteTest {

    @Test
    void heightBasedOnNumberOfLines() {
        assertEquals(3, Sprite.of("  ", "  ", "  ").height());
    }

    @Test
    void widthBasedOnMAxNumberOfColumns() {
        assertEquals(3, Sprite.of(
                "####",
                "######",
                "##"
        ).width());
    }

    @Test
    void pixelClearWhenSpace() {
        var sprite = Sprite.of(
                "          ",
                "  ██  ██  ",
                "    ██    ",
                "  ██  ██  ",
                "          ");
        assertFalse(sprite.pixel(0, 0));
        assertFalse(sprite.pixel(4, 4));
    }

    @Test
    void pixelSetWhenSomethingElse() {
        var sprite = Sprite.of(
                "          ",
                "  ██  ██  ",
                "    ██    ",
                "  ██  ██  ",
                "          ");
        assertTrue(sprite.pixel(1, 1));
        assertTrue(sprite.pixel(3, 3));
    }

    @Test
    void pixelClearWhenOutOfBound() {
        var sprite = Sprite.of(
                "          ",
                "  ██  ██  ",
                "    ██    ",
                "  ██  ██  ",
                "          ");
        assertFalse(sprite.pixel(-1, -1));
        assertFalse(sprite.pixel(0, -1));
        assertFalse(sprite.pixel(-1, 0));
        assertFalse(sprite.pixel(5, 5));
        assertFalse(sprite.pixel(4, 5));
        assertFalse(sprite.pixel(5, 4));
    }

    @Test
    void nullSpriteHasSize0x0ANdAllPixelsAreFalse() {
        var sprite = Sprite.of();
        assertEquals(0, sprite.width());
        assertEquals(0, sprite.height());
        assertFalse(sprite.pixel(0, 0));
        assertFalse(sprite.pixel(3, 3));
    }
}
