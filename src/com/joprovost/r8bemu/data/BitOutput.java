package com.joprovost.r8bemu.data;

import java.util.function.Supplier;

public interface BitOutput extends Described {
    static BitOutput not(DataAccess bit) {
        return new BitOutput() {
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

    static BitOutput of(String description, Supplier<Boolean> condition) {
        return new BitOutput() {
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

    static BitOutput and(BitOutput left, BitOutput right) {
        return new BitOutput() {
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

    static BitOutput or(BitOutput left, BitOutput right) {
        return new BitOutput() {
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
