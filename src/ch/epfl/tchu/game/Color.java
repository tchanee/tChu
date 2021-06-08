package ch.epfl.tchu.game;

import java.util.List;

/**
 * Represents the possible card colors inside the game
 *
 * @author Marvin Chedid (302446)
 * @author Johnny Borkhoche (296169)
 */

public enum Color {
    BLACK,
    VIOLET,
    BLUE,
    GREEN,
    YELLOW,
    ORANGE,
    RED,
    WHITE;

    /**
     * List containing all possible types of the enum Color
     */

    public static final List<Color> ALL = List.of(Color.values());

    /**
     * An int containing the number of values in the enum Color
     */

    public static final int COUNT = ALL.size();

    public static Color of(String s) {
        switch (s) {
            case "BLACK":
                return BLACK;
            case "VIOLET":
                return VIOLET;
            case "BLUE":
                return BLUE;
            case "GREEN":
                return GREEN;
            case "YELLOW":
                return YELLOW;
            case "ORANGE":
                return ORANGE;
            case "RED":
                return RED;
            case "WHITE":
                return WHITE;
            default:
                return null;

        }
    }
}
