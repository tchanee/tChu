package ch.epfl.tchu.gui;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.StringConverter;

import java.util.List;
import java.util.Map;

import static ch.epfl.tchu.gui.MapViewCreator.*;
import static ch.epfl.tchu.gui.InfoViewCreator.*;
import static ch.epfl.tchu.gui.DecksViewCreator.*;
import static ch.epfl.tchu.gui.ActionHandlers.*;


import static javafx.application.Platform.isFxApplicationThread;

/**
 * This class represents the graphical interface of a tChu player
 *
 * @author Marvin Chedid (302446)
 * @author Johnny Borkhoche (296169)
 */

public class GraphicalPlayer {

    private final ObservableGameState ogs;
    private final Node mapView;
    private final Node cardsView;
    private final Node handView;
    private final Node infoView;

    private final ObjectProperty<ClaimRouteHandler> claimRoute;
    private final ObjectProperty<DrawTicketsHandler> drawTickets;
    private final ObjectProperty<DrawCardHandler> drawCard;
    private final ObservableList<Text> infos;

    private final Stage mainStage;

    /**
     * The class's public constructor. It constructs the graphical interface
     *
     * @param thisPlayer the player id of the player who owns this graphical interface
     * @param players    a map of the players' names
     */
    public GraphicalPlayer(PlayerId thisPlayer, Map<PlayerId, String> players) {

        if(players.size() == 3)
            this.ogs = new ObservableGameState3Players(thisPlayer);
        else
            this.ogs = new ObservableGameStateOriginal(thisPlayer);

        claimRoute = new SimpleObjectProperty<>(GraphicalPlayer::claimRoute);
        drawTickets = new SimpleObjectProperty<>(GraphicalPlayer::drawTickets);
        drawCard = new SimpleObjectProperty<>(GraphicalPlayer::drawCard);
        infos = FXCollections.observableArrayList();

        claimRoute.set(null);
        drawTickets.set(null);
        drawCard.set(null);

        CardChooser cardChooser = this::chooseClaimCards;

        mapView = createMapView(ogs, claimRoute, cardChooser);
        cardsView = createCardsView(ogs, drawTickets, drawCard);
        handView = createHandView(ogs);
        infoView = createInfoView(thisPlayer, players, ogs, infos);

        BorderPane pane = new BorderPane(mapView, null, cardsView, handView, infoView);

        this.mainStage = new Stage();
        mainStage.titleProperty().setValue("tChu \u2014 " + players.get(thisPlayer));
        mainStage.setScene(new Scene(pane));
        mainStage.show();

    }

    /**
     * This method calls the observable game state's method with the same name.
     *
     * @param newGameState   the new game state that we wish to update
     * @param newPlayerState the new player state that we wish to update
     */
    public void setState(PublicGameState newGameState, PlayerState newPlayerState) {
        ogs.setState(newGameState, newPlayerState);
    }

    /**
     * This method takes a message and adds it to the bottom of the list of messages in the info view
     *
     * @param message the new message to add
     */
    public void receiveInfo(String message) {
        assert isFxApplicationThread();
        if (infos.size() >= 5)
            infos.remove(0, infos.size() - 4);
        infos.add(new Text(message + '\n'));
    }

    /**
     * This method takes care of actually allowing the player to do an "action", either drawing a card, ticket or
     * claiming a route
     *
     * @param cardHandler       the handler that will handle the player's card drawing
     * @param ticketsHandler    the handler that will handle the player's ticket drawing
     * @param claimRouteHandler the handler that will handle the player's route claiming
     */
    public void startTurn(DrawCardHandler cardHandler, DrawTicketsHandler ticketsHandler, ClaimRouteHandler claimRouteHandler) {
        assert isFxApplicationThread();

        claimRoute.set((r, c) -> {
            claimRouteHandler.onClaimRoute(r, c);
            drawTickets.set(null);
            drawCard.set(null);
            claimRoute.set(null);
        });

        if (ogs.canDrawTickets())
            drawTickets.set(() -> {
                ticketsHandler.onDrawTickets();
                drawTickets.set(null);
                drawCard.set(null);
                claimRoute.set(null);
            });

        if (ogs.canDrawCards())
            drawCard.set((i) -> {
                cardHandler.onDrawCard(i);
                drawTickets.set(null);
                drawCard.set(null);
                claimRoute.set(null);
            });

    }

