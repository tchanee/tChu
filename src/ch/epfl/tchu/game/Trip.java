package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Trip between two stations
 *
 * @author Marvin Chedid (302446)
 * @author Johnny Borkhoche (296169)
 */

final public class Trip {

    final private Station from;
    final private Station to;
    final private int points;

    /**
     * Constructs a trip with a starting station, a destination station and a number of points
     *
     * @param from   Starting station
     * @param to     Arriving station
     * @param points Number of points associated with the trip
     * @throws IllegalArgumentException If the points number is negative
     */

    public Trip(Station from, Station to, int points) {
        Preconditions.checkArgument(points > 0);
        this.from = Objects.requireNonNull(from);
        this.to = Objects.requireNonNull(to);
        this.points = points;
    }

    /**
     * Constructs a list of trips from a list of starting station, a list of destination station and a number of points
     *
     * @param from   A list of starting stations
     * @param to     A list of arriving stations
     * @param points Number of points associated with all the trips
     * @throws IllegalArgumentException If the points number is negative or if the lists are empty
     */

    public static List<Trip> all(List<Station> from, List<Station> to, int points) throws IllegalArgumentException {

        Preconditions.checkArgument(points > 0);
        Preconditions.checkArgument(from.size() != 0);
        Preconditions.checkArgument(to.size() != 0);

        List<Trip> trips = new ArrayList<Trip>();
        from.forEach(temp_from -> to.forEach(temp_to -> trips.add(new Trip(temp_from, temp_to, points))));

        return trips;
    }

    /**
     * Returns the starting station for the ticket
     *
     * @return the starting station
     */

    public Station from() {
        return from;
    }

    /**
     * Returns the destination station for the ticket
     *
     * @return the destination station for the ticket
     */

    public Station to() {
        return to;
    }

    /**
     * Returns the number of points associated with the ticket
     *
     * @return the number of points associated with the ticket
     */

    public int points() {
        return points;
    }

    /**
     * Checks if the stations are connected before returning the number of points
     *
     * @param connectivity it's the connectivity between the stations
     * @return the number of points if they are connected and the inverse of the number of points otherwise
     */

    public int points(StationConnectivity connectivity) {
        return (connectivity.connected(from, to)) ? points() : -points();
    }
}
