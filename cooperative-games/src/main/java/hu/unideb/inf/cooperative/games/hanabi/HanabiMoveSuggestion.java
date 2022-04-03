package hu.unideb.inf.cooperative.games.hanabi;

import hu.unideb.inf.cooperative.games.game.MoveSuggestion;
import hu.unideb.inf.cooperative.games.knowledge.Knowledge;
import hu.unideb.inf.cooperative.games.logic.AtomicFormula;
import hu.unideb.inf.cooperative.games.logic.Formula;
import hu.unideb.inf.cooperative.games.logic.LogicOperator;
import hu.unideb.inf.cooperative.games.logic.OneOperandFormula;
import hu.unideb.inf.cooperative.games.statespace.Operator;
import hu.unideb.inf.cooperative.games.statespace.State;
import org.apache.commons.collections4.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

public class HanabiMoveSuggestion extends MoveSuggestion {

    final double THRESHOLD = .0001;

    public HanabiMoveSuggestion(State state, int depth) {
        this.state = state;
        this.depth = depth;
        this.numberOfStates = 1;
        if (depth == 0 || state.endState()) {
            this.utility[0] = state.miniMaxUtility();
            this.utility[1] = 0.0;
        } else {
            this.utility[0] = Integer.MIN_VALUE;
            this.utility[1] = Integer.MAX_VALUE;
            Set<Operator> operators = new HashSet<>(State.getOperators());
            List<List<Formula>> helpStatement = Knowledge.possibleHelpStatement(
                    (state.getPlayer() == 'A' ? ((HanabiState) state).getCardsInPlayerBHand() : ((HanabiState) state).getCardsInPlayerAHand()),
                    state.getPlayer() == 'A' ? 'B' : 'A',
                    (((HanabiState) state).getCommonKnowledge()));

            for (List<Formula> formulas : helpStatement) {
                operators.add(new HelpStatementSimulator(formulas));
            }
            List<Set<String>> examinedKnowledgeSetForDiscard = new ArrayList<>();
            List<Set<String>> examinedKnowledgeSetForPut = new ArrayList<>();
            for (Operator op : operators) {
                if (!(op instanceof HelpStatementSimulator)) {
                    List<Formula> knowledge = ((HanabiState) state).getCommonKnowledge();
                    int cardIdx = op instanceof DiscardCard ? ((DiscardCard) op).idxOfCardToDiscard : ((PutCard) op).idxOfCardToPut;
                    Set<String> statementsForCard = new HashSet<>();
                    for (Formula formula : knowledge) {
                        OneOperandFormula oneOperandFormula = ((OneOperandFormula) formula);
                        boolean withNot = false;
                        if (oneOperandFormula.getOperand() instanceof OneOperandFormula && ((OneOperandFormula) oneOperandFormula.getOperand()).getOperator() == LogicOperator.NOT) {
                            oneOperandFormula = (OneOperandFormula) oneOperandFormula.getOperand();
                            withNot = true;
                        }
                        if (oneOperandFormula.getOperand() instanceof AtomicFormula && ((AtomicFormula) oneOperandFormula.getOperand()).getWhose() == state.getPlayer() && ((AtomicFormula) oneOperandFormula.getOperand()).getIdx() == cardIdx) {
                            statementsForCard.add(Boolean.toString(withNot) + ((AtomicFormula) oneOperandFormula.getOperand()).getColor() + Integer.toString(((AtomicFormula) oneOperandFormula.getOperand()).getValue()));
                        }
                    }
                    if (op instanceof DiscardCard) {
                        if (examinedKnowledgeSetForDiscard.contains(statementsForCard)) {
                            continue;
                        }
                        examinedKnowledgeSetForDiscard.add(statementsForCard);
                    } else {
                        if (examinedKnowledgeSetForPut.contains(statementsForCard)) {
                            continue;
                        }
                        examinedKnowledgeSetForPut.add(statementsForCard);
                    }
                }
                boolean applicable = false;
                double variance = 0;
                double probabilityForCardInHand;
                double expectedUtility = 0.0;
                int newNumberOfStates = 0;
                if (op instanceof DiscardCard) {
                    List<Double> possibleUtilities = new ArrayList<>();
                    for (Card cardInHand : Card.values()) {
                        probabilityForCardInHand = cardInHandProbability(state.getPlayer(),
                                HanabiState.makeDeck(),
                                (((HanabiState) state).getCommonKnowledge()),
                                (state.getPlayer() == 'A' ? ((HanabiState) state).getCardsInPlayerBHand() : ((HanabiState) state).getCardsInPlayerAHand()),
                                ((HanabiState) state).getFireworkState(),
                                ((HanabiState) state).getDiscardPile(),
                                cardInHand,
                                ((DiscardCard) op).idxOfCardToDiscard);
                        double expectedUtilityWithCardToDraw = 0.0;
                        double probabilityForCardToDraw;
                        for (Card cardToDraw : Card.values()) {
                            if (cardInHand.getValue() == 5 && cardInHand == cardToDraw) {
                                continue;
                            }
                            probabilityForCardToDraw = cardToDrawProbability(state.getPlayer(),
                                    HanabiState.makeDeck(),
                                    (((HanabiState) state).getCommonKnowledge()),
                                    (state.getPlayer() == 'A' ? ((HanabiState) state).getCardsInPlayerBHand() : ((HanabiState) state).getCardsInPlayerAHand()),
                                    ((HanabiState) state).getFireworkState(),
                                    ((HanabiState) state).getDiscardPile(),
                                    ((DiscardCard) op).idxOfCardToDiscard,
                                    cardInHand,
                                    cardToDraw);
                            DiscardCardSimulator possibleOp = new DiscardCardSimulator(cardInHand, cardToDraw, ((DiscardCard) op).idxOfCardToDiscard);
                            if (possibleOp.applicable(state)) {
                                applicable = true;
                                State newState = possibleOp.apply(state);
                                HanabiMoveSuggestion hanabiMoveSuggestion = new HanabiMoveSuggestion(newState, depth - 1);
                                expectedUtilityWithCardToDraw += (probabilityForCardToDraw * hanabiMoveSuggestion.utility[0]);
                                possibleUtilities.add(hanabiMoveSuggestion.utility[0]);
                                newNumberOfStates += hanabiMoveSuggestion.numberOfStates;
                            }
                        }
                        expectedUtility += (probabilityForCardInHand * expectedUtilityWithCardToDraw);
                    }
                    if (applicable) {
                        double sum = 0.0;
                        for (double possibleUtility : possibleUtilities) {
                            sum += Math.pow(possibleUtility - expectedUtility, 2.0);
                        }
                        variance = Math.sqrt(sum / possibleUtilities.size());
                    }
                } else if (op instanceof PutCard) {
                    List<Double> possibleUtilities = new ArrayList<>();
                    for (Card cardInHand : Card.values()) {
                        probabilityForCardInHand = cardInHandProbability(state.getPlayer(),
                                HanabiState.makeDeck(),
                                (((HanabiState) state).getCommonKnowledge()),
                                (state.getPlayer() == 'A' ? ((HanabiState) state).getCardsInPlayerBHand() : ((HanabiState) state).getCardsInPlayerAHand()),
                                ((HanabiState) state).getFireworkState(),
                                ((HanabiState) state).getDiscardPile(),
                                cardInHand,
                                ((PutCard) op).idxOfCardToPut);
                        double expectedUtilityWithCardToDraw = 0.0;
                        double probabilityForCardToDraw;
                        for (Card cardToDraw : Card.values()) {
                            if (cardInHand.getValue() == 5 && cardInHand == cardToDraw) {
                                continue;
                            }
                            probabilityForCardToDraw = cardToDrawProbability(state.getPlayer(),
                                    HanabiState.makeDeck(),
                                    (((HanabiState) state).getCommonKnowledge()),
                                    (state.getPlayer() == 'A' ? ((HanabiState) state).getCardsInPlayerBHand() : ((HanabiState) state).getCardsInPlayerAHand()),
                                    ((HanabiState) state).getFireworkState(),
                                    ((HanabiState) state).getDiscardPile(),
                                    ((PutCard) op).idxOfCardToPut,
                                    cardInHand,
                                    cardToDraw);
                            PutCardSimulator possibleOp = new PutCardSimulator(cardInHand, cardToDraw, ((PutCard) op).idxOfCardToPut);
                            if (possibleOp.applicable(state)) {
                                applicable = true;
                                State newState = possibleOp.apply(state);
                                HanabiMoveSuggestion hanabiMoveSuggestion = new HanabiMoveSuggestion(newState, depth - 1);
                                expectedUtilityWithCardToDraw += (probabilityForCardToDraw * hanabiMoveSuggestion.utility[0]);
                                possibleUtilities.add(hanabiMoveSuggestion.utility[0]);
                                newNumberOfStates += hanabiMoveSuggestion.numberOfStates;
                            }
                        }
                        expectedUtility += (probabilityForCardInHand * expectedUtilityWithCardToDraw);
                    }
                    if (applicable) {
                        double sum = 0.0;
                        for (double possibleUtility : possibleUtilities) {
                            sum += Math.pow(possibleUtility - expectedUtility, 2.0);
                        }
                        variance = Math.sqrt(sum / possibleUtilities.size());
                    }
                } else if (op.applicable(state)) {
                    applicable = true;
                    State newState = op.apply(state);
                    HanabiMoveSuggestion hanabiMoveSuggestion = new HanabiMoveSuggestion(newState, depth - 1);
                    expectedUtility += hanabiMoveSuggestion.utility[0];
                    variance = 0.0;
                    newNumberOfStates = hanabiMoveSuggestion.numberOfStates;
                }
                if (applicable) {
                    if (depth == 2) {
                        System.out.printf(Locale.US, "%s: %f, %f%n", op, expectedUtility, variance);
                    }
                    if (this.utility[0] < expectedUtility || ((Math.abs(this.utility[0] - expectedUtility) < THRESHOLD) && variance < this.utility[1]) || (this.utility[0] - expectedUtility < 10 && this.utility[1] - variance > 5)) {
                        this.utility[0] = expectedUtility;
                        this.utility[1] = variance;
                        this.suggestedOp = op;
                    }
                    this.numberOfStates += newNumberOfStates;
                }
            }
        }
    }

