package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;

import java.util.Objects;

/**
 * A station in the game
 *
 * @author Marvin Chedid (302446)
 * @author Johnny Borkhoche (296169)
 */

public final class Station {

    private final int id;
    private final String name;

    /**
     * Constructs the station with an ID between 0 and 50 and a name
     *
     * @param id   ID of the station
     * @param name Name of the station
     * @throws IllegalArgumentException If the ID is negative
     */

    public Station(int id, String name) throws IllegalArgumentException {
        Preconditions.checkArgument(id >= 0);
        this.id = id;
        this.name = name;
    }

    /**
     * Returns the ID of the station
     *
     * @return id
     */

    public int id() {
        return id;
    }

    /**
     * Returns the name of the station
     *
     * @return name
     */

    public String name() {
        return name;
    }

    /**
     * Overrides the method toString to return the name of the station
     *
     * @return name
     */

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final Station station = (Station) o;
        return id == station.id && name.equals(station.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }
}
