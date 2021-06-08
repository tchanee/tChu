package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;


/**
 * Represents a ticket from point A to point B
 *
 * @author Marvin Chedid (302446)
 * @author Johnny Borkhoche (296169)
 */

public final class Ticket implements Comparable<Ticket> {

    private final List<Trip> trips = new ArrayList<>();
    private final Trip ticketTrip;


    /**
     * Main Constructor for the class Ticket, creates a ticket with the list of itineraries provided and throws an illegal argument exception in case the list is empty
     *
     * @param tripsArgument list of trips
     * @throws IllegalArgumentException if list of trips is empty
     */

    public Ticket(List<Trip> tripsArgument) throws IllegalArgumentException {
        Preconditions.checkArgument(tripsArgument.size() > 0);
        int i = 0;
        do {
            if (tripsArgument.get(i).from().toString().equals(tripsArgument.get(0).from().toString())) {
                trips.add(tripsArgument.get(i));
                ++i;
            } else {
                Preconditions.checkArgument(false);
            }
        } while (i < tripsArgument.size());
        ticketTrip = trips.get(0);
    }

    /**
     * Secondary constructor for the class Ticket, creates a ticket for a single trip from point A to point B
     *
     * @param from   origin city
     * @param to     destination city
     * @param points points attributed to the trip
     */

    public Ticket(Station from, Station to, int points) {
        trips.add(new Trip(from, to, points));
        ticketTrip = trips.get(0);
    }

    /**
     * This method starts by creating a temporary list and a temporary string that will be used to create the ticket's
     * name.
     * First it iterates over the trips and creates the destinations and then it adds the final starting point to the
     * string it's going to return
     *
     * @return ticket name
     */

    private String computeText() {
        if (trips.size() == 1) {
            return trips.get(0).from().name() + " - " + trips.get(0).to().name() + " (" + trips.get(0).points() + ")";
        } else {
            TreeSet<String> tempList = new TreeSet<>();
            String tempString = "";
            for (Trip subTrip : trips) {
                tempString = subTrip.to().name() + " (" + subTrip.points() + ")";
                tempList.add(tempString);
            }
            return trips.get(0).from().name() + " - {" + String.join(", ", tempList) + "}";
        }
    }

    /**
     * Compares two tickets based on their textual description by using the computeText() method. It will return 0 if
     * they are identical, or will return a negative number if this ticket is lexicographically smaller than the one provided as a parameter, or a positive number otherwise.
     *
     * @param that Another ticket
     * @return 0, a negative number or a positive number
     */

    @Override
    public int compareTo(Ticket that) {
        return computeText().compareTo(that.computeText());
    }

    /**
     * Textualizes the ticket origin and destination using the computeText() method
     *
     * @return returns the textual representation of the ticket
     */

    public String text() {
        return computeText();
    }

    /**
     * This method returns the number of taking into account the different types of tickets available: there are four
     * notable cases:
     * <p>
     * Case 1: The ticket is simple or contains one trip. If the two stations are connected within the connectivity
     * then the method returns the value of the points otherwise it returns the inverse.
     * <p>
     * Case 2: The ticket is complex or contains several trips with full connectivity. The number of points
     * corresponding ticket correspond to the trip with the highest number of points.
     * <p>
     * Case 3: The ticket is complex or contains several trips with no connectivity. The number of points corresponding
     * to the ticket is therefore the inverse of the trip with the lower number of points.
     * <p>
     * Case 4: The ticket is complex or contains several trips with partial connectivity. The number of points
     * corresponding to the ticket is therefore the trip with the highest number of points that can be connected
     * within the current connectivity.
     *
     * @param connectivity The connectivity grid or constraints
     * @return The number of points associated with the trip
     */

    public int points(StationConnectivity connectivity) {
        int number = 0;
        List<Integer> tempList = new ArrayList<>();

        boolean containsNegative = false;
        boolean containsPositive = false;

        if (trips.size() == 1) {
            number = ticketTrip.points(connectivity);
        } else {
            for (Trip trip : trips) {
                Trip tempTrip = new Trip(trip.from(), trip.to(), trip.points());
                if (tempTrip.points(connectivity) < 0) {
                    containsNegative = true;
                } else {
                    containsPositive = true;
                }
                tempList.add(tempTrip.points(connectivity));
            }

            number = tempList.get(0);

            for (Integer integer : tempList)
                if (containsNegative && !containsPositive) {
                    if (number <= integer)
                        number = integer;
                } else {
                    if (number <= integer)
                        number = integer;
                }
        }

        return number;
    }

    @Override
    public String toString() {
        return computeText();
    }
}
