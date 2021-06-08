package ch.epfl.tchu.net;

import ch.epfl.tchu.game.*;

import static ch.epfl.tchu.net.Serdes.*;

import java.io.*;
import java.net.Socket;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import static java.nio.charset.StandardCharsets.US_ASCII;
import static ch.epfl.tchu.game.PlayerId.*;

/**
 * This class represents the client of a distant player
 *
 * @author Marvin Chedid (302446)
 * @author Johnny Borkhoche (296169)
 */

public final class RemotePlayerClient {

    private final int port;
    private final String name;
    private final Player self;

    /**
     * Public constructor for the RemotePlayerClient class
     *
     * @param self the player this client belongs to
     * @param name the name of the player to whom this client belongs
     * @param port the port number that the player will be using to communicate with the distant game server
     */
    public RemotePlayerClient(Player self, String name, int port) {
        this.self = self;
        this.name = name;
        this.port = port;
    }

    /**
     * This method runs a loop during which it awaits a message coming from the proxy, splits it into a String array,
     * determines the type of message received, and appropriately either deserializes or serializes said message
     * and calls the corresponding Player method.
     */
    public void run() {
        try (Socket thisSocket = new Socket(name, port);
             BufferedReader r = new BufferedReader(new InputStreamReader(thisSocket.getInputStream(), US_ASCII));
             BufferedWriter w = new BufferedWriter(new OutputStreamWriter(thisSocket.getOutputStream(), US_ASCII))) {
            String command;
            while ((command = r.readLine()) != null) {
                String[] array = command.split(Pattern.quote(" "), -1);
                switch (array[0]) {
                    case "INIT_PLAYERS":
                        PlayerId player = playerIdSerde.deserialize(array[1]);
                        List<String> n = listStringSerde.deserialize(array[2]);
                        Map<PlayerId, String> tempMap = new EnumMap<>(PlayerId.class);
                        tempMap.putIfAbsent(PLAYER_1, n.get(0));
                        tempMap.putIfAbsent(PLAYER_2, n.get(1));
                        if (n.size() == 3)
                            tempMap.putIfAbsent(PLAYER_3, n.get(2));
                        self.initPlayers(player, tempMap);
                        break;
                    case "RECEIVE_INFO":
                        self.receiveInfo(stringSerde.deserialize(array[1]));
                        break;
                    case "UPDATE_STATE":
                        self.updateState(publicGameStateSerde.deserialize(array[1]), playerStateSerde.deserialize(array[2]));
                        break;
                    case "SET_INITIAL_TICKETS":
                        self.setInitialTicketChoice(sortedBagTicketSerde.deserialize(array[1]));
                        break;
                    case "CHOOSE_INITIAL_TICKETS":
                        writeToServer(w, sortedBagTicketSerde.serialize(self.chooseInitialTickets()));
                        break;
                    case "NEXT_TURN":
                        writeToServer(w, turnKindSerde.serialize(self.nextTurn()));
                        break;
                    case "CHOOSE_TICKETS":
                        writeToServer(w, sortedBagTicketSerde.serialize(self.chooseTickets(sortedBagTicketSerde.deserialize(array[1]))));
                        break;
                    case "DRAW_SLOT":
                        writeToServer(w, intSerde.serialize(self.drawSlot()));
                        break;
                    case "ROUTE":
                        writeToServer(w, routeSerde.serialize(self.claimedRoute()));
                        break;
                    case "CARDS":
                        writeToServer(w, sortedBagCardSerde.serialize(self.initialClaimCards()));
                        break;
                    case "CHOOSE_ADDITIONAL_CARDS":
                        writeToServer(w, sortedBagCardSerde.serialize(self.chooseAdditionalCards(listSortedBagCardSerde.deserialize(array[1]))));
                        break;
                }
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /**
     * Auxiliary function written to modularize our code. It serves as a template to write a message into the socket's
     * input stream
     *
     * @param w   the buffered writer used to write onto the socket's input stream
     * @param msg the message to write in the socket's stream
     */
    public void writeToServer(BufferedWriter w, String msg) {
        try {
            w.write(msg);
            w.write('\n');
            w.flush();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
