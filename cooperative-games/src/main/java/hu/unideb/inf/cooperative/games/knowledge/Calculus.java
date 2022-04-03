package hu.unideb.inf.cooperative.games.knowledge;

import hu.unideb.inf.cooperative.games.logic.Formula;
import hu.unideb.inf.cooperative.games.logic.LogicOperator;
import hu.unideb.inf.cooperative.games.logic.OneOperandFormula;
import hu.unideb.inf.cooperative.games.logic.TwoOperandFormula;
import org.apache.commons.collections4.CollectionUtils;

import java.util.*;

public class Calculus {

    protected List<Formula> knowledge;

    protected List<Formula> expandedNEC = new ArrayList<>();

    public Calculus(final List<Formula> knowledge) {
        this.knowledge = knowledge;
    }

    /**
     * Decide if the current knowledge contains the new formula.
     *
     * @param formula The new formula which we want to add.
     * @return {@code true} if the current knowledge contains the new formula, {@code false} otherwise.
     */
    public boolean contains(Formula formula) {
        return knowledge.contains(formula);
    }

    /**
     * Decides whether the original knowledge can be expanded with the new statement.
     * Expands the list of knowledge with the negation of the new statement and then checks whether the resulting list is correct.
     *
     * @param newStatementToTest The new statement with which we want to expand our knowledge.
     * @return {@code true} if the new statement can be added to the knowledge, {@code false} otherwise.
     */
    public boolean canAddTheNewStatement(Formula newStatementToTest) {
        List<Formula> extendedKnowledge = new ArrayList<>(knowledge);
        extendedKnowledge.add(new OneOperandFormula(LogicOperator.NOT, newStatementToTest));
        return !new Calculus(extendedKnowledge).isCorrectKnowledge();
    }


    /**
     * Decides whether the formula list contains any inconsistency.
     * Coordinates the exploration and creation of worlds.
     *
     * @return {@code false} if the formula list contains any inconsistency, {@code true} otherwise.
     */
    public boolean isCorrectKnowledge() {
        if (stop(true)) {
            return false;
        }
        for (Formula formula : knowledge) {
            if (formula instanceof OneOperandFormula && ((OneOperandFormula) formula).getOperator() == LogicOperator.POS && !new Calculus(createNewWorld((OneOperandFormula) formula)).isCorrectKnowledge()) {
                return false;
            }
        }
        return true;
    }

    /**
     * Decides if need to examine the knowledge list further or there are any inconsistency.
     *
     * @param changed Tells you if you need to investigate the knowledge further.
     * @return {@code true} if there is inconsistency and need to stop the examination, {@code false} otherwise.
     */
    public boolean stop(boolean changed) {
        if (!isSyntacticallyCorrect()) {
            return true;
        }
        if (changed) {
            return extractNotNot(false);
        }
        return false;
    }

    /**
     * Extract the NOT(NOT)-shaped forms if the knowledge list has any.
     *
     * @param changed Tells you if you need to investigate the knowledge further.
     * @return {@code true} if Calculus.extractNotNec(knowledge, changed) provides true, {@code false} otherwise.
     * {@code true} if there is inconsistency and need to stop the examination, {@code false} otherwise.
     */
    public boolean extractNotNot(boolean changed) {
        List<Formula> extractedStatements = new ArrayList<>();
        List<Formula> statementsToRemove = new ArrayList<>();
        for (Formula formula : knowledge) {
            if (formula instanceof OneOperandFormula && ((OneOperandFormula) formula).getOperator() == LogicOperator.NOT && ((OneOperandFormula) formula).getOperand() instanceof OneOperandFormula && ((OneOperandFormula) ((OneOperandFormula) formula).getOperand()).getOperator() == LogicOperator.NOT && !knowledge.contains(((OneOperandFormula) ((OneOperandFormula) formula).getOperand()).getOperand())) {
                extractedStatements.add(((OneOperandFormula) ((OneOperandFormula) formula).getOperand()).getOperand());
                statementsToRemove.add(formula);
                changed = true;
            }
        }
        knowledge.addAll(extractedStatements);
        knowledge = new ArrayList<>(CollectionUtils.subtract(knowledge, statementsToRemove));
        return extractNotNec(changed);
    }

