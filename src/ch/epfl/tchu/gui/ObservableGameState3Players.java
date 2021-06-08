package ch.epfl.tchu.gui;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Represents an observable game state for three players that will be used to facilitate View-Model bindings in our MVC
 *
 * @author Marvin Chedid (302446)
 * @author Johnny Borkhoche (296169)
 */
public final class ObservableGameState3Players extends ObservableGameState {

    private final PlayerId playerId;
    private PublicGameState publicGameState;
    private PlayerState playerState;

    //Properties that concern the public player state
    private final IntegerProperty ticketsLeftPercentage = new SimpleIntegerProperty();
    private final IntegerProperty cardsLeftPercentage = new SimpleIntegerProperty();
    private final List<ObjectProperty<Card>> faceUpCards = createFaceUpCards();
    private final Map<Route, ObjectProperty<PlayerId>> routesOwnership = createRoutesOwnership();


    //Properties that concern both players' public states
    private final IntegerProperty p1TotalTicketCount = new SimpleIntegerProperty();
    private final IntegerProperty p2TotalTicketCount = new SimpleIntegerProperty();
    private final IntegerProperty p3TotalTicketCount = new SimpleIntegerProperty();
    private final IntegerProperty p1TotalCardCount = new SimpleIntegerProperty();
    private final IntegerProperty p2TotalCardCount = new SimpleIntegerProperty();
    private final IntegerProperty p3TotalCardCount = new SimpleIntegerProperty();
    private final IntegerProperty p1TotalLocomotiveCount = new SimpleIntegerProperty();
    private final IntegerProperty p2TotalLocomotiveCount = new SimpleIntegerProperty();
    private final IntegerProperty p3TotalLocomotiveCount = new SimpleIntegerProperty();
    private final IntegerProperty p1TotalConstrPoints = new SimpleIntegerProperty();
    private final IntegerProperty p2TotalConstrPoints = new SimpleIntegerProperty();
    private final IntegerProperty p3TotalConstrPoints = new SimpleIntegerProperty();

    //Properties that concern this observable game states owners entire PlayerState
    private final ObservableList<Ticket> playerTickets = new SimpleListProperty<>(FXCollections.observableArrayList());
    private final Map<Card, IntegerProperty> playerCardsCount = playerCardsCount();
    private final Map<Route, BooleanProperty> playerCanClaimRoutes = createPlayerCanClaimRoutes();

    /**
     * ObservableGameState' constructor. Reminder that this class belongs to a single player, so there should be a total of 2 used in the game.
     *
     * @param newPlayerId the playerId to whom this observable game state belongs to
     */
    public ObservableGameState3Players(PlayerId newPlayerId) {
        playerId = newPlayerId;
    }

    /**
     * The only way to modify this class' properties.
     *
     * @param newGameState   the public game state that will be used to update the properties
     * @param newPlayerState the player state to which this game state belongs
     */
    public void setState(PublicGameState newGameState, PlayerState newPlayerState) {

        publicGameState = newGameState;
        playerState = newPlayerState;

        PublicPlayerState player1 = newGameState.playerState(PlayerId.PLAYER_1);
        PublicPlayerState player2 = newGameState.playerState(PlayerId.PLAYER_2);
        PublicPlayerState player3 = newGameState.playerState(PlayerId.PLAYER_3);

        //public player state section
        ticketsLeftPercentage.set((newGameState.ticketsCount() * 100) / Constants.TOTAL_TICKETS_COUNT
        );
        cardsLeftPercentage.set((newGameState.cardState().deckSize() * 100) / Constants.TOTAL_CARDS_COUNT
        );

        for (int slot : Constants.FACE_UP_CARD_SLOTS) {
            Card newCard = newGameState.cardState().faceUpCard(slot);
            faceUpCards.get(slot).set(newCard);
        }

        List<Route> player1Routes = newGameState.playerState(PlayerId.PLAYER_1).routes();
        for (Route route : ChMap.routes()) {
            for (Route playerRoute : player1Routes) {
                if (route.id().equals(playerRoute.id()))
                    routesOwnership.get(route).set(PlayerId.PLAYER_1);
            }
        }

        List<Route> player2Routes = newGameState.playerState(PlayerId.PLAYER_2).routes();
        for (Route route : ChMap.routes()) {
            for (Route playerRoute : player2Routes) {
                if (route.id().equals(playerRoute.id()))
                    routesOwnership.get(route).set(PlayerId.PLAYER_2);
            }
        }

        List<Route> player3Routes = newGameState.playerState(PlayerId.PLAYER_3).routes();
        for (Route route : ChMap.routes()) {
            for (Route playerRoute : player3Routes) {
                if (route.id().equals(playerRoute.id()))
                    routesOwnership.get(route).set(PlayerId.PLAYER_3);
            }
        }


        //both players' public state section
        p1TotalTicketCount.set(player1.ticketCount());
        p2TotalTicketCount.set(player2.ticketCount());
        p3TotalTicketCount.set(player3.ticketCount());

        p1TotalCardCount.set(player1.cardCount());
        p2TotalCardCount.set(player2.cardCount());
        p3TotalCardCount.set(player3.cardCount());

        p1TotalConstrPoints.set(player1.claimPoints());
        p2TotalConstrPoints.set(player2.claimPoints());
        p3TotalConstrPoints.set(player3.claimPoints());

        p1TotalLocomotiveCount.set(newGameState.playerState(PlayerId.PLAYER_1).carCount());
        p2TotalLocomotiveCount.set(newGameState.playerState(PlayerId.PLAYER_2).carCount());
        p3TotalLocomotiveCount.set(newGameState.playerState(PlayerId.PLAYER_3).carCount());

        //the class's owners playerstate' section
        playerTickets.setAll(newPlayerState.tickets().toList());
        for (Card card : Card.ALL) {
            SortedBag<Card> playerCards = newPlayerState.cards();
            int cardCount = playerCards.countOf(card);
            playerCardsCount.get(card).set(cardCount);
        }


        Set<List<Station>> routeStations = new HashSet<>();
        for (Route route : ChMap.routes()) {
            if (routesOwnership.get(route).get() != null)
                routeStations.add(route.stations());
        }

        for (Route route : ChMap.routes()) {
            boolean decider = newGameState.currentPlayerId() == playerId //check if player is current player
                    && routesOwnership.get(route).getValue() == null //check that route doesnt belong to anyone
                    && !routeStations.contains(route.stations()) //check for routes doubles
                    && newPlayerState.canClaimRoute(route); //check that player can claim route
            playerCanClaimRoutes.get(route).set(decider);
        }
    }

