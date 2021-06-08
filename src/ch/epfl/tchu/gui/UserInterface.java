package ch.epfl.tchu.gui;

import java.io.IOException;

/**
 * This class handles the flow of the application
 *
 * @author Marvin Chedid (302446)
 * @author Johnny Borkhoche (296169)
 */

public class UserInterface {

    private UserInterface(){
    }

    /**
     * This method launches the application
     * @param myApp The application being launched
     * @throws IOException is thrown if no socket is accepted in the main application
     * @throws InterruptedException is thrown if no mode is chosen and something was interrupted
     * (related to the handler)
     */

    public static void launch(ApplicationMainAdapter myApp) throws IOException, InterruptedException {
        myApp.initializer();
        if(myApp.canStart()) {
            myApp.chooseYourMode();
            myApp.begin(myApp.modeOfChoice());
        }
    }

}
