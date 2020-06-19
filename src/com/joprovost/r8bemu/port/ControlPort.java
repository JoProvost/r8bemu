package com.joprovost.r8bemu.port;

public interface ControlPort {
    void controlTo(LogicOutputHandler handler);
    void control();

}
