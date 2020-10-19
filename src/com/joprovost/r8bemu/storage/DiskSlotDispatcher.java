package com.joprovost.r8bemu.storage;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;

public class DiskSlotDispatcher implements DiskSlot {

    private final List<DiskSlot> targets = new ArrayList<>();
    private final Executor context;

    public DiskSlotDispatcher(Executor context) {
        this.context = context;
    }

    public void dispatchTo(DiskSlot target) {
        this.targets.add(target);
    }

    @Override
    public void insert(Disk disk) {
        context.execute(() -> {
            for (var target : targets) target.insert(disk);
        });
    }
}
