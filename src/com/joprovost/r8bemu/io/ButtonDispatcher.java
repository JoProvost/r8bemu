package com.joprovost.r8bemu.io;

public class ButtonDispatcher implements Button {

    private Button target;

    public void dispatchTo(Button target) {
        this.target = target;
    }

    @Override
    public void press() {
        if (target != null) target.press();
    }

    @Override
    public void release() {
        if (target != null) target.release();
    }
}
