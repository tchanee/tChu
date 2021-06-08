package ch.epfl.tchu.game;

/**
 * The interface to connect two stations
 *
 * @author Marvin Chedid (302446)
 * @author Johnny Borkhoche (296169)
 */

public interface StationConnectivity {

    /**
     * Returns true if the two stations are connected, and false otherwise
     *
     * @param station1 First station
     * @param station2 Second station
     * @return true if stations are connected, false otherwise
     */

    public abstract boolean connected(Station station1, Station station2);
}

