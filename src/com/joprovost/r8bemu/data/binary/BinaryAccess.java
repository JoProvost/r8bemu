package com.joprovost.r8bemu.data.binary;

import com.joprovost.r8bemu.data.transform.BinaryAccessSubset;

import java.util.function.Function;

public interface BinaryAccess extends BinaryOutput, BinaryInput {

    static BinaryAccess of(BinaryOutput constant) {
        return new BinaryAccess() {
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

    default BinaryAccess describedAs(String description) {
        return BinaryAccessSubset.of(this, mask()).describedAs(description);
    }

    default int post(Function<BinaryOutput, ? extends BinaryOutput> changes) {
        var constant = value();
        update(changes);
        return constant;
    }

    default BinaryAccess pre(Function<BinaryOutput, ? extends BinaryOutput> changes) {
        return update(changes);
    }

    default BinaryAccess update(Function<BinaryOutput, ? extends BinaryOutput> changes) {
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
