package com.joprovost.r8bemu.io.awt;

import com.joprovost.r8bemu.graphic.Sprite;

import javax.swing.*;
import java.awt.*;

public class SpriteIcon implements Icon {

    private final Sprite sprite;
    private final Color color;

    private SpriteIcon(Sprite sprite, Color color) {
        this.sprite = sprite;
        this.color = color;
    }

    public static SpriteIcon of(Sprite sprite) {
        return new SpriteIcon(sprite, Color.BLACK);
    }

    @Override
    public void paintIcon(Component c, Graphics g, int x, int y) {
        g.setColor(color);
        for (int i = 0; i < sprite.width(); i++)
            for (int j = 0; j < sprite.height(); j++)
                if (sprite.pixel(i, j))
                    g.fillRect(x + i, y + j, 1, 1);
    }

    @Override
    public int getIconWidth() {
        return sprite.width();
    }

    @Override
    public int getIconHeight() {
        return sprite.height();
    }
}
