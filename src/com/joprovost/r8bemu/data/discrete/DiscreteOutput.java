package com.joprovost.r8bemu.data.discrete;

import com.joprovost.r8bemu.data.Described;

import java.util.StringJoiner;
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

    static DiscreteOutput and(DiscreteOutput... outputs) {
        return new DiscreteOutput() {
            @Override
            public boolean isSet() {
                for (var output : outputs) if (!output.isSet()) return false;
                return true;
            }

            @Override
            public String description() {
                var joiner = new StringJoiner(" AND ");
                for (var output : outputs) joiner.add(output.description());
                return joiner.toString();
            }
        };
    }

    static DiscreteOutput or(DiscreteOutput... outputs) {
        return new DiscreteOutput() {
            @Override
            public boolean isSet() {
                for (var output : outputs) if (output.isSet()) return true;
                return false;
            }

            @Override
            public String description() {
                var joiner = new StringJoiner(" OR ");
                for (var output : outputs) joiner.add(output.description());
                return joiner.toString();
            }
        };
    }
}
