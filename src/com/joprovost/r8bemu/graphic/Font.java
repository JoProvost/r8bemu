package com.joprovost.r8bemu.graphic;

import java.util.Map;
import java.util.Optional;

public interface Font {
    Optional<Sprite> sprite(Character character);

    static Font of(Map<Character, Sprite> map) {
        return character -> Optional.ofNullable(map.get(character));
    }
}
