package com.joprovost.r8bemu.coco.font;

import com.joprovost.r8bemu.coco.devices.gime.DisplayProcessor;
import com.joprovost.r8bemu.graphic.Font;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class HighResFontTest {
    Font font = new HighResFont();

    @Test
    void hasAllCharactersCovered() {
        for (char character : DisplayProcessor.CHARACTERS.toCharArray()) {
            assertTrue(font.sprite(character).isPresent(), "missing character: '" + character + "'");
        }
    }
}