    /**
     * Extract the NOT(NEC)-shaped forms if the knowledge list has any.
     *
     * @param changed Tells you if you need to investigate the knowledge further.
     * @return {@code true} if Calculus.extractNotPos(knowledge, changed) provides true, {@code false} otherwise.
     * {@code true} if there is inconsistency and need to stop the examination, {@code false} otherwise.
     */
    public boolean extractNotNec(boolean changed) {
        List<Formula> extractedStatements = new ArrayList<>();
        List<Formula> statementsToRemove = new ArrayList<>();
        for (Formula formula : knowledge) {
            if (formula instanceof OneOperandFormula && ((OneOperandFormula) formula).getOperator() == LogicOperator.NOT && ((OneOperandFormula) formula).getOperand() instanceof OneOperandFormula && ((OneOperandFormula) ((OneOperandFormula) formula).getOperand()).getOperator() == LogicOperator.NEC) {
                Formula newStatementBase = ((OneOperandFormula) ((OneOperandFormula) formula).getOperand()).getOperand();
                extractedStatements.add(new OneOperandFormula(((OneOperandFormula) ((OneOperandFormula) formula).getOperand()).getWhoThink(), LogicOperator.POS, new OneOperandFormula(LogicOperator.NOT, newStatementBase)));
                statementsToRemove.add(formula);
                changed = true;
            }
        }
        knowledge.addAll(extractedStatements);
        knowledge = new ArrayList<>(CollectionUtils.subtract(knowledge, statementsToRemove));
        return extractNotPos(changed);
    }

    /**
     * Extract the NOT(POS)-shaped forms if the knowledge list has any.
     *
     * @param changed Tells you if you need to investigate the knowledge further.
     * @return {@code true} if Calculus.extractAnd(knowledge, changed) provides true, {@code false} otherwise.
     * {@code true} if there is inconsistency and need to stop the examination, {@code false} otherwise.
     */
    public boolean extractNotPos(boolean changed) {
        List<Formula> extractedStatements = new ArrayList<>();
        List<Formula> statementsToRemove = new ArrayList<>();
        for (Formula formula : knowledge) {
            if (formula instanceof OneOperandFormula && ((OneOperandFormula) formula).getOperator() == LogicOperator.NOT && ((OneOperandFormula) formula).getOperand() instanceof OneOperandFormula && ((OneOperandFormula) ((OneOperandFormula) formula).getOperand()).getOperator() == LogicOperator.POS) {
                Formula newStatementBase = ((OneOperandFormula) ((OneOperandFormula) formula).getOperand()).getOperand();
                extractedStatements.add(new OneOperandFormula(((OneOperandFormula) ((OneOperandFormula) formula).getOperand()).getWhoThink(), LogicOperator.NEC, new OneOperandFormula(LogicOperator.NOT, newStatementBase)));
                statementsToRemove.add(formula);
                changed = true;
            }
        }
        knowledge.addAll(extractedStatements);
        knowledge = new ArrayList<>(CollectionUtils.subtract(knowledge, statementsToRemove));
        return extractAnd(changed);
    }

    /**
     * Extract the AND-shaped forms if the knowledge list has any.
     *
     * @param changed Tells you if you need to investigate the knowledge further.
     * @return {@code true} if Calculus.extractNotOr(knowledge, changed) provides true, {@code false} otherwise.
     * {@code true} if there is inconsistency and need to stop the examination, {@code false} otherwise.
     */
    public boolean extractAnd(boolean changed) {
        List<Formula> extractedStatements = new ArrayList<>();
        List<Formula> statementsToRemove = new ArrayList<>();
        for (Formula formula : knowledge) {
            if (formula instanceof TwoOperandFormula && ((TwoOperandFormula) formula).getOperator() == LogicOperator.AND) {
                extractedStatements.add(((TwoOperandFormula) formula).getFirstOperand());
                extractedStatements.add(((TwoOperandFormula) formula).getSecondOperand());
                statementsToRemove.add(formula);
                changed = true;
            }
        }
        knowledge.addAll(extractedStatements);
        knowledge = new ArrayList<>(CollectionUtils.subtract(knowledge, statementsToRemove));
        return extractNotOr(changed);
    }

