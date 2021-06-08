package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import static java.util.Objects.checkIndex;

/**
 * Represents the private card state of the game, i.e all of the face up cards, deck, and discards.
 *
 * @author Marvin Chedid (302446)
 * @author Johnny Borkhoche (296169)
 */


public final class CardState extends PublicCardState {

    private final List<Card> faceUpCards;
    public final List<Card> deckCards;
    private final List<Card> discardsCards;


    private CardState(List<Card> faceUpCards, List<Card> deckCards, List<Card> discardsCards) {

        super(faceUpCards, deckCards.size(), discardsCards.size());
        this.faceUpCards = List.copyOf(faceUpCards);
        this.deckCards = List.copyOf(deckCards);
        this.discardsCards = List.copyOf(discardsCards);

    }

    /**
     * Use the following deck to distribute cards into three categories by assuming the last five cards are in the
     * face-up cards pile, that the rest is in the deck of cards pile, and that the discards pile is empty.
     *
     * @param deck The Deck of cards
     * @return the state of the cards for the game
     */

    public static CardState of(Deck<Card> deck) {
        Preconditions.checkArgument(deck.size() >= 5);
        SortedBag.Builder<Card> builder = new SortedBag.Builder<Card>();
        List<Card> faceUpCards = new ArrayList<>();
        for (int i = 0; i < 5; ++i) {
            faceUpCards.add(deck.topCard());
            deck = deck.withoutTopCard();
        }

        List<Card> deckCards = new ArrayList<Card>();
        while (!deck.isEmpty()) {
            deckCards.add(deck.topCard());
            deck = deck.withoutTopCard();
        }

        List<Card> discardsCards = builder.build().toList();
        return new CardState(faceUpCards, deckCards, discardsCards);
    }

    /**
     * Returns another distribution of cards which replaces the card positioned at a specific slot in the face up cards
     * pile by the top card in the deck of cards pile and then discards it.
     *
     * @param slot the position of the card to be replaced
     * @return a new distribution of cards
     */

    public CardState withDrawnFaceUpCard(int slot) {
        Preconditions.checkArgument(deckCards.size() != 0);
        List<Card> tempFaceUpCards = new ArrayList<>(faceUpCards);
        List<Card> tempDeckCards = new ArrayList<>(deckCards);
        tempFaceUpCards.set(checkIndex(slot, 5), deckCards.get(0));
        tempDeckCards.remove(0);
        return new CardState(tempFaceUpCards, tempDeckCards, discardsCards);
    }

    /**
     * Returns the top card in the deck of cards pile without removing it
     *
     * @return the card
     */

    public Card topDeckCard() {
        Preconditions.checkArgument(deckCards.size() != 0);
        return deckCards.get(0);
    }

    /**
     * Returns a new distribution of cards which removed the top card in the deck of cards pile.
     *
     * @return the new distribution
     */

    public CardState withoutTopDeckCard() {
        Preconditions.checkArgument(deckCards.size() != 0);
        List<Card> tempDeckCards = new ArrayList<>(deckCards);
        tempDeckCards.remove(0);
        List<Card> tempFaceUp = new ArrayList<>(faceUpCards);
        List<Card> tempDiscardsCards = new ArrayList<>(discardsCards);
        return new CardState(tempFaceUp, tempDeckCards, tempDiscardsCards);
    }

    /**
     * Returns a new distribution of the cards when the deck of cards pile is empty by taking the cards in the
     * discards pile and putting them in the deck cards pile all the while shuffling them.
     *
     * @param rng a randomization
     * @return a new distribution of cards
     */


    public CardState withDeckRecreatedFromDiscards(Random rng) {
        Preconditions.checkArgument(deckCards.size() == 0);
        List<Card> newDeckCards = new ArrayList<>(discardsCards);
        List<Card> newDiscardsCards = new ArrayList<>();
        Collections.shuffle(newDeckCards, rng);
        return new CardState(faceUpCards, newDeckCards, newDiscardsCards);
    }

    /**
     * Returns a new distribution of cards which adds more cards to the pile of discarded cards
     *
     * @param additionalDiscards cards to be added to the discarded pile
     * @return a new distribution of cards
     */

    public CardState withMoreDiscardedCards(SortedBag<Card> additionalDiscards) {
        List<Card> newDiscardsCards = new ArrayList<>(discardsCards);
        newDiscardsCards.addAll(additionalDiscards.toList());
        return new CardState(faceUpCards, deckCards, newDiscardsCards);
    }
}
