package com.joprovost.r8bemu.coco;

import com.joprovost.r8bemu.data.discrete.DiscreteOutput;
import com.joprovost.r8bemu.graphic.Colors;

import java.awt.*;

import static com.joprovost.r8bemu.data.binary.BinaryOutput.bit;

public class StandardColors {
    public static final Color BLACK = Color.decode("#000000");
    public static final Color BLUE = Color.decode("#0000FF");
    public static final Color GREEN = Color.decode("#00FF00");
    public static final Color CYAN = Color.decode("#00FFFF");
    public static final Color ORANGE = Color.decode("#AA5500");
    public static final Color RED = Color.decode("#FF0000");
    public static final Color MAGENTA = Color.decode("#FF00FF");
    public static final Color YELLOW = Color.decode("#FFFF00");
    public static final Color WHITE = Color.decode("#FFFFFF");
    public static final Color BUFF = WHITE;

    public static Colors cmp() {
        // CMP palette as defined by VCC Emulator
        // Source : https://exstructus.com/tags/coco/australia-colour-palette/
        Color[] colors = {
                Color.decode("#000000"),
                Color.decode("#17882C"),
                Color.decode("#208A23"),
                Color.decode("#3B7C20"),
                Color.decode("#5C631A"),
                Color.decode("#774114"),
                Color.decode("#575617"),
                Color.decode("#8A011F"),
                Color.decode("#7C073E"),
                Color.decode("#63105F"),
                Color.decode("#411679"),
                Color.decode("#1E1888"),
                Color.decode("#012487"),
                Color.decode("#064077"),
                Color.decode("#0F615E"),
                Color.decode("#157B42"),
                Color.decode("#343434"),
                Color.decode("#43CF63"),
                Color.decode("#60CE4B"),
                Color.decode("#82BD3C"),
                Color.decode("#A5A035"),
                Color.decode("#C17C36"),
                Color.decode("#D05946"),
                Color.decode("#CE3B62"),
                Color.decode("#BD2B85"),
                Color.decode("#A02BA7"),
                Color.decode("#7C34C0"),
                Color.decode("#5849CD"),
                Color.decode("#3B66CA"),
                Color.decode("#2B88B8"),
                Color.decode("#2AAA9C"),
                Color.decode("#34C47B"),
                Color.decode("#777777"),
                Color.decode("#87FE9C"),
                Color.decode("#A8FD82"),
                Color.decode("#CEFD74"),
                Color.decode("#F2E06F"),
                Color.decode("#FEBA75"),
                Color.decode("#FE958B"),
                Color.decode("#FD78AB"),
                Color.decode("#FD69D0"),
                Color.decode("#E168F2"),
                Color.decode("#BA73FA"),
                Color.decode("#958CFB"),
                Color.decode("#78AFFB"),
                Color.decode("#68D4FC"),
                Color.decode("#68F7DD"),
                Color.decode("#74FEB8"),
                Color.decode("#FFFFFF"),
                Color.decode("#D0FED8"),
                Color.decode("#F4FEBD"),
                Color.decode("#FFFEAE"),
                Color.decode("#FFFEAD"),
                Color.decode("#FFFBBC"),
                Color.decode("#FED6D5"),
                Color.decode("#FEBAF7"),
                Color.decode("#FEAAFC"),
                Color.decode("#FEA9FC"),
                Color.decode("#FBB9FC"),
                Color.decode("#D6D6FD"),
                Color.decode("#BAFAFE"),
                Color.decode("#AAFFFF"),
                Color.decode("#A9FFFF"),
                Color.decode("#FFFFFF"),
        };

        return value -> colors[value % colors.length];
    }

    public static Colors rgb() {
        return cache(index -> new Color(
                channel(index, 2),
                channel(index, 1),
                channel(index, 0)
        ));
    }

    public static Colors legacy() {
        Color[] colors = {GREEN, YELLOW, BLUE, RED, BUFF, CYAN, MAGENTA, ORANGE, BLACK};
        return value -> colors[value % colors.length];
    }

    public static Colors select(DiscreteOutput composite) {
        Colors cmp = StandardColors.cmp();
        Colors rgb = StandardColors.rgb();
        return color -> composite.isSet() ? cmp.color(color) : rgb.color(color);
    }

    public static Colors cache(Colors colors) {
        Color[] cache = new Color[64];
        for (int i = 0; i < cache.length; i++)
            cache[i] = colors.color(i);
        return value -> cache[value];
    }

    private static int channel(int value, int pos) {
        return 85 * ((bit(value, pos) ? 1 : 0) + (bit(value, pos + 3) ? 2 : 0));
    }
}
