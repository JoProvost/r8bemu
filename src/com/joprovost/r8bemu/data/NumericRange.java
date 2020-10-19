package com.joprovost.r8bemu.data;

public class NumericRange {
    public final double min;
    public final double zero;
    public final double max;

    public NumericRange(double min, double zero, double max) {
        this.min = min;
        this.zero = zero;
        this.max = max;
    }

    public double normalize(double value) {
        if (value == zero) return 0.0;
        if (between(value, min, zero))
            return bounded((zero - value) / (min - zero), -1, 1);
        return bounded((value - zero) / (max - zero), -1, 1);
    }

    public double from(double normalized) {
        if (normalized < 0.0)
            return bounded(normalized * (zero - min) + zero, lowest() , highest());
        if (normalized > 0.0)
            return bounded(normalized * (max - zero) + zero, lowest(), highest());
        return zero;
    }

    public double from(double value, NumericRange unit) {
        return from(unit.normalize(value));
    }

    public boolean between(double value, double a, double b) {
        return (a > b) ? a >= value && value >= b : a <= value && value <= b;
    }

    private double lowest() {
        return Math.min(min, max);
    }

    private double highest() {
        return Math.max(min, max);
    }

    private double bounded(double value, double lowest, double highest) {
        return value < lowest ? lowest : Math.min(value, highest);
    }
}
