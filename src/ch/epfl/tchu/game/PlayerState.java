package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * Represents the entirety of a player's state, namely his tickets, card, routes, the number of wagons/locomotives he has,
 * the number of construction points he's gotten and the number of points that his tickets bring him.
 *
 * @author Marvin Chedid (302446)
 * @author Johnny Borkhoche (296169)
 */

public final class PlayerState extends PublicPlayerState {

    final SortedBag<Ticket> tickets;
    final SortedBag<Card> cards;
    final List<Route> routes;

    //Gets instances of the Ticket Builders
    final Supplier<SortedBag.Builder<Ticket>> ticketSupplier = SortedBag.Builder::new;
    //Gets instances of Card Builders
    final Supplier<SortedBag.Builder<Card>> cardSupplier = SortedBag.Builder::new;


    /**
     * The class's constructor. Takes as parameter the tickets, card and routes that belong to the player and creates
     * a player state
     *
     * @param tickets the player's tickets
     * @param cards   the player's cards
     * @param routes  the routes the player owns
     */
    public PlayerState(SortedBag<Ticket> tickets, SortedBag<Card> cards, List<Route> routes) {
        super(tickets.size(), cards.size(), routes);
        this.tickets = ticketSupplier.get()
                .add(tickets)
                .build();
        this.cards = cardSupplier.get()
                .add(cards)
                .build();
        this.routes = new ArrayList<>();
        this.routes.addAll(routes);


    }

    /**
     * Static construction method for the PlayerState class. It creates a player's initial state, which only possesses
     * 4 cards and no tickets or routes/roads.
     *
     * @param initialCards the 4 cards that the player initially has
     * @return the player's initial state at the start of the game
     */
    public static PlayerState initial(SortedBag<Card> initialCards) {
        Preconditions.checkArgument(initialCards.size() == Constants.INITIAL_CARDS_COUNT);
        List<Route> tempRoutes = new ArrayList<>();
        SortedBag.Builder<Ticket> tempTicketBuilder = new SortedBag.Builder<>();
        SortedBag.Builder<Card> cardBuilder = new SortedBag.Builder<>();
        cardBuilder.add(initialCards);
        return new PlayerState(tempTicketBuilder.build(), cardBuilder.build(), tempRoutes);
    }

    /**
     * Returns the player's tickets
     *
     * @return returns the player's tickets.
     */
    public SortedBag<Ticket> tickets() {
        return ticketSupplier.get()
                .add(tickets)
                .build();
    }

    /**
     * This method adds new tickets to the player's tickets, and returns a new state with the added tickets.
     *
     * @param newTickets the tickets to add to the player's state
     * @return a new player state with the added tickets
     */

    public PlayerState withAddedTickets(SortedBag<Ticket> newTickets) {
        SortedBag<Ticket> tempTickets = ticketSupplier.get()
                .add(tickets)
                .add(newTickets)
                .build();
        return new PlayerState(tempTickets, cards, routes);
    }

    /**
     * This method returns all wagon/locomotive cards of the player
     *
     * @return a SortedBag of the player's cards
     */

    public SortedBag<Card> cards() {
        return cardSupplier.get()
                .add(cards)
                .build();
    }

    /**
     * This method adds a new card to the player's cards, and returns a new state with the added card.
     *
     * @param card the card to be given/added to the player
     * @return a player state similar to the previous one, but with the added card as well
     */
    public PlayerState withAddedCard(Card card) {
        SortedBag<Card> cardsTemp = cardSupplier.get()
                .add(cards)
                .add(card)
                .build();
        return new PlayerState(tickets, cardsTemp, routes);
    }


    /**
     * Returns true if the player can claim the route provided as parameter, false otherwise
     *
     * @param route the route that the player may want to try and claim
     * @return true if the route can be claimed by the player, false otherwise
     */

    public boolean canClaimRoute(Route route) {
        List<SortedBag<Card>> possibleCards = route.possibleClaimCards();
        for (SortedBag<Card> possibleCard : possibleCards)
            if (cards.contains(possibleCard) && this.carCount() >= route.length())
                return true;

        return false;
    }

    /**
     * Returns a list of all possible cards that the player could use to claim the route given as argument, and raises
     * an exception if the player doesn't have enough locomotive cards to claim the route.
     *
     * @param route the route that the player wants to try and claim
     * @return the list of cards that the player could possibly use to claim the route
     */

    public List<SortedBag<Card>> possibleClaimCards(Route route) {
        Preconditions.checkArgument(super.carCount() >= route.length());

        List<SortedBag<Card>> possibleCards = route.possibleClaimCards();
        List<SortedBag<Card>> bag = new ArrayList<SortedBag<Card>>();

        for (SortedBag<Card> possibleCard : possibleCards)
            if (cards.contains(possibleCard))
                bag.add(possibleCard);

        return bag;
    }

    /**
     * Returns a list of all sets of cards that the player could use to claim a tunnel, sorted ascendingly by the number
     * of locomotive cards
     *
     * @param additionalCardsCount the cards that the player is forced to play in addition to the initial and drawn ones
     * @param initialCards         the initial cards that the player used
     * @param drawnCards           the three cards that the player picked from the deck ("pioche")
     * @return returns the list of all sets of cards that the player could/might use to claim a tunnel
     */

