package com.joprovost.r8bemu.io.awt;

import com.joprovost.r8bemu.data.BitOutput;
import com.joprovost.r8bemu.io.Joystick;

import javax.swing.event.MouseInputAdapter;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

public class MouseJoystickDriver extends MouseInputAdapter {
    private final Joystick joystick;
    private final BitOutput mouse;
    private final Cursor blankCursor = Toolkit.getDefaultToolkit().createCustomCursor(
            new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB),
            new Point(0, 0), "blank cursor");

    public MouseJoystickDriver(Joystick joystick, BitOutput mouse) {
        this.joystick = joystick;
        this.mouse = mouse;
    }

    @Override
    public void mousePressed(MouseEvent e) {
        press(e);
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        release(e);
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        release(e);
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        press(e);
    }

    private void press(MouseEvent e) {
        move(e);
        if (mouse.isSet()) joystick.press();
    }

    private void release(MouseEvent e) {
        move(e);
        if (mouse.isSet()) joystick.release();
    }

    private void move(MouseEvent e) {
        if (mouse.isSet()) {
            joystick.horizontal(e.getX() * (Joystick.MAXIMUM - Joystick.MINIMUM) / e.getComponent().getWidth() + Joystick.MINIMUM);
            joystick.vertical(e.getY() * (Joystick.MAXIMUM - Joystick.MINIMUM) / e.getComponent().getHeight() + Joystick.MINIMUM);
            e.getComponent().setCursor(blankCursor);
        } else {
            e.getComponent().setCursor(Cursor.getDefaultCursor());
        }
    }
}
