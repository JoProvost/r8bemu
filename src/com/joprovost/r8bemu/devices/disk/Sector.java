package com.joprovost.r8bemu.devices.disk;

import com.joprovost.r8bemu.memory.MemoryDevice;

public interface Sector extends MemoryDevice {
    int id();

    int attribute();

    int size();
}
