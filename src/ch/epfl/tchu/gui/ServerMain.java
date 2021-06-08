package ch.epfl.tchu.gui;


import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;

import static javafx.application.Platform.isFxApplicationThread;

/**
 * This class contains the main program of a tChu server. It takes care of creating a game instance and running it.
 *
 * @author Marvin Chedid (302446)
 * @author Johnny Borkhoche (296169)
 */
public final class ServerMain extends Application {

    /**
     * Main method that will simply call the JavaFX method `start`
     *
     * @param args the program arguments
     */
    public static void main(String[] args)  {
        Platform.setImplicitExit(false);
        launch(args);
    }

    /**
     * JavaFX method that creates a new instance of application we recently adapted for tChu and runs it on a new thread
     */
    @Override
    public void start(Stage primaryStage) throws IOException {
            ApplicationMainAdapter myApp = new ApplicationMainAdapter();
            new Thread(() -> {
                try {
                    UserInterface.launch(myApp);
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();
    }


}
