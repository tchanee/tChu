package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Represents the public game state of a game of tChu, namely the number of tickets, the public card state, the current
 * player, the last player (if known) and the public player states.
 *
 * @author Marvin Chedid (302446)
 * @author Johnny Borkhoche (296169)
 */

public class PublicGameState {

    private final int ticketsCount;
    private final PublicCardState cardState;
    private final PlayerId currentPlayerId;
    private final PlayerId lastPlayer;
    private final Map<PlayerId, PublicPlayerState> playerState;


    /**
     * Public game state's constructor, which initializes the public game state with the given parameters
     *
     * @param ticketsCount    the number of tickets
     * @param cardState       the game's initial card state
     * @param currentPlayerId the current player's id
     * @param playerState     the game's current player states
     * @param lastPlayer      the last player of the game (null if not known yet)
     * @throws NullPointerException if the current player id or the card state are null
     */
    public PublicGameState(int ticketsCount, PublicCardState cardState, PlayerId currentPlayerId,
                           Map<PlayerId, PublicPlayerState> playerState, PlayerId lastPlayer) throws NullPointerException {
        Preconditions.checkArgument(ticketsCount >= 0);
        //Preconditions.checkArgument(playerState.size() == 3);
        if (currentPlayerId == null || cardState == null) {
            throw new NullPointerException();
        }
        this.ticketsCount = ticketsCount;
        this.cardState = cardState;
        this.currentPlayerId = currentPlayerId;
        this.lastPlayer = lastPlayer;
        this.playerState = new TreeMap<>(playerState);
    }

    /**
     * Returns the number of tickets in the public game state
     *
     * @return the number of tickets left
     */

    public int ticketsCount() {
        return ticketsCount;
    }

    /**
     * Returns true if there are still tickets that can be drawn, false otherwise
     *
     *
     * @return true if there is at least 1 more ticket that can be drawn
     */

    public boolean canDrawTickets() {
        if (playerState.size() == 2)
            return ticketsCount > 0;
        else
            return ticketsCount > 2;
    }

    /**
     * Returns the public card state of the wagon/locomotive cards, i.e the face up cards and sizes of the deck and the discards
     *
     * @return the public card state of the current game state
     */
    public PublicCardState cardState() {
        return this.cardState;
    }

    /**
     * Returns true if a player can still draw cards, i.e if both the deck and discards have atleast 5 cards combined
     *
     * @return true if a player can still draw cards
     */
    public boolean canDrawCards() {
        return (cardState.deckSize() + cardState.discardsSize()) >= 5;
    }

    /**
     * Returns the current player's id
     *
     * @return the current player's id
     */
    public PlayerId currentPlayerId() {
        return currentPlayerId;
    }

    /**
     * Returns the public player state of the player whose id is passed as parameter, i.e the amount of tickets and cards he has and his claimed routes
     *
     * @param playerId the player whose public state you want to return
     * @return the player's public state
     */
    public PublicPlayerState playerState(PlayerId playerId) {
        return playerState.get(playerId);
    }

    /**
     * Returns the current player's public player state, i.e the amount of tickets and cards he has and his claimed routes
     *
     * @return the current player's public state
     */
    public PublicPlayerState currentPlayerState() {
        return playerState.get(currentPlayerId);
    }

    /**
     * Returns the map of player states
     *
     * @return the map of player states
     */
    public Map<PlayerId, PublicPlayerState> getPlayerState() {
        return playerState;
    }

    /**
     * This method returns a list of all the routes that have been claimed so far by both players
     *
     * @return a list of all the routes claimed so far
     */
    public List<Route> claimedRoutes() {
        List<Route> routesClaimed = new ArrayList<>();
        routesClaimed.addAll(playerState.get(PlayerId.PLAYER_1).routes());
        routesClaimed.addAll(playerState.get(PlayerId.PLAYER_2).routes());
        if (playerState.size() == 3)
            routesClaimed.addAll(playerState.get(PlayerId.PLAYER_3).routes());
        return routesClaimed;
    }

    /**
     * This method returns the last player's id if it is known, or *null* if it is still unknown, i.e the game hasn't reached it's last round yet
     *
     * @return the last player's id if it is known, null otherwise
     */

    public PlayerId lastPlayer() {
        return lastPlayer;
    }
}