    private static List<ObjectProperty<Card>> createFaceUpCards() {
        return Stream.generate(
                (Supplier<SimpleObjectProperty<Card>>) SimpleObjectProperty::new)
                .limit(5)
                .collect(Collectors.toList());
    }

    private static Map<Route, ObjectProperty<PlayerId>> createRoutesOwnership() {

        Map<Route, ObjectProperty<PlayerId>> map = new HashMap<>();
        for (Route route : ChMap.routes()) {
            ObjectProperty<PlayerId> objPropId = new SimpleObjectProperty<PlayerId>();
            map.putIfAbsent(route, objPropId);
        }
        return map;
    }

    private static Map<Card, IntegerProperty> playerCardsCount() {

        Map<Card, IntegerProperty> map = new HashMap<>();
        for (Card card : Card.ALL) {
            IntegerProperty count = new SimpleIntegerProperty();
            map.putIfAbsent(card, count);
        }
        return map;
    }

    private static Map<Route, BooleanProperty> createPlayerCanClaimRoutes() {

        Map<Route, BooleanProperty> map = new HashMap<>();
        for (Route route : ChMap.routes()) {
            BooleanProperty bool = new SimpleBooleanProperty();
            map.putIfAbsent(route, bool);
        }
        return map;
    }

    /**
     * This method returns this observableGameState's player's playerId
     *
     * @return the playerId of the player that owns this observable game state
     */
    public PlayerId getPlayerId() {
        return playerId;
    }

    /**
     * This method returns the property that contains the percentage of tickets left in the Tickets deck
     *
     * @return the property that contains the percentage of tickets left in the Tickets deck
     */
    public ReadOnlyIntegerProperty ticketsLeftPercentageProperty() {
        return ticketsLeftPercentage;
    }

    /**
     * This method returns the percentage of cards left in the Card deck
     *
     * @return the percentage of cards left in the Card deck
     */
    public int getCardsLeftPercentage() {
        return cardsLeftPercentage.get();
    }

    /**
     * This method returns the property that contains the percentage of cards left in the Card deck
     *
     * @return the property that contains the percentage of cards left in the Card deck
     */
    public ReadOnlyIntegerProperty cardsLeftPercentageProperty() {
        return cardsLeftPercentage;
    }

    /**
     * This method returns a list that contains the 5 object properties of the 5 face up cards
     *
     * @return a list that contains the 5 object properties of the 5 face up cards
     */
    public List<ObjectProperty<Card>> getFaceUpCards() {
        return faceUpCards;
    }

    /**
     * This method returns a property that contains the PlayerId that owns the route passed as parameter. The property
     * contains null if no player owns the route
     *
     * @param route the route whose ownership we want to check
     * @return an object property containing the PlayerId of the player owning the route passed as argument, null if the route isn't owned by anyone
     */
    public ReadOnlyObjectProperty<PlayerId> getRouteOwnershipProperty(Route route) {
        return routesOwnership.get(route);
    }

    /**
     * This method returns a property that contains Player_1's total ticket count
     *
     * @return a property that contains Player_1's total ticket count
     */
    public ReadOnlyIntegerProperty p1TotalTicketCountProperty() {
        return p1TotalTicketCount;
    }


    /**
     * This method returns a property that contains Player_2's total ticket count
     *
     * @return a property that contains Player_2's total ticket count
     */
    public ReadOnlyIntegerProperty p2TotalTicketCountProperty() {
        return p2TotalTicketCount;
    }

