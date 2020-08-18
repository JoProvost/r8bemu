package com.joprovost.r8bemu.io.awt;

import com.joprovost.r8bemu.data.BitAccess;
import com.joprovost.r8bemu.data.link.LinePort;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.nio.file.Path;
import java.util.function.Consumer;
import java.util.function.Function;

public class Actions {
    public static Function<Window, Action> presentation() {
        return window -> new AbstractAction(null, new ImageIcon(Actions.class.getResource("/images/maximized_32x32.png"))) {
            {
                setEnabled(!isMac());
            }

            @Override
            public void actionPerformed(ActionEvent e) {
                GraphicsDevice device = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
                if (device.getFullScreenWindow() == null) {
                    putValue(Action.SMALL_ICON, new ImageIcon(Actions.class.getResource("/images/windowed_32x32.png")));
                    device.setFullScreenWindow(window);
                } else {
                    putValue(Action.SMALL_ICON, new ImageIcon(Actions.class.getResource("/images/maximized_32x32.png")));
                    device.setFullScreenWindow(null);
                }
            }
        };
    }

    public static Function<Window, Action> toggle(ActionIcon icon, BitAccess state) {
        return window -> new AbstractAction(null, icon.icon(state)) {
            @Override
            public void actionPerformed(ActionEvent e) {
                boolean newState = state.isClear();
                state.set(newState);
                putValue(Action.SMALL_ICON, icon.icon(newState));
            }
        };
    }

    public static Function<Window, Action> toggle(ActionIcon icon, LinePort line) {
        return window -> new AbstractAction(null, icon.icon(line)) {
            {
                line.to(state -> putValue(Action.SMALL_ICON, icon.icon(state)));
            }

            @Override
            public void actionPerformed(ActionEvent e) {
                line.set(line.isClear());
            }
        };
    }

    public static Function<Window, Action> action(ActionIcon icon, Runnable action) {
        return window -> new AbstractAction(null, icon.icon()) {
            @Override
            public void actionPerformed(ActionEvent e) {
                action.run();
            }
        };
    }

    public static Function<Window, Action> file(ActionIcon icon, Consumer<File> action, Path home, final FileNameExtensionFilter filter) {
        return window -> new AbstractAction(null, icon.icon()) {
            @Override
            public void actionPerformed(ActionEvent e) {
                    var files = new JFileChooser(home.toFile());
                    files.setAcceptAllFileFilterUsed(false);
                    files.addChoosableFileFilter(filter);
                    int returnVal = files.showOpenDialog(window);
                    if (returnVal == JFileChooser.APPROVE_OPTION) {
                        action.accept(files.getSelectedFile());
                    }
                }
            };
        }

    private static boolean isMac() {
        return System.getProperty("os.name").toLowerCase().contains("mac");
    }
}
