package ch.epfl.tchu.gui;

import java.io.IOException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import static ch.epfl.tchu.gui.ActionHandlers.*;

import static javafx.application.Platform.runLater;

/**
 * This class contains the interface adapter components of the game tchu.
 *
 * @author Marvin Chedid (302446)
 * @author Johnny Borkhoche (296169)
 */

public class ApplicationMainAdapter {

    private final BlockingQueue<String> choice = new ArrayBlockingQueue<>(1);
    private final BlockingQueue<Boolean> start = new ArrayBlockingQueue<>(1);
    private ApplicationMain main;
    //Handler that adds the choice picked to be used in the blocking queue start
    private final BeginHandler handler = () -> {
        try {
            start.put(true);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    };

    /**
     * This initialises the new game by calling a new instance of application main
     */

    public void initializer() {
        runLater(() -> this.main = new ApplicationMain(handler));
    }

    /**
     * This method which receives the information from the button in order to launch the modal window that allows
     * the mode picking process
     */

    public boolean canStart(){
        try {
            return start.take();
        } catch (Exception e){
            return false;
        }
    }

    /**
     * This method simply calls the application main's method but on the JavaFX thread
     */
    public void chooseYourMode(){
        ActionHandlers.ChooseModeHandler chooseModeHandler = (s) -> {
            try {
                choice.put(s);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        };
        runLater(() -> main.run(chooseModeHandler));
    }

    /**
     *  This method returns the choice that was picked in the method choices
     * @return the mode of choice
     */

    public String modeOfChoice() {
        try {
            return choice.take();
        } catch (Exception e){
            return "";
        }
    }

    /**
     *  This method simply calls the application main's method but on the JavaFX thread
     * @param mode the mode desired that would normally be used via the blocking queue choice
     * @throws IOException if no player has connected
     */
    public void begin(String mode) throws IOException {
        main.begin(mode);
    }


}
