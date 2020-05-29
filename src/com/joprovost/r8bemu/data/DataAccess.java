package com.joprovost.r8bemu.data;

import java.util.function.Function;

public interface DataAccess extends DataOutput, DataInput {

    String description();

    default DataAccess describedAs(String description) {
        return Subset.of(this, mask()).describedAs(description);
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
        set(changes.apply(this));
        return this;
    }

    default void set(boolean value) {
        if (value) set(mask());
        else set(0);
    }

    default DataAccess replace(DataOutput value) {
        set(value);
        return this;
    }
}
