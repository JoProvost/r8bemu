package com.joprovost.r8bemu.devices.keyboard;

import java.util.List;

public interface KeyboardBuffer {
    void type(List<Key> key);
    void pause(int delay);
}