    protected static double cardInHandProbability(char player, List<Card> deck, List<Formula> knowledge, List<Card> cardsInOtherPlayerHand, Map<Color, List<Card>> firework, List<Card> throwingDeck, Card possibleCard, int idx) {
        deck = new ArrayList<>(CollectionUtils.subtract(deck, cardsInOtherPlayerHand));
        deck = new ArrayList<>(CollectionUtils.subtract(deck, firework.values().stream()
                .flatMap(Collection::stream)
                .collect(Collectors.toList())));
        deck = new ArrayList<>(CollectionUtils.subtract(deck, throwingDeck));
        List<Formula> knowledgeForCurrentCard = new ArrayList<>();
        for (Formula formula : knowledge) {
            if (formula instanceof OneOperandFormula
                    && ((OneOperandFormula) formula).getOperator() == LogicOperator.NEC
                    && ((OneOperandFormula) formula).getWhoThink() == player) {
                OneOperandFormula oneOperandFormula = ((OneOperandFormula) formula);
                if (oneOperandFormula.getOperand() instanceof AtomicFormula
                        && ((AtomicFormula) oneOperandFormula.getOperand()).getWhose() == player) {
                    AtomicFormula atomicFormula = (AtomicFormula) oneOperandFormula.getOperand();
                    if (atomicFormula.getColor() != null && atomicFormula.getValue() != 0 && atomicFormula.getIdx() != idx) {
                        deck.remove(Arrays.stream(Card.values())
                                .filter(c -> c.getColor() == atomicFormula.getColor()
                                        && c.getValue() == atomicFormula.getValue()).findFirst().get());
                    } else if (atomicFormula.getIdx() == idx) {
                        knowledgeForCurrentCard.add(oneOperandFormula.getOperand());
                    }
                } else if (oneOperandFormula.getOperand() instanceof OneOperandFormula
                        && ((OneOperandFormula) oneOperandFormula.getOperand()).getOperator() == LogicOperator.NOT
                        && ((OneOperandFormula) oneOperandFormula.getOperand()).getOperand() instanceof AtomicFormula
                        && ((AtomicFormula) ((OneOperandFormula) oneOperandFormula.getOperand()).getOperand()).getWhose() == player
                        && ((AtomicFormula) ((OneOperandFormula) oneOperandFormula.getOperand()).getOperand()).getIdx() == idx) {
                    knowledgeForCurrentCard.add(oneOperandFormula.getOperand());
                }
            }
        }
        for (Formula formula : knowledgeForCurrentCard) {
            if (formula instanceof AtomicFormula) {
                AtomicFormula atomicFormula = (AtomicFormula) formula;
                if (atomicFormula.getValue() == possibleCard.getValue() && atomicFormula.getColor() == possibleCard.getColor()) {
                    return 1.0;
                }
                if (atomicFormula.getValue() != 0 && atomicFormula.getColor() == null) {
                    deck.removeAll(deck.stream().filter(c -> c.getValue() != atomicFormula.getValue()).collect(Collectors.toList()));
                }
                if (atomicFormula.getColor() != null && atomicFormula.getValue() == 0) {
                    deck.removeAll(deck.stream().filter(c -> c.getColor() != atomicFormula.getColor()).collect(Collectors.toList()));
                }
            } else {
                AtomicFormula atomicFormula = (AtomicFormula) ((OneOperandFormula) formula).getOperand();
                if (atomicFormula.getValue() == possibleCard.getValue() && atomicFormula.getColor() == possibleCard.getColor()) {
                    deck.removeAll(deck.stream().filter(c -> c.getValue() == atomicFormula.getValue()).filter(c -> c.getColor() == atomicFormula.getColor()).collect(Collectors.toList()));
                }
                if (atomicFormula.getValue() != 0 && atomicFormula.getColor() == null) {
                    deck.removeAll(deck.stream().filter(c -> c.getValue() == atomicFormula.getValue()).collect(Collectors.toList()));
                }
                if (atomicFormula.getColor() != null && atomicFormula.getValue() == 0) {
                    deck.removeAll(deck.stream().filter(c -> c.getColor() == atomicFormula.getColor()).collect(Collectors.toList()));
                }
            }
        }
        if (!deck.contains(possibleCard)) {
            return 0.0;
        }
        return 1.0 * deck.stream().filter(c -> c.equals(possibleCard)).count() / deck.size();
    }

