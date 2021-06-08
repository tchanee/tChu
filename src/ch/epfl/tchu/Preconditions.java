package ch.epfl.tchu;

/**
 * Preconditions for arguments
 *
 * @author Marvin Chedid (302446)
 * @author Johnny Borkhoche (296169)
 */

public final class Preconditions {

    /**
     * Constructs an instance of a precondition and is private as it's non instantiable for an outside user
     */

    private Preconditions() {
    }

    /**
     * Makes sure the argument follows a certain condition necessary for the proper functioning of the code.
     *
     * @param shouldBeTrue the condition the argument should satisfy
     * @throws IllegalArgumentException if the argument doesn't satisfy the condition
     */

    public static void checkArgument(boolean shouldBeTrue) throws IllegalArgumentException {
        if (!shouldBeTrue)
            throw new IllegalArgumentException();
    }

}
