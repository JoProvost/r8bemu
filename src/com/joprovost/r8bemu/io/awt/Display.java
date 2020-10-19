package com.joprovost.r8bemu.io.awt;

import com.joprovost.r8bemu.graphic.Screen;

import javax.swing.*;
import java.awt.*;

import static java.awt.RenderingHints.KEY_INTERPOLATION;
import static java.awt.RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR;

public class Display extends JPanel {
    private final Renderer renderer = new Renderer(this::repaint);

    public Display() {
        setMinimumSize(new Dimension(768, 576));
        setPreferredSize(new Dimension(768, 576));
        setVisible(true);

        setFocusable(true);
        setFocusTraversalKeysEnabled(false);
        grabFocus();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(KEY_INTERPOLATION, VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        renderer.render(g2d, getWidth(), getHeight());
    }

    public Screen screen() {
        return renderer;
    }
}
