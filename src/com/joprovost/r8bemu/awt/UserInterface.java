package com.joprovost.r8bemu.awt;

import javax.swing.*;
import java.awt.*;

import static java.awt.BorderLayout.CENTER;
import static java.awt.Color.BLACK;

public class UserInterface extends JFrame {

    private final FrameBuffer frameBuffer;

    private UserInterface(String name, FrameBuffer frameBuffer) {
        super(name);
        this.frameBuffer = frameBuffer;
    }

    public static UserInterface show(FrameBuffer frameBuffer) {
        UserInterface ui = new UserInterface("R8BEmu", frameBuffer);

        Container content = ui.getContentPane();
        content.setBackground(BLACK);
        content.add(ui.frameBuffer, CENTER);

        ui.setResizable(true);
        ui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        ui.pack();
        ui.setVisible(true);
        return ui;
    }
}
