package com.joprovost.r8bemu.data.discrete;

import com.joprovost.r8bemu.data.Described;

import java.util.function.Supplier;

public interface DiscreteOutput extends Supplier<Boolean>, Described {
    static DiscreteOutput not(DiscreteOutput bit) {
        return new DiscreteOutput() {
            @Override
            public boolean isSet() {
                return bit.isClear();
            }

            @Override
            public String description() {
                return "!(" + bit.description() + ")";
            }
        };
    }

    boolean isSet();

    default boolean isClear() {
        return !isSet();
    }

    default Boolean get() {
        return isSet();
    }

    static DiscreteOutput of(String description, Supplier<Boolean> condition) {
        return new DiscreteOutput() {
            @Override
            public boolean isSet() {
                return condition.get();
            }

            @Override
            public String description() {
                return description;
            }
        };
    }

    static DiscreteOutput and(DiscreteOutput left, DiscreteOutput right) {
        return new DiscreteOutput() {
            @Override
            public boolean isSet() {
                return left.isSet() && right.isSet();
            }

            @Override
            public String description() {
                return left.description() + " AND " + right.isSet();
            }
        };
    }

    static DiscreteOutput or(DiscreteOutput left, DiscreteOutput right) {
        return new DiscreteOutput() {
            @Override
            public boolean isSet() {
                return left.isSet() || right.isSet();
            }

            @Override
            public String description() {
                return left.description() + " OR " + right.isSet();
            }
        };
    }
}
