package com.joprovost.r8bemu.io.awt;

import javax.swing.*;
import java.awt.*;

public class ResourceImage {
    public static Image of(String name) {
        return new ImageIcon(UserInterface.class.getResource(name)).getImage();
    }
}
