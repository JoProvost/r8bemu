package com.joprovost.r8bemu.coco.font;

import com.joprovost.r8bemu.graphic.Font;
import com.joprovost.r8bemu.graphic.Sprite;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class LegacyFont3 implements Font {
    private static final Font LEGACY = new LegacyFont();
    private static final Map<Character, Sprite> SPRITES = new HashMap<>() {{
        put('#', Sprite.of(
                "                ",
                "                ",
                "                ",
                "      ██  ██    ",
                "      ██  ██    ",
                "    ██████████  ",
                "      ██  ██    ",
                "    ██████████  ",
                "      ██  ██    ",
                "      ██  ██    ",
                "                ",
                "                "
        ));

        put('%', Sprite.of(
                "                ",
                "                ",
                "                ",
                "    ████        ",
                "    ████    ██  ",
                "          ██    ",
                "        ██      ",
                "      ██        ",
                "    ██    ████  ",
                "          ████  ",
                "                ",
                "                "
        ));


        put('\'', Sprite.of(
                "                ",
                "                ",
                "                ",
                "        ██      ",
                "        ██      ",
                "      ██        ",
                "                ",
                "                ",
                "                ",
                "                ",
                "                ",
                "                "
        ));

        put('*', Sprite.of(
                "                ",
                "                ",
                "                ",
                "        ██      ",
                "    ██  ██  ██  ",
                "      ██████    ",
                "      ██████    ",
                "    ██  ██  ██  ",
                "        ██      ",
                "                ",
                "                ",
                "                "
        ));

        put('.', Sprite.of(
                "                ",
                "                ",
                "                ",
                "                ",
                "                ",
                "                ",
                "                ",
                "                ",
                "                ",
                "        ██      ",
                "                ",
                "                "
        ));

        put('/', Sprite.of(
                "                ",
                "                ",
                "                ",
                "                ",
                "            ██  ",
                "          ██    ",
                "        ██      ",
                "      ██        ",
                "    ██          ",
                "                ",
                "                ",
                "                "
        ));


        put('0', Sprite.of(
                "                ",
                "                ",
                "                ",
                "      ██████    ",
                "    ██      ██  ",
                "    ██    ████  ",
                "    ██  ██  ██  ",
                "    ████    ██  ",
                "    ██      ██  ",
                "      ██████    ",
                "                ",
                "                ",
                "                "
        ));

        put('4', Sprite.of(
                "                ",
                "                ",
                "                ",
                "          ██    ",
                "        ████    ",
                "      ██  ██    ",
                "    ██    ██    ",
                "    ██████████  ",
                "          ██    ",
                "          ██    ",
                "                ",
                "                "
        ));

        put('9', Sprite.of(
                "                ",
                "                ",
                "                ",
                "      ██████    ",
                "    ██      ██  ",
                "    ██      ██  ",
                "      ██████    ",
                "            ██  ",
                "            ██  ",
                "      ██████    ",
                "                ",
                "                ",
                "                "
        ));

        put(':', Sprite.of(
                "                ",
                "                ",
                "                ",
                "                ",
                "                ",
                "        ██      ",
                "                ",
                "                ",
                "        ██      ",
                "                ",
                "                ",
                "                "
        ));

        put(',', Sprite.of(
                "                ",
                "                ",
                "                ",
                "                ",
                "                ",
                "                ",
                "                ",
                "                ",
                "        ██      ",
                "        ██      ",
                "      ██        ",
                "                "
        ));

        put(';', Sprite.of(
                "                ",
                "                ",
                "                ",
                "                ",
                "                ",
                "        ██      ",
                "                ",
                "                ",
                "        ██      ",
                "        ██      ",
                "      ██        ",
                "                "
        ));

        put('?', Sprite.of(
                "                ",
                "                ",
                "                ",
                "      ██████    ",
                "    ██      ██  ",
                "            ██  ",
                "          ██    ",
                "        ██      ",
                "                ",
                "        ██      ",
                "                ",
                "                ",
                "                "
        ));

        put('@', Sprite.of(
                "                ",
                "                ",
                "                ",
                "      ██████    ",
                "    ██      ██  ",
                "            ██  ",
                "      ████  ██  ",
                "    ██    ████  ",
                "    ██    ████  ",
                "      ██████    ",
                "                ",
                "                "
        ));

        put('\\', Sprite.of(
                "                ",
                "                ",
                "                ",
                "                ",
                "    ██          ",
                "      ██        ",
                "        ██      ",
                "          ██    ",
                "            ██  ",
                "                ",
                "                ",
                "                "
        ));

        put('G', Sprite.of(
                "                ",
                "                ",
                "                ",
                "      ██████    ",
                "    ██      ██  ",
                "    ██          ",
                "    ██          ",
                "    ██    ████  ",
                "    ██      ██  ",
                "      ██████    ",
                "                ",
                "                "
        ));

        put('N', Sprite.of(
                "                ",
                "                ",
                "                ",
                "    ██      ██  ",
                "    ██      ██  ",
                "    ████    ██  ",
                "    ██  ██  ██  ",
                "    ██    ████  ",
                "    ██      ██  ",
                "    ██      ██  ",
                "                ",
                "                "
        ));


        put('O', Sprite.of(
                "                ",
                "                ",
                "                ",
                "      ██████    ",
                "    ██      ██  ",
                "    ██      ██  ",
                "    ██      ██  ",
                "    ██      ██  ",
                "    ██      ██  ",
                "      ██████    ",
                "                ",
                "                "
        ));

        put('S', Sprite.of(
                "                ",
                "                ",
                "                ",
                "      ██████    ",
                "    ██      ██  ",
                "    ██          ",
                "      ██████    ",
                "            ██  ",
                "    ██      ██  ",
                "      ██████    ",
                "                ",
                "                "
        ));


    }};

    @Override
    public Optional<Sprite> sprite(Character character) {
        return Optional.ofNullable(SPRITES.get(character))
                       .or(() -> LEGACY.sprite(character));
    }
}
