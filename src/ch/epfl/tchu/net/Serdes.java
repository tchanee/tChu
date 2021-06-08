package ch.epfl.tchu.net;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;


import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Function;
import java.util.regex.Pattern;

import static ch.epfl.tchu.game.PlayerId.*;

/**
 * A class containing all possible types of Serde (serializer/deserializer) that will be used to encode/decode information
 * during a game of tChu
 *
 * @author Marvin Chedid (302446)
 * @author Johnny Borkhoche (296169)
 */
public final class Serdes {
    private Serdes() {
    }

    //Int serde
    public static final Function<Integer, String> intEncoder = String::valueOf;
    public static final Function<String, Integer> intDecoder = Integer::parseInt;
    /**
     * Serde that takes care of serializing/deserializing objects of type Int
     */
    public static final Serde<Integer> intSerde = Serde.of(intEncoder, intDecoder);

    //String serde
    public static final Function<String, String> stringEncoder = str -> Base64.getEncoder().encodeToString(str.getBytes(StandardCharsets.UTF_8));
    public static final Function<String, String> stringDecoder = str -> new String(Base64.getDecoder().decode(str), StandardCharsets.UTF_8);
    /**
     * Serde that takes care of serializing/deserializing objects of type
     */
    public static final Serde<String> stringSerde = Serde.of(stringEncoder, stringDecoder);

    //Enum serde
    /**
     * Serde that takes care of serializing/deserializing objects of type PlayerId
     */
    public static final Serde<PlayerId> playerIdSerde = Serde.oneOf(PlayerId.ALL);
    /**
     * Serde that takes care of serializing/deserializing objects of type TurnKind
     */
    public static final Serde<Player.TurnKind> turnKindSerde = Serde.oneOf(Player.TurnKind.ALL);
    /**
     * Serde that takes care of serializing/deserializing objects of type Card
     */
    public static final Serde<Card> cardSerde = Serde.oneOf(Card.ALL);
    /**
     * Serde that takes care of serializing/deserializing objects of type Route
     */
    public static final Serde<Route> routeSerde = Serde.oneOf(ChMap.routes());
    /**
     * Serde that takes care of serializing/deserializing objects of type Ticket
     */
    public static final Serde<Ticket> ticketSerde = Serde.oneOf(ChMap.tickets());

    //List serde
    /**
     * Serde that takes care of serializing/deserializing objects of type List<String>
     */
    public static final Serde<List<String>> listStringSerde = Serde.listOf(stringSerde, ",");
    /**
     * Serde that takes care of serializing/deserializing objects of type List<Card>
     */
    public static final Serde<List<Card>> listCardSerde = Serde.listOf(cardSerde, ",");
    /**
     * Serde that takes care of serializing/deserializing objects of type List<Route>
     */
    public static final Serde<List<Route>> listRouteSerde = Serde.listOf(routeSerde, ",");
    /**
     * Serde that takes care of serializing/deserializing objects of type List<Ticket>
     */
    public static final Serde<List<Ticket>> listTicketSerde = Serde.listOf(ticketSerde, ",");

    //SortedBag<Card> serde
    public static final Function<SortedBag<Card>, String> sortedBagCardEncoder = sbc -> listCardSerde.serialize(sbc.toList());
    public static final Function<String, SortedBag<Card>> sortedBagCardDecoder = msg -> !msg.equals("") ? SortedBag.of(listCardSerde.deserialize(msg)) : SortedBag.of();
    /**
     * Serde that takes care of serializing/deserializing objects of type SortedBag<Card>
     */
    public static final Serde<SortedBag<Card>> sortedBagCardSerde = Serde.of(sortedBagCardEncoder, sortedBagCardDecoder);

    //SortedBag<Ticket> serde
    public static final Function<SortedBag<Ticket>, String> sortedBagTicketEncoder = sbt -> listTicketSerde.serialize(sbt.toList());
    public static final Function<String, SortedBag<Ticket>> sortedBagTicketDecoder = msg -> !msg.equals("") ? SortedBag.of(listTicketSerde.deserialize(msg)) : SortedBag.of();
    /**
     * Serde that takes care of serializing/deserializing objects of type SortedBag<Ticket>
     */
    public static final Serde<SortedBag<Ticket>> sortedBagTicketSerde = Serde.of(sortedBagTicketEncoder, sortedBagTicketDecoder);

