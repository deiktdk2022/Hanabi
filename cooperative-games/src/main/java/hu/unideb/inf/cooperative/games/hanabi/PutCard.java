package hu.unideb.inf.cooperative.games.hanabi;

import hu.unideb.inf.cooperative.games.knowledge.Knowledge;
import hu.unideb.inf.cooperative.games.statespace.Operator;
import hu.unideb.inf.cooperative.games.statespace.State;

public class PutCard extends Operator {

    protected int idxOfCardToPut;

    public PutCard(int idxOfCardToPut) {
        this.idxOfCardToPut = idxOfCardToPut;
    }

    @Override
    public boolean applicable(State state) {
        if (!(state instanceof HanabiState)) {
            return false;
        }
        if (idxOfCardToPut < 1 || HanabiState.NUMBER_OF_CARDS_IN_HAND < idxOfCardToPut) {
            return false;
        }
        HanabiState hanabiState = (HanabiState) state;
        if (hanabiState.getPlayer() == 'A' && hanabiState.getCardsInPlayerAHand().get(idxOfCardToPut - 1) == null) {
            return false;
        }
        return hanabiState.getPlayer() != 'B' || hanabiState.getCardsInPlayerBHand().get(idxOfCardToPut - 1) != null;
    }

    @Override
    public State apply(State state) {
        if (!(state instanceof HanabiState)) {
            return null;
        }
        HanabiState hanabiState = ((HanabiState) state).copyHanabiState();
        Card cardToPut = null;
        hanabiState.setCommonKnowledge(Knowledge.deleteKnowledgeBecauseOfNewCard(hanabiState.getCommonKnowledge(), idxOfCardToPut, hanabiState.getPlayer()));
        if (hanabiState.getPlayer() == 'A') {
            cardToPut = hanabiState.getCardsInPlayerAHand().get(idxOfCardToPut - 1);
            hanabiState.getCardsInPlayerAHand().set(idxOfCardToPut - 1, null);
            if (!hanabiState.getDeck().isEmpty()) {
                hanabiState.getCardsInPlayerAHand().set(idxOfCardToPut - 1, hanabiState.getDeck().remove(0));
            }
        }
        if (hanabiState.getPlayer() == 'B') {
            cardToPut = hanabiState.getCardsInPlayerBHand().get(idxOfCardToPut - 1);
            hanabiState.getCardsInPlayerBHand().set(idxOfCardToPut - 1, null);
            if (!hanabiState.getDeck().isEmpty()) {
                hanabiState.getCardsInPlayerBHand().set(idxOfCardToPut - 1, hanabiState.getDeck().remove(0));
            }
        }
        assert cardToPut != null;
        Color color = cardToPut.getColor();
        int number = cardToPut.getValue();
        int currentNumber = hanabiState.getFireworkState().get(color).size();
        if (currentNumber == number - 1) {
            hanabiState.getFireworkState().get(color).add(cardToPut);
            if (number == 5 && hanabiState.getNoteTokens() < 8) {
                hanabiState.increaseNoteTokens();
            }
        } else {
            hanabiState.getDiscardPile().add(cardToPut);
            hanabiState.increaseStormTokens();
        }
        return hanabiState;
    }

    @Override
    public String toString() {
        return String.format("PutCard(%s)", idxOfCardToPut);
    }
}