    /**
     * This method opens up a modal dialog box which allows the player to choose which tickets he wants from the
     * provided set of tickets
     *
     * @param tickets        the set of tickets that the player can choose from
     * @param ticketsHandler the handler that handles the player's ticket choosing action
     */
    public void chooseTickets(SortedBag<Ticket> tickets, ChooseTicketsHandler ticketsHandler) {
        assert isFxApplicationThread();

        //Ticket Stage
        Stage thisStage = new Stage(StageStyle.UTILITY);
        thisStage.titleProperty().setValue(StringsFr.TICKETS_CHOICE);
        thisStage.initOwner(mainStage);
        thisStage.initModality(Modality.WINDOW_MODAL);
        thisStage.setOnCloseRequest(Event::consume);

        //VBox
        VBox vbox = new VBox();

        //Textflow
        TextFlow textFlow = new TextFlow();
        Text t = new Text();
        boolean plural = tickets.size() == 5;
        t.textProperty().set(String.format(StringsFr.CHOOSE_TICKETS, plural ? "3" : "1", plural ? "s" : ""));
        textFlow.getChildren().add(t);

        //ListView
        ObservableList<Ticket> ticketList = new SimpleListProperty<>(FXCollections.observableArrayList());
        ticketList.addAll(tickets.toList());
        ListView<Ticket> listView = new ListView<Ticket>(ticketList);
        listView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        //Button
        Button button = new Button();
        button.textProperty().set("Choisir");
        button.disableProperty().bind(Bindings.size(listView.getSelectionModel().getSelectedItems()).lessThan((tickets.size() - 2)));
        button.setOnAction(e -> {
            thisStage.hide();
            ticketsHandler.onChooseTickets(SortedBag.of(listView.getSelectionModel().getSelectedItems()));
        });

        //Adding the children
        vbox.getChildren().add(textFlow);
        vbox.getChildren().add(listView);
        vbox.getChildren().add(button);

        //Ticket scene
        Scene thisScene = new Scene(vbox);
        thisScene.getStylesheets().add("chooser.css");

        thisStage.setScene(thisScene);
        thisStage.show();
    }

    /**
     * This method creates a modal dialog box so that the player can choose the set of cards he wishes to use to
     * claim the route
     *
     * @param cards        the list of sets of cards that the player may choose from
     * @param cardsHandler the handler that will handle the player picking a specific set
     */
    public void chooseClaimCards(List<SortedBag<Card>> cards, ChooseCardsHandler cardsHandler) {
        assert isFxApplicationThread();

        //Card Stage
        Stage thisStage = new Stage(StageStyle.UTILITY);
        thisStage.titleProperty().setValue(StringsFr.CARDS_CHOICE);
        thisStage.initOwner(mainStage);
        thisStage.initModality(Modality.WINDOW_MODAL);
        thisStage.setOnCloseRequest(Event::consume);

        //VBox
        VBox vbox = new VBox();

        //Textflow
        TextFlow textFlow = new TextFlow();
        Text t = new Text();
        t.textProperty().set(StringsFr.CHOOSE_CARDS);
        textFlow.getChildren().add(t);

        //ListView
        ObservableList<SortedBag<Card>> cardsList = new SimpleListProperty<>(FXCollections.observableArrayList());
        cardsList.addAll(cards);
        ListView<SortedBag<Card>> listView = new ListView<SortedBag<Card>>(cardsList);
        listView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        listView.setCellFactory(v ->
                new TextFieldListCell<>(new CardBagStringConverter()));

        //Button
        Button button = new Button();
        button.textProperty().set("Choisir");
        button.disableProperty().bind(Bindings.size(listView.getSelectionModel().getSelectedItems()).isNotEqualTo(1));
        button.setOnAction(e -> {
            thisStage.hide();
            cardsHandler.onChooseCards(listView.getSelectionModel().getSelectedItem());
        });

        vbox.getChildren().add(textFlow);
        vbox.getChildren().add(listView);
        vbox.getChildren().add(button);

        //Card scene
        Scene thisScene = new Scene(vbox);
        thisScene.getStylesheets().add("chooser.css");

        thisStage.setScene(thisScene);
        thisStage.show();

    }

