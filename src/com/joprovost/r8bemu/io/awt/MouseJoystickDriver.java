package com.joprovost.r8bemu.io.awt;

import com.joprovost.r8bemu.data.BitOutput;
import com.joprovost.r8bemu.io.Joystick;

import javax.swing.event.MouseInputAdapter;
import java.awt.event.MouseEvent;

public class MouseJoystickDriver extends MouseInputAdapter {
    private final Joystick joystick;
    private final BitOutput mouse;

    public MouseJoystickDriver(Joystick joystick, BitOutput mouse) {
        this.joystick = joystick;
        this.mouse = mouse;
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (mouse.isClear()) return;
        move(e);
        joystick.press();
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (mouse.isClear()) return;
        move(e);
        joystick.release();
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        if (mouse.isClear()) return;
        move(e);
        joystick.release();
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (mouse.isClear()) return;
        move(e);
        joystick.press();
    }

    private void move(MouseEvent e) {
        joystick.horizontal(e.getX() * (Joystick.MAXIMUM - Joystick.MINIMUM) / e.getComponent().getWidth() + Joystick.MINIMUM);
        joystick.vertical(e.getY() * (Joystick.MAXIMUM - Joystick.MINIMUM) / e.getComponent().getHeight() + Joystick.MINIMUM);
    }
}
