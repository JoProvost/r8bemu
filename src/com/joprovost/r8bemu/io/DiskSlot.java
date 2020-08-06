package com.joprovost.r8bemu.io;

import java.util.concurrent.Executor;

public interface DiskSlot {

    static DiskSlotDispatcher dispatcher(Executor context) {
        return new DiskSlotDispatcher(context);
    }

    void insert(Disk disk);
}
