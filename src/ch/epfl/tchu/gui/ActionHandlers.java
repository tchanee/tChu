package ch.epfl.tchu.gui;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.Card;
import ch.epfl.tchu.game.Route;
import ch.epfl.tchu.game.Ticket;

/**
 * This interface's only goal is to encompass 5 functional interfaces that each represent an action handler
 *
 * @author Marvin Chedid (302446)
 * @author Johnny Borkhoche (296169)
 */

public interface ActionHandlers {

    /**
     * This action handler handles the game when a ticket is drawn
     */
    public interface DrawTicketsHandler {

        /**
         * This method is called when the player wants to draw tickets
         */
        public abstract void onDrawTickets();

    }

    /**
     * This action handler handles the game when a card is drawn
     */
    public interface DrawCardHandler {

        /**
         * This method is called when the player wants the draw a card
         *
         * @param i the slot of the card that the player wants to draw (-1 for a card from the deck, [0;4] for faceUpCards)
         */
        public abstract void onDrawCard(int i);

    }

    /**
     * This action handler handles the game when a route is being claimed
     */
    public interface ClaimRouteHandler {

        /**
         * This method is called when the player wants to claim a route
         *
         * @param route the roue the player wants to claim
         * @param cards the set of cards that the player uses to claim the route
         */
        public abstract void onClaimRoute(Route route, SortedBag<Card> cards);
    }

    /**
     * This action handler handles the game when tickets have to be chosen
     */
    public interface ChooseTicketsHandler {

        /**
         * This method is called when the player has to choose tickets
         *
         * @param tickets the set of tickets that the player has chosen to keep
         */
        public abstract void onChooseTickets(SortedBag<Ticket> tickets);

    }

    /**
     * This action handler handles the game when cards need to be chosen when doing an action
     */
    public interface ChooseCardsHandler {

        /**
         * This method is called when the player either has to choose initial or additional cards when claiming a route
         *
         * @param cards the cards chosen by the player (can be empty)
         */
        public abstract void onChooseCards(SortedBag<Card> cards);

    }
    public interface ChooseModeHandler {

        /**
         * This method is called when the player either has to choose initial or additional cards when claiming a route
         *
         * @param mode the cards chosen by the player (can be empty)
         */
        public abstract void onChooseMode(String mode);

    }
    public interface BeginHandler {

        /**
         * This method is called when the player either has to choose initial or additional cards when claiming a route
         *
         */
        public abstract void onBegin();

    }

}