    //List<SortedBag<Card>> serde
    public static final Function<List<SortedBag<Card>>, String> listSortedBagCardEncoder = lsbc -> {
        StringBuilder encoded = new StringBuilder();
        for (int i = 0; i < lsbc.size(); ++i) {
            encoded.append(sortedBagCardSerde.serialize(lsbc.get(i)));
            if (i != lsbc.size() - 1)
                encoded.append(";");
        }
        return encoded.toString();
    };
    public static final Function<String, List<SortedBag<Card>>> listSortedBagCardDecoder = msg -> {
        List<SortedBag<Card>> decoded = new ArrayList<>();
        List<String> array_first = List.of(msg.split(Pattern.quote(";"), -1));
        array_first.forEach(s -> decoded.add(sortedBagCardSerde.deserialize(s)));
        return decoded;
    };
    /**
     * Serde that takes care of serializing/deserializing objects of type List<SortedBag<Card>>
     */
    public static final Serde<List<SortedBag<Card>>> listSortedBagCardSerde = Serde.of(listSortedBagCardEncoder, listSortedBagCardDecoder);

    //Map<PlayerId, String> serde
    public static final Function<Map<PlayerId, String>, String> mapPlayerIdStringEncoder = (mpids -> {
        if (mpids.size() == 2)
            return playerIdSerde.serialize(PlayerId.PLAYER_1)
                    + "," + stringSerde.serialize(mpids.get(PLAYER_1))
                    + ":" + playerIdSerde.serialize(PlayerId.PLAYER_2)
                    + "," + stringSerde.serialize(mpids.get(PLAYER_2));
        else return playerIdSerde.serialize(PlayerId.PLAYER_1)
                + "," + stringSerde.serialize(mpids.get(PLAYER_1))
                + ":" + playerIdSerde.serialize(PlayerId.PLAYER_2)
                + "," + stringSerde.serialize(mpids.get(PLAYER_2))
                + ":" + playerIdSerde.serialize(PlayerId.PLAYER_3)
                + "," + stringSerde.serialize(mpids.get(PLAYER_3));
    });

    public static final Function<String, Map<PlayerId, String>> mapPlayerIdStringDecoder = msg -> {
        String[] array = msg.split(Pattern.quote(":"), -1);
        String[] array_1 = array[0].split(Pattern.quote(","), -1);
        String[] array_2 = array[1].split(Pattern.quote(","), -1);
        Map<PlayerId, String> tempMap = new TreeMap<>();
        tempMap.putIfAbsent(playerIdSerde.deserialize(array_1[0]), stringSerde.deserialize(array_1[1]));
        tempMap.putIfAbsent(playerIdSerde.deserialize(array_2[0]), stringSerde.deserialize(array_2[1]));
        if (array.length == 3) {
            String[] array_3 = array[2].split(Pattern.quote(","), -1);
            tempMap.putIfAbsent(playerIdSerde.deserialize(array_3[0]), stringSerde.deserialize(array_3[1]));
        }
        return tempMap;
    };
    /**
     * Serde that takes care of serializing/deserializing objects of type Map<PlayerId, String>>
     */
    public static final Serde<Map<PlayerId, String>> mapPlayerIdStringSerde = Serde.of(mapPlayerIdStringEncoder, mapPlayerIdStringDecoder);

    //PublicCardState serde
    public static final Function<PublicCardState, String> publicCardStateEncoder = pcs -> listCardSerde.serialize(pcs.faceUpCards())
            + ";" + intSerde.serialize(pcs.deckSize())
            + ";" + intSerde.serialize(pcs.discardsSize());
    public static final Function<String, PublicCardState> publicCardStateDecoder = msg -> {
        String[] array = msg.split(Pattern.quote(";"), -1);
        return new PublicCardState(listCardSerde.deserialize(array[0]),
                intSerde.deserialize(array[1]),
                intSerde.deserialize(array[2]));

    };
    /**
     * Serde that takes care of serializing/deserializing objects of type PublicCardState
     */
    public static final Serde<PublicCardState> publicCardStateSerde = Serde.of(publicCardStateEncoder, publicCardStateDecoder);

