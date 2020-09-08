package com.joprovost.r8bemu.font;

import java.util.Comparator;
import java.util.stream.Stream;

public interface Sprite {
    static Sprite of(String... lines) {
        return new Sprite() {
            final int width = Stream.of(lines).map(String::length).max(Comparator.naturalOrder()).orElse(0) / 2;
            final int height = lines.length;

            @Override
            public boolean pixel(int x, int y) {
                return x >= 0 && y >= 0 && y < height() && (x * 2 < lines[y].length() && lines[y].charAt(x * 2) != ' ');
            }

            @Override
            public int width() {
                return width;
            }

            @Override
            public int height() {
                return height;
            }
        };
    }

    boolean pixel(int x, int y);
    int width();
    int height();
}
