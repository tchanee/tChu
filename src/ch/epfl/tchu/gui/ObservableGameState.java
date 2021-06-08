package ch.epfl.tchu.gui;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.collections.ObservableList;

import java.util.List;

public abstract class ObservableGameState {

    public abstract void setState(PublicGameState newGameState, PlayerState newPlayerState);

    public abstract PlayerId getPlayerId();

    public abstract boolean canDrawCards();

    public abstract boolean canDrawTickets();

    public abstract ReadOnlyIntegerProperty ticketsLeftPercentageProperty();

    public abstract int getCardsLeftPercentage();

    public abstract List<ObjectProperty<Card>> getFaceUpCards();

    public abstract ReadOnlyObjectProperty<PlayerId> getRouteOwnershipProperty(Route route);

    public abstract ReadOnlyIntegerProperty p1TotalTicketCountProperty();

    public abstract ReadOnlyIntegerProperty p2TotalTicketCountProperty();

    public abstract ReadOnlyIntegerProperty p3TotalTicketCountProperty();

    public abstract ReadOnlyIntegerProperty p1TotalCardCountProperty();

    public abstract ReadOnlyIntegerProperty p2TotalCardCountProperty();

    public abstract ReadOnlyIntegerProperty p3TotalCardCountProperty();

    public abstract List<SortedBag<Card>> possibleClaimCards(Route route);

    public abstract ReadOnlyObjectProperty<Card> faceUpCard(int slot);

    public abstract ReadOnlyBooleanProperty getPlayerCanClaimRouteProperty(Route route);

    public abstract ReadOnlyIntegerProperty getPlayerCardCount(Card card);

    public abstract ObservableList<Ticket> getPlayerTickets();

    public abstract ReadOnlyIntegerProperty cardsLeftPercentageProperty();

    public abstract ReadOnlyIntegerProperty p1TotalConstrPointsProperty();

    public abstract ReadOnlyIntegerProperty p2TotalConstrPointsProperty();

    public abstract ReadOnlyIntegerProperty p3TotalConstrPointsProperty();

    public abstract ReadOnlyIntegerProperty p1TotalLocomotiveCountProperty();

    public abstract ReadOnlyIntegerProperty p2TotalLocomotiveCountProperty();

    public abstract ReadOnlyIntegerProperty p3TotalLocomotiveCountProperty();

}
