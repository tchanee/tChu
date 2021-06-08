package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;

import java.util.*;

/**
 * Represents the state of a game of tChu
 *
 * @author Marvin Chedid (302446)
 * @author Johnny Borkhoche (296169)
 */

public final class GameState extends PublicGameState {

    private final Deck<Ticket> allTickets;
    private final int ticketsCount;
    private final CardState privateCardState;
    private final PlayerId currentPlayerId;
    private final PlayerId lastPlayer;
    final Map<PlayerId, PlayerState> privatePlayerState;

    private GameState(Deck<Ticket> allTickets, int ticketsCount, CardState privateCardState, PlayerId currentPlayerId,
                      Map<PlayerId, PlayerState> privatePlayerState, PlayerId lastPlayer) {

        super(ticketsCount, privateCardState, currentPlayerId, new TreeMap<>(privatePlayerState), lastPlayer);

        Preconditions.checkArgument(allTickets.size() >= 0);
        Preconditions.checkArgument(allTickets.size() == ticketsCount);

        this.allTickets = allTickets;
        this.ticketsCount = ticketsCount;
        this.privateCardState = privateCardState;
        this.currentPlayerId = currentPlayerId;
        this.lastPlayer = lastPlayer;
        this.privatePlayerState = new TreeMap<>(privatePlayerState);
    }

    /**
     * Returns the initial state of the game tChu. This method is to be used whenever a new game of tChu is going to start.
     * The state returned is randomly generated everytime.
     *
     * @param tickets the tickets that the game's ticket deck will start with
     * @param rng     the random generator that will guarantee this game's random start every time
     * @return an initial game state of tChu
     */
    public static GameState initial(SortedBag<Ticket> tickets, Random rng, int numberOfPlayers) {

        Deck<Card> deckCards = Deck.of(Constants.ALL_CARDS, rng);
        Deck<Ticket> ticketDeck = Deck.of(tickets, rng);

        SortedBag.Builder<Card> builderPlayer1 = new SortedBag.Builder<>();
        builderPlayer1.add(deckCards.topCards(Constants.INITIAL_CARDS_COUNT));
        deckCards = deckCards.withoutTopCards(Constants.INITIAL_CARDS_COUNT);

        SortedBag.Builder<Card> builderPlayer2 = new SortedBag.Builder<>();
        builderPlayer2.add(deckCards.topCards(Constants.INITIAL_CARDS_COUNT));
        deckCards = deckCards.withoutTopCards(Constants.INITIAL_CARDS_COUNT);

        SortedBag.Builder<Card> builderPlayer3 = new SortedBag.Builder<>();

        if (numberOfPlayers == 3) {
            builderPlayer3.add(deckCards.topCards(Constants.INITIAL_CARDS_COUNT));
            deckCards = deckCards.withoutTopCards(Constants.INITIAL_CARDS_COUNT);
        }

        CardState publicCardState = CardState.of(deckCards);
        PlayerId currentPlayerId = PlayerId.ALL.get(rng.nextInt(2));

        Map<PlayerId, PlayerState> privatePlayerState = new TreeMap<>();

        privatePlayerState.putIfAbsent(PlayerId.PLAYER_1, PlayerState.initial(builderPlayer1.build()));
        privatePlayerState.putIfAbsent(PlayerId.PLAYER_2, PlayerState.initial(builderPlayer2.build()));
        if (numberOfPlayers == 3)
            privatePlayerState.putIfAbsent(PlayerId.PLAYER_3, PlayerState.initial(builderPlayer3.build()));

        return new GameState(ticketDeck, tickets.size(), publicCardState, currentPlayerId, privatePlayerState, null);
    }


    /**
     * Returns the count number of tickets from the top of the tickets deck. Throws an exception if the tickets deck is
     * empty, or if you are trying to return more tickets than there actually are
     *
     * @param count the amount of tickets you want to return from the top of the ticket deck
     * @return a sorted bag of the count amount of tickets from the top of the deck
     */
    public SortedBag<Ticket> topTickets(int count) {
        Preconditions.checkArgument(count >= 0 && count <= ticketsCount);
        return allTickets.topCards(count);
    }

