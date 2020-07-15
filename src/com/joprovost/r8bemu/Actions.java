package com.joprovost.r8bemu;

import com.joprovost.r8bemu.data.LogicAccess;
import com.joprovost.r8bemu.data.LogicOutput;
import com.joprovost.r8bemu.io.CassetteRecorderDispatcher;
import com.joprovost.r8bemu.mc6809.Signal;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.nio.file.Path;
import java.util.function.Function;

public class Actions {
    static Function<Window, Action> presentation() {
        return window -> new AbstractAction("View", new ImageIcon(Actions.class.getResource("/images/maximized_32x32.png"))) {
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

    static Function<Window, Action> reset() {
        return window -> new AbstractAction("Reset", new ImageIcon(Actions.class.getResource("/images/reset_32x32.png"))) {
            @Override
            public void actionPerformed(ActionEvent e) {
                Signal.RESET.set();
            }
        };
    }

    static Function<Window, Action> rewindCassette(CassetteRecorderDispatcher cassette) {
        return window -> new AbstractAction("Cassette", new ImageIcon(Actions.class.getResource("/images/rewind_32x32.png"))) {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                cassette.rewind();
            }
        };
    }

    static Function<Window, Action> insertCassette(Path home, CassetteRecorderDispatcher cassette) {
        return window -> new AbstractAction("Cassette", new ImageIcon(Actions.class.getResource("/images/cassette_64x32.png"))) {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                var files = new JFileChooser(home.toFile());
                files.setAcceptAllFileFilterUsed(false);
                files.addChoosableFileFilter(new FileNameExtensionFilter("Audio file", "wav"));
                int returnVal = files.showOpenDialog(window);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    cassette.insert(files.getSelectedFile());
                }
            }
        };
    }

    static Function<Window, Action> keyboard(LogicAccess buffered) {
        return window -> new AbstractAction("View", keyboardIcon(buffered)) {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (buffered.isSet()) buffered.clear();
                else buffered.set();
                putValue(Action.SMALL_ICON, keyboardIcon(buffered));
            }
        };
    }

    private static ImageIcon keyboardIcon(LogicOutput buffered) {
        if (buffered.isSet()) {
            return new ImageIcon(Actions.class.getResource("/images/keyboard_raw_64x32.png"));
        } else {
            return new ImageIcon(Actions.class.getResource("/images/keyboard_buffered_64x32.png"));
        }
    }
}