    /**
     * Extract the NOT(OR)-shaped forms if the knowledge list has any.
     *
     * @param changed Tells you if you need to investigate the knowledge further.
     * @return {@code true} if Calculus.extractNotImp(knowledge, changed) provides true, {@code false} otherwise.
     * {@code true} if there is inconsistency and need to stop the examination, {@code false} otherwise.
     */
    public boolean extractNotOr(boolean changed) {
        List<Formula> extractedStatements = new ArrayList<>();
        List<Formula> statementsToRemove = new ArrayList<>();
        for (Formula formula : knowledge) {
            if (formula instanceof OneOperandFormula && ((OneOperandFormula) formula).getOperator() == LogicOperator.NOT && ((OneOperandFormula) formula).getOperand() instanceof TwoOperandFormula && ((TwoOperandFormula) ((OneOperandFormula) formula).getOperand()).getOperator() == LogicOperator.OR) {
                TwoOperandFormula twoOperandFormula = (TwoOperandFormula) ((OneOperandFormula) formula).getOperand();
                extractedStatements.add(new OneOperandFormula(LogicOperator.NOT, twoOperandFormula.getFirstOperand()));
                extractedStatements.add(new OneOperandFormula(LogicOperator.NOT, twoOperandFormula.getSecondOperand()));
                statementsToRemove.add(formula);
                changed = true;
            }
        }
        knowledge.addAll(extractedStatements);
        knowledge = new ArrayList<>(CollectionUtils.subtract(knowledge, statementsToRemove));
        return extractNotImp(changed);
    }

    /**
     * Extract the NOT(IMP)-shaped forms if the knowledge list has any.
     *
     * @param changed Tells you if you need to investigate the knowledge further.
     * @return {@code true} if Calculus.extractAnd(knowledge, changed) provides true, {@code false} otherwise.
     * {@code true} if there is inconsistency and need to stop the examination, {@code false} otherwise.
     */
    public boolean extractNotImp(boolean changed) {
        List<Formula> extractedStatements = new ArrayList<>();
        List<Formula> statementsToRemove = new ArrayList<>();
        for (Formula formula : knowledge) {
            if (formula instanceof OneOperandFormula && ((OneOperandFormula) formula).getOperator() == LogicOperator.NOT && ((OneOperandFormula) formula).getOperand() instanceof TwoOperandFormula && ((TwoOperandFormula) ((OneOperandFormula) formula).getOperand()).getOperator() == LogicOperator.IMP) {
                TwoOperandFormula twoOperandFormula = (TwoOperandFormula) ((OneOperandFormula) formula).getOperand();
                extractedStatements.add(twoOperandFormula.getFirstOperand());
                extractedStatements.add(new OneOperandFormula(LogicOperator.NOT, twoOperandFormula.getSecondOperand()));
                statementsToRemove.add(formula);
                changed = true;
            }
        }
        knowledge.addAll(extractedStatements);
        knowledge = new ArrayList<>(CollectionUtils.subtract(knowledge, statementsToRemove));
        return extractNotAnd(changed);
    }

    /**
     * Extract the NOT(And)-shaped forms if the knowledge list has any.
     *
     * @param changed Tells you if you need to investigate the knowledge further.
     * @return {@code true} if Calculus.extractEquiv(knowledge, changed) provides true, {@code false} otherwise.
     * {@code true} if there is inconsistency and need to stop the examination, {@code false} otherwise.
     */
    public boolean extractNotAnd(boolean changed) {
        List<Formula> extractedStatements = new ArrayList<>();
        List<Formula> statementsToRemove = new ArrayList<>();
        for (Formula formula : knowledge) {
            if (formula instanceof OneOperandFormula && ((OneOperandFormula) formula).getOperator() == LogicOperator.NOT && ((OneOperandFormula) formula).getOperand() instanceof TwoOperandFormula && ((TwoOperandFormula) ((OneOperandFormula) formula).getOperand()).getOperator() == LogicOperator.AND) {
                TwoOperandFormula twoOperandFormula = (TwoOperandFormula) ((OneOperandFormula) formula).getOperand();
                extractedStatements.add(new TwoOperandFormula(LogicOperator.OR, new OneOperandFormula(LogicOperator.NOT, twoOperandFormula.getFirstOperand()), new OneOperandFormula(LogicOperator.NOT, twoOperandFormula.getSecondOperand())));
                statementsToRemove.add(formula);
                changed = true;
            }
        }
        knowledge.addAll(extractedStatements);
        knowledge = new ArrayList<>(CollectionUtils.subtract(knowledge, statementsToRemove));
        return extractNotEquiv(changed);
    }

