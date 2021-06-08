package ch.epfl.tchu.gui;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import static javafx.application.Platform.runLater;
import static ch.epfl.tchu.game.Player.TurnKind.*;
import static ch.epfl.tchu.gui.ActionHandlers.*;
import static ch.epfl.tchu.SortedBag.*;

/**
 * This class adapts the previous GraphicalPlayer class as an object of type Player.
 *
 * @author Marvin Chedid (302446)
 * @author Johnny Borkhoche (296169)
 */
public final class GraphicalPlayerAdapter implements Player {

    private GraphicalPlayer gp;
    private final BlockingQueue<SortedBag<Ticket>> tickets = new ArrayBlockingQueue<>(1);
    private final BlockingQueue<SortedBag<Card>> cards = new ArrayBlockingQueue<>(1);
    private final BlockingQueue<Integer> card_slot = new ArrayBlockingQueue<>(1);
    private final BlockingQueue<TurnKind> turn = new ArrayBlockingQueue<>(1);
    private final BlockingQueue<Route> routes = new ArrayBlockingQueue<>(1);
    private final Builder<Card> testingCard = new Builder<>();
    private final Builder<Ticket> testingTicket = new Builder<>();

    /**
     * This method constructs, on the JavaFX thread, an instance of a graphical player that will be adapted
     *
     * @param thisPlayer the player id of the player to whom the instance created belongs
     * @param players    a map of the players' names
     */
    public void initPlayers(PlayerId thisPlayer, Map<PlayerId, String> players) {
        runLater(() -> {
            this.gp = new GraphicalPlayer(thisPlayer, players);});
    }

    /**
     * This method simply calls the player's method (with the same name as this one) but on the JavaFX thread
     *
     * @param msg the message to receive
     */
    public void receiveInfo(String msg) {
        runLater(() -> gp.receiveInfo(msg));
    }

    /**
     * This method simply calls the player's method (with the same name as this one) but on the JavaFX thread
     *
     * @param newGameState   the new game state to be updated
     * @param newPlayerState the new player state to be updated
     */
    public void updateState(PublicGameState newGameState, PlayerState newPlayerState) {
        runLater(() -> gp.setState(newGameState, newPlayerState));
    }

    /**
     * This method is called on the JavaFX thread; it calls the players method that lets him choose tickets
     *
     * @param ticketsChoice the set of tickets that the player will be choosing from
     */
    public void setInitialTicketChoice(SortedBag<Ticket> ticketsChoice) {
        ChooseTicketsHandler chooseTicketsHandler = (s) -> {
            try {
                tickets.put(s);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        };
        runLater(() -> gp.chooseTickets(ticketsChoice, chooseTicketsHandler));

    }

    /**
     * This method blocks the game thread until the queue used by the method `setInitialTicketChoice` contains a value
     *
     * @return the set of tickets chosen
     */
    public SortedBag<Ticket> chooseInitialTickets() {
        try {
            return tickets.take();
        } catch (InterruptedException e) {
            e.printStackTrace();
            return testingTicket.build();
        }
    }


    /**
     * This method is called on the JavaFX thread. It calls the player's `startTurn` method, providing it with
     * the needed handlers
     *
     * @return the type of turn that the player will be playing
     */
    public TurnKind nextTurn() {
        DrawTicketsHandler ticketsHandler = () -> {
            try {
                turn.put(DRAW_TICKETS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        };
        DrawCardHandler cardsHandler = (slot) -> {
            try {
                turn.put(DRAW_CARDS);
                card_slot.put(slot);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        };
        ClaimRouteHandler claimRouteHandler = (route, cardsUsed) -> {
            try {
                turn.put(CLAIM_ROUTE);
                routes.put(route);
                cards.put(cardsUsed);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        };
        runLater(() -> gp.startTurn(cardsHandler, ticketsHandler, claimRouteHandler));
        try {
            return turn.take();
        } catch (InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * this method simply chains both methods `setInitialTicketChoice` and `chooseInitialTickets` from the same class
     *
     * @param options a sorted bag of the tickets that the player may choose from
     * @return a set of the initially chosen tickets by the player
     */
    public SortedBag<Ticket> chooseTickets(SortedBag<Ticket> options) {
        setInitialTicketChoice(options);
        return chooseInitialTickets();
    }

    /**
     * This method tests (without blocking the game) if the queue containing the card slots contains a value. If it does,
     * this means that drawSlot is being called for the first time this turn and said card in the slot is returned.
     * If it doesn't, it means that this method is being called so that the player draws a second card, and so the player's
     * `drawCard` method is called on the JavaFX thread
     *
     * @return the slot of the card drawn by the player
     */
    public int drawSlot() {
        DrawCardHandler cardsHandler = (slot) -> {
            try {
                turn.put(DRAW_CARDS);
                card_slot.put(slot);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        };

        if (card_slot.isEmpty()) {
            runLater(() -> gp.drawCard(cardsHandler));
            try {
                turn.take();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        try {
            return card_slot.take();
        } catch (InterruptedException e) {
            e.printStackTrace();
            return -1;
        }
    }

    /**
     * This method extracts and returns the first element in the queue containing the routes
     *
     * @return the first route in the queue containing the routes
     */
    public Route claimedRoute() {
        try {
            return routes.take();
        } catch (InterruptedException e) {
            e.printStackTrace();
            return new Route("", null, null, 0, null, null);
        }
    }

    /**
     * This method extracts and returns the first set of cards contained in the queue containing the sets of cards
     *
     * @return the set of cards in the corresponding queue
     */
    public SortedBag<Card> initialClaimCards() {
        try {
            return cards.take();
        } catch (InterruptedException e) {
            e.printStackTrace();
            return testingCard.build();
        }
    }

    /**
     * This method is called on the JavaFX thread. It calls the player's method with the same name as this one, then
     * blocks the game until a value is placed in the queue containing the sets of cards
     *
     * @param options the list of all possible sets of additional cards that the player may choose to claim the tunnel
     * @return the set that is placed in the queue containing the sets of cards
     */
    public SortedBag<Card> chooseAdditionalCards(List<SortedBag<Card>> options) {
        ChooseCardsHandler chooseCardsHandler = (cardsChosen) -> {
            try {
                cards.put(Objects.requireNonNullElseGet(cardsChosen, SortedBag::of));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        };
        try {
            runLater(() -> gp.chooseAdditionalCards(options, chooseCardsHandler));
            return cards.take();
        } catch (InterruptedException e) {
            e.printStackTrace();
            return testingCard.build();
        }
    }
}
