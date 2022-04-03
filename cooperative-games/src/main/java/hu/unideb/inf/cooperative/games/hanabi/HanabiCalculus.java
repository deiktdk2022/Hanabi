package hu.unideb.inf.cooperative.games.hanabi;

import hu.unideb.inf.cooperative.games.knowledge.Calculus;
import hu.unideb.inf.cooperative.games.logic.*;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


public class HanabiCalculus extends Calculus {

    public HanabiCalculus(List<Formula> knowledge) {
        super(knowledge);
    }

    /**
     * Decides whether the original knowledge can be expanded with the new statement.
     * Expands the list of knowledge with the negation of the new statement and then checks whether the resulting list is correct.
     *
     * @param newStatementToTest The new statement with which we want to expand our knowledge.
     * @return {@code true} if the new statement can be added to the knowledge, {@code false} otherwise.
     */
    @Override
    public boolean canAddTheNewStatement(Formula newStatementToTest) {
        List<Formula> extendedKnowledge = new ArrayList<>(knowledge);
        extendedKnowledge.add(new OneOperandFormula(LogicOperator.NOT, newStatementToTest));
        return !new HanabiCalculus(extendedKnowledge).isCorrectKnowledge();
    }

    /**
     * Decides whether the formula list contains any inconsistency.
     * Coordinates the exploration and creation of worlds.
     *
     * @return {@code false} if the formula list contains any inconsistency, {@code true} otherwise.
     */
    @Override
    public boolean isCorrectKnowledge() {
        if (stop(true)) {
            return false;
        }
        for (Formula formula : knowledge) {
            if (formula instanceof OneOperandFormula && ((OneOperandFormula) formula).getOperator() == LogicOperator.POS && !new HanabiCalculus(createNewWorld((OneOperandFormula) formula)).isCorrectKnowledge()) {
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
    @Override
    public boolean stop(boolean changed) {
        if (!isSyntacticallyCorrect() || !isSemanticallyCorrect(changed)) {
            return true;
        }
        if (changed) {
            return extractNotNot(false);
        }
        return false;
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
                return extractNec(changed) && new HanabiCalculus(newPremodel).isCorrectKnowledge();
            }
        }
        return extractNec(changed);
    }

    /**
     * Decides whether the formula list contains a semantic inconsistency.
     *
     * @return {@code false} if the list contains a syntactic inconsistency, {@code true} otherwise.
     */
    public boolean isSemanticallyCorrect(boolean changed) {
        Map<Integer, List<Formula>> colorStatementForPlayerAsCard = new HashMap<>();
        Map<Integer, List<Formula>> valueStatementForPlayerAsCard = new HashMap<>();
        Map<Integer, List<Formula>> colorStatementForPlayerBsCard = new HashMap<>();
        Map<Integer, List<Formula>> valueStatementForPlayerBsCard = new HashMap<>();
        extractAndBetweenColorAndValue();
        boolean[] foundNotOperatorBetweenColorAndValue = new boolean[]{false, false, false, false, false};          //minden lap indexéhez tartozó NOT(szín,szám) állításokat külön bontok ki, és a megfelelő indexen jelzem, ha az a lapindex már kibontásra került.
        for (Formula formula : knowledge) {
            AtomicFormula atomicFormula;
            if (formula instanceof OneOperandFormula && ((OneOperandFormula) formula).getOperator() == LogicOperator.NOT && ((OneOperandFormula) formula).getOperand() instanceof AtomicFormula) {
                atomicFormula = (AtomicFormula) ((OneOperandFormula) formula).getOperand();
                if (atomicFormula.getColor() != null && atomicFormula.getValue() != 0) {
                    if (!changed && !foundNotOperatorBetweenColorAndValue[atomicFormula.getIdx() - 1] && !extractNotAndBetweenColorAndValue(formula)) {
                        return false;
                    }
                    foundNotOperatorBetweenColorAndValue[atomicFormula.getIdx() - 1] = true;
                }
            } else if (formula instanceof AtomicFormula) {
                atomicFormula = (AtomicFormula) formula;
            } else {
                continue;
            }
            if (atomicFormula.getWhose() == 'A') {
                if (atomicFormula.getColor() != null && atomicFormula.getValue() == 0) {
                    colorStatementForPlayerAsCard.computeIfAbsent(atomicFormula.getIdx(), k -> new ArrayList<>()).add(formula);
                }
                if (atomicFormula.getValue() != 0 && atomicFormula.getColor() == null) {
                    valueStatementForPlayerAsCard.computeIfAbsent(atomicFormula.getIdx(), k -> new ArrayList<>()).add(formula);
                }
            } else if (atomicFormula.getWhose() == 'B') {
                if (atomicFormula.getColor() != null && atomicFormula.getValue() == 0) {
                    colorStatementForPlayerBsCard.computeIfAbsent(atomicFormula.getIdx(), k -> new ArrayList<>()).add(formula);
                }
                if (atomicFormula.getValue() != 0 && atomicFormula.getColor() == null) {
                    valueStatementForPlayerBsCard.computeIfAbsent(atomicFormula.getIdx(), k -> new ArrayList<>()).add(formula);
                }
            }
        }
        List<Map<Integer, List<Formula>>> listOfMapsForExamine = new ArrayList<>(Arrays.asList(colorStatementForPlayerAsCard, colorStatementForPlayerBsCard, valueStatementForPlayerAsCard, valueStatementForPlayerBsCard));
        for (Map<Integer, List<Formula>> map : listOfMapsForExamine) {
            for (Map.Entry<Integer, List<Formula>> entry : map.entrySet()) {
                if (entry.getValue().stream().filter(f -> f instanceof OneOperandFormula).distinct().count() == 5 || 1 < entry.getValue().stream().filter(f -> f instanceof AtomicFormula).distinct().count()) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Extract {@code AtomicFormula} which contains statement for color and value too.
     */
    public void extractAndBetweenColorAndValue() {
        List<Formula> newFormulaToAdd = new ArrayList<>();
        List<Formula> statementsToRemove = new ArrayList<>();
        for (Formula formula : knowledge) {
            if (formula instanceof AtomicFormula && ((AtomicFormula) formula).getColor() != null && ((AtomicFormula) formula).getValue() != 0) {
                newFormulaToAdd.add(new AtomicFormula(((AtomicFormula) formula).getWhose(), ((AtomicFormula) formula).getIdx(), ((AtomicFormula) formula).getColor(), 0));
                newFormulaToAdd.add(new AtomicFormula(((AtomicFormula) formula).getWhose(), ((AtomicFormula) formula).getIdx(), null, ((AtomicFormula) formula).getValue()));
                statementsToRemove.add(formula);
            }
        }
        knowledge.addAll(newFormulaToAdd);
        knowledge = new ArrayList<>(CollectionUtils.subtract(knowledge, statementsToRemove));
    }

    /**
     * Extract {@code NOT(AtomicFormula)} which contains statement for color and value too, with create premodels and examine if they are correct or not.
     *
     * @param formula The formula which contains a statement for color and value too with NOT operator.
     * @return {@code true} if all the premodels correct, {@code false} otherwise.
     */
    public boolean extractNotAndBetweenColorAndValue(Formula formula) {

        AtomicFormula atomicFormula = (AtomicFormula) ((OneOperandFormula) formula).getOperand();
        List<Formula> forColor = new ArrayList<>();
        forColor.addAll(knowledge.stream().filter(f -> f instanceof AtomicFormula).filter(f -> ((AtomicFormula) f).getIdx() == atomicFormula.getIdx()).filter(f -> ((AtomicFormula) f).getWhose() == atomicFormula.getWhose()).collect(Collectors.toList()));
        forColor.addAll(knowledge.stream().filter(f -> f instanceof OneOperandFormula).filter(f -> ((OneOperandFormula) f).getOperator() == LogicOperator.NOT).filter(f -> ((OneOperandFormula) f).getOperand() instanceof AtomicFormula).filter(f -> ((AtomicFormula) ((OneOperandFormula) f).getOperand()).getIdx() == atomicFormula.getIdx()).filter(f -> ((AtomicFormula) ((OneOperandFormula) f).getOperand()).getWhose() == atomicFormula.getWhose()).collect(Collectors.toList()));
        forColor.add(new OneOperandFormula(LogicOperator.NOT, new AtomicFormula(atomicFormula.getWhose(), atomicFormula.getIdx(), atomicFormula.getColor(), 0)));
        forColor.remove(formula);
        HanabiCalculus hanabiCalculusForColor = new HanabiCalculus(forColor);
        List<Formula> forValue = new ArrayList<>();
        forValue.addAll(knowledge.stream().filter(f -> f instanceof AtomicFormula).filter(f -> ((AtomicFormula) f).getIdx() == atomicFormula.getIdx()).filter(f -> ((AtomicFormula) f).getWhose() == atomicFormula.getWhose()).collect(Collectors.toList()));
        forValue.addAll(knowledge.stream().filter(f -> f instanceof OneOperandFormula).filter(f -> ((OneOperandFormula) f).getOperator() == LogicOperator.NOT).filter(f -> ((OneOperandFormula) f).getOperand() instanceof AtomicFormula).filter(f -> ((AtomicFormula) ((OneOperandFormula) f).getOperand()).getIdx() == atomicFormula.getIdx()).filter(f -> ((AtomicFormula) ((OneOperandFormula) f).getOperand()).getWhose() == atomicFormula.getWhose()).collect(Collectors.toList()));
        forValue.add(new OneOperandFormula(LogicOperator.NOT, new AtomicFormula(atomicFormula.getWhose(), atomicFormula.getIdx(), null, atomicFormula.getValue())));
        forValue.remove(formula);
        HanabiCalculus hanabiCalculusForValue = new HanabiCalculus(forValue);
        return (hanabiCalculusForColor.isSemanticallyCorrect(false) && hanabiCalculusForColor.isSyntacticallyCorrect()) || (hanabiCalculusForValue.isSemanticallyCorrect(false) && hanabiCalculusForValue.isSyntacticallyCorrect());
    }
}
