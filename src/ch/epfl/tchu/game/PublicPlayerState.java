package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;

import java.util.List;

/**
 * Represents the public part of a player's state, namely the routes he has claimed, the number of construction points
 * he has and the number of wagon cards he still has
 *
 * @author Marvin Chedid (302446)
 * @author Johnny Borkhoche (296169)
 */

public class PublicPlayerState {

    private final int ticketCount;
    private final int cardCount;
    private final List<Route> routes;

    /**
     * Public constructor of public player state
     *
     * @param ticketCount the number of tickets the player has
     * @param cardCount   the number of cards the player has
     * @param routes      the list of all routes that the player has claimed
     */
    public PublicPlayerState(int ticketCount, int cardCount, List<Route> routes) {
        Preconditions.checkArgument(ticketCount >= 0 && cardCount >= 0);
        this.ticketCount = ticketCount;
        this.cardCount = cardCount;
        this.routes = List.copyOf(routes);
    }

    /**
     * Returns the number of tickets that the player has
     *
     * @return the number of tickets that the player currently has
     */
    public int ticketCount() {
        return ticketCount;
    }

    /**
     * Returns the number of cards that the player has
     *
     * @return the number of cards that the player currently has
     */
    public int cardCount() {
        return cardCount;
    }

    /**
     * Returns a list of all the routes that the player has
     *
     * @return a list of all the routes that the player currently has
     */
    public List<Route> routes() {
        return routes;
    }

    /**
     * Returns the number of construction (claim) points that the player has obtained
     *
     * @return number of claim points obtained by the player
     */
    public int claimPoints() {
        int points = 0;
        for (Route route : routes)
            points += route.claimPoints();

        return points;
    }

    /**
     * Returns the number of wagon/locomotive cards that the player still has
     *
     * @return the number of locomotive cards still possessed by the player
     */
    public int carCount() {
        int routesLength = 0;

        for (Route route : routes())
            routesLength += route.length();

        return Constants.INITIAL_CAR_COUNT - routesLength;
    }
}
