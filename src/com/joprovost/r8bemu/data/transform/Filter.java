package com.joprovost.r8bemu.data.transform;

import com.joprovost.r8bemu.data.binary.BinaryAccess;
import com.joprovost.r8bemu.data.binary.BinaryOutput;

public class Filter implements BinaryAccess {
    final BinaryAccess data;
    final BinaryOutput filter;

    private Filter(BinaryAccess data, BinaryOutput filter) {
        this.data = data;
        this.filter = filter;
    }

    public static Filter of(BinaryAccess data, BinaryOutput filter) {
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
