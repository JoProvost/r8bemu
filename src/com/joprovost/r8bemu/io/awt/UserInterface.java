package com.joprovost.r8bemu.io.awt;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import static java.awt.BorderLayout.CENTER;
import static java.awt.BorderLayout.PAGE_START;

public class UserInterface extends JFrame {
    public static final Function<Window, Action> SEPARATOR = window -> null;

    private UserInterface(String name, Display display, List<Function<Window, Action>> actions) {
        super(name);

        JToolBar toolbar = toolbar(actions, display);
        add(display, CENTER);
        add(toolbar, PAGE_START);

        display.addMouseMotionListener(new MouseAdapter() {
            public void mouseMoved(MouseEvent e) {
                if (toolbar.isVisible()) {
                    toolbar.setVisible(e.getY() == 0);
                } else {
                    toolbar.setVisible(e.getY() < toolbar.getPreferredSize().height);
                }
            }
        });
    }

    public static UserInterface show(Display display, List<Function<Window, Action>> actions) {
        UserInterface ui = new UserInterface("R8BEmu", display, actions);
        List<Image> icons  = new ArrayList<>();
        icons.add(new ImageIcon(UserInterface.class.getResource("/images/logo_64x64.png")).getImage());
        icons.add(new ImageIcon(UserInterface.class.getResource("/images/logo_128x128.png")).getImage());
        ui.setIconImages(icons);

        ui.setResizable(true);
        ui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        ui.pack();
        ui.setVisible(true);
        ui.setMinimumSize(ui.getSize());
        return ui;
    }

    private JToolBar toolbar(List<Function<Window, Action>> actions, Display display) {
        JToolBar toolBar = new JToolBar();
        toolBar.setVisible(false);
        toolBar.setFloatable(false);
        toolBar.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(128, 128, 128)));

        for (var action : actions) {
            if (action == SEPARATOR) toolBar.addSeparator();
            else {
                var button = new JButton(action.apply(this));
                button.setBorder(BorderFactory.createEmptyBorder(2,2,2,2));
                button.setContentAreaFilled(false);
                button.addActionListener(xit-> display.requestFocusInWindow());
                toolBar.add(button);
            }
        }

        toolBar.setRequestFocusEnabled(false);

        return toolBar;
    }
}
