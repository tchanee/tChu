package ch.epfl.tchu.game;


import ch.epfl.tchu.Preconditions;

import java.util.List;

import static java.util.Objects.checkIndex;

/**
 * Represents the state of "public" cards, i.e the ones in the faceUpCards deck (the 5 that are visible on the game board), the deckCards deck (the "pioche") and finally the discardsCards deck (the "defausse")
 *
 * @author Marvin Chedid (302446)
 * @author Johnny Borkhoche (296169)
 */

public class PublicCardState {
    private final List<Card> faceUpCards;
    private final int deckSize;
    private final int discardsSize;

    /**
     * Constructs a public state of cards on the board, namely the face up cards (whose size must be equal to 5),
     * the deck and the discarded cards whose size must atleast equal 0.
     *
     * @param faceUpCards  list of the 5 visible cards on the board
     * @param deckSize     the size of the deckCards deck ("pioche")
     * @param discardsSize the size of the discardsDeck deck ("defausse")
     */
    public PublicCardState(List<Card> faceUpCards, int deckSize, int discardsSize) {
        Preconditions.checkArgument(faceUpCards.size() == 5);
        Preconditions.checkArgument(deckSize >= 0);
        Preconditions.checkArgument(discardsSize >= 0);

        this.faceUpCards = List.copyOf(faceUpCards);
        this.deckSize = deckSize;
        this.discardsSize = discardsSize;
    }


    /**
     * Returns a list of the 5 visible cards
     *
     * @return list of the visible cards
     */

    public List<Card> faceUpCards() {
        return List.copyOf(faceUpCards);
    }

    /**
     * Returns the visible card at the index given as parameter
     *
     * @param slot the index of the faceUpCard that we want to return
     * @return the card at the provided index, from the faceUpCard deck
     */
    public Card faceUpCard(int slot) {
        return faceUpCards.get(checkIndex(slot, 5));
    }

    /**
     * Returns the size of deckCards ("pioche")
     *
     * @return the size of the deckCards deck
     */
    public int deckSize() {
        return deckSize;
    }

    /**
     * Returns true if the deckCards deck is empty (the "pioche")
     *
     * @return true of the size of the deck is empty
     */
    public boolean isDeckEmpty() {
        return deckSize == 0;
    }

    /**
     * Returns the size of discardsSize ("defausse")
     *
     * @return the size of the discardsDeck deck
     */
    public int discardsSize() {
        return discardsSize;
    }
}
