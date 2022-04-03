package hu.unideb.inf.cooperative.games.hanabi;

import hu.unideb.inf.cooperative.games.knowledge.Knowledge;
import hu.unideb.inf.cooperative.games.statespace.Operator;
import hu.unideb.inf.cooperative.games.statespace.State;

public class DiscardCard extends Operator {

    protected int idxOfCardToDiscard;

    public DiscardCard(int idxOfCardToDiscard) {
        this.idxOfCardToDiscard = idxOfCardToDiscard;
    }

    @Override
    public boolean applicable(State state) {
        if (!(state instanceof HanabiState)) {
            return false;
        }
        if (idxOfCardToDiscard < 1 || HanabiState.NUMBER_OF_CARDS_IN_HAND < idxOfCardToDiscard) {
            return false;
        }
        HanabiState hanabiState = (HanabiState) state;
        if (hanabiState.getNoteTokens() == HanabiState.MAX_VALUE_OF_NOTE_TOKENS) {
            return false;
        }
        if (hanabiState.getPlayer() == 'A' && hanabiState.getCardsInPlayerAHand().get(idxOfCardToDiscard - 1) == null) {
            return false;
        }
        return hanabiState.getPlayer() != 'B' || hanabiState.getCardsInPlayerBHand().get(idxOfCardToDiscard - 1) != null;
    }

    @Override
    public HanabiState apply(State state) {
        if (!(state instanceof HanabiState)) {
            return null;
        }
        HanabiState hanabiState = ((HanabiState) state).copyHanabiState();
        hanabiState.setCommonKnowledge(Knowledge.deleteKnowledgeBecauseOfNewCard(hanabiState.getCommonKnowledge(), idxOfCardToDiscard, hanabiState.getPlayer()));
        if (hanabiState.getPlayer() == 'A') {
            Card cardToDrop = hanabiState.getCardsInPlayerAHand().get(idxOfCardToDiscard - 1);
            hanabiState.getCardsInPlayerAHand().set(idxOfCardToDiscard - 1, null);
            if (!hanabiState.getDeck().isEmpty()) {
                hanabiState.getCardsInPlayerAHand().set(idxOfCardToDiscard - 1, hanabiState.getDeck().remove(0));
            }
            hanabiState.getDiscardPile().add(cardToDrop);
        }
        if (hanabiState.getPlayer() == 'B') {
            Card cardToDrop = hanabiState.getCardsInPlayerBHand().get(idxOfCardToDiscard - 1);
            hanabiState.getCardsInPlayerBHand().set(idxOfCardToDiscard - 1, null);
            if (!hanabiState.getDeck().isEmpty()) {
                hanabiState.getCardsInPlayerBHand().set(idxOfCardToDiscard - 1, hanabiState.getDeck().remove(0));
            }
            hanabiState.getDiscardPile().add(cardToDrop);
        }
        if (hanabiState.getNoteTokens() < HanabiState.MAX_VALUE_OF_NOTE_TOKENS) {
            hanabiState.increaseNoteTokens();
        }
        return hanabiState;
    }


    @Override
    public String toString() {
        return String.format("DiscardCard(%s)", idxOfCardToDiscard);
    }
}
