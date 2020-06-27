package com.joprovost.r8bemu.data;

public class DataOutputReference implements DataOutput {
    private DataOutput subject;

    private DataOutputReference(DataOutput subject) {
        this.subject = subject;
    }

    public static DataOutputReference empty() {
        return DataOutputReference.of(Value.NONE);
    }

    public static DataOutputReference of(DataOutput subject) {
        return new DataOutputReference(subject);
    }

    @Override
    public int value() {
        return subject.value();
    }

    @Override
    public int mask() {
        return subject.mask();
    }


    @Override
    public String description() {
        return subject.description();
    }

    @Override
    public String toString() {
        return subject.toString();
    }

    public DataOutputReference referTo(DataOutput subject) {
        this.subject = subject;
        return this;
    }
}
