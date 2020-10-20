package com.joprovost.r8bemu.coco.font;

import com.joprovost.r8bemu.coco.devices.MC6847;
import com.joprovost.r8bemu.graphic.Font;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class LegacyFontTest {
    Font font = new LegacyFont();

    @Test
    void hasAllCharactersCovered() {
        for (char character : MC6847.CHARACTERS) {
            assertTrue(font.sprite(character).isPresent(), "missing character: '" + character + "'");
        }
    }
}
