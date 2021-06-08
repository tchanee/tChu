package ch.epfl.tchu.gui;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.Card;
import ch.epfl.tchu.game.ChMap;
import ch.epfl.tchu.game.PlayerId;
import ch.epfl.tchu.game.Route;
import javafx.beans.property.ObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

import java.util.ArrayList;
import java.util.List;

/**
 * Package private class that takes care of creating the map view
 *
 * @author Marvin Chedid (302446)
 * @author Johnny Borkhoche (296169)
 */
public class MapViewCreator {
    public static final List<Group> listNodes = new ArrayList<>();

    /**
     * Functional interface that will be used to choose a set of cards from the available options
     */
    @FunctionalInterface
    interface CardChooser {
        /**
         * Method that will be called when the player has to choose a set of cards from the available options
         *
         * @param options the available options (sets of cards) that the player may choose from
         * @param handler the action handler that will handle the player's action of choosing a set of cards
         */
        void chooseCards(List<SortedBag<Card>> options,
                         ActionHandlers.ChooseCardsHandler handler);
    }

    /**
     * The only method in its class, it is called when creating the map of the game in the graphical interface
     *
     * @param ogs         the observable game state
     * @param property    the property containing the ClaimRouteHandler action handler
     * @param cardChooser the cardChooser that will be used when cards have to be chosen by the player
     * @return a node which corresponds to the map view, which includes the image, the routes and so on
     */
    public static Node createMapView(ObservableGameState ogs, ObjectProperty<ActionHandlers.ClaimRouteHandler> property, CardChooser cardChooser) {

        Pane grid = new Pane();
        grid.getStylesheets().add("map.css");
        grid.getStylesheets().add("colors.css");

        //Background: map
        Image mapImage = new Image("map.png");
        ImageView map = new ImageView();
        map.setImage(mapImage);
        grid.getChildren().add(map);

        //Drawing
        for (Route route : ChMap.routes()) {

            Group thisRoute = new Group();
            thisRoute.setId(route.id());
            thisRoute.getStyleClass().add("route");
            thisRoute.getStyleClass().add(route.color() == null ? "NEUTRAL" : route.color().name());
            thisRoute.getStyleClass().add(route.level().name());

            for (int i = 1; i <= route.length(); ++i) {
                Group thisCell = new Group();
                thisCell.setId(route.id() + "_" + i);

                Rectangle r = new Rectangle(36, 12);
                r.getStyleClass().add("track");
                r.getStyleClass().add("filled");

                Group wagon = new Group();
                wagon.getStyleClass().add("car");

                Rectangle r1 = new Rectangle(36, 12);
                r1.getStyleClass().add("filled");

                Circle c1 = new Circle(3);
                c1.setCenterX(12);
                c1.setCenterY(6);

                Circle c2 = new Circle(3);
                c2.setCenterX(24);
                c2.setCenterY(6);

                wagon.getChildren().add(r1);
                wagon.getChildren().add(c1);
                wagon.getChildren().add(c2);

                thisCell.getChildren().add(r);
                thisCell.getChildren().add(wagon);


                thisRoute.getChildren().add(thisCell);
                listNodes.add(thisCell);
            }

            //Listener
            ogs.getRouteOwnershipProperty(route).addListener(new ChangeListener<PlayerId>() {
                @Override
                public void changed(ObservableValue<? extends PlayerId> observable, PlayerId oldValue, PlayerId newValue) {
                    if (oldValue == null && newValue != null)
                        thisRoute.getStyleClass().add(newValue.name());
                }
            });

            //Disable property
            thisRoute.disableProperty().bind(property.isNull().or(ogs.getPlayerCanClaimRouteProperty(route).not()));


            //Mouse clicked event
            thisRoute.setOnMouseClicked(event -> {
                List<SortedBag<Card>> possibleClaimCards = ogs.possibleClaimCards(route);
                ActionHandlers.ClaimRouteHandler claimRouteH = property.get();
                ActionHandlers.ChooseCardsHandler chooseCardsH = chosenCards -> claimRouteH.onClaimRoute(route, chosenCards);
                cardChooser.chooseCards(possibleClaimCards, chooseCardsH);

            });
            grid.getChildren().add(thisRoute);
        }

        return grid;
    }

}