    public List<SortedBag<Card>> possibleAdditionalCards(int additionalCardsCount, SortedBag<Card> initialCards,
                                                         SortedBag<Card> drawnCards) {

        Preconditions.checkArgument(additionalCardsCount >= 1 && additionalCardsCount <= Constants.ADDITIONAL_TUNNEL_CARDS);
        Preconditions.checkArgument(initialCards.toSet().size() <= 2);
        Preconditions.checkArgument(initialCards.size() > 0);
        Preconditions.checkArgument(drawnCards.size() == Constants.ADDITIONAL_TUNNEL_CARDS);

        Card tempCard = null;

        for (Card card : initialCards.toList()) {
            if (tempCard == null) {
                if (card != Card.LOCOMOTIVE)
                    tempCard = card;
            } else {
                if (card != Card.LOCOMOTIVE && card != tempCard)
                    Preconditions.checkArgument(false);
            }
        }

        List<Card> claimedCards = initialCards.toList();
        List<SortedBag<Card>> bag = new ArrayList<SortedBag<Card>>();

        int wagonNeeded = 0;
        int wagonInitialCards = 0;
        int wagonAvailable = 0;

        int locNeeded = 0;
        int locInitialCards = 0;
        int locAvailable = 0;

        Card card = null;


        for (Card claimedCard : claimedCards)
            if (claimedCard != Card.LOCOMOTIVE)
                card = claimedCard;

        for (Card cardDrawn : drawnCards) {
            wagonNeeded = ((cardDrawn != Card.LOCOMOTIVE) && (cardDrawn == card)) ? wagonNeeded + 1 : wagonNeeded;
            locNeeded = (cardDrawn == Card.LOCOMOTIVE) ? locNeeded + 1 : locNeeded;
        }
        for (Card initialCard : initialCards) {
            wagonInitialCards = ((initialCard != Card.LOCOMOTIVE) && (initialCard == card)) ? wagonInitialCards + 1 : wagonInitialCards;
            locInitialCards = (initialCard == Card.LOCOMOTIVE) ? locInitialCards + 1 : locInitialCards;
        }
        for (Card availableCard : cards) {
            wagonAvailable = ((availableCard != Card.LOCOMOTIVE) && (availableCard == card)) ? wagonAvailable + 1 : wagonAvailable;
            locAvailable = ((availableCard == Card.LOCOMOTIVE)) ? locAvailable + 1 : locAvailable;
        }

        SortedBag.Builder<Card> initialCardsBuilder = new SortedBag.Builder<>();
        initialCardsBuilder.add(initialCards);

        if (wagonNeeded == 0) {
            if (locNeeded > 0)
                if (locAvailable - locInitialCards >= locNeeded) {
                    SortedBag<Card> otherCardsTemp = cardSupplier.get()
                            .add(additionalCardsCount, Card.LOCOMOTIVE)
                            .build();
                    bag.add(otherCardsTemp);
                }
        } else {
            if ((locAvailable - locInitialCards >= locNeeded) && (wagonAvailable - wagonInitialCards >= wagonNeeded)) {
                for (int i = 0; i <= additionalCardsCount; ++i) {
                    SortedBag<Card> tempCards = cardSupplier.get()
                            .add(additionalCardsCount - i, card)
                            .add(i, Card.LOCOMOTIVE)
                            .build();
                    if (cards.contains(tempCards))
                        bag.add(tempCards);

                }
            } else if ((locAvailable - locInitialCards >= locNeeded)) {
                SortedBag<Card> tempCards = cardSupplier.get()
                        .add(additionalCardsCount, Card.LOCOMOTIVE)
                        .build();
                if (cards.contains(tempCards))
                    bag.add(tempCards);

            } else if ((wagonAvailable - wagonInitialCards >= wagonNeeded)) {
                SortedBag<Card> tempCards = cardSupplier.get()
                        .add(additionalCardsCount, card)
                        .build();
                if (cards.contains(tempCards))
                    bag.add(tempCards);

            }
        }
        return bag;
    }

    /**
     * Returns a player state similar to **this** but where we add the route that the player claimed to the list of his
     * claimed routes, all the while removing the claimCards he used to claim the route from his list of cards
     *
     * @param route      the route that the player has claimed
     * @param claimCards the cards that the player used to claim the route and that should be removed from his list of cards
     * @return the player's new player state, with the changes taken into consideration
     */

    public PlayerState withClaimedRoute(Route route, SortedBag<Card> claimCards) {
        List<Route> newRoutes = new ArrayList<>(List.copyOf(routes));
        newRoutes.add(route);
        return new PlayerState(tickets, cards.difference(claimCards), newRoutes);
    }

    /**
     * Returns the total number of points that the player has due to his tickets. The amount returned could be negative
     * if the player couldn't complete some tickets.
     *
     * @return the amount of points attributed to the player due to his tickets
     */

    public int ticketPoints() {

        int points = 0;

        StationPartition.Builder stationConnectivityBuilder = new StationPartition.Builder(51);
        for (Route route : routes)
            stationConnectivityBuilder.connect(route.station1(), route.station2());

        StationPartition testConnectivity = stationConnectivityBuilder.build();
        for (Ticket ticket : tickets)
            points += ticket.points(testConnectivity);

        return points;
    }

    /**
     * Returns the final amount of points that the player has gotten, namely the ticket points (obtained through
     * his tickets) and the claim points (his construction points basically)
     *
     * @return the final amount of points that the player has gotten
     */

    public int finalPoints() {
        return claimPoints() + ticketPoints();
    }
}
