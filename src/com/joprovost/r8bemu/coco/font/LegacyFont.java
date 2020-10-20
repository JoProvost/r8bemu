package com.joprovost.r8bemu.coco.font;

import com.joprovost.r8bemu.graphic.Font;
import com.joprovost.r8bemu.graphic.Sprite;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class LegacyFont implements Font {
    private static final Font GRAPHIC = new GraphicFont();
    private static final Map<Character, Sprite> SPRITES = new HashMap<>() {{
        put(' ', Sprite.of(
                "                ",
                "                ",
                "                ",
                "                ",
                "                ",
                "                ",
                "                ",
                "                ",
                "                ",
                "                ",
                "                ",
                "                "
        ));

        put('!', Sprite.of(
                "                ",
                "                ",
                "                ",
                "        ██      ",
                "        ██      ",
                "        ██      ",
                "        ██      ",
                "        ██      ",
                "                ",
                "        ██      ",
                "                ",
                "                "
        ));

        put('"', Sprite.of(
                "                ",
                "                ",
                "                ",
                "      ██  ██    ",
                "      ██  ██    ",
                "      ██  ██    ",
                "                ",
                "                ",
                "                ",
                "                ",
                "                ",
                "                "
        ));

        put('#', Sprite.of(
                "                ",
                "                ",
                "                ",
                "      ██  ██    ",
                "      ██  ██    ",
                "    ████  ████  ",
                "                ",
                "    ████  ████  ",
                "      ██  ██    ",
                "      ██  ██    ",
                "                ",
                "                "
        ));

        put('$', Sprite.of(
                "                ",
                "                ",
                "                ",
                "        ██      ",
                "      ████████  ",
                "    ██          ",
                "      ██████    ",
                "            ██  ",
                "    ████████    ",
                "        ██      ",
                "                ",
                "                "
        ));

        put('%', Sprite.of(
                "                ",
                "                ",
                "                ",
                "    ████    ██  ",
                "    ████    ██  ",
                "          ██    ",
                "        ██      ",
                "      ██        ",
                "    ██    ████  ",
                "    ██    ████  ",
                "                ",
                "                "
        ));

        put('&', Sprite.of(
                "                ",
                "                ",
                "                ",
                "      ██        ",
                "    ██  ██      ",
                "    ██  ██      ",
                "      ██        ",
                "    ██  ██  ██  ",
                "    ██    ██    ",
                "      ████  ██  ",
                "                ",
                "                "
        ));

        put('\'', Sprite.of(
                "                ",
                "                ",
                "                ",
                "      ████      ",
                "      ████      ",
                "      ████      ",
                "                ",
                "                ",
                "                ",
                "                ",
                "                ",
                "                "
        ));

        put('(', Sprite.of(
                "                ",
                "                ",
                "                ",
                "        ██      ",
                "      ██        ",
                "    ██          ",
                "    ██          ",
                "    ██          ",
                "      ██        ",
                "        ██      ",
                "                ",
                "                "
        ));

        put(')', Sprite.of(
                "                ",
                "                ",
                "                ",
                "        ██      ",
                "          ██    ",
                "            ██  ",
                "            ██  ",
                "            ██  ",
                "          ██    ",
                "        ██      ",
                "                ",
                "                "
        ));

        put('*', Sprite.of(
                "                ",
                "                ",
                "                ",
                "                ",
                "        ██      ",
                "      ██████    ",
                "    ██████████  ",
                "      ██████    ",
                "        ██      ",
                "                ",
                "                ",
                "                "
        ));

        put('+', Sprite.of(
                "                ",
                "                ",
                "                ",
                "                ",
                "        ██      ",
                "        ██      ",
                "    ██████████  ",
                "        ██      ",
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
                "    ████        ",
                "    ████        ",
                "      ██        ",
                "    ██          ",
                "                ",
                "                "
        ));

        put('-', Sprite.of(
                "                ",
                "                ",
                "                ",
                "                ",
                "                ",
                "                ",
                "    ██████████  ",
                "                ",
                "                ",
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
                "    ████        ",
                "    ████        ",
                "                ",
                "                "
        ));

        put('/', Sprite.of(
                "                ",
                "                ",
                "                ",
                "            ██  ",
                "            ██  ",
                "          ██    ",
                "        ██      ",
                "      ██        ",
                "    ██          ",
                "    ██          ",
                "                ",
                "                "
        ));

        put('0', Sprite.of(
                "                ",
                "                ",
                "                ",
                "      ████      ",
                "    ██    ██    ",
                "    ██    ██    ",
                "    ██    ██    ",
                "    ██    ██    ",
                "    ██    ██    ",
                "      ████      ",
                "                ",
                "                "
        ));

        put('1', Sprite.of(
                "                ",
                "                ",
                "                ",
                "        ██      ",
                "      ████      ",
                "        ██      ",
                "        ██      ",
                "        ██      ",
                "        ██      ",
                "      ██████    ",
                "                ",
                "                "
        ));

        put('2', Sprite.of(
                "                ",
                "                ",
                "                ",
                "      ██████    ",
                "    ██      ██  ",
                "            ██  ",
                "      ██████    ",
                "    ██          ",
                "    ██          ",
                "    ██████████  ",
                "                ",
                "                "
        ));

        put('3', Sprite.of(
                "                ",
                "                ",
                "                ",
                "      ██████    ",
                "    ██      ██  ",
                "            ██  ",
                "      ██████    ",
                "            ██  ",
                "    ██      ██  ",
                "      ██████    ",
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
                "    ██████████  ",
                "          ██    ",
                "          ██    ",
                "          ██    ",
                "                ",
                "                "
        ));

        put('5', Sprite.of(
                "                ",
                "                ",
                "                ",
                "    ██████████  ",
                "    ██          ",
                "    ████████    ",
                "            ██  ",
                "            ██  ",
                "    ██      ██  ",
                "      ██████    ",
                "                ",
                "                "
        ));

        put('6', Sprite.of(
                "                ",
                "                ",
                "                ",
                "      ██████    ",
                "    ██          ",
                "    ██          ",
                "    ████████    ",
                "    ██      ██  ",
                "    ██      ██  ",
                "      ██████    ",
                "                ",
                "                "
        ));

        put('7', Sprite.of(
                "                ",
                "                ",
                "                ",
                "    ██████████  ",
                "            ██  ",
                "          ██    ",
                "        ██      ",
                "      ██        ",
                "    ██          ",
                "    ██          ",
                "                ",
                "                "
        ));

        put('8', Sprite.of(
                "                ",
                "                ",
                "                ",
                "      ██████    ",
                "    ██      ██  ",
                "    ██      ██  ",
                "      ██████    ",
                "    ██      ██  ",
                "    ██      ██  ",
                "      ██████    ",
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
                "      ████████  ",
                "            ██  ",
                "            ██  ",
                "      ██████    ",
                "                ",
                "                "
        ));

        put(':', Sprite.of(
                "                ",
                "                ",
                "                ",
                "                ",
                "      ████      ",
                "      ████      ",
                "                ",
                "      ████      ",
                "      ████      ",
                "                ",
                "                ",
                "                "
        ));

        put(';', Sprite.of(
                "                ",
                "                ",
                "                ",
                "      ████      ",
                "      ████      ",
                "                ",
                "      ████      ",
                "      ████      ",
                "        ██      ",
                "      ██        ",
                "                ",
                "                "
        ));

        put('<', Sprite.of(
                "                ",
                "                ",
                "                ",
                "            ██  ",
                "          ██    ",
                "        ██      ",
                "      ██        ",
                "        ██      ",
                "          ██    ",
                "            ██  ",
                "                ",
                "                "
        ));

        put('=', Sprite.of(
                "                ",
                "                ",
                "                ",
                "                ",
                "                ",
                "    ██████████  ",
                "                ",
                "    ██████████  ",
                "                ",
                "                ",
                "                ",
                "                "
        ));

        put('>', Sprite.of(
                "                ",
                "                ",
                "                ",
                "      ██        ",
                "        ██      ",
                "          ██    ",
                "            ██  ",
                "          ██    ",
                "        ██      ",
                "      ██        ",
                "                ",
                "                "
        ));

        put('?', Sprite.of(
                "                ",
                "                ",
                "                ",
                "      ████      ",
                "    ██    ██    ",
                "          ██    ",
                "        ██      ",
                "        ██      ",
                "                ",
                "        ██      ",
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
                "    ██  ██  ██  ",
                "    ██  ██  ██  ",
                "      ██████    ",
                "                ",
                "                "
        ));

        put('A', Sprite.of(
                "                ",
                "                ",
                "                ",
                "        ██      ",
                "      ██  ██    ",
                "    ██      ██  ",
                "    ██████████  ",
                "    ██      ██  ",
                "    ██      ██  ",
                "    ██      ██  ",
                "                ",
                "                "
        ));

        put('B', Sprite.of(
                "                ",
                "                ",
                "                ",
                "    ████████    ",
                "      ██    ██  ",
                "      ██    ██  ",
                "      ██████    ",
                "      ██    ██  ",
                "      ██    ██  ",
                "    ████████    ",
                "                ",
                "                "
        ));

        put('C', Sprite.of(
                "                ",
                "                ",
                "                ",
                "      ██████    ",
                "    ██      ██  ",
                "    ██          ",
                "    ██          ",
                "    ██          ",
                "    ██      ██  ",
                "      ██████    ",
                "                ",
                "                "
        ));

        put('D', Sprite.of(
                "                ",
                "                ",
                "                ",
                "    ████████    ",
                "      ██    ██  ",
                "      ██    ██  ",
                "      ██    ██  ",
                "      ██    ██  ",
                "      ██    ██  ",
                "    ████████    ",
                "                ",
                "                "
        ));

        put('E', Sprite.of(
                "                ",
                "                ",
                "                ",
                "    ██████████  ",
                "    ██          ",
                "    ██          ",
                "    ██████      ",
                "    ██          ",
                "    ██          ",
                "    ██████████  ",
                "                ",
                "                "
        ));

        put('F', Sprite.of(
                "                ",
                "                ",
                "                ",
                "    ██████████  ",
                "    ██          ",
                "    ██          ",
                "    ██████      ",
                "    ██          ",
                "    ██          ",
                "    ██          ",
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
                "      ████████  ",
                "                ",
                "                "
        ));

        put('H', Sprite.of(
                "                ",
                "                ",
                "                ",
                "    ██      ██  ",
                "    ██      ██  ",
                "    ██      ██  ",
                "    ██████████  ",
                "    ██      ██  ",
                "    ██      ██  ",
                "    ██      ██  ",
                "                ",
                "                "
        ));

        put('I', Sprite.of(
                "                ",
                "                ",
                "                ",
                "      ██████    ",
                "        ██      ",
                "        ██      ",
                "        ██      ",
                "        ██      ",
                "        ██      ",
                "      ██████    ",
                "                ",
                "                "
        ));

        put('J', Sprite.of(
                "                ",
                "                ",
                "                ",
                "            ██  ",
                "            ██  ",
                "            ██  ",
                "            ██  ",
                "            ██  ",
                "    ██      ██  ",
                "      ██████    ",
                "                ",
                "                "
        ));

        put('K', Sprite.of(
                "                ",
                "                ",
                "                ",
                "    ██      ██  ",
                "    ██    ██    ",
                "    ██  ██      ",
                "    ████        ",
                "    ██  ██      ",
                "    ██    ██    ",
                "    ██      ██  ",
                "                ",
                "                "
        ));

        put('L', Sprite.of(
                "                ",
                "                ",
                "                ",
                "    ██          ",
                "    ██          ",
                "    ██          ",
                "    ██          ",
                "    ██          ",
                "    ██          ",
                "    ██████████  ",
                "                ",
                "                "
        ));

        put('M', Sprite.of(
                "                ",
                "                ",
                "                ",
                "    ██      ██  ",
                "    ████  ████  ",
                "    ██  ██  ██  ",
                "    ██  ██  ██  ",
                "    ██      ██  ",
                "    ██      ██  ",
                "    ██      ██  ",
                "                ",
                "                "
        ));

        put('N', Sprite.of(
                "                ",
                "                ",
                "                ",
                "    ██      ██  ",
                "    ████    ██  ",
                "    ██  ██  ██  ",
                "    ██    ████  ",
                "    ██      ██  ",
                "    ██      ██  ",
                "    ██      ██  ",
                "                ",
                "                "
        ));

        put('O', Sprite.of(
                "                ",
                "                ",
                "                ",
                "    ██████████  ",
                "    ██      ██  ",
                "    ██      ██  ",
                "    ██      ██  ",
                "    ██      ██  ",
                "    ██      ██  ",
                "    ██████████  ",
                "                ",
                "                "
        ));

        put('P', Sprite.of(
                "                ",
                "                ",
                "                ",
                "    ████████    ",
                "    ██      ██  ",
                "    ██      ██  ",
                "    ████████    ",
                "    ██          ",
                "    ██          ",
                "    ██          ",
                "                ",
                "                "
        ));

        put('Q', Sprite.of(
                "                ",
                "                ",
                "                ",
                "      ██████    ",
                "    ██      ██  ",
                "    ██      ██  ",
                "    ██      ██  ",
                "    ██  ██  ██  ",
                "    ██    ██    ",
                "      ████  ██  ",
                "                ",
                "                "
        ));

        put('R', Sprite.of(
                "                ",
                "                ",
                "                ",
                "    ████████    ",
                "    ██      ██  ",
                "    ██      ██  ",
                "    ████████    ",
                "    ██  ██      ",
                "    ██    ██    ",
                "    ██      ██  ",
                "                ",
                "                "
        ));

        put('S', Sprite.of(
                "                ",
                "                ",
                "                ",
                "      ██████    ",
                "    ██      ██  ",
                "      ██        ",
                "        ██      ",
                "          ██    ",
                "    ██      ██  ",
                "      ██████    ",
                "                ",
                "                "
        ));

        put('T', Sprite.of(
                "                ",
                "                ",
                "                ",
                "    ██████████  ",
                "        ██      ",
                "        ██      ",
                "        ██      ",
                "        ██      ",
                "        ██      ",
                "        ██      ",
                "                ",
                "                "
        ));

        put('U', Sprite.of(
                "                ",
                "                ",
                "                ",
                "    ██      ██  ",
                "    ██      ██  ",
                "    ██      ██  ",
                "    ██      ██  ",
                "    ██      ██  ",
                "    ██      ██  ",
                "      ██████    ",
                "                ",
                "                "
        ));

        put('V', Sprite.of(
                "                ",
                "                ",
                "                ",
                "    ██      ██  ",
                "    ██      ██  ",
                "    ██      ██  ",
                "      ██  ██    ",
                "      ██  ██    ",
                "        ██      ",
                "        ██      ",
                "                ",
                "                "
        ));

        put('W', Sprite.of(
                "                ",
                "                ",
                "                ",
                "    ██      ██  ",
                "    ██      ██  ",
                "    ██      ██  ",
                "    ██      ██  ",
                "    ██  ██  ██  ",
                "    ████  ████  ",
                "    ██      ██  ",
                "                ",
                "                "
        ));

        put('X', Sprite.of(
                "                ",
                "                ",
                "                ",
                "    ██      ██  ",
                "    ██      ██  ",
                "      ██  ██    ",
                "        ██      ",
                "      ██  ██    ",
                "    ██      ██  ",
                "    ██      ██  ",
                "                ",
                "                "
        ));

        put('Y', Sprite.of(
                "                ",
                "                ",
                "                ",
                "    ██      ██  ",
                "    ██      ██  ",
                "      ██  ██    ",
                "        ██      ",
                "        ██      ",
                "        ██      ",
                "        ██      ",
                "                ",
                "                "
        ));

        put('Z', Sprite.of(
                "                ",
                "                ",
                "                ",
                "    ██████████  ",
                "            ██  ",
                "          ██    ",
                "        ██      ",
                "      ██        ",
                "    ██          ",
                "    ██████████  ",
                "                ",
                "                "
        ));


        put('[', Sprite.of(
                "                ",
                "                ",
                "                ",
                "      ██████    ",
                "      ██        ",
                "      ██        ",
                "      ██        ",
                "      ██        ",
                "      ██        ",
                "      ██████    ",
                "                ",
                "                "
        ));

        put('\\', Sprite.of(
                "                ",
                "                ",
                "                ",
                "    ██          ",
                "    ██          ",
                "      ██        ",
                "        ██      ",
                "          ██    ",
                "            ██  ",
                "            ██  ",
                "                ",
                "                "
        ));

        put(']', Sprite.of(
                "                ",
                "                ",
                "                ",
                "      ██████    ",
                "          ██    ",
                "          ██    ",
                "          ██    ",
                "          ██    ",
                "          ██    ",
                "      ██████    ",
                "                ",
                "                "
        ));

        put('↑', Sprite.of(
                "                ",
                "                ",
                "                ",
                "        ██      ",
                "      ██████    ",
                "    ██  ██  ██  ",
                "        ██      ",
                "        ██      ",
                "        ██      ",
                "        ██      ",
                "                ",
                "                "
        ));

        put('←', Sprite.of(
                "                ",
                "                ",
                "                ",
                "                ",
                "        ██      ",
                "      ██        ",
                "    ██████████  ",
                "      ██        ",
                "        ██      ",
                "                ",
                "                ",
                "                "
        ));

    }};

    @Override
    public Optional<Sprite> sprite(Character character) {
        return Optional.ofNullable(SPRITES.get(character))
                       .or(() -> GRAPHIC.sprite(character));
    }
}