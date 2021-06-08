package ch.epfl.tchu.game;

import java.util.List;

/**
 * Card in the game
 *
 * @author Marvin Chedid (302446)
 * @author Johnny Borkhoche (296169)
 */

public enum Card {
    BLACK(Color.BLACK),
    VIOLET(Color.VIOLET),
    BLUE(Color.BLUE),
    GREEN(Color.GREEN),
    YELLOW(Color.YELLOW),
    ORANGE(Color.ORANGE),
    RED(Color.RED),
    WHITE(Color.WHITE),
    LOCOMOTIVE(null);

    /**
     * Constructs a card out of the color passed in parameter
     *
     * @param color Color of the card
     */

    private Card(Color color) {
        this.cardColor = color;
    }

    /**
     * List containing all possible types of the enum Card
     */

    public static final List<Card> ALL = List.of(Card.values());

    /**
     * An int containing the number of values in the enum Card
     */

    public static final int COUNT = ALL.size();

    /**
     * A list that contains the possible types of wagons
     */

    public static final List<Card> CARS = List.of(Card.values()).subList(0, Color.COUNT);

    private final Color cardColor;

    /**
     * Returns the card associated to the given color
     *
     * @param color Color of the card
     * @return the card given the value
     */

    public static Card of(Color color) {
        if (color == null) {
            return LOCOMOTIVE;
        } else {
            return Card.valueOf(color.toString());
        }
    }

    /**
     * Returns the color of the card
     *
     * @return the color of the card
     */

    public Color color() {
        return this.cardColor;
    }
}
