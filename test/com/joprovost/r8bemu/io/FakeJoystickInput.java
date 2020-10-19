package com.joprovost.r8bemu.io;

public class FakeJoystickInput implements JoystickInput {
    private double horizontal;
    private double vertical;
    private boolean pressed;
    private boolean release;

    @Override
    public void horizontal(double value) {
        this.horizontal = value;
    }

    @Override
    public void vertical(double value) {
        this.vertical = value;
    }

    @Override
    public void press() {
        this.pressed = true;
    }

    @Override
    public void release() {
        this.release = true;
    }

    public boolean pressed() {
        return pressed;
    }

    public boolean released() {
        return release;
    }

    public double horizontal() {
        return horizontal;
    }

    public double vertical() {
        return vertical;
    }
}