    /**
     * Returns a game state similar to the previous one, but without the count tickets from the top of the ticket deck
     *
     * @param count the amount of tickets that should be removed from the game state
     * @return a new game state without the count amount of tickets from the top of the deck
     */
    public GameState withoutTopTickets(int count) {
        Preconditions.checkArgument(count >= 0 && count <= ticketsCount);
        return new GameState(allTickets.withoutTopCards(count), ticketsCount - count, privateCardState, currentPlayerId,
                privatePlayerState, lastPlayer);
    }

    /**
     * Returns the card at the top of deck of cards
     *
     * @return the card at the top of the deck of cards
     */
    public Card topCard() {
        Preconditions.checkArgument(privateCardState.deckSize() > 0);
        return privateCardState.topDeckCard();
    }

    /**
     * Returns a game state just like the previous one but without the top card from the deck of cards (it is removed)
     *
     * @return a new game state without the topmost card from the deck of cards
     */
    public GameState withoutTopCard() {
        Preconditions.checkArgument(privateCardState.deckSize() > 0);
        return new GameState(allTickets, ticketsCount, privateCardState.withoutTopDeckCard(), currentPlayerId,
                privatePlayerState, lastPlayer);
    }

    /**
     * Returns a game state similar to the previous one, but with the cards provided as argument added to the discards
     * deck (to the "defausse")
     *
     * @param discardedCards the cards to be discarded
     * @return a game state where the cards provided as parameter were discarded. Everything else remains unchanged
     */
    public GameState withMoreDiscardedCards(SortedBag<Card> discardedCards) {
        return new GameState(allTickets, ticketsCount, privateCardState.withMoreDiscardedCards(discardedCards),
                currentPlayerId, privatePlayerState, lastPlayer);
    }

    /**
     * Returns a similar game state to the previous one if the deck of cards is not empty. Else, it returns a new game state
     * similar to the previous one, but with the deck of cards recreated from the discarded cards
     *
     * @param rng the randomness that will be used to shuffle the deck if it is recreated
     * @return a game state similar to the previous one, with the deck of cards recreated if needed
     */
    public GameState withCardsDeckRecreatedIfNeeded(Random rng) {
        return privateCardState.deckCards.size() == 0
                ? new GameState(allTickets, ticketsCount, privateCardState.withDeckRecreatedFromDiscards(rng), currentPlayerId, privatePlayerState, lastPlayer)
                : new GameState(allTickets, ticketsCount, privateCardState, currentPlayerId, privatePlayerState, lastPlayer);
    }

    /**
     * Returns a game state similar to the previous one, but where the player provided as argument has the tickets given
     * as argument added to his bag of tickets
     *
     * @param playerId      the player to which we will add the tickets he chose
     * @param chosenTickets the tickets chosen by the player
     * @return a game state where the player given has the tickets he chose added to his bag of tickets
     */
    public GameState withInitiallyChosenTickets(PlayerId playerId, SortedBag<Ticket> chosenTickets) {
        Preconditions.checkArgument(privatePlayerState.get(playerId).tickets().size() < 1);
        Map<PlayerId, PlayerState> mapToReturn = new TreeMap<>(privatePlayerState);
        mapToReturn.put(playerId, mapToReturn.get(playerId).withAddedTickets(chosenTickets));
        return new GameState(allTickets, ticketsCount, privateCardState, currentPlayerId, mapToReturn, lastPlayer);
    }

    /**
     * This method returns a game state where the current player is given the tickets he chose and the drawn
     * tickets from the top of the tickets deck are discarded.
     *
     * @param drawnTickets  the tickets drawn from the top of the tickets deck
     * @param chosenTickets the tickets chosen by the player, which will be given to him
     * @return a game state where the player has the tickets he chose, and the rest are discarded
     */
    public GameState withChosenAdditionalTickets(SortedBag<Ticket> drawnTickets, SortedBag<Ticket> chosenTickets) {
        Preconditions.checkArgument(drawnTickets.contains(chosenTickets));
        Deck<Ticket> allTicketsToReturn = allTickets.withoutTopCards(drawnTickets.size());
        Map<PlayerId, PlayerState> mapToReturn = new TreeMap<>(privatePlayerState);
        mapToReturn.put(currentPlayerId, mapToReturn.get(currentPlayerId).withAddedTickets(chosenTickets));
        return new GameState(allTicketsToReturn, ticketsCount - drawnTickets.size(), privateCardState, currentPlayerId, mapToReturn, lastPlayer);
    }

