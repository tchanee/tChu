package ch.epfl.tchu.gui;

import ch.epfl.tchu.game.Card;
import ch.epfl.tchu.game.Color;
import ch.epfl.tchu.game.Ticket;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.collections.ObservableList;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

import java.util.ArrayList;
import java.util.List;


/**
 * Package private class that contains the methods to create both the hand view and the cards view
 *
 * @author Marvin Chedid (302446)
 * @author Johnny Borkhoche (296169)
 */

class DecksViewCreator {

    /**
     * This method takes care of creating the player's hand view (i.e the tickets and cards currently in his hand)
     *
     * @param ogs the observable game state that belongs to the player whose hand view this method creates
     * @return a node that contains the tickets and cards currently in the player's hands
     */
    public static Node createHandView(ObservableGameState ogs) {

        //Main hbox
        HBox hbox = new HBox();
        hbox.getStylesheets().add("decks.css");
        hbox.getStylesheets().add("colors.css");

        //Text box
        ObservableList<Ticket> ticketObservableList = ogs.getPlayerTickets();
        ListView<Ticket> tickets = new ListView<>(ticketObservableList);
        tickets.setId("tickets");

        hbox.getChildren().add(tickets);

        //Sub hbox
        HBox minihbox = new HBox();
        minihbox.setId("hand-pane");

        //Card Stack
        List<String> colorsAppended = new ArrayList<>();
        Color.ALL.forEach(card -> colorsAppended.add(card.name()));
        colorsAppended.add("NEUTRAL");
        for (String color : colorsAppended) {
            StackPane pane = new StackPane();
            pane.getStyleClass().add(color);
            pane.getStyleClass().add("card");

            //Text
            Text counter = new Text();
            counter.getStyleClass().add("count");

            Rectangle r1 = new Rectangle(60, 90);
            r1.getStyleClass().add("outside");
            Rectangle r2 = new Rectangle(40, 70);
            r2.getStyleClass().add("filled");
            r2.getStyleClass().add("inside");
            Rectangle r3 = new Rectangle(40, 70);
            r3.getStyleClass().add("train-image");

            pane.getChildren().add(r1);
            pane.getChildren().add(r2);
            pane.getChildren().add(r3);

            pane.getChildren().add(counter);

            ReadOnlyIntegerProperty count = ogs.getPlayerCardCount(Card.of(Color.of(color)));
            pane.visibleProperty().bind(Bindings.greaterThan(count, 0));
            counter.textProperty().bind(Bindings.convert(count));
            counter.visibleProperty().bind(Bindings.greaterThan(count, 1));
            minihbox.getChildren().add(pane);
        }

        hbox.getChildren().add(minihbox);

        return hbox;
    }

    /**
     * This method takes care of creating the player's view of the common ticket deck, face up cards and card deck
     *
     * @param ogs                              the observable game state that belongs to the player whose card view this method creates
     * @param drawTicketsHandlerObjectProperty the property that contains the ticket drawing handler
     * @param drawCardHandlerObjectProperty    the property that contains the card drawing handler
     * @return a node that represents the common ticket deck, face up cards and card deck on the floor
     */
    public static Node createCardsView(ObservableGameState ogs,
                                       ObjectProperty<ActionHandlers.DrawTicketsHandler> drawTicketsHandlerObjectProperty,
                                       ObjectProperty<ActionHandlers.DrawCardHandler> drawCardHandlerObjectProperty) {

        VBox vbox = new VBox();
        vbox.getStylesheets().add("decks.css");
        vbox.getStylesheets().add("colors.css");
        vbox.setId("card-pane");

        //Ticket Button
        Button ticketGauge = new Button();
        ticketGauge.textProperty().set("Billets");
        ticketGauge.getStyleClass().add("gauged");

        Group ticketGaugeGroup = new Group();
        Rectangle ticketR1 = new Rectangle(50, 5);
        ticketR1.getStyleClass().add("background");
        Rectangle ticketR2 = new Rectangle(50, 5);
        ticketR2.getStyleClass().add("foreground");

        ticketGaugeGroup.getChildren().add(ticketR1);
        ticketGaugeGroup.getChildren().add(ticketR2);

        //Graphics
        ticketGauge.setGraphic(ticketGaugeGroup);
        //Null if we can't access
        ticketGauge.disableProperty().bind(drawTicketsHandlerObjectProperty.isNull());
        //Listens if it changes
        ticketR2.widthProperty().bind(ogs.ticketsLeftPercentageProperty().multiply(50).divide(100));
        //Event handler
        ticketGauge.setOnMouseClicked(event -> {
            drawTicketsHandlerObjectProperty.get().onDrawTickets();
        });

        //Card Button
        Button cardGauge = new Button();
        cardGauge.textProperty().set("Cartes");
        cardGauge.getStyleClass().add("gauged");
        Group cardGaugeGroup = new Group();

        Rectangle cardR1 = new Rectangle(50, 5);
        cardR1.getStyleClass().add("background");
        Rectangle cardR2 = new Rectangle(50, 5);
        cardR2.getStyleClass().add("foreground");

        cardGaugeGroup.getChildren().add(cardR1);
        cardGaugeGroup.getChildren().add(cardR2);

        //Graphics
        cardGauge.setGraphic(cardGaugeGroup);
        //Null if we can't access
        cardGauge.disableProperty().bind(drawCardHandlerObjectProperty.isNull());
        //Listener
        cardR2.widthProperty().bind(ogs.cardsLeftPercentageProperty().multiply(50).divide(100));
        //Mouse clicked
        cardGauge.setOnMouseClicked(event -> {
            drawCardHandlerObjectProperty.get().onDrawCard(-1);
        });

        vbox.getChildren().add(ticketGauge);

        //Card Stack
        List<ObjectProperty<Card>> faceUpCards = ogs.getFaceUpCards();
        for (int i = 0; i < faceUpCards.size(); ++i) {
            Card cardUnboxed = faceUpCards.get(i).get();
            StackPane pane = new StackPane();
            pane.getStyleClass().add("RED");
            pane.getStyleClass().add("card");


            Rectangle r1 = new Rectangle(60, 90);
            r1.getStyleClass().add("outside");
            Rectangle r2 = new Rectangle(40, 70);
            r2.getStyleClass().add("filled");
            r2.getStyleClass().add("inside");
            Rectangle r3 = new Rectangle(40, 70);
            r3.getStyleClass().add("train-image");

            pane.getChildren().add(r1);
            pane.getChildren().add(r2);
            pane.getChildren().add(r3);

            vbox.getChildren().add(pane);

            //Listener
            faceUpCards.get(i).addListener((o, oV, nV) ->
                    pane.getStyleClass().set(0, (o.getValue() != Card.LOCOMOTIVE) ? nV.name() : "NEUTRAL"));

            //Event handler
            int slot = i;
            pane.setOnMouseClicked(event ->
                    drawCardHandlerObjectProperty.get().onDrawCard(slot)
            );
        }

        vbox.getChildren().add(cardGauge);

        return vbox;
    }
}
