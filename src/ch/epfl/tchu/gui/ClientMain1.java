package ch.epfl.tchu.gui;


import ch.epfl.tchu.net.RemotePlayerClient;
import javafx.application.Application;
import javafx.stage.Stage;

/**
 * This class contains the main program of the tChu client 1. It takes care of creating a game client and running it. Can be used in 2 players or 3 players game modes.
 *
 * @author Marvin Chedid (302446)
 * @author Johnny Borkhoche (296169)
 */
public final class ClientMain1 extends Application {

    /**
     * Main method that will simply call the JavaFX method `start`
     *
     * @param args the program arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

    /**
     * JavaFX method that creates and starts up the game client and runs it on a new thread
     */
    @Override
    public void start(Stage primaryStage) {
        System.out.println("Client 1 is starting!");
        String name = getParameters().getRaw().size() != 0 ? getParameters().getRaw().get(0) : "localhost";
        int port = getParameters().getRaw().size() != 0 ? Integer.parseInt(getParameters().getRaw().get(1)) : 5109;
        RemotePlayerClient client = new RemotePlayerClient(new GraphicalPlayerAdapter(), name, port);
        new Thread(client::run).start();
    }
}
