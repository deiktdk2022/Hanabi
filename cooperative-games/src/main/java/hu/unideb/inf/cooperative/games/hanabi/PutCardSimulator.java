package hu.unideb.inf.cooperative.games.hanabi;

import hu.unideb.inf.cooperative.games.statespace.Operator;
import hu.unideb.inf.cooperative.games.statespace.State;

public class PutCardSimulator extends Operator {

    protected Card cardToPut;
    protected Card cardToDraw;
    protected int cardIdx;

    public PutCardSimulator(Card cardToPut, Card cardToDraw, int cardIdx) {
        this.cardToPut = cardToPut;
        this.cardToDraw = cardToDraw;
        this.cardIdx = cardIdx;
    }

    @Override
    public boolean applicable(State state) {
        if (!(state instanceof HanabiState)) {
            return false;
        }
        if (cardIdx < 1 || HanabiState.NUMBER_OF_CARDS_IN_HAND < cardIdx) {
            return false;
        }
        HanabiState hanabiState = (HanabiState) state;
        if (hanabiState.getPlayer() == 'A' && hanabiState.getCardsInPlayerAHand().get(cardIdx - 1) == null) {
            return false;
        }
        return hanabiState.getPlayer() != 'B' || hanabiState.getCardsInPlayerBHand().get(cardIdx - 1) != null;
    }

    @Override
    public State apply(State state) {
        if (!(state instanceof HanabiState)) {
            return null;
        }
        HanabiState hanabiState = ((HanabiState) state).copyHanabiState();
        if (hanabiState.getPlayer() == 'A') {
            hanabiState.getCardsInPlayerAHand().set(cardIdx - 1, null);
            if (!hanabiState.getDeck().isEmpty()) {
                hanabiState.getCardsInPlayerAHand().set(cardIdx - 1, cardToDraw);
            }
        }
        if (hanabiState.getPlayer() == 'B') {
            hanabiState.getCardsInPlayerBHand().set(cardIdx - 1, null);
            if (!hanabiState.getDeck().isEmpty()) {
                hanabiState.getCardsInPlayerBHand().set(cardIdx - 1, cardToDraw);
            }
        }
        assert cardToPut != null;
        Color color = cardToPut.getColor();
        int number = cardToPut.getValue();
        int currentNumber = hanabiState.getFireworkState().get(color).size();
        if (currentNumber == number - 1) {
            hanabiState.getFireworkState().get(color).add(cardToPut);
        } else {
            hanabiState.getDiscardPile().add(cardToPut);
            hanabiState.increaseStormTokens();
        }
        hanabiState.changePlayer();
        return hanabiState;
    }

    @Override
    public String toString() {
        return String.format("PutCard(%s - %s - %s)", cardToPut, cardIdx, cardToDraw);
    }

    public int getCardIdx() {
        return cardIdx;
    }
}
