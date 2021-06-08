package ch.epfl.tchu.game;

import ch.epfl.tchu.SortedBag;

import java.util.List;
import java.util.Map;

/**
 * This interface represents a player in the game tChu
 *
 * @author Marvin Chedid (302446)
 * @author Johnny Borkhoche (296169)
 */

public interface Player {

    /**
     * This enum represents the three possible actions that the player may undergo during his turn: drawing tickets, drawing cards (from the deck or the face up cards) and claiming a route (overground or underground).
     */
    public enum TurnKind {

        DRAW_TICKETS(),
        DRAW_CARDS(),
        CLAIM_ROUTE();


        private TurnKind() {
        }

        public static final List<TurnKind> ALL = List.of(TurnKind.values());
    }

    /**
     * This method is to be called at the start of a game to communicate to the player his own id as well as the names of both players
     *
     * @param ownId       the personal id of the player
     * @param playerNames the player names of both players
     */
    void initPlayers(PlayerId ownId, Map<PlayerId, String> playerNames);

    /**
     * This method is called whenever a piece of info must be communicated to the player throughout the game
     *
     * @param info the information to be forwarded to the player
     */
    void receiveInfo(String info);

    /**
     * This method is called every time the game state changes; the player's local public game state will be updated
     *
     * @param newState the new public game state
     * @param ownState the player's new player state
     */
    void updateState(PublicGameState newState, PlayerState ownState);

    /**
     * This method is called at the start of the game so that the player may know the 5 initial tickets that he will be choosing from
     *
     * @param tickets a sorted bag of the 5 tickets that the player may choose from at the start of the game
     */
    void setInitialTicketChoice(SortedBag<Ticket> tickets);

    /**
     * This method is called at the start of the game so that the player may choose his initial tickets
     *
     * @return a sorted bag of the tickets he chose to keep
     */
    SortedBag<Ticket> chooseInitialTickets();

    /**
     * This method is called at the start of the player's turn so as to know what kind of turn will he be undergoing
     *
     * @return the type of the turn for the player
     */
    TurnKind nextTurn();

    /**
     * This method is called when the player decides to draw additional tickets during the game
     *
     * @param options a sorted bag of the tickets that the player may choose from
     * @return a sorted bag of the tickets he chose to keep
     */
    SortedBag<Ticket> chooseTickets(SortedBag<Ticket> options);

    /**
     * This method is called when the player chooses to draw wagon/locomotive cards: it will return an index which will represent whether the player wants to draw a card from the deck of cards or from the face up cards.
     *
     * @return -1 if the player is drawing the card from the top of the deck of cards or {0, 1, 2, 3, 4} which corresponds to the index of the face up card that the player wishes to draw
     */
    int drawSlot();

    /**
     * This method is called when the player decides to try and claim a route so as to know which route it is
     *
     * @return the route that the player is trying to claim
     */
    Route claimedRoute();

    /**
     * This method is called when the player tries to claim a route so as to know what are the initial cards that the player uses to try and do so
     *
     * @return a sorted bag of the cards that the player initially used to try and claim the route
     */
    SortedBag<Card> initialClaimCards();

    /**
     * This method is called when the player tries to claim a tunnel and additional cards are necessary for a successful claim. It allows the player to choose from a list of sets of additional cards that may be used to claim the tunnel.
     *
     * @param options the list of all possible sets of additional cards that the player may choose to claim the tunnel
     * @return a sorted bag of the additional cards that the player chose to use - may be empty if player chooses none
     */
    SortedBag<Card> chooseAdditionalCards(List<SortedBag<Card>> options);

}
