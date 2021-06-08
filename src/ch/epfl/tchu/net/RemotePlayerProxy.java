package ch.epfl.tchu.net;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;

import static ch.epfl.tchu.net.Serdes.*;
import static ch.epfl.tchu.net.MessageId.*;

import java.io.*;
import java.net.Socket;
import java.util.*;

import static java.nio.charset.StandardCharsets.US_ASCII;

/**
 * This class represents the proxy for a distant player
 *
 * @author Marvin Chedid (302446)
 * @author Johnny Borkhoche (296169)
 */

public final class RemotePlayerProxy implements Player {

    private final BufferedWriter w;
    private final BufferedReader r;

    /**
     * RemotePlayerProxy's public constructor, which takes the socket that this proxy will connect to as a parameter
     *
     * @param socket the socket that the distant player will read from and write to
     */
    public RemotePlayerProxy(Socket socket) {
        try {
            this.w = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), US_ASCII));
            this.r = new BufferedReader(new InputStreamReader(socket.getInputStream(), US_ASCII));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /**
     * Same documentation as the super method from the class Player.java
     */
    @Override
    public void initPlayers(PlayerId ownId, Map<PlayerId, String> playerNames) {
        String id = playerIdSerde.serialize(ownId);

        List<String> names = new ArrayList<>();

        names.add(playerNames.get(PlayerId.PLAYER_1));
        names.add(playerNames.get(PlayerId.PLAYER_2));
        if (playerNames.size() == 3)
            names.add(playerNames.get(PlayerId.PLAYER_3));

        String players = listStringSerde.serialize(names);
        String msg = INIT_PLAYERS.name() + " " + id + " " + players;
        writeToServer(msg);
    }

    /**
     * Same documentation as the super method from the class Player.java
     */
    @Override
    public void receiveInfo(String info) {
        String infoReceived = stringSerde.serialize(info);
        String msg = RECEIVE_INFO.name() + " " + infoReceived;
        writeToServer(msg);
    }

    /**
     * Same documentation as the super method from the class Player.java
     */
    @Override
    public void updateState(PublicGameState newState, PlayerState ownState) {
        String newStateSerial = publicGameStateSerde.serialize(newState);
        String ownStateSerial = playerStateSerde.serialize(ownState);
        String msg = UPDATE_STATE.name() + " " + newStateSerial + " " + ownStateSerial;
        writeToServer(msg);
    }

    /**
     * Same documentation as the super method from the class Player.java
     */
    @Override
    public void setInitialTicketChoice(SortedBag<Ticket> tickets) {
        String ticketsSerial = sortedBagTicketSerde.serialize(tickets);
        String msg = SET_INITIAL_TICKETS.name() + " " + ticketsSerial;
        writeToServer(msg);
    }

    /**
     * Same documentation as the super method from the class Player.java
     */
    @Override
    public SortedBag<Ticket> chooseInitialTickets() {
        String msg = CHOOSE_INITIAL_TICKETS.name();
        writeToServer(msg);
        return sortedBagTicketSerde.deserialize(readFromServer());
    }

    /**
     * Same documentation as the super method from the class Player.java
     */
    @Override
    public TurnKind nextTurn() {
        String msg = NEXT_TURN.name();
        writeToServer(msg);
        return turnKindSerde.deserialize(readFromServer());
    }

    /**
     * Same documentation as the super method from the class Player.java
     */
    @Override
    public SortedBag<Ticket> chooseTickets(SortedBag<Ticket> options) {
        String listOptions = sortedBagTicketSerde.serialize(options);
        String msg = CHOOSE_TICKETS.name() + " " + listOptions;
        writeToServer(msg);
        return sortedBagTicketSerde.deserialize(readFromServer());
    }

    /**
     * Same documentation as the super method from the class Player.java
     */
    @Override
    public int drawSlot() {
        String msg = DRAW_SLOT.name();
        writeToServer(msg);
        return intSerde.deserialize(readFromServer());
    }

    /**
     * Same documentation as the super method from the class Player.java
     */
    @Override
    public Route claimedRoute() {
        String msg = ROUTE.name();
        writeToServer(msg);
        return routeSerde.deserialize(readFromServer());
    }

    /**
     * Same documentation as the super method from the class Player.java
     */
    @Override
    public SortedBag<Card> initialClaimCards() {
        String msg = CARDS.name();
        writeToServer(msg);
        return sortedBagCardSerde.deserialize(readFromServer());
    }

    /**
     * Same documentation as the super method from the class Player.java
     */
    @Override
    public SortedBag<Card> chooseAdditionalCards(List<SortedBag<Card>> options) {
        String listOptions = listSortedBagCardSerde.serialize(options);
        String msg = CHOOSE_ADDITIONAL_CARDS.name() + " " + listOptions;
        writeToServer(msg);
        return sortedBagCardSerde.deserialize(readFromServer());
    }

    /**
     * Function to write to the server
     *
     * @param msg the information that we want to send
     */
    public void writeToServer(String msg) {
        try {
            w.write(msg);
            w.write('\n');
            w.flush();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /**
     * Reads from the server
     *
     * @return returns the message read
     */
    public String readFromServer() {
        try {
            return r.readLine();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
