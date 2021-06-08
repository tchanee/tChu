package ch.epfl.tchu.gui;


import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.Card;
import ch.epfl.tchu.game.Route;
import ch.epfl.tchu.game.Trail;

import java.util.List;

/**
 * This class allows the generation of text messages that describe the development of the game. Usually the texts
 * describe actions undertaken by players so that their counterpart may know of their actions.
 *
 * @author Marvin Chedid (302446)
 * @author Johnny Borkhoche (296169)
 */

public final class Info {

    private final String playerName;

    public Info(String playerName) {
        this.playerName = playerName;
    }

    /**
     * Returns the french name of the card given as parameter in 2 possible ways: as a singular if the second parameter
     * equals 1, otherwise in plural.
     *
     * @param card  the card whose french name we want to return
     * @param count 1 if we want to return the french name in singular form
     * @return the french name of the provided card
     */
    public static String cardName(Card card, int count) {
        if (card.color() == null) {
            return StringsFr.LOCOMOTIVE_CARD + StringsFr.plural(count);
        }
        switch (card.color()) {
            case BLACK:
                return StringsFr.BLACK_CARD + StringsFr.plural(count);
            case VIOLET:
                return StringsFr.VIOLET_CARD + StringsFr.plural(count);
            case BLUE:
                return StringsFr.BLUE_CARD + StringsFr.plural(count);
            case GREEN:
                return StringsFr.GREEN_CARD + StringsFr.plural(count);
            case YELLOW:
                return StringsFr.YELLOW_CARD + StringsFr.plural(count);
            case ORANGE:
                return StringsFr.ORANGE_CARD + StringsFr.plural(count);
            case RED:
                return StringsFr.RED_CARD + StringsFr.plural(count);
            case WHITE:
                return StringsFr.WHITE_CARD + StringsFr.plural(count);
            default:
                return StringsFr.LOCOMOTIVE_CARD + StringsFr.plural(count);
        }
    }

    /**
     * Returns a message saying that the game is over, both players ended up in a DRAW, i.e they ended up with
     * the same amount of points
     *
     * @param playerNames a list of the player names
     * @param points      the number of points the game ended in a draw at
     * @return returns the generated message when the game ends in a draw
     */
    public static String draw(List<String> playerNames, int points) {
        String players = playerNames.get(0) + StringsFr.AND_SEPARATOR + playerNames.get(1);
        return String.format(StringsFr.DRAW, players, points);
    }

    /**
     * Returns the statement that the player will play first
     *
     * @return statement
     */

    public String willPlayFirst() {
        return String.format(StringsFr.WILL_PLAY_FIRST, playerName);
    }

    /**
     * Returns the statement that the player kept the number of cards
     *
     * @param count number of tickets
     * @return statement
     */

    public String keptTickets(int count) {
        return String.format(StringsFr.KEPT_N_TICKETS, playerName, count, StringsFr.plural(count));
    }

    /**
     * Returns the statement that the player it's the player's turn
     *
     * @return statement
     */

    public String canPlay() {
        return String.format(StringsFr.CAN_PLAY, playerName);
    }

    /**
     * Returns the statement that the player drew this number of tickets
     *
     * @param count number of tickets
     * @return statement
     */

    public String drewTickets(int count) {
        return String.format(StringsFr.DREW_TICKETS, playerName, count, StringsFr.plural(count));
    }

    /**
     * Returns the statement that the player has drawn a blind card
     *
     * @return statement
     */

    public String drewBlindCard() {
        return String.format(StringsFr.DREW_BLIND_CARD, playerName);
    }

    /**
     * Returns the statement that the player has drawn the specific card
     *
     * @param card card drawn
     * @return statement
     */

    public String drewVisibleCard(Card card) {
        String cardString = cardName(card, 1);
        return String.format(StringsFr.DREW_VISIBLE_CARD, playerName, cardString);
    }

    /**
     * Returns the statement that the player claimed the route with a specific set of cards
     *
     * @param route the route claimed
     * @param cards the cards used
     * @return statement
     */

