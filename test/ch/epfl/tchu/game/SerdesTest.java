package ch.epfl.tchu.game;


import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.net.Serdes;
import org.junit.jupiter.api.Test;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class SerdesTest {

    @Test
    public void serdeInt(){
        assertEquals(2974, Serdes.intSerde.deserialize(Serdes.intSerde.serialize(2974)));
    }

    @Test
    public void serdeString(){
        assertEquals("Q2hhcmxlcw==", Serdes.stringSerde.serialize("Charles"));
    }

    @Test
    public void serdePlayerId(){
        assertEquals(PlayerId.PLAYER_1, Serdes.playerIdSerde.deserialize(Serdes.playerIdSerde.serialize(PlayerId.PLAYER_1)));
        assertEquals(PlayerId.PLAYER_2, Serdes.playerIdSerde.deserialize(Serdes.playerIdSerde.serialize(PlayerId.PLAYER_2)));
    }

    @Test
    public void serdeTurnKind(){
        assertEquals(Player.TurnKind.CLAIM_ROUTE, Serdes.turnKindSerde.deserialize(Serdes.turnKindSerde.serialize(Player.TurnKind.CLAIM_ROUTE)));
        assertEquals(Player.TurnKind.DRAW_CARDS, Serdes.turnKindSerde.deserialize(Serdes.turnKindSerde.serialize(Player.TurnKind.DRAW_CARDS)));
        assertEquals(Player.TurnKind.DRAW_TICKETS, Serdes.turnKindSerde.deserialize(Serdes.turnKindSerde.serialize(Player.TurnKind.DRAW_TICKETS)));
    }

    @Test
    public void serdeCard(){
        assertEquals(Card.ALL.get(0), Serdes.cardSerde.deserialize(Serdes.cardSerde.serialize(Card.ALL.get(0))));
        assertEquals(Card.ALL.get(1), Serdes.cardSerde.deserialize(Serdes.cardSerde.serialize(Card.ALL.get(1))));
    }

    @Test
    public void serdeRoute(){
        assertEquals(ChMap.routes().get(0), Serdes.routeSerde.deserialize(Serdes.routeSerde.serialize(ChMap.routes().get(0))));
        assertEquals(ChMap.routes().get(1), Serdes.routeSerde.deserialize(Serdes.routeSerde.serialize(ChMap.routes().get(1))));
    }

    @Test
    public void serdeTicket(){
        assertEquals(ChMap.tickets().get(0), Serdes.ticketSerde.deserialize(Serdes.ticketSerde.serialize(ChMap.tickets().get(0))));
        assertEquals(ChMap.tickets().get(1), Serdes.ticketSerde.deserialize(Serdes.ticketSerde.serialize(ChMap.tickets().get(1))));
    }

    @Test
    public void serdeListString(){
        assertEquals(List.of("p", "bite", "pute"), Serdes.listStringSerde.deserialize(Serdes.listStringSerde.serialize(List.of("p", "bite", "pute"))));
    }

    @Test
    public void serdeListCard(){
        assertEquals(List.of(Card.ORANGE, Card.WHITE, Card.BLUE), Serdes.listCardSerde.deserialize(Serdes.listCardSerde.serialize(List.of(Card.ORANGE, Card.WHITE, Card.BLUE))));
    }

    @Test
    public void serdeListRoute(){
        assertEquals(List.of(ChMap.routes().get(0), ChMap.routes().get(1), ChMap.routes().get(2)), Serdes.listRouteSerde.deserialize(Serdes.listRouteSerde.serialize(List.of(ChMap.routes().get(0), ChMap.routes().get(1), ChMap.routes().get(2)))));
    }

    @Test
    public void serdeSortedBagCard(){
        assertEquals(SortedBag.of(1 ,Card.ORANGE, 1, Card.YELLOW), Serdes.sortedBagCardSerde.deserialize(Serdes.sortedBagCardSerde.serialize(SortedBag.of(1 ,Card.ORANGE, 1, Card.YELLOW))));
    }

    @Test
    public void serdeSortedBagTicket(){
        assertEquals(SortedBag.of(1, ChMap.tickets().get(0), 1, ChMap.tickets().get(1)), Serdes.sortedBagTicketSerde.deserialize(Serdes.sortedBagTicketSerde.serialize(SortedBag.of(1, ChMap.tickets().get(0), 1, ChMap.tickets().get(1)))));
    }

    @Test
    public void serdeListOfSortedBagCard(){
        List<SortedBag<Card>> tt = List.of(SortedBag.of(1 ,Card.ORANGE, 1, Card.YELLOW), SortedBag.of(1 ,Card.WHITE, 1, Card.RED));
        assertEquals(tt, Serdes.listSortedBagCardSerde.deserialize(Serdes.listSortedBagCardSerde.serialize(tt)));
    }

    @Test
    public void serdePublicCardState(){
        List<Card> fuc = List.of(Card.BLUE, Card.BLACK, Card.ORANGE, Card.ORANGE, Card.RED);
        PublicCardState pcs = new PublicCardState(fuc, fuc.size(), 0);
        PublicCardState pcsOne = Serdes.publicCardStateSerde.deserialize(Serdes.publicCardStateSerde.serialize(pcs));

        assertEquals(pcs.faceUpCards(), pcsOne.faceUpCards());
    }

    @Test
    public void serdePublicPlayerState(){
        PublicPlayerState pps = new PublicPlayerState(10, 10, ChMap.routes().subList(0, 5));
        PublicPlayerState ppsOne = Serdes.publicPlayerStateSerde.deserialize(Serdes.publicPlayerStateSerde.serialize(pps));

        assertEquals(pps.routes(), ppsOne.routes());
        assertEquals(pps.cardCount(), ppsOne.cardCount());
        assertEquals(pps.ticketCount(), ppsOne.ticketCount());
    }

    @Test
    public void serdePlayerState(){
        SortedBag<Ticket> tickets = SortedBag.of(ChMap.tickets());
        SortedBag<Card> cards = SortedBag.of(Card.ALL);
        PlayerState ps = new PlayerState(tickets, cards, ChMap.routes());
        PlayerState psOne = Serdes.playerStateSerde.deserialize(Serdes.playerStateSerde.serialize(ps));

        assertEquals(ps.cards(), psOne.cards());
        assertEquals(ps.tickets(), psOne.tickets());
        assertEquals(ps.routes(), psOne.routes());
    }

    @Test
    public void serdePublicGameState(){
        List<Card> fu = List.of(Card.RED, Card.WHITE, Card.BLUE, Card.BLACK, Card.RED);
        PublicCardState cs = new PublicCardState(fu, 30, 31);
        List<Route> rs1 = ChMap.routes().subList(0, 2);
        Map<PlayerId, PublicPlayerState> ps = Map.of(
                PlayerId.PLAYER_1, new PublicPlayerState(10, 11, rs1),
                PlayerId.PLAYER_2, new PublicPlayerState(20, 21, List.of()));
        PublicGameState gs = new PublicGameState(40, cs, PlayerId.PLAYER_2, ps, null);
        PublicGameState gsN = Serdes.publicGameStateSerde.deserialize(Serdes.publicGameStateSerde.serialize(gs));

        assertEquals(gs.ticketsCount(), gsN.ticketsCount());
        assertEquals(gs.currentPlayerState().routes(), gsN.currentPlayerState().routes());
        assertEquals(gs.currentPlayerId(), gsN.currentPlayerId());
        assertEquals(gs.lastPlayer(), gsN.lastPlayer());


        List<Card> fuc = List.of(Card.BLUE, Card.BLACK, Card.ORANGE, Card.ORANGE, Card.RED);
        PublicCardState pcs = new PublicCardState(fuc, fuc.size(), 0);

        PublicPlayerState pps = new PublicPlayerState(1, 4, ChMap.routes().subList(0, 5));
        PublicPlayerState ppsN = new PublicPlayerState(1, 4, ChMap.routes().subList(1, 7));

        Map<PlayerId, PublicPlayerState> map = new EnumMap<>(PlayerId.class);
        map.put(PlayerId.PLAYER_1, pps);
        map.put(PlayerId.PLAYER_2, ppsN);

        PublicGameState game = new PublicGameState(3, pcs, PlayerId.PLAYER_1, map, null);
        PublicGameState gameN = Serdes.publicGameStateSerde.deserialize(Serdes.publicGameStateSerde.serialize(game));

        assertEquals(game.ticketsCount(), gameN.ticketsCount());
        assertEquals(game.currentPlayerState().routes(), gameN.currentPlayerState().routes());
        assertEquals(game.currentPlayerId(), gameN.currentPlayerId());
        assertEquals(game.lastPlayer(), gameN.lastPlayer());
    }
}