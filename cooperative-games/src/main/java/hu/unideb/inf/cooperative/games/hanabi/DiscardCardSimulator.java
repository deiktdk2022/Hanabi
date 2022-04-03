package hu.unideb.inf.cooperative.games.hanabi;

import hu.unideb.inf.cooperative.games.statespace.Operator;
import hu.unideb.inf.cooperative.games.statespace.State;

public class DiscardCardSimulator extends Operator {

    protected Card cardToDiscard;
    protected Card cardToDraw;
    protected int cardIdx;

    public DiscardCardSimulator(Card cardToDiscard, Card cardToDraw, int cardIdx) {
        this.cardToDiscard = cardToDiscard;
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
        if (hanabiState.getNoteTokens() == HanabiState.MAX_VALUE_OF_NOTE_TOKENS) {
            return false;
        }
        if (hanabiState.getPlayer() == 'A' && hanabiState.getCardsInPlayerAHand().get(cardIdx - 1) == null) {
            return false;
        }
        return hanabiState.getPlayer() != 'B' || hanabiState.getCardsInPlayerBHand().get(cardIdx - 1) != null;
    }

    @Override
    public HanabiState apply(State state) {
        if (!(state instanceof HanabiState)) {
            return null;
        }
        HanabiState hanabiState = ((HanabiState) state).copyHanabiState();
        if (hanabiState.getPlayer() == 'A') {
            hanabiState.getCardsInPlayerAHand().set(cardIdx - 1, null);
            if (!hanabiState.getDeck().isEmpty()) {
                hanabiState.getCardsInPlayerAHand().set(cardIdx - 1, cardToDraw);
            }
            hanabiState.getDiscardPile().add(cardToDiscard);
        }
        if (hanabiState.getPlayer() == 'B') {
            hanabiState.getCardsInPlayerBHand().set(cardIdx - 1, null);
            if (!hanabiState.getDeck().isEmpty()) {
                hanabiState.getCardsInPlayerBHand().set(cardIdx - 1, cardToDraw);
            }
            hanabiState.getDiscardPile().add(cardToDiscard);
        }
        if (hanabiState.getNoteTokens() < 9) {
            hanabiState.increaseNoteTokens();
        }
        hanabiState.changePlayer();
        return hanabiState;
    }


    @Override
    public String toString() {
        return String.format("DiscardCard(%s - %s - %s)", cardToDiscard, cardIdx, cardToDraw);
    }
}
