package ch.epfl.tchu.net;


/**
 * This enum enumerates all types of messages that the server could send to the clients. The messages actually correspond
 * to the methods in the Player interface.
 *
 * @author Marvin Chedid (302446)
 * @author Johnny Borkhoche (296169)
 */
public enum MessageId {
    INIT_PLAYERS(),
    RECEIVE_INFO(),
    UPDATE_STATE(),
    SET_INITIAL_TICKETS(),
    CHOOSE_INITIAL_TICKETS(),
    NEXT_TURN(),
    CHOOSE_TICKETS(),
    DRAW_SLOT(),
    ROUTE(),
    CARDS(),
    CHOOSE_ADDITIONAL_CARDS();

    /**
     * MessageId's constructor.
     *
     */
    private MessageId() {
    }
}
