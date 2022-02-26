package com.joprovost.r8bemu.storage;

import com.joprovost.r8bemu.devices.memory.Addressable;

public interface Sector extends Addressable {
    int id();
    int size();
}
