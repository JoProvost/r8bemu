package com.joprovost.r8bemu.data;

import java.util.function.Function;

public interface DataAccess extends DataOutput, DataInput, LogicAccess {

    String description();

    default DataAccess describedAs(String description) {
        return DataAccessSubset.of(this, mask()).describedAs(description);
    }

    default DataOutput post(Function<DataOutput, ? extends DataOutput> changes) {
        var constant = Value.of(this);
        update(changes);
        return constant;
    }

    default DataAccess pre(Function<DataOutput, ? extends DataOutput> changes) {
        return update(changes);
    }

    default DataAccess update(Function<DataOutput, ? extends DataOutput> changes) {
        value(changes.apply(this));
        return this;
    }

    default void set(int bits) {
        value(value() | bits);
    }

    default void clear(int bits) {
        value(value() & (~bits));
    }

    default void set(boolean value) {
        if (value) value(mask());
        else value(0);
    }
}
