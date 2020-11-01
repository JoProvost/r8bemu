package com.joprovost.r8bemu.graphic;

import java.util.Comparator;
import java.util.stream.Stream;

public interface Sprite {
    static Sprite of(String... lines) {
        final boolean[][] pixels = new boolean[lines.length][];
        for (int y = 0; y < pixels.length; y++) {
            pixels[y] = new boolean[lines[y].length() / 2];
            for (int x = 0; x < pixels[y].length; x++) {
                pixels[y][x] = lines[y].charAt(x * 2) != ' ';
            }
        }
        return of(pixels);
    }

    static Sprite of(boolean[][] lines) {
        return new Sprite() {
            final int width = Stream.of(lines)
                                    .map(x -> x.length)
                                    .max(Comparator.naturalOrder()).orElse(0);
            final int height = lines.length;

            @Override
            public boolean pixel(int x, int y) {
                return x >= 0 && y >= 0 && y < height()
                        && x < lines[y].length && lines[y][x];
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

    default Sprite at(int ox, int oy) {
        return Transform.move(this, ox, oy);
    }

    default Sprite xor(Sprite mask) {
        return Transform.xor(this, mask);
    }

    default Sprite or(Sprite mask) {
        return Transform.or(this, mask);
    }

    default Sprite and(Sprite mask) {
        return Transform.and(this, mask);
    }

    default Sprite with(Sprite overlay) {
        return and(overlay.bold().not()).or(overlay);
    }

    default Sprite not() {
        return Transform.not(this);
    }

    default Sprite bold() {
        return Transform.bold(this);
    }

    static String asString(Sprite sprite) {
        String[] array = new String[sprite.height()];
        for (int y = 0; y < array.length; y++) {
            array[y] = "";
            for (int x = 0; x < sprite.width(); x++) {
                array[y] += sprite.pixel(x, y) ? "██" : "  ";
            }
        }
        return "Sprite.of(\n    \"" + String.join("\",\n    \"", array) + "\"\n);\n";
    }
}
