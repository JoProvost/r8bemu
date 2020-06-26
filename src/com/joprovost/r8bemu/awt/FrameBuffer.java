package com.joprovost.r8bemu.awt;

import com.joprovost.r8bemu.Display;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

import static java.awt.Color.BLACK;
import static java.awt.Color.BLUE;
import static java.awt.Color.CYAN;
import static java.awt.Color.GREEN;
import static java.awt.Color.LIGHT_GRAY;
import static java.awt.Color.MAGENTA;
import static java.awt.Color.ORANGE;
import static java.awt.Color.RED;
import static java.awt.Color.YELLOW;
import static java.awt.RenderingHints.KEY_INTERPOLATION;
import static java.awt.RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR;

public class FrameBuffer extends Canvas implements Display {

    public static final int width = 256;
    public static final int height = 192;
    private final BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
    private final Timer timer;

    public FrameBuffer() {
        timer = new Timer(10, e -> repaint());
        timer.setRepeats(false);
        setMinimumSize(new Dimension(width * 2, height * 2));
        setPreferredSize(new Dimension(width * 2, height * 2));
        setBackground(java.awt.Color.white);
        setVisible(true);
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(KEY_INTERPOLATION, VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        g2d.drawImage(image, 0, 0, getWidth(), getHeight(), null);
    }

    public FrameBuffer clear(Color color) {
        for (int x = 0; x < width; x++)
            for (int y = 0; y < height; y++)
                pixel(x, y, color);
        return this;
    }

    @Override
    public void pixel(int x, int y, Color color) {
        switch (color) {
            case GREEN: image.setRGB(x, y, GREEN.getRGB());
                break;
            case YELLOW: image.setRGB(x, y, YELLOW.getRGB());
                break;
            case BLUE: image.setRGB(x, y, BLUE.getRGB());
                break;
            case RED: image.setRGB(x, y, RED.getRGB());
                break;
            case BUFF: image.setRGB(x, y, LIGHT_GRAY.getRGB());
                break;
            case CYAN: image.setRGB(x, y, CYAN.getRGB());
                break;
            case MAGENTA: image.setRGB(x, y, MAGENTA.getRGB());
                break;
            case ORANGE: image.setRGB(x, y, ORANGE.getRGB());
                break;
            case BLACK: image.setRGB(x, y, BLACK.getRGB());
                break;
        }
        timer.start();
    }
}
