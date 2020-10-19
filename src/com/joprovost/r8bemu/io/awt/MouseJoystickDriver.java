package com.joprovost.r8bemu.io.awt;

import com.joprovost.r8bemu.data.NumericRange;
import com.joprovost.r8bemu.data.discrete.DiscreteOutput;
import com.joprovost.r8bemu.io.JoystickInput;

import javax.swing.event.MouseInputAdapter;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import static com.joprovost.r8bemu.io.JoystickInput.AXIS_RANGE;

public class MouseJoystickDriver extends MouseInputAdapter {
    private final JoystickInput joystick;
    private final DiscreteOutput mouse;
    private final Cursor blankCursor = Toolkit.getDefaultToolkit().createCustomCursor(
            new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB),
            new Point(0, 0), "blank cursor");

    public MouseJoystickDriver(JoystickInput joystick, DiscreteOutput mouse) {
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
        move(e);
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        move(e);
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
            int border = 64;

            Component c = e.getComponent();
            NumericRange xRange = new NumericRange(border, c.getWidth() / 2.0, c.getWidth() - border);
            NumericRange yRange = new NumericRange(border, c.getHeight() / 2.0, c.getHeight() - border);

            joystick.horizontal(AXIS_RANGE.from(e.getX(), xRange));
            joystick.vertical(AXIS_RANGE.from(e.getY(), yRange));
            c.setCursor(blankCursor);
        } else {
            e.getComponent().setCursor(Cursor.getDefaultCursor());
        }
    }
}
