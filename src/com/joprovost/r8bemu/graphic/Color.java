package com.joprovost.r8bemu.graphic;

public class Color {
    public final int red;
    public final int green;
    public final int blue;

    public Color(int r, int g, int b) {
        red = r & 0xFF;
        green = g & 0xFF;
        blue = b & 0xFF;
    }

    public static Color decode(String nm) throws NumberFormatException {
        return fromRGB(Integer.decode(nm));
    }

    public static Color fromRGB(int rgb) {
        return new Color((rgb >> 16) & 0xFF, (rgb >> 8) & 0xFF, rgb & 0xFF);
    }

    public int getRGB() {
        return (red << 16) + (green << 8) + blue;
    }
}
