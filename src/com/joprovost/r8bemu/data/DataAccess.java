package com.joprovost.r8bemu.data;

import com.joprovost.r8bemu.data.transform.DataAccessSubset;

import java.util.function.Function;

public interface DataAccess extends DataOutput, DataInput {

    static DataAccess of(DataOutput constant) {
        return new DataAccess() {
            @Override
            public String description() {
                return constant.description();
            }

            @Override
            public void value(int value) {
                throw new UnsupportedOperationException("Constant value");
            }

            @Override
            public int value() {
                return constant.value();
            }

            @Override
            public int mask() {
                return constant.mask();
            }

            @Override
            public String toString() {
                return constant.toString();
            }
        };
    }

    default DataAccess describedAs(String description) {
        return DataAccessSubset.of(this, mask()).describedAs(description);
    }

    default int post(Function<DataOutput, ? extends DataOutput> changes) {
        var constant = value();
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
