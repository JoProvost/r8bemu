package com.joprovost.r8bemu.coco.font;

import com.joprovost.r8bemu.graphic.Font;
import com.joprovost.r8bemu.graphic.Sprite;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class HighResFont implements Font {
    private static final Font LEGACY3 = new LegacyFont3();
    private static final Map<Character, Sprite> SPRITES = new HashMap<>() {{
        put('^', Sprite.of(
                "        ██      ",
                "      ██  ██    ",
                "    ██      ██  ",
                "                ",
                "                ",
                "                ",
                "                ",
                "                "
        ));
        put('a', Sprite.of(
                "                ",
                "                ",
                "      ██████    ",
                "            ██  ",
                "      ████████  ",
                "    ██      ██  ",
                "      ████████  ",
                "                "
        ));

        put('b', Sprite.of(
                "    ██          ",
                "    ██          ",
                "    ██  ████    ",
                "    ████    ██  ",
                "    ██      ██  ",
                "    ████    ██  ",
                "    ██  ████    ",
                "                "
        ));

        put('c', Sprite.of(
                "                ",
                "                ",
                "      ██████    ",
                "    ██      ██  ",
                "    ██          ",
                "    ██      ██  ",
                "      ██████    ",
                "                "
        ));

        put('d', Sprite.of(
                "            ██  ",
                "            ██  ",
                "      ████  ██  ",
                "    ██    ████  ",
                "    ██      ██  ",
                "    ██    ████  ",
                "      ████  ██  ",
                "                "
        ));

        put('e', Sprite.of(
                "                ",
                "                ",
                "      ██████    ",
                "    ██      ██  ",
                "    ██████████  ",
                "    ██          ",
                "      ██████    ",
                "                "
        ));

        put('f', Sprite.of(
                "          ██    ",
                "        ██  ██  ",
                "        ██      ",
                "      ██████    ",
                "        ██      ",
                "        ██      ",
                "        ██      ",
                "                "
        ));

        put('g', Sprite.of(
                "                ",
                "                ",
                "      ████  ██  ",
                "    ██    ████  ",
                "    ██    ████  ",
                "      ████  ██  ",
                "            ██  ",
                "      ██████    "
        ));

        put('h', Sprite.of(
                "    ██          ",
                "    ██          ",
                "    ██  ████    ",
                "    ████    ██  ",
                "    ██      ██  ",
                "    ██      ██  ",
                "    ██      ██  ",
                "                "
        ));

        put('i', Sprite.of(
                "                ",
                "        ██      ",
                "                ",
                "      ████      ",
                "        ██      ",
                "        ██      ",
                "      ██████    ",
                "                "
        ));

        put('j', Sprite.of(
                "                ",
                "            ██  ",
                "                ",
                "            ██  ",
                "            ██  ",
                "            ██  ",
                "    ██      ██  ",
                "      ██████    "
        ));

        put('k', Sprite.of(
                "    ██          ",
                "    ██          ",
                "    ██    ██    ",
                "    ██  ██      ",
                "    ████        ",
                "    ██  ██      ",
                "    ██    ██    ",
                "                "
        ));

        put('l', Sprite.of(
                "      ████      ",
                "        ██      ",
                "        ██      ",
                "        ██      ",
                "        ██      ",
                "        ██      ",
                "      ██████    ",
                "                "
        ));

        put('m', Sprite.of(
                "                ",
                "                ",
                "    ████  ██    ",
                "    ██  ██  ██  ",
                "    ██  ██  ██  ",
                "    ██  ██  ██  ",
                "    ██  ██  ██  ",
                "                "
        ));

        put('n', Sprite.of(
                "                ",
                "                ",
                "    ██  ████    ",
                "    ████    ██  ",
                "    ██      ██  ",
                "    ██      ██  ",
                "    ██      ██  ",
                "                "
        ));

        put('o', Sprite.of(
                "                ",
                "                ",
                "      ██████    ",
                "    ██      ██  ",
                "    ██      ██  ",
                "    ██      ██  ",
                "      ██████    ",
                "                "
        ));

        put('p', Sprite.of(
                "                ",
                "                ",
                "    ████████    ",
                "    ██      ██  ",
                "    ██      ██  ",
                "    ████████    ",
                "    ██          ",
                "    ██          "
        ));

        put('q', Sprite.of(
                "                ",
                "                ",
                "      ████████  ",
                "    ██      ██  ",
                "    ██      ██  ",
                "      ████████  ",
                "            ██  ",
                "            ██  "
        ));

        put('r', Sprite.of(
                "                ",
                "                ",
                "    ██  ████    ",
                "    ████    ██  ",
                "    ██          ",
                "    ██          ",
                "    ██          ",
                "                "
        ));

        put('s', Sprite.of(
                "                ",
                "                ",
                "      ████████  ",
                "    ██          ",
                "      ██████    ",
                "            ██  ",
                "    ████████    ",
                "                "
        ));

        put('t', Sprite.of(
                "      ██        ",
                "      ██        ",
                "    ██████      ",
                "      ██        ",
                "      ██        ",
                "      ██    ██  ",
                "        ████    ",
                "                "
        ));

        put('u', Sprite.of(
                "                ",
                "                ",
                "    ██      ██  ",
                "    ██      ██  ",
                "    ██      ██  ",
                "    ██    ████  ",
                "      ████  ██  ",
                "                "
        ));

        put('v', Sprite.of(
                "                ",
                "                ",
                "    ██      ██  ",
                "    ██      ██  ",
                "    ██      ██  ",
                "      ██  ██    ",
                "        ██      ",
                "                "
        ));

        put('w', Sprite.of(
                "                ",
                "                ",
                "    ██      ██  ",
                "    ██  ██  ██  ",
                "    ██  ██  ██  ",
                "      ██  ██    ",
                "      ██  ██    ",
                "                "
        ));

        put('x', Sprite.of(
                "                ",
                "                ",
                "    ██      ██  ",
                "      ██  ██    ",
                "        ██      ",
                "      ██  ██    ",
                "    ██      ██  ",
                "                "
        ));

        put('y', Sprite.of(
                "                ",
                "                ",
                "    ██      ██  ",
                "    ██      ██  ",
                "    ██      ██  ",
                "      ████████  ",
                "            ██  ",
                "      ██████    "
        ));

        put('z', Sprite.of(
                "                ",
                "                ",
                "    ██████████  ",
                "          ██    ",
                "        ██      ",
                "      ██        ",
                "    ██████████  ",
                "                "
        ));

        put('{', Sprite.of(
                "          ██    ",
                "        ██      ",
                "        ██      ",
                "      ██        ",
                "        ██      ",
                "        ██      ",
                "          ██    ",
                "                "
        ));

        put('¦', Sprite.of(
                "        ██      ",
                "        ██      ",
                "        ██      ",
                "                ",
                "        ██      ",
                "        ██      ",
                "        ██      ",
                "                "
        ));

        put('}', Sprite.of(
                "      ██        ",
                "        ██      ",
                "        ██      ",
                "          ██    ",
                "        ██      ",
                "        ██      ",
                "      ██        ",
                "                "
        ));

        put('~', Sprite.of(
                "      ██        ",
                "    ██  ██  ██  ",
                "          ██    ",
                "                ",
                "                ",
                "                ",
                "                ",
                "                "
        ));

        put('_', Sprite.of(
                "                ",
                "                ",
                "                ",
                "                ",
                "                ",
                "                ",
                "    ██████████  ",
                "                "
        ));

        put('Ç', Sprite.of(
                "      ██████    ",
                "    ██      ██  ",
                "    ██          ",
                "    ██          ",
                "    ██          ",
                "    ██      ██  ",
                "      ██████    ",
                "        ██      "
        ));

        put('ü', Sprite.of(
                "    ██      ██  ",
                "                ",
                "    ██      ██  ",
                "    ██      ██  ",
                "    ██      ██  ",
                "    ██    ████  ",
                "      ████  ██  ",
                "                "
        ));

        put('é', Sprite.of(
                "          ██    ",
                "        ██      ",
                "      ██████    ",
                "    ██      ██  ",
                "    ██████████  ",
                "    ██          ",
                "      ██████    ",
                "                "
        ));

        put('â', Sprite.of(
                "        ██      ",
                "      ██  ██    ",
                "      ██████    ",
                "            ██  ",
                "      ████████  ",
                "    ██      ██  ",
                "      ████████  ",
                "                "
        ));

        put('ä', Sprite.of(
                "      ██  ██    ",
                "                ",
                "      ██████    ",
                "            ██  ",
                "      ████████  ",
                "    ██      ██  ",
                "      ████████  ",
                "                "
        ));

        put('à', Sprite.of(
                "      ██        ",
                "        ██      ",
                "      ██████    ",
                "            ██  ",
                "      ████████  ",
                "    ██      ██  ",
                "      ████████  ",
                "                "
        ));

        put('å', Sprite.of(
                "        ██      ",
                "                ",
                "      ██████    ",
                "            ██  ",
                "      ████████  ",
                "    ██      ██  ",
                "      ████████  ",
                "                "
        ));

        put('ç', Sprite.of(
                "                ",
                "                ",
                "      ██████    ",
                "    ██      ██  ",
                "    ██          ",
                "    ██      ██  ",
                "      ██████    ",
                "        ██      "
        ));

        put('ê', Sprite.of(
                "        ██      ",
                "      ██  ██    ",
                "      ██████    ",
                "    ██      ██  ",
                "    ██████████  ",
                "    ██          ",
                "      ██████    ",
                "                "
        ));

        put('ë', Sprite.of(
                "      ██  ██    ",
                "                ",
                "      ██████    ",
                "    ██      ██  ",
                "    ██████████  ",
                "    ██          ",
                "      ██████    ",
                "                "
        ));

        put('è', Sprite.of(
                "      ██        ",
                "        ██      ",
                "      ██████    ",
                "    ██      ██  ",
                "    ██████████  ",
                "    ██          ",
                "      ██████    ",
                "                "
        ));

        put('ï', Sprite.of(
                "      ██  ██    ",
                "                ",
                "      ████      ",
                "        ██      ",
                "        ██      ",
                "        ██      ",
                "      ██████    ",
                "                "
        ));

        put('î', Sprite.of(
                "        ██      ",
                "      ██  ██    ",
                "                ",
                "      ████      ",
                "        ██      ",
                "        ██      ",
                "      ██████    ",
                "                "
        ));

        put('ß', Sprite.of(
                "                ",
                "        ████    ",
                "      ██    ██  ",
                "      ██████    ",
                "      ██    ██  ",
                "      ██    ██  ",
                "      ██████    ",
                "    ██          "
        ));

        put('Ä', Sprite.of(
                "    ██      ██  ",
                "        ██      ",
                "      ██  ██    ",
                "    ██      ██  ",
                "    ██████████  ",
                "    ██      ██  ",
                "    ██      ██  ",
                "                "
        ));

        put('Å', Sprite.of(
                "        ██      ",
                "        ██      ",
                "      ██  ██    ",
                "    ██      ██  ",
                "    ██████████  ",
                "    ██      ██  ",
                "    ██      ██  ",
                "                "
        ));

        put('ó', Sprite.of(
                "          ██    ",
                "        ██      ",
                "      ██████    ",
                "    ██      ██  ",
                "    ██      ██  ",
                "    ██      ██  ",
                "      ██████    ",
                "                "
        ));

        put('æ', Sprite.of(
                "                ",
                "                ",
                "    ████  ██    ",
                "        ██  ██  ",
                "      ████████  ",
                "    ██  ██      ",
                "      ████████  ",
                "                "
        ));

        put('Æ', Sprite.of(
                "      ████████  ",
                "    ██  ██      ",
                "    ██  ██      ",
                "    ████████    ",
                "    ██  ██      ",
                "    ██  ██      ",
                "    ██  ██████  ",
                "                "
        ));

        put('ô', Sprite.of(
                "        ██      ",
                "      ██  ██    ",
                "      ██████    ",
                "    ██      ██  ",
                "    ██      ██  ",
                "    ██      ██  ",
                "      ██████    ",
                "                "
        ));

        put('ö', Sprite.of(
                "      ██  ██    ",
                "                ",
                "      ██████    ",
                "    ██      ██  ",
                "    ██      ██  ",
                "    ██      ██  ",
                "      ██████    ",
                "                "
        ));

        put('ø', Sprite.of(
                "                ",
                "                ",
                "      ██████    ",
                "    ██    ████  ",
                "    ██  ██  ██  ",
                "    ████    ██  ",
                "      ██████    ",
                "                "
        ));

        put('û', Sprite.of(
                "        ██      ",
                "      ██  ██    ",
                "                ",
                "    ██      ██  ",
                "    ██      ██  ",
                "    ██    ████  ",
                "      ████  ██  ",
                "                "
        ));

        put('ù', Sprite.of(
                "      ██        ",
                "        ██      ",
                "    ██      ██  ",
                "    ██      ██  ",
                "    ██      ██  ",
                "    ██    ████  ",
                "      ████  ██  ",
                "                "
        ));

        put('Ø', Sprite.of(
                "      ██████    ",
                "    ██    ████  ",
                "    ██  ██  ██  ",
                "    ██  ██  ██  ",
                "    ██  ██  ██  ",
                "    ████    ██  ",
                "      ██████    ",
                "                "
        ));

        put('Ö', Sprite.of(
                "    ██      ██  ",
                "      ██████    ",
                "    ██      ██  ",
                "    ██      ██  ",
                "    ██      ██  ",
                "    ██      ██  ",
                "      ██████    ",
                "                "
        ));

        put('Ü', Sprite.of(
                "      ██  ██    ",
                "    ██      ██  ",
                "    ██      ██  ",
                "    ██      ██  ",
                "    ██      ██  ",
                "    ██      ██  ",
                "      ██████    ",
                "                "
        ));

        put('§', Sprite.of(
                "      ██████    ",
                "    ██          ",
                "      ██████    ",
                "    ██      ██  ",
                "      ██████    ",
                "            ██  ",
                "      ██████    ",
                "                "
        ));

        put('£', Sprite.of(
                "          ██    ",
                "        ██  ██  ",
                "        ██      ",
                "      ██████    ",
                "        ██      ",
                "    ██  ██      ",
                "      ████████  ",
                "                "
        ));

        put('±', Sprite.of(
                "        ██      ",
                "        ██      ",
                "    ██████████  ",
                "        ██      ",
                "        ██      ",
                "                ",
                "    ██████████  ",
                "                "
        ));

        put('°', Sprite.of(
                "        ██      ",
                "      ██  ██    ",
                "        ██      ",
                "                ",
                "                ",
                "                ",
                "                ",
                "                "
        ));

        put('ƒ', Sprite.of(
                "          ██    ",
                "        ██  ██  ",
                "        ██      ",
                "      ██████    ",
                "        ██      ",
                "        ██      ",
                "      ██        ",
                "    ██          "
        ));

        put('＿', Sprite.of(
                "                ",
                "                ",
                "                ",
                "                ",
                "                ",
                "                ",
                "                ",
                "████████████████"
        ));


    }};

    @Override
    public Optional<Sprite> sprite(Character character) {
        return Optional.ofNullable(SPRITES.get(character))
                       .or(() -> LEGACY3.sprite(character)
                                        .map(x -> x.clipY(3, 8)));
    }
}
