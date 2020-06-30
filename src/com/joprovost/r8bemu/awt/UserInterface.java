package com.joprovost.r8bemu.awt;

import javax.swing.*;
import java.util.List;

import static java.awt.BorderLayout.CENTER;

public class UserInterface extends JFrame {

    static {
        System.setProperty("apple.laf.useScreenMenuBar", "true");
    }

    private UserInterface(String name) {
        super(name);
    }

    public static UserInterface show(FrameBuffer frameBuffer, List<Action> actions) {
        UserInterface ui = new UserInterface("R8BEmu");
        ui.getContentPane().add(frameBuffer, CENTER);
        ui.setJMenuBar(menu(actions));

        ui.setResizable(true);
        ui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        ui.pack();
        ui.setVisible(true);
        ui.setMinimumSize(ui.getSize());
        return ui;
    }

    public static JMenuBar menu(List<Action> actions) {
        JMenuBar menubar = new JMenuBar();
        JMenu actionMenu = new JMenu("Actions");
        for (var action : actions) {
            actionMenu.add(new JMenuItem(action));
        }
        menubar.add(actionMenu);
        return menubar;
    }
}
