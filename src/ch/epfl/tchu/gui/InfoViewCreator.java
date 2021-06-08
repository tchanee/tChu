package ch.epfl.tchu.gui;

import ch.epfl.tchu.game.PlayerId;
import javafx.beans.binding.Bindings;
import javafx.collections.ObservableList;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.Separator;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.util.Map;

import static ch.epfl.tchu.game.PlayerId.*;

/**
 * Package private class that contains the method to create the info view (i.e the last 5 happenings in the game)
 *
 * @author Marvin Chedid (302446)
 * @author Johnny Borkhoche (296169)
 */

class InfoViewCreator {

    /**
     * This method takes care of creating the info view of the last 5 information that will be shown to the player
     *
     * @param playerId    the playerId to whom this interface belongs
     * @param playerNames a map of the player names
     * @param ogs         the player's observable game state
     * @param gameInfo    an observable list that contains the last 5 game information
     * @return a node containing the view of the last 5 information that the player should be seeing
     */
    public static Node createInfoView(PlayerId playerId, Map<PlayerId, String> playerNames, ObservableGameState ogs,
                                      ObservableList<Text> gameInfo) {

        VBox vbox = new VBox();
        vbox.getStylesheets().add("info.css");
        vbox.getStylesheets().add("colors.css");

        PlayerId currPlayer = playerId;

        //Player Stats
        for (int i = 0; i < playerNames.size(); i++) {
            VBox playerVBox = new VBox();
            playerVBox.setId("player-stats");

            TextFlow textFlow = new TextFlow();
            textFlow.getStyleClass().add("PLAYER_n");

            Circle c = new Circle(5);
            c.getStyleClass().add("filled");

            if (playerNames.size() == 2) {
                if (playerId == PLAYER_1 && i == 0)
                    c.setFill(Color.LIGHTBLUE);
                else if (playerId == PLAYER_1 && i == 1)
                    c.setFill(Color.PINK);
                else if (playerId == PLAYER_2 && i == 0)
                    c.setFill(Color.PINK);
                else if (playerId == PLAYER_2 && i == 1)
                    c.setFill(Color.LIGHTBLUE);
                if (i == 1) {
                    currPlayer = (currPlayer == PLAYER_1) ? PlayerId.PLAYER_2 : PLAYER_1;
                }
            } else {
                if (playerId == PLAYER_1 && i == 0)
                    c.setFill(Color.LIGHTBLUE);
                else if (playerId == PLAYER_1 && i == 1)
                    c.setFill(Color.PINK);
                else if (playerId == PLAYER_1 && i == 2)
                    c.setFill(Color.LIGHTGREEN);
                else if (playerId == PLAYER_2 && i == 0)
                    c.setFill(Color.PINK);
                else if (playerId == PLAYER_2 && i == 1)
                    c.setFill(Color.LIGHTGREEN);
                else if (playerId == PLAYER_2 && i == 2)
                    c.setFill(Color.LIGHTBLUE);
                else if (playerId == PLAYER_3 && i == 0)
                    c.setFill(Color.LIGHTGREEN);
                else if (playerId == PLAYER_3 && i == 1)
                    c.setFill(Color.LIGHTBLUE);
                else if (playerId == PLAYER_3 && i == 2)
                    c.setFill(Color.PINK);
                if (i >= 1) {
                    currPlayer = (currPlayer == PLAYER_1) ? PlayerId.PLAYER_2 : (currPlayer == PLAYER_2) ? PLAYER_3 : PLAYER_1;
                }
            }

            Text t = new Text();

            if (currPlayer == PLAYER_1)
                t.textProperty().bind(Bindings.format(StringsFr.PLAYER_STATS, playerNames.get(currPlayer),
                        ogs.p1TotalTicketCountProperty(), ogs.p1TotalCardCountProperty(), ogs.p1TotalLocomotiveCountProperty(), ogs.p1TotalConstrPointsProperty()));
            else if (currPlayer == PLAYER_2)
                t.textProperty().bind(Bindings.format(StringsFr.PLAYER_STATS, playerNames.get(currPlayer),
                        ogs.p2TotalTicketCountProperty(), ogs.p2TotalCardCountProperty(), ogs.p2TotalLocomotiveCountProperty(), ogs.p2TotalConstrPointsProperty()));
            else if (currPlayer == PLAYER_3)
                t.textProperty().bind(Bindings.format(StringsFr.PLAYER_STATS, playerNames.get(currPlayer),
                        ogs.p3TotalTicketCountProperty(), ogs.p3TotalCardCountProperty(), ogs.p3TotalLocomotiveCountProperty(), ogs.p3TotalConstrPointsProperty()));

            textFlow.getChildren().add(c);
            textFlow.getChildren().add(t);

            playerVBox.getChildren().add(textFlow);

            vbox.getChildren().add(playerVBox);
        }

        //Separator
        vbox.getChildren().add(new Separator(Orientation.HORIZONTAL));

        //Game info
        TextFlow textFlow = new TextFlow();
        textFlow.setId("game-info");
        Bindings.bindContent(textFlow.getChildren(), gameInfo);

        vbox.getChildren().add(textFlow);

        return vbox;
    }
}