    /**
     * Returns a game state where the face up card at the given index is given to the current player, and is then replaced
     * by the card from the top of the deck of cards
     *
     * @param slot the index of the face up cards which the player chose
     * @return a game state where the current player has the face up card he chose, and where said card is replaced by one from the top of the deck of cards
     */
    public GameState withDrawnFaceUpCard(int slot) {
        Preconditions.checkArgument(canDrawCards());
        Map<PlayerId, PlayerState> mapToReturn = new TreeMap<>(privatePlayerState);
        mapToReturn.put(currentPlayerId, mapToReturn.get(currentPlayerId).withAddedCard(privateCardState.faceUpCard(slot)));
        CardState cardStateToReturn = privateCardState.withDrawnFaceUpCard(slot);
        return new GameState(allTickets, ticketsCount, cardStateToReturn, currentPlayerId, mapToReturn, lastPlayer);
    }

    /**
     * Returns a game state similar to the previous one but where the card from the top of the deck of cards has been
     * taken by the current player
     *
     * @return a game state like the previous one but where the current player has picked the top card from the deck of cards
     */
    public GameState withBlindlyDrawnCard() {
        Preconditions.checkArgument(canDrawCards());
        Map<PlayerId, PlayerState> mapToReturn = new TreeMap<>(privatePlayerState);
        mapToReturn.put(currentPlayerId, mapToReturn.get(currentPlayerId).withAddedCard(privateCardState.topDeckCard()));
        CardState cardStateToReturn = privateCardState.withoutTopDeckCard();
        return new GameState(allTickets, ticketsCount, cardStateToReturn, currentPlayerId, mapToReturn, lastPlayer);
    }

    /**
     * This method returns a game state similar to the previous one but where the current player has claimed the route given
     * as a parameter using the cards also provided as parameter.
     *
     * @param route the route claimed by the current player
     * @param cards the cards used to claim the route by the current player
     * @return a game state where the current player has claimed the route given using the cards provided
     */
    public GameState withClaimedRoute(Route route, SortedBag<Card> cards) {
        Map<PlayerId, PlayerState> mapToReturn = new TreeMap<>(privatePlayerState);
        PlayerState playerToReturn = mapToReturn.get(currentPlayerId).withClaimedRoute(route, cards);
        mapToReturn.put(currentPlayerId, playerToReturn);
        return new GameState(allTickets, ticketsCount, privateCardState.withMoreDiscardedCards(cards), currentPlayerId, mapToReturn, lastPlayer);
    }

    /**
     * Returns true if the current player has 2 wagon cards or less (and the last player isn't defined yet). Returns
     * false otherwise
     *
     * @return true if the next turn will be the last turn of the game
     */
    public boolean lastTurnBegins() {
        return privatePlayerState.get(currentPlayerId).carCount() <= 2 && lastPlayer == null;
    }

    /**
     * This method ends the turn of the current player, chooses the next player, and if the next turn is the final one,
     * also defines the last player (which will be the current player at the time of the call to this method)
     *
     * @return a game state with the current player updated as the next player
     */

    public GameState forNextTurn() {
        PlayerId playerIdToReturn;
        if(privatePlayerState.size() == 2)
             playerIdToReturn = currentPlayerId.next();
        else
            playerIdToReturn = currentPlayerId.next3Players();
        return lastTurnBegins()
                ? new GameState(allTickets, ticketsCount, privateCardState, playerIdToReturn, privatePlayerState, playerIdToReturn) //playerIdToReturn.next();
                : new GameState(allTickets, ticketsCount, privateCardState, playerIdToReturn, privatePlayerState, lastPlayer);
    }

    /**
     * Overrides the inherited method to return the complete player state
     *
     * @param playerId the player whose public state you want to return
     * @return the PlayerState of the player with the corresponding ID
     */

    @Override
    public PlayerState playerState(PlayerId playerId) {
        return privatePlayerState.get(playerId);
    }

    /**
     * Overrides the inherited method to return the complete player state
     *
     * @return the PlayerState of the current player
     */

    @Override
    public PlayerState currentPlayerState() {
        return privatePlayerState.get(currentPlayerId);
    }

}
