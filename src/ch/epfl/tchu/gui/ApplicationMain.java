package ch.epfl.tchu.gui;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;
import ch.epfl.tchu.net.RemotePlayerProxy;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.effect.Glow;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import javax.swing.*;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static ch.epfl.tchu.gui.ActionHandlers.*;
import static javafx.application.Platform.isFxApplicationThread;

/**
 * This class contains the interface components of the game tchu.
 *
 * @author Marvin Chedid (302446)
 * @author Johnny Borkhoche (296169)
 */

public class ApplicationMain {


    private final List<String> modes = List.of("2 joueurs","3 joueurs");
    private final Stage mainStage;

    /**
     * Constructor that initializes the main screen
     * @param handler the handler that is passed for the button
     */

    public ApplicationMain(BeginHandler handler){

        this.mainStage = new Stage();
        
        VBox vbox = new VBox();
        Button button = new Button();
        button.setAlignment(Pos.BASELINE_CENTER);
        button.textProperty().set("Choisis ton mode");
        button.setOnAction(e -> {
            button.disableProperty().set(true);
            handler.onBegin();
            mainStage.hide();
        });

//        Glow glow = new Glow();
//        button.setEffect(glow);
        VBox vbox2 = new VBox();
        Text text1 = new Text("" +
                "\n \n                                                                  Hello, and welcome to tChu! \n \n  This version was made by Marvin Chedid and Johnny Borkhoche. We hope you enjoy our version of the game.\n" +
                    "   Using the button below, you may pick between two game modes: 2 players or 3 players. Both versions only have minor differences.\n" +
                "   Note that in either case, after picking your game mode, you will have to connect to the host's ServerMain by passing the\n IP and port number to the program arguments of your ClientMains (there are by default values for running everything on the same computer).\n The game will only start up when the required number of players in either game mode have joined.\n \n" +
                "                                                                  Let's get started!");


        vbox.getChildren().add(button);
        vbox2.getChildren().add(text1);

        VBox.setMargin(button, new Insets(15, 0, 5, 210));
        BorderPane pane = new BorderPane(vbox2, WelcomeScreenView.createWelcomeScreen(),null, vbox,null);

        mainStage.titleProperty().setValue("tChu");
        mainStage.setScene(new Scene(pane, javafx.scene.paint.Color.WHITE));
        mainStage.show();
    }


    /**
     * This method presents the pick window so you can choose between the modes available to you
     * @param handler that process the mode of choice
     */

    public void run(ChooseModeHandler handler){
        assert isFxApplicationThread();
        //VBox
        VBox vbox = new VBox();

        Stage thisStage = new Stage(StageStyle.UTILITY);

        thisStage.titleProperty().setValue("Mode");
        thisStage.initOwner(mainStage);
        thisStage.initModality(Modality.WINDOW_MODAL);
        thisStage.setOnCloseRequest(Event::consume);



        //Text flow
        TextFlow textFlow = new TextFlow();
        Text t = new Text();
        t.textProperty().set("Choisissez votre mode de jeu");
        textFlow.getChildren().add(t);

        //ListView
        ObservableList<String> StringList = new SimpleListProperty<>(FXCollections.observableArrayList());
        StringList.addAll(modes);
        ListView<String> listView = new ListView<String>(StringList);
        listView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        //Button
        Button button = new Button();
        button.textProperty().set("Choisir");
        button.disableProperty().bind(Bindings.size(listView.getSelectionModel().getSelectedItems()).isNotEqualTo(1));
        button.setOnAction(e -> {
            thisStage.hide();
            handler.onChooseMode(listView.getSelectionModel().getSelectedItems().get(0));
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
     * Runs the game depending on the mode
     * @param mode Mode of the game either 2 or 3 players
     * @throws IOException if no player connects
     */

    public void begin(String mode) throws IOException {

        assert isFxApplicationThread();

        String namePlayer1 = "Ada";
        String namePlayer2 = "Charles";
        String namePlayer3 = "Xi";

        ServerSocket serverSocket1 = new ServerSocket(5109);
        ServerSocket serverSocket2 = new ServerSocket(5110);

        Map<PlayerId, String> names = Map.of(
                PlayerId.PLAYER_1, namePlayer1,
                PlayerId.PLAYER_2, namePlayer2);

        Map<PlayerId, String> names3Players = Map.of(
                PlayerId.PLAYER_1, namePlayer1,
                PlayerId.PLAYER_2, namePlayer2,
                PlayerId.PLAYER_3, namePlayer3);

        SortedBag<Ticket> tickets = SortedBag.of(ChMap.tickets());
        if (mode.equals(modes.get(0))) {
            GameOriginal.play(Map.of(
                    PlayerId.PLAYER_1, new GraphicalPlayerAdapter(),
                    PlayerId.PLAYER_2, new RemotePlayerProxy(serverSocket1.accept())), names, tickets, new Random());
        }
        else if (mode.equals(modes.get(1))) {
            Game3Players.play(Map.of(
                    PlayerId.PLAYER_1, new GraphicalPlayerAdapter(),
                    PlayerId.PLAYER_2, new RemotePlayerProxy(serverSocket1.accept()),
                    PlayerId.PLAYER_3, new RemotePlayerProxy(serverSocket2.accept())), names3Players, tickets, new Random()
            );
        }
    }
}
