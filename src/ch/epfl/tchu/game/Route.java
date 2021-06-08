package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * Represents a route that connects two neighboring cities
 *
 * @author Marvin Chedid (302446)
 * @author Johnny Borkhoche (296169)
 */

public final class Route {

    private final String id;
    private final Station station1;
    private final Station station2;
    private final int length;
    private final Level level;
    private final Color color;

    //Gets instances of Card Builders
    final Supplier<SortedBag.Builder<Card>> cardSupplier = SortedBag.Builder::new;

    public enum Level {

        OVERGROUND(0),
        UNDERGROUND(1);

        private Level(int enumLevel) {
            this.enumLevel = enumLevel;
        }

        public int numberedLevel() {
            return enumLevel;
        }

        private final int enumLevel;
    }

    /**
     * The public constructor of the Route class
     *
     * @param id       the route's id
     * @param station1 the origin station
     * @param station2 the destination station
     * @param length   the length of the route
     * @param level    overground or tunnel
     * @param color    the color of the route
     * @throws IllegalArgumentException in case the parameters do not pass certain conditions
     * @throws NullPointerException     in case some of the parameters are passed as null
     */
    public Route(String id, Station station1, Station station2, int length, Level level, Color color)
            throws IllegalArgumentException, NullPointerException {
        Preconditions.checkArgument(!(station1.equals(station2)));
        Preconditions.checkArgument(length > 0);
        Preconditions.checkArgument(length < 7);

        this.id = Objects.requireNonNull(id);
        this.station1 = Objects.requireNonNull(station1);
        this.station2 = Objects.requireNonNull(station2);
        this.level = Objects.requireNonNull(level);
        this.length = length;
        this.color = color;
    }

    /**
     * Returns the route's id
     *
     * @return the id of the route
     */
    public String id() {
        return id;
    }

    /**
     * Returns the station of origin for this route
     *
     * @return the origin station of the route
     */
    public Station station1() {
        return station1;
    }

    /**
     * Returns the destination station for this route
     *
     * @return the destination station for this route
     */
    public Station station2() {
        return station2;
    }

    /**
     * Returns the length of this route
     *
     * @return the length of the route
     */
    public int length() {
        return length;
    }

    /**
     * Returns the level on which the route is on
     *
     * @return the level of this route
     */

    public Level level() {
        return level;
    }

    /**
     * Returns the color of this route, which can be null if it is neutral
     *
     * @return the color of this route, if neutral it returns null instead
     */
    public Color color() {
        return color;
    }

    /**
     * Returns a List of two elements, respectively the origin city and the destination city
     *
     * @return a List of the origin city at index 0, and destination city at index 1
     */
    public List<Station> stations() {
        return List.of(station1, station2);
    }

    /**
     * Returns the station that is on the other end of the route of the station provided as a parameter
     *
     * @param station station which we know and who's opposite (on the route) we which to know
     * @return Returns the name of the opposite station of the provided station as parameter
     * @throws IllegalArgumentException If the station provided as argument is neither the origin nor destination city of the route
     */

    public Station stationOpposite(Station station) throws IllegalArgumentException {
        Preconditions.checkArgument(station != null);
        if (station.equals(station1)) {
            return station2;
        } else {
            Preconditions.checkArgument(station.equals(station2));
            return station1;
        }
    }

    /**
     * Returns a List of all possible cards that could be used by the player to claim a route, sorted in
     * ascending order by number of Locomotive cards and then by color
     *
     * @return a list of all possible cards that the player might use to claim a specific route
     */
    public List<SortedBag<Card>> possibleClaimCards() {
        List<SortedBag<Card>> bag = new ArrayList<SortedBag<Card>>();
        if (this.level.numberedLevel() == 0) {
            if (this.color == null) {
                for (Color color : Color.values())
                    if (!(Card.of(color) == Card.LOCOMOTIVE)) {
                        SortedBag<Card> tempCards = cardSupplier.get()
                                .add(length, Card.of(color))
                                .build();
                        bag.add(tempCards);
                    }
            } else {
                SortedBag<Card> tempCards = cardSupplier.get()
                        .add(length, Card.of(color))
                        .build();
                bag.add(tempCards);
            }
        } else {
            if (this.color == null) {
                for (int i = 0; i < length; ++i) {
                    for (Card card : Card.values())
                        if (!(card == Card.LOCOMOTIVE)) {
                            SortedBag<Card> tempCards = cardSupplier.get()
                                    .add(length - i, card)
                                    .add(i, Card.LOCOMOTIVE)
                                    .build();
                            bag.add(tempCards);
                        }
                }
                SortedBag<Card> tempCards = cardSupplier.get()
                        .add(length, Card.LOCOMOTIVE)
                        .build();
                bag.add(tempCards);
            } else {
                for (int i = 0; i <= length; ++i) {
                    SortedBag<Card> tempCards = cardSupplier.get()
                            .add(length - i, Card.of(color))
                            .add(i, Card.LOCOMOTIVE)
                            .build();
                    bag.add(tempCards);
                }
            }
        }
        return bag;
    }

    /**
     * This methods calculates the number of additional cards to be used. First, it determines the number of cards
     * in the drawn cards that match the claim cards in terms of color. The number of cards with the same color
     * in the drawn cards is the number of cards of this color to be added and is saved in the variable tempWagon. Now,
     * the number of locomotive cards is saved in locCount. Finally the method returns the sum of both these variables.
     *
     * @param claimCards the cards that are presented by the player
     * @param drawnCards the 3 cards drawn from the deck
     * @return the number of cards to be added
     * @throws IllegalArgumentException if the the level isn't underground or if the number of drawn cards is less than 3
     */

    public int additionalClaimCardsCount(SortedBag<Card> claimCards, SortedBag<Card> drawnCards)
            throws IllegalArgumentException {

        Preconditions.checkArgument(level != Level.OVERGROUND);
        Preconditions.checkArgument(drawnCards.size() == Constants.ADDITIONAL_TUNNEL_CARDS);

        List<Card> claimedCards = claimCards.toList();
        List<Card> drewCards = drawnCards.toList();

        int tempWagon = 0;
        int locCount = 0;
        Card card = null;

        for (Card claimedCard : claimedCards)
            if (claimedCard != Card.LOCOMOTIVE)
                card = claimedCard;

        for (Card drewCard : drewCards) {
            tempWagon = ((drewCard != Card.LOCOMOTIVE) && (drewCard == card)) ? tempWagon + 1 : tempWagon;
            locCount = (drewCard == Card.LOCOMOTIVE) ? locCount + 1 : locCount;
        }

        return tempWagon + locCount;
    }

    /**
     * Returns the claimed points by using a switch case. The points are those mentioned in Etape 2 on the website
     * if none of the following lengths are presented, then this function returns 0
     *
     * @return the points to be claimed
     */

    public int claimPoints() {
        switch (length) {
            case 1:
                return 1;
            case 2:
                return 2;
            case 3:
                return 4;
            case 4:
                return 7;
            case 5:
                return 10;
            case 6:
                return 15;
            default:
                return 0;
        }
    }

}
