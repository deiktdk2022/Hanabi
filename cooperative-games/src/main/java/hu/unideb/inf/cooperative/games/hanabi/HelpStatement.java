package hu.unideb.inf.cooperative.games.hanabi;

import hu.unideb.inf.cooperative.games.knowledge.Knowledge;
import hu.unideb.inf.cooperative.games.logic.Formula;
import hu.unideb.inf.cooperative.games.statespace.Operator;
import hu.unideb.inf.cooperative.games.statespace.State;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class HelpStatement extends Operator {

    protected List<Formula> statement;

    public HelpStatement(List<Formula> statement) {
        this.statement = statement;
    }

    @Override
    public boolean applicable(State state) {
        if (!(state instanceof HanabiState)) {
            return false;
        }
        return 0 < ((HanabiState) state).getNoteTokens();
    }

    @Override
    public State apply(State state) {
        if (!(state instanceof HanabiState)) {
            return null;
        }
        HanabiState hanabiState = ((HanabiState) state).copyHanabiState();
        for (Formula formula : statement) {
            if (!hanabiState.getCommonKnowledge().contains(formula)) {
                hanabiState.getCommonKnowledge().add(formula);
            }
        }
        List<Formula> additionalKnowledge = Knowledge.createAdditionalKnowledge(statement, hanabiState.getPlayer() == 'A' ? 'B' : 'A', hanabiState.getPlayer() == 'A' ? hanabiState.getCardsInPlayerBHand() : hanabiState.getCardsInPlayerAHand());
        for (Formula formula : additionalKnowledge) {
            if (!hanabiState.getCommonKnowledge().contains(formula)) {
                hanabiState.getCommonKnowledge().add(formula);
            }
        }
        hanabiState.getCommonKnowledge().addAll(Knowledge.createAdditionalKnowledgeUsingCalculus(hanabiState.getCommonKnowledge(), Stream.concat(HanabiState.getPossibleNewKnowledgeForPlayers().get('B').stream(), HanabiState.getPossibleNewKnowledgeForPlayers().get('A').stream()).collect(Collectors.toList())));
        Knowledge.simplifyAtomicFormulasWithNot(hanabiState.getCommonKnowledge(), 'A');
        Knowledge.simplifyAtomicFormulasWithNot(hanabiState.getCommonKnowledge(), 'B');
        hanabiState.decreaseNoteTokens();
        return hanabiState;
    }

    @Override
    public String toString() {
        return "HelpStatement: " + statement;
    }
}
