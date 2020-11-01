package com.joprovost.r8bemu.graphic;

import java.util.function.BiPredicate;

public class Transform {
    public static Sprite move(Sprite sprite, int ox, int oy) {
        return new Sprite() {
            @Override
            public boolean pixel(int x, int y) {
                return sprite.pixel(x - ox, y - oy);
            }

            @Override
            public int width() {
                return sprite.width() + ox;
            }

            @Override
            public int height() {
                return sprite.height() + oy;
            }
        };
    }

    public static Sprite combine(Sprite sprite, Sprite mask, BiPredicate<Boolean, Boolean> transform) {
        return new Sprite() {
            @Override
            public boolean pixel(int x, int y) {
                return transform.test(sprite.pixel(x, y), mask.pixel(x, y));
            }

            @Override
            public int width() {
                return Math.max(sprite.width(), sprite.width());
            }

            @Override
            public int height() {
                return Math.max(sprite.height(), sprite.height());
            }
        };
    }

    public static Sprite xor(Sprite sprite, Sprite mask) {
        return combine(sprite, mask, (a, b) -> a ^ b);
    }

    public static Sprite and(Sprite sprite, Sprite mask) {
        return combine(sprite, mask, (a, b) -> a && b);
    }

    public static Sprite or(Sprite sprite, Sprite mask) {
        return combine(sprite, mask, (a, b) -> a || b);
    }

    public static Sprite not(Sprite origin) {
        return combine(origin, origin, (a, b) -> !a);
    }

    static Sprite bold(Sprite sprite) {
        return new Sprite() {
            @Override
            public boolean pixel(int x, int y) {
                for (int ix = -1; ix <= 1; ix++)
                    for (int iy = -1; iy <= 1; iy++)
                        if (sprite.pixel(x + ix, y + iy)) return true;
                return false;
            }

            @Override
            public int width() {
                return sprite.width();
            }

            @Override
            public int height() {
                return sprite.height();
            }
        };
    }
}
