package com.joprovost.r8bemu.awt;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

import static java.awt.BorderLayout.CENTER;
import static java.awt.BorderLayout.PAGE_START;

public class UserInterface extends JFrame {

    private UserInterface(String name, FrameBuffer frameBuffer, List<Action> actions) {
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

    private JToolBar toolbar(List<Action> actions) {
        JToolBar toolBar = new JToolBar();
        toolBar.setVisible(false);
        toolBar.setFloatable(false);

        actions.forEach(toolBar::add);
        toolBar.addSeparator();
        toolBar.add(presentationMode());
        toolBar.setRequestFocusEnabled(false);

        return toolBar;
    }

    private AbstractAction presentationMode() {
        return new AbstractAction("Enter Presentation Mode") {
            @Override
            public void actionPerformed(ActionEvent e) {
                GraphicsDevice device = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
                if (device.getFullScreenWindow() == null) {
                    putValue(Action.NAME, "Exit Presentation Mode");
                    device.setFullScreenWindow(UserInterface.this);
                } else {
                    putValue(Action.NAME, "Enter Presentation Mode");
                    device.setFullScreenWindow(null);
                }
            }
        };
    }

    public static UserInterface show(FrameBuffer frameBuffer, List<Action> actions) {
        UserInterface ui = new UserInterface("R8BEmu", frameBuffer, actions);
        ui.setResizable(true);
        ui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        ui.pack();
        ui.setVisible(true);
        ui.setMinimumSize(ui.getSize());
        return ui;
    }
}
