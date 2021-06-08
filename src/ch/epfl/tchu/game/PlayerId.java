package ch.epfl.tchu.game;

import java.util.List;

/**
 * Represents the identity of the player
 *
 * @author Marvin Chedid (302446)
 * @author Johnny Borkhoche (296169)
 */

public enum PlayerId {
    PLAYER_1(0),
    PLAYER_2(1),
    PLAYER_3(2);

    public PlayerId next() {
        switch(id) {
            case 0:
                return PLAYER_2;
            case 1:
                return PLAYER_1;
            case 2:
                return PLAYER_1;
            default:
                return null;
        }
    }
    public PlayerId next3Players() {
        switch(id) {
            case 0:
                return PLAYER_2;
            case 1:
                return PLAYER_3;
            case 2:
                return PLAYER_1;
            default:
                return null;
        }
    }

    private final int id;

    private PlayerId(int id) {
        this.id = id;
    }

    /**
     * List containing all possible types of the enum PlayerId
     */

    public static final List<PlayerId> ALL = List.of(PlayerId.values());

    /**
     * An int containing the number of values in the enum PlayerId
     */

    public static final int COUNT = ALL.size();
}
