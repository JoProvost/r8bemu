package com.joprovost.r8bemu.data;

public class Filter implements DataAccess {
    final DataAccess data;
    final DataOutput filter;

    private Filter(DataAccess data, DataOutput filter) {
        this.data = data;
        this.filter = filter;
    }

    public static Filter of(DataAccess data, DataOutput filter) {
        return new Filter(data, filter);
    }

    @Override
    public String description() {
        return data.description();
    }

    @Override
    public void set(boolean value) {
        set(data.mask() & filter.unsigned());
    }

    @Override
    public void set(int value) {
        data.set((data.unsigned() & ~filter.unsigned()) | (value & filter.unsigned()));
    }

    @Override
    public int unsigned() {
        return data.unsigned() & mask();
    }

    @Override
    public int mask() {
        return filter.unsigned();
    }
}