    /**
     * Extract the NOT(EQUIV)-shaped forms if the knowledge list has any.
     *
     * @param changed Tells you if you need to investigate the knowledge further.
     * @return {@code true} if Calculus.extractImp(knowledge, changed) provides true, {@code false} otherwise.
     * {@code true} if there is inconsistency and need to stop the examination, {@code false} otherwise.
     */
    public boolean extractNotEquiv(boolean changed) {
        List<Formula> extractedStatements = new ArrayList<>();
        List<Formula> statementsToRemove = new ArrayList<>();
        for (Formula formula : knowledge) {
            if (formula instanceof OneOperandFormula && ((OneOperandFormula) formula).getOperator() == LogicOperator.NOT && ((OneOperandFormula) formula).getOperand() instanceof TwoOperandFormula && ((TwoOperandFormula) ((OneOperandFormula) formula).getOperand()).getOperator() == LogicOperator.IMP) {
                TwoOperandFormula twoOperandFormula = (TwoOperandFormula) ((OneOperandFormula) formula).getOperand();
                extractedStatements.add(new TwoOperandFormula(LogicOperator.OR, twoOperandFormula.getFirstOperand(), twoOperandFormula.getSecondOperand()));
                extractedStatements.add(new TwoOperandFormula(LogicOperator.OR, new OneOperandFormula(LogicOperator.NOT, twoOperandFormula.getFirstOperand()), new OneOperandFormula(LogicOperator.NOT, twoOperandFormula.getSecondOperand())));
                statementsToRemove.add(formula);
                changed = true;
            }
        }
        knowledge.addAll(extractedStatements);
        knowledge = new ArrayList<>(CollectionUtils.subtract(knowledge, statementsToRemove));
        return extractImp(changed);
    }

    /**
     * Extract the IMP-shaped forms if the knowledge list has any.
     *
     * @param changed Tells you if you need to investigate the knowledge further.
     * @return {@code true} if Calculus.extractEquiv(knowledge, changed) provides true, {@code false} otherwise.
     * {@code true} if there is inconsistency and need to stop the examination, {@code false} otherwise.
     */
    public boolean extractImp(boolean changed) {
        List<Formula> extractedStatements = new ArrayList<>();
        List<Formula> statementsToRemove = new ArrayList<>();
        for (Formula formula : knowledge) {
            if (formula instanceof TwoOperandFormula && ((TwoOperandFormula) formula).getOperator() == LogicOperator.IMP) {
                extractedStatements.add(new TwoOperandFormula(LogicOperator.OR, new OneOperandFormula(LogicOperator.NOT, ((TwoOperandFormula) formula).getFirstOperand()), ((TwoOperandFormula) formula).getSecondOperand()));
                statementsToRemove.add(formula);
                changed = true;
            }
        }
        knowledge.addAll(extractedStatements);
        knowledge = new ArrayList<>(CollectionUtils.subtract(knowledge, statementsToRemove));
        return extractEquiv(changed);
    }