    protected static double cardToDrawProbability(char player, List<Card> deck, List<Formula> knowledge, List<Card> cardsInOtherPlayerHand, Map<Color, List<Card>> firework, List<Card> throwingDeck, int discardCardIdx, Card cardToDiscard, Card possibleCardToDraw) {
        deck = new ArrayList<>(CollectionUtils.subtract(deck, cardsInOtherPlayerHand));
        deck = new ArrayList<>(CollectionUtils.subtract(deck, firework.values().stream()
                .flatMap(Collection::stream)
                .collect(Collectors.toList())));
        deck = new ArrayList<>(CollectionUtils.subtract(deck, throwingDeck));
        for (Formula formula : knowledge) {
            if (formula instanceof OneOperandFormula
                    && ((OneOperandFormula) formula).getOperator() == LogicOperator.NEC
                    && ((OneOperandFormula) formula).getWhoThink() == player) {
                OneOperandFormula oneOperandFormula = ((OneOperandFormula) formula);
                if (oneOperandFormula.getOperand() instanceof AtomicFormula
                        && ((AtomicFormula) oneOperandFormula.getOperand()).getWhose() == player
                        && ((AtomicFormula) oneOperandFormula.getOperand()).getIdx() != discardCardIdx
                        && ((AtomicFormula) oneOperandFormula.getOperand()).getColor() != null
                        && ((AtomicFormula) oneOperandFormula.getOperand()).getValue() != 0) {
                    AtomicFormula atomicFormula = (AtomicFormula) oneOperandFormula.getOperand();
                    deck.remove(Arrays.stream(Card.values())
                            .filter(c -> c.getColor() == atomicFormula.getColor()
                                    && c.getValue() == atomicFormula.getValue()).findFirst().get());

                }
            }
        }
        deck.remove(cardToDiscard);
        if (deck.isEmpty()) {
            return 1.0;
        }
        if (!deck.contains(possibleCardToDraw)) {
            return 0.0;
        }
        return 1.0 * deck.stream().filter(c -> c.equals(possibleCardToDraw)).count() / deck.size();
    }

}

