package hu.unideb.inf.cooperative.games.knowledge;

import hu.unideb.inf.cooperative.games.hanabi.Card;
import hu.unideb.inf.cooperative.games.hanabi.Color;
import hu.unideb.inf.cooperative.games.hanabi.HanabiCalculus;
import hu.unideb.inf.cooperative.games.hanabi.HanabiState;
import hu.unideb.inf.cooperative.games.logic.*;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public final class Knowledge {

    private Knowledge() {
    }

    public static List<Formula> knowledgeFromViewForOwnCards(char currentPlayer, List<Card> cardsInOtherPlayerHand, List<Card> discardPile, Map<Color, List<Card>> fireworkState) {
        List<Formula> knowledge = new ArrayList<>();
        List<Card> allCard = Stream.of(cardsInOtherPlayerHand, discardPile).flatMap(Collection::stream).collect(Collectors.toList());
        for (Map.Entry<Color, List<Card>> entry : fireworkState.entrySet()) {
            allCard.addAll(entry.getValue());
        }
        Map<Card, Long> numberOfCardInType = allCard.stream().collect(Collectors.groupingBy(e -> e, Collectors.counting()));
        for (Map.Entry<Card, Long> entry : numberOfCardInType.entrySet()) {
            int value = entry.getKey().getValue();
            Color color = entry.getKey().getColor();
            if (value == 5
                    || ((value == 4 || value == 3 || value == 2) && entry.getValue() == 2)
                    || (value == 1 && entry.getValue() == 3)) {
                IntStream.rangeClosed(1, 5).forEach(p -> knowledge.add(new OneOperandFormula(currentPlayer, LogicOperator.NEC, (new OneOperandFormula(LogicOperator.NOT, new AtomicFormula(currentPlayer, p, color, value))))));
            }
        }
        return knowledge;
    }

    public static List<List<Formula>> possibleHelpStatement(List<Card> cards, char player, List<Formula> currentKnowledge) {
        List<List<Formula>> statements = new ArrayList<>();
        statements.addAll(Knowledge.possibleHelpStatementForColor(cards, player, currentKnowledge));
        statements.addAll(Knowledge.possibleHelpStatementForValue(cards, player, currentKnowledge));
        statements.addAll(Knowledge.possibleHelpStatementForColorWithNot(cards, player, currentKnowledge));
        statements.addAll(Knowledge.possibleHelpStatementForValueWithNot(cards, player, currentKnowledge));
        return statements;
    }

    public static List<List<Formula>> possibleHelpStatementForColor(List<Card> cards, char player, List<Formula> currentKnowledge) {
        List<List<Formula>> result = new ArrayList<>();
        for (Color color : Color.values()) {
            List<Formula> statementForColor = new ArrayList<>();
            for (int i = 0; i < HanabiState.NUMBER_OF_CARDS_IN_HAND; i++) {
                if (cards.get(i) == null) {
                    continue;
                }
                if (cards.get(i).getColor() == color) {
                    statementForColor.add(new OneOperandFormula(player, LogicOperator.NEC, new AtomicFormula(player, i + 1, color, 0)));
                }
            }
            if (!statementForColor.isEmpty()) {
                for (Formula formula : statementForColor) {
                    if (!currentKnowledge.contains(formula)) {
                        result.add(statementForColor);
                        break;
                    }
                }
            }
        }
        return result;
    }

    public static List<List<Formula>> possibleHelpStatementForColorWithNot(List<Card> cards, char player, List<Formula> currentKnowledge) {
        List<List<Formula>> result = new ArrayList<>();
        for (Color color : Color.values()) {
            List<Formula> statementForColor = new ArrayList<>();
            for (int i = 0; i < HanabiState.NUMBER_OF_CARDS_IN_HAND; i++) {
                if (cards.get(i) == null) {
                    continue;
                }
                if (cards.get(i).getColor() != color) {
                    statementForColor.add(new OneOperandFormula(player, LogicOperator.NEC, new OneOperandFormula(LogicOperator.NOT, new AtomicFormula(player, i + 1, color, 0))));
                }
            }
            if (statementForColor.size() == 5) {
                for (Formula formula : statementForColor) {
                    if (!currentKnowledge.contains(formula)) {
                        result.add(statementForColor);
                        break;
                    }
                }
            }
        }
        return result;
    }

    public static List<List<Formula>> possibleHelpStatementForValue(List<Card> cards, char player, List<Formula> currentKnowledge) {
        List<List<Formula>> result = new ArrayList<>();
        for (int j = 1; j < 6; j++) {
            List<Formula> statementForValue = new ArrayList<>();
            for (int i = 0; i < HanabiState.NUMBER_OF_CARDS_IN_HAND; i++) {
                if (cards.get(i) == null) {
                    continue;
                }
                if (cards.get(i).getValue() == j) {
                    statementForValue.add(new OneOperandFormula(player, LogicOperator.NEC, new AtomicFormula(player, i + 1, null, j)));
                }
            }
            if (!statementForValue.isEmpty()) {
                for (Formula formula : statementForValue) {
                    if (!currentKnowledge.contains(formula)) {
                        result.add(statementForValue);
                        break;
                    }
                }
            }
        }
        return result;
    }

    public static List<List<Formula>> possibleHelpStatementForValueWithNot(List<Card> cards, char player, List<Formula> currentKnowledge) {
        List<List<Formula>> result = new ArrayList<>();
        for (int j = 1; j < 6; j++) {
            List<Formula> statementForValue = new ArrayList<>();
            for (int i = 0; i < HanabiState.NUMBER_OF_CARDS_IN_HAND; i++) {
                if (cards.get(i) == null) {
                    continue;
                }
                if (cards.get(i).getValue() != j) {
                    statementForValue.add(new OneOperandFormula(player, LogicOperator.NEC, new OneOperandFormula(LogicOperator.NOT, new AtomicFormula(player, i + 1, null, j))));
                }
            }
            if (statementForValue.size() == 5) {
                for (Formula formula : statementForValue) {
                    if (!currentKnowledge.contains(formula)) {
                        result.add(statementForValue);
                        break;
                    }
                }
            }
        }
        return result;
    }

    public static List<Formula> deleteKnowledgeBecauseOfNewCard(List<Formula> currentKnowledge, int idxOfCardToDelete, char player) {
        List<Formula> newKnowledge = new ArrayList<>();
        for (Formula formula : currentKnowledge) {
            if (formula instanceof OneOperandFormula) {
                OneOperandFormula oneOperandFormula = (OneOperandFormula) formula;
                while (true) {
                    if (oneOperandFormula.getOperand() instanceof AtomicFormula) {
                        if (((AtomicFormula) oneOperandFormula.getOperand()).getIdx() != idxOfCardToDelete || ((AtomicFormula) oneOperandFormula.getOperand()).getWhose() != player) {
                            newKnowledge.add(formula);
                        }
                        break;
                    }
                    oneOperandFormula = (OneOperandFormula) oneOperandFormula.getOperand();
                }
            }
        }
        return newKnowledge;
    }

    public static List<Formula> createAdditionalKnowledgeUsingCalculus(List<Formula> currentKnowledge, List<Formula> possibleStatements) {
        List<Formula> additionalKnowledge = new ArrayList<>();
        HanabiCalculus hanabiCalculus = new HanabiCalculus(currentKnowledge);
        for (Formula formula : possibleStatements) {
            if (!currentKnowledge.contains(formula) && hanabiCalculus.canAddTheNewStatement(formula)) {
                additionalKnowledge.add(formula);
            }
        }
        return additionalKnowledge;
    }

    public static void simplifyAtomicFormulasWithNot(List<Formula> currentKnowledge, char player) {
        List<Formula> formulasToRemove = new ArrayList<>();
        List<Formula> formulasToAdd = new ArrayList<>();
        for (int cardIdx = 1; cardIdx <= HanabiState.NUMBER_OF_CARDS_IN_HAND; cardIdx++) {
            int finalCardIdx = cardIdx;
            for (Color color : Color.values()) {
                List<Formula> formulasForColor = currentKnowledge.stream()
                        .filter(f -> f instanceof OneOperandFormula)
                        .filter(f -> ((OneOperandFormula) f).getOperand() instanceof OneOperandFormula)
                        .filter(f -> ((OneOperandFormula) ((OneOperandFormula) f).getOperand()).getOperator() == LogicOperator.NOT)
                        .filter(f -> ((OneOperandFormula) ((OneOperandFormula) f).getOperand()).getOperand() instanceof AtomicFormula)
                        .filter(f -> ((AtomicFormula) ((OneOperandFormula) ((OneOperandFormula) f).getOperand()).getOperand()).getWhose() == player)
                        .filter(f -> ((AtomicFormula) ((OneOperandFormula) ((OneOperandFormula) f).getOperand()).getOperand()).getIdx() == finalCardIdx)
                        .filter(f -> ((AtomicFormula) ((OneOperandFormula) ((OneOperandFormula) f).getOperand()).getOperand()).getColor() == color).collect(Collectors.toList());
                if (formulasForColor.stream().distinct().count() == 5) {
                    formulasToAdd.add(new OneOperandFormula(player, LogicOperator.NEC, new OneOperandFormula(LogicOperator.NOT, new AtomicFormula(player, cardIdx, color, 0))));
                    formulasToRemove.addAll(formulasForColor);
                }
            }
            for (int value = 1; value <= 5; value++) {
                int finalValue = value;
                List<Formula> formulasForColor = currentKnowledge.stream()
                        .filter(f -> f instanceof OneOperandFormula)
                        .filter(f -> ((OneOperandFormula) f).getOperand() instanceof OneOperandFormula)
                        .filter(f -> ((OneOperandFormula) ((OneOperandFormula) f).getOperand()).getOperator() == LogicOperator.NOT)
                        .filter(f -> ((OneOperandFormula) ((OneOperandFormula) f).getOperand()).getOperand() instanceof AtomicFormula)
                        .filter(f -> ((AtomicFormula) ((OneOperandFormula) ((OneOperandFormula) f).getOperand()).getOperand()).getWhose() == player)
                        .filter(f -> ((AtomicFormula) ((OneOperandFormula) ((OneOperandFormula) f).getOperand()).getOperand()).getIdx() == finalCardIdx)
                        .filter(f -> ((AtomicFormula) ((OneOperandFormula) ((OneOperandFormula) f).getOperand()).getOperand()).getValue() == finalValue).collect(Collectors.toList());
                if (formulasForColor.stream().distinct().count() == 5) {
                    formulasToAdd.add(new OneOperandFormula(player, LogicOperator.NEC, new OneOperandFormula(LogicOperator.NOT, new AtomicFormula(player, cardIdx, null, value))));
                    formulasToRemove.addAll(formulasForColor);
                }
            }
        }
        currentKnowledge.removeAll(formulasToRemove);
        for (Formula formula : formulasToAdd) {
            if (!currentKnowledge.contains(formula)) {
                currentKnowledge.add(formula);
            }
        }
    }

    public static List<Formula> createAdditionalKnowledge(List<Formula> newStatements, char player, List<Card> cardsInPlayerHand) {
        List<Formula> additionalKnowledge = new ArrayList<>();
        AtomicFormula baseOfTheNewStatement;
        boolean withNot = false;
        if (((OneOperandFormula) newStatements.get(0)).getOperand() instanceof OneOperandFormula) {
            baseOfTheNewStatement = (AtomicFormula) ((OneOperandFormula) ((OneOperandFormula) newStatements.get(0)).getOperand()).getOperand();
            withNot = true;
        } else {
            baseOfTheNewStatement = (AtomicFormula) ((OneOperandFormula) newStatements.get(0)).getOperand();
        }
        boolean[] cardIdx = new boolean[HanabiState.MAX_VALUE_OF_FIREWORK];
        Arrays.fill(cardIdx, false);
        for (Formula formula : newStatements) {
            if (withNot) {
                cardIdx[((AtomicFormula) ((OneOperandFormula) ((OneOperandFormula) formula).getOperand()).getOperand()).getIdx() - 1] = true;
            } else {
                cardIdx[((AtomicFormula) ((OneOperandFormula) formula).getOperand()).getIdx() - 1] = true;
            }
        }
        for (int i = 0; i < HanabiState.MAX_VALUE_OF_FIREWORK; i++) {
            if (!cardIdx[i] && cardsInPlayerHand.get(i) != null) {
                if (withNot) {
                    additionalKnowledge.add(new OneOperandFormula(player, LogicOperator.NEC, new AtomicFormula(player, i + 1, baseOfTheNewStatement.getColor(), baseOfTheNewStatement.getValue())));
                } else {
                    additionalKnowledge.add(new OneOperandFormula(player, LogicOperator.NEC, new OneOperandFormula(LogicOperator.NOT, new AtomicFormula(player, i + 1, baseOfTheNewStatement.getColor(), baseOfTheNewStatement.getValue()))));
                }
            }
        }
        return additionalKnowledge;
    }
}