    /**
     * Extract the EQUIV-shaped forms if the knowledge list has any.
     *
     * @param changed Tells you if you need to investigate the knowledge further.
     * @return {@code true} if Calculus.extractOr(knowledge, changed) provides true, {@code false} otherwise.
     * {@code true} if there is inconsistency and need to stop the examination, {@code false} otherwise.
     */
    public boolean extractEquiv(boolean changed) {
        List<Formula> extractedStatements = new ArrayList<>();
        List<Formula> statementsToRemove = new ArrayList<>();
        for (Formula formula : knowledge) {
            if (formula instanceof TwoOperandFormula && ((TwoOperandFormula) formula).getOperator() == LogicOperator.IMP) {
                extractedStatements.add(new TwoOperandFormula(LogicOperator.OR, new OneOperandFormula(LogicOperator.NOT, ((TwoOperandFormula) formula).getFirstOperand()), ((TwoOperandFormula) formula).getSecondOperand()));
                extractedStatements.add(new TwoOperandFormula(LogicOperator.OR, new OneOperandFormula(LogicOperator.NOT, ((TwoOperandFormula) formula).getSecondOperand()), ((TwoOperandFormula) formula).getFirstOperand()));
                statementsToRemove.add(formula);
                changed = true;
            }
        }
        knowledge.addAll(extractedStatements);
        knowledge = new ArrayList<>(CollectionUtils.subtract(knowledge, statementsToRemove));
        return extractOr(changed);
    }

    /**
     * Extract the OR-shaped forms if the knowledge list has any.
     *
     * @param changed Tells you if you need to investigate the knowledge further.
     * @return {@code true} if Calculus.extractNec(knowledge, changed) and the new premodel provides true, {@code false} otherwise.
     * {@code true} if there is inconsistency and need to stop the examination, {@code false} otherwise.
     */
    public boolean extractOr(boolean changed) {
        List<Formula> newPremodel = new ArrayList<>(List.copyOf(knowledge));
        for (Formula formula : knowledge) {
            if (formula instanceof TwoOperandFormula && ((TwoOperandFormula) formula).getOperator() == LogicOperator.OR) {
                knowledge.remove(formula);
                knowledge.add(((TwoOperandFormula)formula).getFirstOperand());
                newPremodel.remove(formula);
                newPremodel.add(((TwoOperandFormula)formula).getSecondOperand());
                changed = true;
                return extractNec(changed) && new Calculus(newPremodel).isCorrectKnowledge();
            }
        }
        return extractNec(changed);
    }

    /**
     * Extract the NEC-shaped forms if the knowledge list has any.
     *
     * @param changed Tells if need to investigate the knowledge further.
     * @return {@code true} if Calculus.stop(knowledge, changed) provides true, {@code false} otherwise.
     * {@code true} if there is inconsistency and need to stop the examination, {@code false} otherwise.
     */
    public boolean extractNec(boolean changed) {
        List<Formula> extractedStatements = new ArrayList<>();
        for (Formula formula : knowledge) {
            if (formula instanceof OneOperandFormula && ((OneOperandFormula) formula).getOperator() == LogicOperator.NEC && !knowledge.contains(((OneOperandFormula) formula).getOperand()) && !expandedNEC.contains(formula)) {
                extractedStatements.add(((OneOperandFormula) formula).getOperand());
                expandedNEC.add(formula);
                changed = true;
            }
        }
        knowledge.addAll(extractedStatements);
        return stop(changed);
    }

    /**
     * Decides whether the formula list contains a syntactic inconsistency.
     *
     * @return {@code false} if the list contains a syntactic inconsistency, {@code true} otherwise.
     */
    public boolean isSyntacticallyCorrect() {
        for (Formula formula : knowledge) {
            if (formula instanceof OneOperandFormula && ((OneOperandFormula) formula).getOperator() == LogicOperator.NOT && knowledge.contains(((OneOperandFormula) formula).getOperand())) {
                return false;
            }
        }
        return true;
    }

    /**
     * It creates a new world that contains the formula of the possibility statement and all the necessities that continue to be inherited.
     *
     * @param possibilityToExpand The statement that contains the possibility that we want to expand.
     * @return A list of knowledge in the new world.
     */
    public List<Formula> createNewWorld(OneOperandFormula possibilityToExpand) {
        List<Formula> newWorld = new ArrayList<>(Collections.singletonList(possibilityToExpand.getOperand()));
        for (Formula formula : knowledge) {
            if (formula instanceof OneOperandFormula) {
                OneOperandFormula oneOperandFormula = ((OneOperandFormula) formula);
                if (oneOperandFormula.getOperator() == LogicOperator.NEC && oneOperandFormula.getWhoThink() == possibilityToExpand.getWhoThink()) {
                    newWorld.add(formula);
                }
            }
        }
        return newWorld;
    }
}
