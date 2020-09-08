package com.joprovost.r8bemu.font;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class Model3Font implements Font {
    private static final Font MODEL2 = new Model2Font();
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
    }};

    @Override
    public Optional<Sprite> sprite(Character character) {
        return Optional.ofNullable(SPRITES.get(character))
                       .or(() -> MODEL2.sprite(character));
    }
}
