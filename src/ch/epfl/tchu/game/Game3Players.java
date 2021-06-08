package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.gui.Info;

import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * This class represents a game of tChu. It takes care of "playing" the game correctly from start to end.
 *
 * @author Marvin Chedid (302446)
 * @author Johnny Borkhoche (296169)
 */
public final class Game3Players {

    private Game3Players() {
    }

    /**
     * This method facilitates the running of a game of tChu. It essentially "plays" the game's steps, as its name intends.
     *
     * @param players     a map of 2 players, associating each player Id with its player
     * @param playerNames a map of 2 players, which associates each player Id to a name
     * @param tickets     a sorted bag of tickets
     * @param rng         the random variable that will be used to create the initial state of the game, shuffle the cards, create decks when needed..etc
     */
    public static void play(Map<PlayerId, Player> players, Map<PlayerId, String> playerNames,
                            SortedBag<Ticket> tickets, Random rng) {

        //Creating initial game state
        GameState gameTurn = GameState.initial(tickets, rng, 3);

        Preconditions.checkArgument(players.size() == PlayerId.COUNT);
        Preconditions.checkArgument(playerNames.size() == PlayerId.COUNT);

        Info player1 = new Info(playerNames.get(PlayerId.PLAYER_1));
        Info player2 = new Info(playerNames.get(PlayerId.PLAYER_2));
        Info player3 = new Info(playerNames.get(PlayerId.PLAYER_3));

        //Initializing players
        players.get(PlayerId.PLAYER_1).initPlayers(PlayerId.PLAYER_1, playerNames);
        players.get(PlayerId.PLAYER_1).updateState(gameTurn, gameTurn.playerState(PlayerId.PLAYER_1));

        players.get(PlayerId.PLAYER_2).initPlayers(PlayerId.PLAYER_2, playerNames);
        players.get(PlayerId.PLAYER_2).updateState(gameTurn, gameTurn.playerState(PlayerId.PLAYER_2));

        players.get(PlayerId.PLAYER_3).initPlayers(PlayerId.PLAYER_3, playerNames);
        players.get(PlayerId.PLAYER_3).updateState(gameTurn, gameTurn.playerState(PlayerId.PLAYER_3));


        //Informing the players of who is starting
        if (gameTurn.currentPlayerId() == PlayerId.PLAYER_1)
            sendInfoToPlayers(players, player1.willPlayFirst());
        else if (gameTurn.currentPlayerId() == PlayerId.PLAYER_2)
            sendInfoToPlayers(players, player2.willPlayFirst());
        else
            sendInfoToPlayers(players, player3.willPlayFirst());


        //Players choose their initial tickets
        players.get(PlayerId.PLAYER_1).setInitialTicketChoice(gameTurn.topTickets(Constants.INITIAL_TICKETS_COUNT));
        gameTurn = gameTurn.withoutTopTickets(Constants.INITIAL_TICKETS_COUNT);

        players.get(PlayerId.PLAYER_2).setInitialTicketChoice(gameTurn.topTickets(Constants.INITIAL_TICKETS_COUNT));
        gameTurn = gameTurn.withoutTopTickets(Constants.INITIAL_TICKETS_COUNT);

        players.get(PlayerId.PLAYER_3).setInitialTicketChoice(gameTurn.topTickets(Constants.INITIAL_TICKETS_COUNT));
        gameTurn = gameTurn.withoutTopTickets(Constants.INITIAL_TICKETS_COUNT);


        informPlayersOfGameState(players, gameTurn);

        gameTurn = gameTurn.withInitiallyChosenTickets(PlayerId.PLAYER_1,
                players.get(PlayerId.PLAYER_1).chooseInitialTickets());
        gameTurn = gameTurn.withInitiallyChosenTickets(PlayerId.PLAYER_2,
                players.get(PlayerId.PLAYER_2).chooseInitialTickets());
        gameTurn = gameTurn.withInitiallyChosenTickets(PlayerId.PLAYER_3,
                players.get(PlayerId.PLAYER_3).chooseInitialTickets());


        sendInfoToPlayers(players, player1.keptTickets(gameTurn.playerState(PlayerId.PLAYER_1).ticketCount()));
        sendInfoToPlayers(players, player2.keptTickets(gameTurn.playerState(PlayerId.PLAYER_2).ticketCount()));
        sendInfoToPlayers(players, player3.keptTickets(gameTurn.playerState(PlayerId.PLAYER_3).ticketCount()));

        //Game starts
        do {

            Player currentPlayer = players.get(gameTurn.currentPlayerId());
            PlayerState currentPlayerState = gameTurn.currentPlayerState();
            Info currentPlayerInfo = (gameTurn.currentPlayerId() == PlayerId.PLAYER_1) ? player1
                    : (gameTurn.currentPlayerId() == PlayerId.PLAYER_2) ? player2
                    : player3;
            sendInfoToPlayers(players, currentPlayerInfo.canPlay());
            informPlayersOfGameState(players, gameTurn);
            Player.TurnKind turn = currentPlayer.nextTurn();

            switch (turn) {
                case DRAW_TICKETS:
                    sendInfoToPlayers(players, currentPlayerInfo.drewTickets(Constants.IN_GAME_TICKETS_COUNT));
                    SortedBag<Ticket> ticketsChosen = currentPlayer.chooseTickets(
                            gameTurn.topTickets(Constants.IN_GAME_TICKETS_COUNT));
                    gameTurn = gameTurn.withChosenAdditionalTickets(gameTurn.topTickets(
                            Constants.IN_GAME_TICKETS_COUNT), ticketsChosen);
                    sendInfoToPlayers(players, currentPlayerInfo.keptTickets(ticketsChosen.size()));
                    informPlayersOfGameState(players, gameTurn);
                    break;
                case DRAW_CARDS:
                    for (int i = 0; i < 2; ++i)
                        if (gameTurn.canDrawCards()) {
                            if (i == 1) informPlayersOfGameState(players, gameTurn);
                            int slot = currentPlayer.drawSlot();
                            switch (slot) {
                                case Constants.DECK_SLOT:
                                    gameTurn = gameTurn.withCardsDeckRecreatedIfNeeded(rng);
                                    gameTurn = gameTurn.withBlindlyDrawnCard();
                                    sendInfoToPlayers(players, currentPlayerInfo.drewBlindCard());
                                    break;
                                case 0:
                                case 1:
                                case 2:
                                case 3:
                                case 4:
                                    gameTurn = gameTurn.withCardsDeckRecreatedIfNeeded(rng);
                                    Card faceUpCardDrawn = gameTurn.cardState().faceUpCard(slot);
                                    gameTurn = gameTurn.withDrawnFaceUpCard(slot);
                                    sendInfoToPlayers(players, currentPlayerInfo.drewVisibleCard(faceUpCardDrawn));
                                    break;
                            }
                        } else
                            gameTurn = gameTurn.withCardsDeckRecreatedIfNeeded(rng);
                    informPlayersOfGameState(players, gameTurn);
                    break;
                case CLAIM_ROUTE:
                    Route routeToClaim = currentPlayer.claimedRoute();
                    if (gameTurn.currentPlayerState().canClaimRoute(routeToClaim))
                        if (routeToClaim.level() == Route.Level.OVERGROUND) {
                            SortedBag<Card> initialClaimedCards = currentPlayer.initialClaimCards();
                            gameTurn = gameTurn.withClaimedRoute(routeToClaim, initialClaimedCards);
                            sendInfoToPlayers(players, currentPlayerInfo.claimedRoute(routeToClaim,
                                    initialClaimedCards));
                        } else if (routeToClaim.level() == Route.Level.UNDERGROUND) {

                            SortedBag.Builder<Card> builder = new SortedBag.Builder<>();

                            for (int i = 0; i < Constants.ADDITIONAL_TUNNEL_CARDS; ++i) {
                                gameTurn = gameTurn.withCardsDeckRecreatedIfNeeded(rng);
                                builder.add(gameTurn.topCard());
                                gameTurn = gameTurn.withoutTopCard();
                            }

                            SortedBag<Card> drawnCards = builder.build();
                            SortedBag<Card> initialClaimedCards = currentPlayer.initialClaimCards();

                            sendInfoToPlayers(players, currentPlayerInfo.attemptsTunnelClaim(routeToClaim,
                                    initialClaimedCards));

                            int nbCards = routeToClaim.additionalClaimCardsCount(initialClaimedCards, drawnCards);

                            sendInfoToPlayers(players, currentPlayerInfo.drewAdditionalCards(drawnCards, nbCards));

                            SortedBag<Card> chosenCards = SortedBag.of();

                            if (nbCards >= 1) {
                                List<SortedBag<Card>> cardsToPlay = currentPlayerState.possibleAdditionalCards(nbCards,
                                        initialClaimedCards, drawnCards);
                                chosenCards = (cardsToPlay.isEmpty())
                                        ? SortedBag.of()
                                        : currentPlayer.chooseAdditionalCards(cardsToPlay);
                            }

                            SortedBag.Builder<Card> totalCardsPlayedBuilder = new SortedBag.Builder<>();
                            totalCardsPlayedBuilder.add(initialClaimedCards);
                            totalCardsPlayedBuilder.add(chosenCards);
                            SortedBag<Card> totalCardsPlayed = totalCardsPlayedBuilder.build();

                            if (chosenCards.size() != 0 || nbCards == 0) {
                                gameTurn = gameTurn.withClaimedRoute(routeToClaim, totalCardsPlayed);
                                sendInfoToPlayers(players, currentPlayerInfo.claimedRoute(routeToClaim,
                                        totalCardsPlayed));
                            } else
                                sendInfoToPlayers(players, currentPlayerInfo.didNotClaimRoute(routeToClaim));

                            gameTurn = gameTurn.withMoreDiscardedCards(drawnCards);
                        }
                    informPlayersOfGameState(players, gameTurn);
                    break;
                default:
                    break;
            }

            if (gameTurn.currentPlayerId().equals(gameTurn.lastPlayer()))
                break;

            if (gameTurn.lastTurnBegins()) {
                sendInfoToPlayers(players, currentPlayerInfo.lastTurnBegins(gameTurn.currentPlayerState().carCount()));
            }

            gameTurn = gameTurn.forNextTurn();

        } while (true);
        //End of game

        //Final counting
        informPlayersOfGameState(players, gameTurn);

        int pointsPlayer1 = gameTurn.playerState(PlayerId.PLAYER_1).finalPoints();
        int pointsPlayer2 = gameTurn.playerState(PlayerId.PLAYER_2).finalPoints();
        int pointsPlayer3 = gameTurn.playerState(PlayerId.PLAYER_3).finalPoints();

        Trail longestPlayer1 = Trail.longest(gameTurn.playerState(PlayerId.PLAYER_1).routes());
        Trail longestPlayer2 = Trail.longest(gameTurn.playerState(PlayerId.PLAYER_2).routes());
        Trail longestPlayer3 = Trail.longest(gameTurn.playerState(PlayerId.PLAYER_3).routes());

        if (longestPlayer1.length() > longestPlayer2.length() && longestPlayer1.length() > longestPlayer3.length()) {
            sendInfoToPlayers(players, player1.getsLongestTrailBonus(longestPlayer1));
            pointsPlayer1 += Constants.LONGEST_TRAIL_BONUS_POINTS;
        } else if (longestPlayer1.length() < longestPlayer2.length() && longestPlayer2.length() > longestPlayer3.length()) {
            sendInfoToPlayers(players, player2.getsLongestTrailBonus(longestPlayer2));
            pointsPlayer2 += Constants.LONGEST_TRAIL_BONUS_POINTS;
        } else if (longestPlayer1.length() < longestPlayer3.length() && longestPlayer2.length() < longestPlayer3.length()) {
            sendInfoToPlayers(players, player3.getsLongestTrailBonus(longestPlayer3));
            pointsPlayer3 += Constants.LONGEST_TRAIL_BONUS_POINTS;
        }
        else{
            pointsPlayer1 += Constants.LONGEST_TRAIL_BONUS_POINTS;
            pointsPlayer2 += Constants.LONGEST_TRAIL_BONUS_POINTS;
            pointsPlayer3 += Constants.LONGEST_TRAIL_BONUS_POINTS;
            sendInfoToPlayers(players, player1.getsLongestTrailBonus(longestPlayer1));
            sendInfoToPlayers(players, player2.getsLongestTrailBonus(longestPlayer2));
            sendInfoToPlayers(players, player3.getsLongestTrailBonus(longestPlayer3));
        }

        if (pointsPlayer1 > pointsPlayer2 && pointsPlayer1 > pointsPlayer3)
            sendInfoToPlayers(players, player1.won(pointsPlayer1, pointsPlayer2));
        else if (pointsPlayer1 < pointsPlayer2 && pointsPlayer2 > pointsPlayer3)
            sendInfoToPlayers(players, player2.won(pointsPlayer2, pointsPlayer1));
        else if (pointsPlayer3 > pointsPlayer1 && pointsPlayer2 < pointsPlayer3)
            sendInfoToPlayers(players, player3.won(pointsPlayer3, pointsPlayer1));
        else
            sendInfoToPlayers(players, Info.draw(List.of(playerNames.get(PlayerId.PLAYER_1),
                    playerNames.get(PlayerId.PLAYER_2),playerNames.get(PlayerId.PLAYER_3)), pointsPlayer1));

    }

    /**
     * Auxiliary method to send information to players
     *
     * @param players a map of the game' players
     * @param info    the info to send to all players
     */
    private static void sendInfoToPlayers(Map<PlayerId, Player> players, String info) {
        players.get(PlayerId.PLAYER_1).receiveInfo(info);
        players.get(PlayerId.PLAYER_2).receiveInfo(info);
        players.get(PlayerId.PLAYER_3).receiveInfo(info);
    }

    /**
     * Auxiliary method to update players of the new game state
     *
     * @param players   the players to inform
     * @param gameState the updated game state
     */
    private static void informPlayersOfGameState(Map<PlayerId, Player> players, GameState gameState) {
        for (PlayerId playerToInform : PlayerId.ALL)
            players.get(playerToInform).updateState(gameState, gameState.playerState(playerToInform));

    }
}
