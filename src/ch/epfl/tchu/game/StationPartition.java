package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;

import java.util.Objects;

/**
 * Represents a partition of the stations
 *
 * @author Marvin Chedid (302446)
 * @author Johnny Borkhoche (296169)
 */

public final class StationPartition implements StationConnectivity {

    private final int[] partition;

    private StationPartition(int[] partition) {
        this.partition = partition;
    }

    @Override
    public boolean connected(Station station1, Station station2) {
        return (station1.id() > partition.length || station2.id() > partition.length)
                ? Objects.equals(station1.id(), station2.id())
                : partition[station1.id()] == partition[station2.id()];
    }

    public static final class Builder {

        private final int[] partition;

        /**
         * Creates a partition
         *
         * @param stationcount the number of stations in the partition
         */

        public Builder(int stationcount) {
            Preconditions.checkArgument(stationcount >= 0);

            this.partition = new int[stationcount];
            for (int i = 0; i < stationcount; ++i)
                partition[i] = i;
        }

        /**
         * Connects two stations by changing the representative of the subset of stations that station 2 belongs to
         * and assigning it to the representative of the subset of stations that station 1 belongs to.
         *
         * @param station1 the first station
         * @param station2 the second station
         * @return the builder the a partition
         */

        public Builder connect(Station station1, Station station2) {
            for (int i = 0; i < partition.length; ++i)
                if (partition[i] == partition[station2.id()] && i != station2.id())
                    partition[i] = partition[station1.id()];

            partition[station2.id()] = partition[station1.id()];

            return this;
        }

        /**
         * Returns a new partition of the stations
         *
         * @return new partition of an instance
         */

        public StationPartition build() {
            return new StationPartition(partition);
        }

    }
}
