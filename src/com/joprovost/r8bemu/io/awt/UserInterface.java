package com.joprovost.r8bemu.io.awt;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.function.Function;

import static java.awt.BorderLayout.CENTER;
import static java.awt.BorderLayout.PAGE_START;

public class UserInterface extends JFrame {
    public static final Function<Window, Action> SEPARATOR = window -> null;

    private UserInterface(String name, FrameBuffer frameBuffer, List<Function<Window, Action>> actions) {
        super(name);

        var toolbar = toolbar(actions);
        add(frameBuffer, CENTER);
        add(toolbar, PAGE_START);

        frameBuffer.addMouseMotionListener(new MouseAdapter() {
            public void mouseMoved(MouseEvent e) {
                toolbar.setVisible(e.getY() < 5);
            }
        });
    }

    public static UserInterface show(FrameBuffer frameBuffer, List<Function<Window, Action>> actions) {
        UserInterface ui = new UserInterface("R8BEmu", frameBuffer, actions);
        ui.setResizable(true);
        ui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        ui.pack();
        ui.setVisible(true);
        ui.setMinimumSize(ui.getSize());
        return ui;
    }

    private JToolBar toolbar(List<Function<Window, Action>> actions) {
        JToolBar toolBar = new JToolBar();
        toolBar.setVisible(false);
        toolBar.setFloatable(false);

        for (var action : actions) {
            if (action == SEPARATOR) toolBar.addSeparator();
            else toolBar.add(action.apply(this));
        }

        toolBar.setRequestFocusEnabled(false);

        return toolBar;
    }
}