    /**
     * This method is only called when the player has already drawn 1 card, either from the face up cards or from the
     * card deck. This entails that the player definitely has to pick 1 more card.
     *
     * @param cardHandler the handler that will handle the player's drawing the 2nd card
     */
    public void drawCard(DrawCardHandler cardHandler) {
        assert isFxApplicationThread();
        drawCard.set((i) -> {
            cardHandler.onDrawCard(i);
            drawTickets.set(null);
            drawCard.set(null);
            claimRoute.set(null);
        });
    }

    /**
     * This method creates a modal dialog box so that the player can choose which additional cards he wishes to use
     * to claim the tunnel
     *
     * @param cards        a list of the sets of cards that the player can choose from
     * @param cardsHandler the handler that handles the player's card-set picking action
     */
    public void chooseAdditionalCards(List<SortedBag<Card>> cards, ChooseCardsHandler cardsHandler) {
        assert isFxApplicationThread();

        //Card Stage
        Stage thisStage = new Stage(StageStyle.UTILITY);
        thisStage.titleProperty().setValue(StringsFr.CARDS_CHOICE);
        thisStage.initOwner(mainStage);
        thisStage.initModality(Modality.WINDOW_MODAL);
        thisStage.setOnCloseRequest(Event::consume);

        //VBox
        VBox vbox = new VBox();

        //Textflow
        TextFlow textFlow = new TextFlow();
        Text t = new Text();
        t.textProperty().set(StringsFr.CHOOSE_ADDITIONAL_CARDS);
        textFlow.getChildren().add(t);

        //ListView
        ObservableList<SortedBag<Card>> cardsList = new SimpleListProperty<>(FXCollections.observableArrayList());
        cardsList.addAll(cards);
        ListView<SortedBag<Card>> listView = new ListView<SortedBag<Card>>(cardsList);
        listView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        listView.setCellFactory(v ->
                new TextFieldListCell<>(new CardBagStringConverter()));

        //Button
        Button button = new Button();
        button.textProperty().set("Choisir");
        button.setOnAction(e -> {
            thisStage.hide();
            cardsHandler.onChooseCards(listView.getSelectionModel().getSelectedItem());
        });

        vbox.getChildren().add(textFlow);
        vbox.getChildren().add(listView);
        vbox.getChildren().add(button);

        //Ticket scene
        Scene thisScene = new Scene(vbox);
        thisScene.getStylesheets().add("chooser.css");

        thisStage.setScene(thisScene);
        thisStage.show();

    }

    private static void claimRoute(Route route, SortedBag<Card> cards) {
        System.out.printf("Prise de possession d'une route : %s - %s %s%n", route.station1(), route.station2(), cards);
    }

    private static void drawTickets() {
        System.out.println("Tirage de billets !");
    }

    private static void drawCard(int slot) {
        System.out.printf("Tirage de cartes (emplacement %s)!\n", slot);
    }

    /**
     * A new class defined here which is used when setting the Cell Factory of the list view of the cards
     */
    public static class CardBagStringConverter extends StringConverter<SortedBag<Card>> {

        private CardBagStringConverter() {
        }

        /**
         * This method takes care of formatting the set of cards given and outputting them as a string which follows
         * a specific format
         *
         * @param cards the cards that we wish to represent as a string
         * @return a string constituted from the cards passed as argument
         */
        @Override
        public String toString(SortedBag<Card> cards) {
            StringBuilder result = new StringBuilder();
            int counter = 0;
            for (Card c : cards.toSet()) {
                int count = cards.countOf(c);
                if (count > 0 && counter >= 1) {
                    result.append(StringsFr.AND_SEPARATOR).append(count).append(" ").append(Info.cardName(c, count));
                } else if (count > 0) {
                    result.append(count).append(" ").append(Info.cardName(c, count));
                    counter += 1;
                }
            }
            return result.toString();
        }

        @Override
        public SortedBag<Card> fromString(String string) {
            throw new UnsupportedOperationException();
        }
    }
}
