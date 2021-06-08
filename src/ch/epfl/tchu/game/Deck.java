package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;

import java.util.*;

/**
 * Represents a generic deck of cards of any type
 *
 * @author Marvin Chedid (302446)
 * @author Johnny Borkhoche (296169)
 */

public final class Deck<C extends Comparable<C>> {

    private final int size;
    private final List<C> deck;


    private Deck(int size, List<C> deck) {
        this.deck = new ArrayList<>(deck);
        this.size = size;
    }

    /**
     * This method returns a deck of cards which consists of a shuffled version of the sorted bag of cards provided as parameter
     *
     * @param cards the sorted bag of cards from which we wish to create a deck
     * @param rng   the Random variable that will be used to shuffle the cards
     * @param <C>   the type of the deck
     * @return a shuffled deck of cards
     */
    public static <C extends Comparable<C>> Deck<C> of(SortedBag<C> cards, Random rng) {
        List<C> tempList = cards.toList();
        Collections.shuffle(tempList, rng);
        return new Deck<C>(tempList.size(), tempList);
    }

    /**
     * Returns the size of this Deck
     *
     * @return size of the deck of cards
     */
    public int size() {
        return size;
    }

    /**
     * Returns true if the deck is empty, false otherwise
     *
     * @return true if empty, false if not
     */

    public boolean isEmpty() {
        return size == 0;
    }

    /**
     * Returns the card on top of the deck, throws IllegalArgumentException if deck is empty
     *
     * @return the top card of the deck
     */
    public C topCard() {
        if (isEmpty()) {
            throw new IllegalArgumentException();
        }
        return deck.get(0);
    }

    /**
     * Returns a new deck that contains the same cards except the top one
     *
     * @return the same deck without the top card
     */
    public Deck<C> withoutTopCard() {
        if (isEmpty()) {
            throw new IllegalArgumentException();
        }
        List<C> newList = new ArrayList<C>(deck);
        newList.remove(0);
        return new Deck<C>(size - 1, newList);
    }

    /**
     * Returns a multi-set that contains "count" number of cards starting from the top of the deck
     *
     * @param count number of cards to be taken from the top of the deck
     * @return a set containing "count" cards from the top of the deck
     */
    public SortedBag<C> topCards(int count) {
        Preconditions.checkArgument(count >= 0);
        Preconditions.checkArgument(count <= size);

        SortedBag.Builder<C> builder = new SortedBag.Builder<C>();
        for (int i = 0; i < count; ++i)
            builder.add(deck.get(i));

        return builder.build();
    }

    /**
     * Returns a deck similar to the receptor of this method but without the top "count" cards (from the top of the deck)
     *
     * @param count number of cards to be ignored when creating a new deck, starting from the top of the previous one
     * @return a new Deck, with the same cards as the previous one, but without the "count" number of cards from the top of the previous one
     */
    public Deck<C> withoutTopCards(int count) {
        Preconditions.checkArgument(count >= 0);
        Preconditions.checkArgument(count <= size);

        List<C> tempList = new ArrayList<>();

        for (int i = count; i < size; ++i)
            tempList.add(deck.get(i));

        return new Deck<>(size - count, tempList);
    }
}
