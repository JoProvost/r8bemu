package com.joprovost.r8bemu.data;

public interface Described {
    Described EMPTY = () -> "";
    String description();
}
