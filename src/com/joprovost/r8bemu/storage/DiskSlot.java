package com.joprovost.r8bemu.storage;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.Executor;

public interface DiskSlot {

    static DiskSlotDispatcher dispatcher(Executor context) {
        return new DiskSlotDispatcher(context);
    }

    void insert(Disk disk);

    default void insert(File disk) {
        try {
            insert(Disk.of(disk));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
