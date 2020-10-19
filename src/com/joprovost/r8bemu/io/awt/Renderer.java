package com.joprovost.r8bemu.io.awt;

import com.joprovost.r8bemu.graphic.Screen;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class Renderer implements Screen {
    private BufferedImage image;
    private final Timer timer;

    public Renderer(Runnable callback) {
        timer = new Timer(1000/60, e -> callback.run());
        timer.setRepeats(false);
    }

    public void render(Graphics2D g2d, int width, int height) {
        if (image == null) return;
        g2d.drawImage(image, 0, 0, width, height, null);
    }

    public void pixel(int x, int y, Color color, int width, int height) {
        if (image == null || width != image.getWidth() || height != image.getHeight())
            image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        image.setRGB(x, y, color.getRGB());
        timer.start();
    }
}