    //PublicPlayerstate serde
    public static final Function<PublicPlayerState, String> publicPlayerStateEncoder = pps -> intSerde.serialize(pps.ticketCount())
            + ";" + intSerde.serialize(pps.cardCount())
            + ";" + listRouteSerde.serialize(pps.routes());
    public static final Function<String, PublicPlayerState> publicPlayerStateDecoder = msg -> {
        String[] array = msg.split(Pattern.quote(";"), -1);
        return new PublicPlayerState(intSerde.deserialize(array[0]),
                intSerde.deserialize(array[1]),
                listRouteSerde.deserialize(array[2]));

    };
    /**
     * Serde that takes care of serializing/deserializing objects of type PublicPlayerState
     */
    public static final Serde<PublicPlayerState> publicPlayerStateSerde = Serde.of(publicPlayerStateEncoder, publicPlayerStateDecoder);

    //PlayerState serde
    public static final Function<PlayerState, String> playerStateEncoder = ps -> sortedBagTicketSerde.serialize(ps.tickets())
            + ";" + sortedBagCardSerde.serialize(ps.cards())
            + ";" + listRouteSerde.serialize(ps.routes());
    public static final Function<String, PlayerState> playerStateDecoder = msg -> {
        String[] array = msg.split(Pattern.quote(";"), -1);
        return new PlayerState(sortedBagTicketSerde.deserialize(array[0]),
                sortedBagCardSerde.deserialize(array[1]),
                listRouteSerde.deserialize(array[2]));

    };
    /**
     * Serde that takes care of serializing/deserializing objects of type PlayerState
     */
    public static final Serde<PlayerState> playerStateSerde = Serde.of(playerStateEncoder, playerStateDecoder);

    //PublicGameState serde
    public static final Function<PublicGameState, String> publicGameStateEncoder = (pgs -> {
        if (pgs.getPlayerState().size() == 2)
            return intSerde.serialize(pgs.ticketsCount())
                    + ":" + publicCardStateSerde.serialize(pgs.cardState())
                    + ":" + playerIdSerde.serialize(pgs.currentPlayerId())
                    + ":" + publicPlayerStateSerde.serialize(pgs.playerState(PLAYER_1))
                    + ":" + publicPlayerStateSerde.serialize(pgs.playerState(PLAYER_2))
                    + ":" + playerIdSerde.serialize(pgs.lastPlayer());
        else return intSerde.serialize(pgs.ticketsCount())
                + ":" + publicCardStateSerde.serialize(pgs.cardState())
                + ":" + playerIdSerde.serialize(pgs.currentPlayerId())
                + ":" + publicPlayerStateSerde.serialize(pgs.playerState(PLAYER_1))
                + ":" + publicPlayerStateSerde.serialize(pgs.playerState(PLAYER_2))
                + ":" + publicPlayerStateSerde.serialize(pgs.playerState(PLAYER_3))
                + ":" + playerIdSerde.serialize(pgs.lastPlayer());

    });
    public static final Function<String, PublicGameState> publicGameStateDecoder = msg -> {
        String[] array = msg.split(Pattern.quote(":"), -1);
        Map<PlayerId, PublicPlayerState> tempMap = new TreeMap<>();
        if (array.length == 7) {
            tempMap = Map.of(PLAYER_1, publicPlayerStateSerde.deserialize(array[3]),
                    PLAYER_2, publicPlayerStateSerde.deserialize(array[4]), PLAYER_3, publicPlayerStateSerde.deserialize(array[5]));
            return new PublicGameState(intSerde.deserialize(array[0]),
                    publicCardStateSerde.deserialize(array[1]),
                    playerIdSerde.deserialize(array[2]),
                    tempMap,
                    playerIdSerde.deserialize(array[6]));
        } else {
            tempMap = Map.of(PLAYER_1, publicPlayerStateSerde.deserialize(array[3]),
                    PLAYER_2, publicPlayerStateSerde.deserialize(array[4]));
            return new PublicGameState(intSerde.deserialize(array[0]),
                    publicCardStateSerde.deserialize(array[1]),
                    playerIdSerde.deserialize(array[2]),
                    tempMap,
                    playerIdSerde.deserialize(array[5]));
        }
    };
    /**
     * Serde that takes care of serializing/deserializing objects of type PublicGameState
     */
    public static final Serde<PublicGameState> publicGameStateSerde = Serde.of(publicGameStateEncoder, publicGameStateDecoder);

}
