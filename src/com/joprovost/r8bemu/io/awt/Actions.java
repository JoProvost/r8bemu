package com.joprovost.r8bemu.io.awt;

import com.joprovost.r8bemu.data.discrete.DiscretePort;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.nio.file.Path;
import java.util.function.Consumer;
import java.util.function.Function;

public class Actions {
    public static Function<Window, Action> presentation(ActionIcon actionIcon) {
        return window -> new AbstractAction(null, actionIcon.icon()) {
            final Runnable fullScreenToggle = fullScreenToggle(
                    window,
                    () -> putValue(Action.SMALL_ICON, actionIcon.selected()),
                    () -> putValue(Action.SMALL_ICON, actionIcon.normal()),
                    () -> setEnabled(false));

            @Override
            public void actionPerformed(ActionEvent e) {
                fullScreenToggle.run();
            }
        };
    }

    public static Function<Window, Action> toggle(ActionIcon icon, DiscretePort line) {
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

    public static Runnable fullScreenToggle(Window window, Runnable windowEnteringFullScreen, Runnable windowExitingFullScreen, Runnable onError) {
        try {
            return MacOS.isMac() ? MacOS.fullScreenToggle(window, windowEnteringFullScreen, windowExitingFullScreen) :
                    () -> {
                GraphicsDevice device = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
                if (device.getFullScreenWindow() == null) {
                    windowEnteringFullScreen.run();
                    device.setFullScreenWindow(window);
                } else {
                    windowExitingFullScreen.run();
                    device.setFullScreenWindow(null);
                }
            };
        } catch (ReflectiveOperationException e) {
            onError.run();
            return () -> {};
        }
    }
}
