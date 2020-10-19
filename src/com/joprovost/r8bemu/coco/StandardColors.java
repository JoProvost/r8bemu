package com.joprovost.r8bemu.coco;

import com.joprovost.r8bemu.data.discrete.DiscreteOutput;
import com.joprovost.r8bemu.graphic.Colors;

import java.awt.*;

public class StandardColors {
    public static final Color BLACK = Color.decode("#000000");
    public static final Color LOW_INTENSITY_BLUE = Color.decode("#000055");
    public static final Color LOW_INTENSITY_GREEN = Color.decode("#005500");
    public static final Color LOW_INTENSITY_CYAN = Color.decode("#005555");
    public static final Color LOW_INTENSITY_RED = Color.decode("#550000");
    public static final Color LOW_INTENSITY_MAGENTA = Color.decode("#550055");
    public static final Color LOW_INTENSITY_BROWN = Color.decode("#555500");
    public static final Color LOW_INTENSITY_WHITE = Color.decode("#555555");
    public static final Color MEDIUM_INTENSITY_BLUE = Color.decode("#0000AA");
    public static final Color FULL_INTENSITY_BLUE = Color.decode("#0000FF");
    public static final Color BLUE = FULL_INTENSITY_BLUE;
    public static final Color GREEN_TINT_BLUE = Color.decode("#0055AA");
    public static final Color CYAN_TINT_BLUE = Color.decode("#0055FF");
    public static final Color RED_TINT_BLUE = Color.decode("#5500AA");
    public static final Color MAGENTA_TINT_BLUE = Color.decode("#5500FF");
    public static final Color BROWN_TINT_BLUE = Color.decode("#5555AA");
    public static final Color FADED_BLUE = Color.decode("#5555FF");
    public static final Color MEDIUM_INTENSITY_GREEN = Color.decode("#00AA00");
    public static final Color BLUE_TINT_GREEN = Color.decode("#00AA55");
    public static final Color FULL_INTENSITY_GREEN = Color.decode("#00FF00");
    public static final Color GREEN = FULL_INTENSITY_GREEN;
    public static final Color CYAN_TINT_GREEN = Color.decode("#00FF55");
    public static final Color RED_TINT_GREEN = Color.decode("#55AA00");
    public static final Color MAGENTA_TINT_GREEN = Color.decode("#55AA55");
    public static final Color BROWN_TINT_GREEN = Color.decode("#55FF00");
    public static final Color FADED_GREEN = Color.decode("#55FF55");
    public static final Color MEDIUM_INTENSITY_CYAN = Color.decode("#00AAAA");
    public static final Color BLUE_TINT_CYAN = Color.decode("#55FFFF");
    public static final Color GREEN_TINT_CYAN = Color.decode("#00FFAA");
    public static final Color FULL_INTENSITY_CYAN = Color.decode("#00FFFF");
    public static final Color CYAN = FULL_INTENSITY_CYAN;
    public static final Color RED_TINT_CYAN = Color.decode("#55AAAA");
    public static final Color MAGENTA_TINT_CYAN = Color.decode("#55AAFF");
    public static final Color BROWN_TINT_CYAN = Color.decode("#55FFAA");
    public static final Color FADED_CYAN = Color.decode("#55FFFF");
    public static final Color MEDIUM_INTENSITY_RED = Color.decode("#AA0000");
    public static final Color BLUE_TINT_RED = Color.decode("#AA0055");
    public static final Color LIGHT_ORANGE = Color.decode("#AA5500");
    public static final Color ORANGE = LIGHT_ORANGE;
    public static final Color CYAN_TINT_RED = Color.decode("#AA5555");
    public static final Color FULL_INTENSITY_RED = Color.decode("#FF0000");
    public static final Color RED = FULL_INTENSITY_RED;
    public static final Color MAGENTA_TINT_RED = Color.decode("#FF0055");
    public static final Color BROWN_TINT_RED = Color.decode("#FF5500");
    public static final Color FADED_RED = Color.decode("#FF5555");
    public static final Color MEDIUM_INTENSITY_MAGENTA = Color.decode("#AA00AA");
    public static final Color BLUE_TINT_MAGENTA = Color.decode("#AA00FF");
    public static final Color GREEN_TINT_MAGENTA = Color.decode("#AA55AA");
    public static final Color CYAN_TINT_MAGENTA = Color.decode("#AA55FF");
    public static final Color RED_TINT_MAGENTA = Color.decode("#FF00AA");
    public static final Color FULL_INTENSITY_MAGENTA = Color.decode("#FF00FF");
    public static final Color MAGENTA = FULL_INTENSITY_MAGENTA;
    public static final Color BROWN_TINT_MAGENTA = Color.decode("#FF55AA");
    public static final Color FADED_MAGENTA = Color.decode("#FF55FF");
    public static final Color MEDIUM_INTENSITY_YELLOW = Color.decode("#AAAA00");
    public static final Color BLUE_TINT_YELLOW = Color.decode("#AAAA55");
    public static final Color GREEN_TINT_YELLOW = Color.decode("#AAFF00");
    public static final Color CYAN_TINT_YELLOW = Color.decode("#AAFF55");
    public static final Color RED_TINT_YELLOW = Color.decode("#FFAA00");
    public static final Color MAGENTA_TINT_YELLOW = Color.decode("#FFAA55");
    public static final Color FULL_INTENSITY_YELLOW = Color.decode("#FFFF00");
    public static final Color YELLOW = FULL_INTENSITY_YELLOW;
    public static final Color FADED_YELLOW = Color.decode("#FFFF55");
    public static final Color MEDIUM_INTENSITY_WHITE = Color.decode("#AAAAAA");
    public static final Color LIGHT_BLUE = Color.decode("#AAAAFF");
    public static final Color LIGHT_GREEN = Color.decode("#AAFFAA");
    public static final Color LIGHT_CYAN = Color.decode("#AAFFFF");
    public static final Color LIGHT_RED = Color.decode("#FFAAAA");
    public static final Color LIGHT_MAGENTA = Color.decode("#FFAAFF");
    public static final Color LIGHT_YELLOW = Color.decode("#FFFFAA");
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
        // RGB palette
        // Source : https://exstructus.com/tags/coco/australia-colour-palette/
        Color[] colors = {
                BLACK,
                LOW_INTENSITY_BLUE,
                LOW_INTENSITY_GREEN,
                LOW_INTENSITY_CYAN,
                LOW_INTENSITY_RED,
                LOW_INTENSITY_MAGENTA,
                LOW_INTENSITY_BROWN,
                LOW_INTENSITY_WHITE,
                MEDIUM_INTENSITY_BLUE,
                FULL_INTENSITY_BLUE,
                GREEN_TINT_BLUE,
                CYAN_TINT_BLUE,
                RED_TINT_BLUE,
                MAGENTA_TINT_BLUE,
                BROWN_TINT_BLUE,
                FADED_BLUE,
                MEDIUM_INTENSITY_GREEN,
                BLUE_TINT_GREEN,
                FULL_INTENSITY_GREEN,
                CYAN_TINT_GREEN,
                RED_TINT_GREEN,
                MAGENTA_TINT_GREEN,
                BROWN_TINT_GREEN,
                FADED_GREEN,
                MEDIUM_INTENSITY_CYAN,
                BLUE_TINT_CYAN,
                GREEN_TINT_CYAN,
                FULL_INTENSITY_CYAN,
                RED_TINT_CYAN,
                MAGENTA_TINT_CYAN,
                BROWN_TINT_CYAN,
                FADED_CYAN,
                MEDIUM_INTENSITY_RED,
                BLUE_TINT_RED,
                LIGHT_ORANGE,
                CYAN_TINT_RED,
                FULL_INTENSITY_RED,
                MAGENTA_TINT_RED,
                BROWN_TINT_RED,
                FADED_RED,
                MEDIUM_INTENSITY_MAGENTA,
                BLUE_TINT_MAGENTA,
                GREEN_TINT_MAGENTA,
                CYAN_TINT_MAGENTA,
                RED_TINT_MAGENTA,
                FULL_INTENSITY_MAGENTA,
                BROWN_TINT_MAGENTA,
                FADED_MAGENTA,
                MEDIUM_INTENSITY_YELLOW,
                BLUE_TINT_YELLOW,
                GREEN_TINT_YELLOW,
                CYAN_TINT_YELLOW,
                RED_TINT_YELLOW,
                MAGENTA_TINT_YELLOW,
                FULL_INTENSITY_YELLOW,
                FADED_YELLOW,
                MEDIUM_INTENSITY_WHITE,
                LIGHT_BLUE,
                LIGHT_GREEN,
                LIGHT_CYAN,
                LIGHT_RED,
                LIGHT_MAGENTA,
                LIGHT_YELLOW,
                WHITE,
        };

        return value -> colors[value % colors.length];
    }

    public static Colors legacy() {
        Color[] colors = {
                GREEN,
                YELLOW,
                BLUE,
                RED,
                BUFF,
                CYAN,
                MAGENTA,
                ORANGE,
                BLACK
        };

        return value -> colors[value % colors.length];
    }

    public static Colors select(DiscreteOutput composite) {
        Colors cmp = StandardColors.cmp();
        Colors rgb = StandardColors.rgb();
        return color -> composite.isSet() ? cmp.color(color) : rgb.color(color);
    }
}
