package ch.epfl.tchu.game;

import java.util.*;

/**
 * Represents a trail/road in the player's network
 *
 * @author Marvin Chedid (302446)
 * @author Johnny Borkhoche (296169)
 */

public final class Trail {

    private final List<Station> stations;
    private final int length;


    private Trail(List<Station> stations, int length) {
        this.stations = new ArrayList<Station>(stations);
        this.length = length;
    }

    /**
     * The method returns the trail made out of the routes taken in argument and forms the longest trail made out
     * of the given routes.
     * In a nutshell, this algorithm goes through the list of provided routes, creates trails out of them, then
     * tries to extend them by finding possible routes that could form a longer route when correctly combined.
     * At the end, it returns the longest created Trail. If there are multiple trails of max length, it simply
     * returns the first one.
     *
     * @param routes these are the routes that will form the trail
     * @return a trail made of the routes passed in argument following the methodology specified above
     */

    public static Trail longest(List<Route> routes) {

        List<Trail> baseRoutes = new ArrayList<>();
        List<String> baseRoutesID = new ArrayList<>();

        for (Route route : routes) {

            List<Station> tempStations = new ArrayList<>();
            tempStations.add(route.station1());
            tempStations.add(route.station2());
            baseRoutes.add(new Trail(tempStations, route.length()));
            baseRoutesID.add(route.id());

            List<Station> tempStations1 = new ArrayList<>();
            tempStations1.add(route.station2());
            tempStations1.add(route.station1());
            baseRoutes.add(new Trail(tempStations1, route.length()));
            baseRoutesID.add(route.id());

        }

        List<Trail> tempTrail = new ArrayList<>(baseRoutes);
        List<String> tempTrailID = new ArrayList<>(baseRoutesID);

        for (int k = 2; k < 4 * routes.size(); ++k)
            for (int i = 0; i < tempTrail.size(); ++i)
                for (int j = 0; j < baseRoutes.size(); ++j) {

                    Station i_1 = tempTrail.get(i).stations.get(tempTrail.get(i).stations.size() - 2);
                    Station i_2 = tempTrail.get(i).station2();
                    Station j_1 = baseRoutes.get(j).station1();
                    Station j_2 = baseRoutes.get(j).station2();

                    if ((i_2.id() == j_1.id()) && !(i_1.id() == j_2.id()) && tempTrail.get(i).stations.size() == k) {
                        List<Station> tempRoutes = new ArrayList<>(tempTrail.get(i).stations);
                        tempRoutes.addAll(baseRoutes.get(j).stations);
                        Trail temp = new Trail(tempRoutes, tempTrail.get(i).length() + baseRoutes.get(j).length());
                        if (!(tempTrailID.get(i).contains(baseRoutesID.get(j)))) {
                            tempTrail.add(temp);
                            tempTrailID.add(tempTrailID.get(i) + "_" + baseRoutesID.get(j));
                        }
                    }
                }

        int max = 1;
        int index = -1;

        for (int i = 0; i < tempTrail.size(); ++i)
            if (tempTrail.get(i).length() >= max) {
                max = tempTrail.get(i).length();
                index = i;
            }

        if (index == -1) {
            List<Station> emptyList = new ArrayList<>();
            return new Trail(emptyList, 0);
        }

        return tempTrail.get(index);
    }

    /**
     * Returns the length of the whole trail
     *
     * @return the length of the whole trail
     */

    public int length() {
        return length;
    }

    /**
     * Returns the name of the first station in the trail
     *
     * @return the starting station
     */

    public Station station1() {
        return (length == 0) ? null : stations.get(0);
    }

    /**
     * Returns the name of the last station in the trail
     *
     * @return the destination
     */

    public Station station2() {
        return (length == 0) ? null : stations.get(stations.size() - 1);
    }

    /**
     * Overrides the toString method to give the name of the whole trail
     *
     * @return name of the trail
     */

    @Override
    public String toString() {

        if (stations.isEmpty())
            return "";

        List<String> stationNames = new ArrayList<>();
        List<String> stationNameDefinitive = new ArrayList<>();

        for (Station station : stations)
            stationNames.add(station.name());

        for (int i = 0; i < stationNames.size() - 1; ++i)
            if (!(stationNames.get(i).equals(stationNames.get(i + 1))))
                stationNameDefinitive.add(stationNames.get(i));

        stationNameDefinitive.add(stationNames.get(stationNames.size() - 1));
        return String.join(" - ", stationNameDefinitive) + " (" + length + ")";
    }

    public List<Station> getStations() {
        return stations;
    }
}
