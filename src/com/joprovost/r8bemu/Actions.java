package com.joprovost.r8bemu;

import com.joprovost.r8bemu.io.CassetteRecorderDispatcher;
import com.joprovost.r8bemu.mc6809.Signal;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.function.Function;

public class Actions {
    static Function<Window, Action> presentation() {
        return window -> new AbstractAction("View", new ImageIcon(Actions.class.getResource("/icons/maximized_32x32.png"))) {
            @Override
            public void actionPerformed(ActionEvent e) {
                GraphicsDevice device = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
                if (device.getFullScreenWindow() == null) {
                    putValue(Action.SMALL_ICON, new ImageIcon(Actions.class.getResource("/icons/windowed_32x32.png")));
                    device.setFullScreenWindow(window);
                } else {
                    putValue(Action.SMALL_ICON, new ImageIcon(Actions.class.getResource("/icons/maximized_32x32.png")));
                    device.setFullScreenWindow(null);
                }
            }
        };
    }

    static Function<Window, Action> reset() {
        return window -> new AbstractAction("Reset", new ImageIcon(Actions.class.getResource("/icons/reset_32x32.png"))) {
            @Override
            public void actionPerformed(ActionEvent e) {
                Signal.RESET.set();
            }
        };
    }

    static Function<Window, Action> rewindCassette(String home, CassetteRecorderDispatcher cassette) {
        return window -> new AbstractAction("Cassette", new ImageIcon(Actions.class.getResource("/icons/rewind_32x32.png"))) {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                cassette.rewind();
            }
        };
    }

    static Function<Window, Action> insertCassette(String home, CassetteRecorderDispatcher cassette) {
        return window -> new AbstractAction("Cassette", new ImageIcon(Actions.class.getResource("/icons/cassette_64x32.png"))) {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                var files = new JFileChooser(home);
                int returnVal = files.showOpenDialog(window);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    cassette.insert(files.getSelectedFile());
                }
            }
        };
    }
}