    public String claimedRoute(Route route, SortedBag<Card> cards) {
        String routeString = route.station1() + StringsFr.EN_DASH_SEPARATOR + route.station2();
        String cardString = cardExtractor(cards);
        return String.format(StringsFr.CLAIMED_ROUTE, playerName, routeString, cardString);
    }

    /**
     * Returns the statement that the player is attempting to claim the tunnel with the given cards
     *
     * @param route        the tunnel to be claimed
     * @param initialCards the cards used
     * @return statement
     */

    public String attemptsTunnelClaim(Route route, SortedBag<Card> initialCards) {
        String routeString = route.station1() + StringsFr.EN_DASH_SEPARATOR + route.station2();
        String cardString = cardExtractor(initialCards);
        return String.format(StringsFr.ATTEMPTS_TUNNEL_CLAIM, playerName, routeString, cardString);
    }

    /**
     * Returns a statement that presents the drawn cards and announces whether some of these cards should be presented
     * to claim the tunnel with their respective cost.
     *
     * @param drawnCards     cards drawn that will need to be matched
     * @param additionalCost the additional cost required to claim the tunnel
     * @return statement.
     */

    public String drewAdditionalCards(SortedBag<Card> drawnCards, int additionalCost) {
        String cardString = cardExtractor(drawnCards);
        if (additionalCost == 0) {
            return String.format(StringsFr.ADDITIONAL_CARDS_ARE, cardString) + StringsFr.NO_ADDITIONAL_COST;
        } else {
            return String.format(StringsFr.ADDITIONAL_CARDS_ARE, cardString) + String.format(StringsFr.SOME_ADDITIONAL_COST, additionalCost, StringsFr.plural(additionalCost));
        }
    }

    /**
     * Returns a message declaring that the player did not/could not claim the given tunnel
     *
     * @param route route that the player did not/could not take possession of
     * @return message declaring that the player couldn't possess the given route
     */
    public String didNotClaimRoute(Route route) {
        String result = route.station1() + StringsFr.EN_DASH_SEPARATOR + route.station2();
        return String.format(StringsFr.DID_NOT_CLAIM_ROUTE, playerName, result);
    }

    /**
     * Returns a message declaring that the last turn has begun, because the player only has 2 or less wagons left
     *
     * @param carCount the amount of wagons the player still has
     * @return a message declaring the start of the last turn
     */
    public String lastTurnBegins(int carCount) {
        return String.format(StringsFr.LAST_TURN_BEGINS, playerName, carCount, StringsFr.plural(carCount));
    }

    /**
     * Returns a message declaring that the player obtains bonus points at the end of the game for
     * having [one of] the longest trail[s].
     *
     * @param longestTrail the longest trail which awards the player bonus points
     * @return the message declaring that the player has been awarded the bonus points for having [one of] the longest trail[s]
     */
    public String getsLongestTrailBonus(Trail longestTrail) {
        String result = longestTrail.station1().name() + StringsFr.EN_DASH_SEPARATOR + longestTrail.station2().name();
        return String.format(StringsFr.GETS_BONUS, playerName, result);
    }

    /**
     * Returns a message declaring that the winner has won the game with
     *
     * @param points      number of points that the winner has at the end of winning
     * @param loserPoints number of points the loser has at the end of the game
     * @return a message declaring the amount of points the winner won with
     */
    public String won(int points, int loserPoints) {
        return String.format(StringsFr.WINS, playerName, points, StringsFr.plural(points),
                loserPoints, StringsFr.plural(loserPoints));
    }

    private static String cardExtractor(SortedBag<Card> cards) {
        StringBuilder result = new StringBuilder();
        int counter = 0;
        for (Card c : cards.toSet()) {
            int count = cards.countOf(c);
            if (count > 0 && counter >= 1) {
                result.append(StringsFr.AND_SEPARATOR).append(count).append(" ").append(cardName(c, count));
            } else if (count > 0) {
                result.append(count).append(" ").append(cardName(c, count));
                counter += 1;
            }
        }
        return result.toString();
    }
}