    /**
     * This method returns a property that contains Player_3's total ticket count
     *
     * @return a property that contains Player_3's total ticket count
     */
    public ReadOnlyIntegerProperty p3TotalTicketCountProperty() {
        return p3TotalTicketCount;
    }

    /**
     * This method returns a property containing Player_1's card count
     *
     * @return a property containing Player_1's card count
     */
    public ReadOnlyIntegerProperty p1TotalCardCountProperty() {
        return p1TotalCardCount;
    }

    /**
     * This method returns a property containing Player_2's card count
     *
     * @return a property containing Player_2's card count
     */
    public ReadOnlyIntegerProperty p2TotalCardCountProperty() {
        return p2TotalCardCount;
    }

    /**
     * This method returns a property containing Player_2's card3count
     *
     * @return a property containing Player_2's card count3*/
    public ReadOnlyIntegerProperty p3TotalCardCountProperty() {
        return p3TotalCardCount;
    }

    /**
     * This method returns a property that contains Player_1's total locomotive count
     *
     * @return a property that contains Player_1's total locomotive count
     */
    public ReadOnlyIntegerProperty p1TotalLocomotiveCountProperty() {
        return p1TotalLocomotiveCount;
    }

    /**
     * This method returns a property that contains Player_2's total locomotive count
     *
     * @return a property that contains Player_2's total locomotive count
     */
    public ReadOnlyIntegerProperty p2TotalLocomotiveCountProperty() {
        return p2TotalLocomotiveCount;
    }

    /**
     * This method returns a property that contains Player_3's total locomotive count
     *
     * @return a property that contains Player_3's total locomotive count
     */
    public ReadOnlyIntegerProperty p3TotalLocomotiveCountProperty() {
        return p2TotalLocomotiveCount;
    }
    /**
     * This method returns a property that contains Player_1's total construction points
     *
     * @return a property that contains Player_1's total construction points
     */
    public ReadOnlyIntegerProperty p1TotalConstrPointsProperty() {
        return p1TotalConstrPoints;
    }

    /**
     * This method returns a property that contains Player_2's total construction points
     *
     * @return a property that contains Player_2's total construction points
     */
    public ReadOnlyIntegerProperty p2TotalConstrPointsProperty() {
        return p2TotalConstrPoints;
    }

    /**
     * This method returns a property that contains Player_3's total construction points
     *
     * @return a property that contains Player_3's total construction points
     */
    public ReadOnlyIntegerProperty p3TotalConstrPointsProperty() {
        return p3TotalConstrPoints;
    }

    /**
     * This method returns an observable list that contains all of this observableGameState's owner's tickets.
     *
     * @return an observable list that contains all of this observableGameState's owner's tickets.
     */
    public ObservableList<Ticket> getPlayerTickets() {
        return playerTickets;
    }

    /**
     * This method returns an integer property that specifies how many of the card passed as parameter does the player
     * who owns this observableGameState have
     *
     * @param card the card whose count we want to get
     * @return an integer property that specifies how many of the card passed as parameter does the player have
     */
    public ReadOnlyIntegerProperty getPlayerCardCount(Card card) {
        return playerCardsCount.get(card);
    }

    /**
     * This method returns a boolean property that is true if the player can claim the route passed as parameter, false
     * otherwise
     *
     * @param route the route that we want to know if the player can claim
     * @return a boolean property that is true if the player can claim the route, false otherwise
     */
    public ReadOnlyBooleanProperty getPlayerCanClaimRouteProperty(Route route) {
        return playerCanClaimRoutes.get(route);
    }

    /**
     * This method returns an object property observing the face up card at the given slot
     *
     * @param slot the slot at which we want the face up card
     * @return an object property observing the face up card at the given slot
     */
    public ReadOnlyObjectProperty<Card> faceUpCard(int slot) {
        return faceUpCards.get(slot);
    }

    /**
     * This method corresponds to PublicGameState's canDrawTickets() method. It returns true if there is atleast 1 ticket left
     *
     * @return true if the player can still draw a ticket
     */
    public boolean canDrawTickets() {
        return publicGameState.canDrawTickets();
    }

    /**
     * This method corresponds to PublicGameState's canDrawCards() method. It returns true if the current card deck's size and the discards size amount to at least 5 cards.
     *
     * @return true if the player can draw cards, false otherwise
     */
    public boolean canDrawCards() {
        return publicGameState.canDrawCards();
    }

    /**
     * This method corresponds to PlayerState's possibleClaimCards() method which returns a list of all possible cards that the player could use to claim the route given as argument, and raises
     * an exception if the player doesn't have enough locomotive cards to claim the route.
     *
     * @param route the route that the player wants to try and claim
     * @return the list of cards that the player could possibly use to claim the route
     */
    public List<SortedBag<Card>> possibleClaimCards(Route route) {
        return playerState.possibleClaimCards(route);
    }



}
