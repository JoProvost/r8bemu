package com.joprovost.r8bemu.data.transform;

import com.joprovost.r8bemu.data.DataAccess;
import com.joprovost.r8bemu.data.DataOutput;

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
        value(data.mask() & filter.value());
    }

    @Override
    public void value(int value) {
        data.value((data.value() & ~filter.value()) | (value & filter.value()));
    }

    @Override
    public int value() {
        return data.value() & mask();
    }

    @Override
    public int mask() {
        return filter.value();
    }
}
