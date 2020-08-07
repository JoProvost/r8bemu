package com.joprovost.r8bemu;

import com.joprovost.r8bemu.data.BitAccess;
import com.joprovost.r8bemu.data.BitOutput;
import com.joprovost.r8bemu.io.CassetteRecorder;
import com.joprovost.r8bemu.io.CassetteRecorderDispatcher;
import com.joprovost.r8bemu.io.Disk;
import com.joprovost.r8bemu.io.DiskSlot;
import com.joprovost.r8bemu.io.sound.Mixer;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.nio.file.Path;
import java.util.function.Function;

public class Actions {
    static Function<Window, Action> presentation() {
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

    static Function<Window, Action> reset(Runnable action) {
        return window -> new AbstractAction(null, new ImageIcon(Actions.class.getResource("/images/reset_32x32.png"))) {
            @Override
            public void actionPerformed(ActionEvent e) {
                action.run();
            }
        };
    }

    static Function<Window, Action> reboot(Runnable action) {
        return window -> new AbstractAction(null, new ImageIcon(Actions.class.getResource("/images/power_32x32.png"))) {
            @Override
            public void actionPerformed(ActionEvent e) {
                action.run();
            }
        };
    }

    static Function<Window, Action> rewindCassette(CassetteRecorderDispatcher cassette) {
        return window -> new AbstractAction(null, new ImageIcon(Actions.class.getResource("/images/rewind_32x32.png"))) {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                cassette.rewind();
            }
        };
    }

    static Function<Window, Action> insertCassette(Path home, CassetteRecorder cassette) {
        return window -> new AbstractAction(null, new ImageIcon(Actions.class.getResource("/images/cassette_32x32.png"))) {
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

    static Function<Window, Action> insertDisk(Path home, DiskSlot drive) {
        return window -> new AbstractAction(null, new ImageIcon(Actions.class.getResource("/images/disk_32x32.png"))) {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                var files = new JFileChooser(home.toFile());
                files.setAcceptAllFileFilterUsed(false);
                files.addChoosableFileFilter(new FileNameExtensionFilter("Disk image", "dsk", "DSK"));
                int returnVal = files.showOpenDialog(window);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    try {
                        drive.insert(Disk.of(files.getSelectedFile()));
                    } catch (IOException ignored) {
                    }
                }
            }
        };
    }

    static Function<Window, Action> mute(Mixer mixer) {
        return window -> new AbstractAction(null, mutedIcon(false)) {
            boolean muted = false;
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if (muted) {
                    mixer.volume(Mixer.VOLUME_DEFAULT);
                    muted = false;
                } else {
                    mixer.volume(Mixer.VOLUME_MUTED);
                    muted = true;
                }
                putValue(Action.SMALL_ICON, mutedIcon(muted));
            }
        };
    }

    static Function<Window, Action> keyboard(BitAccess buffered) {
        return window -> new AbstractAction(null, keyboardIcon(buffered)) {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (buffered.isSet()) buffered.clear();
                else buffered.set();
                putValue(Action.SMALL_ICON, keyboardIcon(buffered));
            }
        };
    }

    private static ImageIcon keyboardIcon(BitOutput buffered) {
        if (buffered.isSet()) {
            return new ImageIcon(Actions.class.getResource("/images/keyboard_abc_68x32.png"));
        } else {
            return new ImageIcon(Actions.class.getResource("/images/keyboard_dpad_68x32.png"));
        }
    }

    private static ImageIcon mutedIcon(boolean muted) {
        if (muted) {
            return new ImageIcon(Actions.class.getResource("/images/muted_32x32.png"));
        } else {
            return new ImageIcon(Actions.class.getResource("/images/mute_32x32.png"));
        }
    }

    private static boolean isMac() {
        return System.getProperty("os.name").toLowerCase().contains("mac");
    }
}
