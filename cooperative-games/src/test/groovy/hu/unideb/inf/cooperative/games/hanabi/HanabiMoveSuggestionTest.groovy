package hu.unideb.inf.cooperative.games.hanabi

import hu.unideb.inf.cooperative.games.logic.AtomicFormula
import hu.unideb.inf.cooperative.games.logic.LogicOperator
import hu.unideb.inf.cooperative.games.logic.OneOperandFormula
import spock.lang.Specification
import spock.lang.Unroll

class HanabiMoveSuggestionTest extends Specification {

    @Unroll
    def "Test probability of a card"() {
        given:
            def actual

        when:
            actual = HanabiMoveSuggestion.cardInHandProbability(player, deck, knowledge, cardsAtOtherPlayer, firework, throwingDekc, card, idx)

        then:
            actual == expected

        where:
            player << ['A' as char,
                       'A' as char,
                       'B' as char]
            deck << [HanabiState.makeDeck(),
                     HanabiState.makeDeck(),
                     HanabiState.makeDeck()]
            knowledge << [[new OneOperandFormula('A' as char, LogicOperator.NEC, new AtomicFormula('A' as char, 1, Color.BLUE, 0)),
                           new OneOperandFormula('A' as char, LogicOperator.NEC, new AtomicFormula('A' as char, 1, null, 1))],
                          [new OneOperandFormula('A' as char, LogicOperator.NEC, new AtomicFormula('A' as char, 1, Color.GREEN, 0)),
                           new OneOperandFormula('A' as char, LogicOperator.NEC, new AtomicFormula('A' as char, 1, null, 1))],
                          [new OneOperandFormula('B' as char, LogicOperator.NEC, new AtomicFormula('B' as char, 1, Color.RED, 0))]]
            cardsAtOtherPlayer << [[Card.YELLOW_ONE, Card.RED_FIVE, Card.BLUE_ONE, Card.GREEN_TWO, Card.GREEN_TWO],
                                   [Card.YELLOW_ONE, Card.RED_FIVE, Card.BLUE_ONE, Card.GREEN_ONE, Card.GREEN_TWO],
                                   []]
            firework << [[:],
                         [:],
                         [:]]
            throwingDekc << [[],
                             [Card.GREEN_TWO],
                             []]
            card << [Card.BLUE_ONE,
                     Card.GREEN_TWO,
                     Card.RED_FOUR]
            idx << [1,
                    1,
                    1]
            expected << [1.0,
                         0.0,
                         0.2]
    }


    @Unroll
    def "Test probability of a card to draw"() {
        given:
            def actual

        when:
            actual = HanabiMoveSuggestion.cardToDrawProbability(player, deck, knowledge, cardsAtOtherPlayer, firework, discardPile, discardCardIdx, cardToDiscard, cardToDraw)

        then:
            actual == expected

        where:
            player << ['A' as char,
                       'A' as char,
                       'B' as char]
            deck << [HanabiState.makeDeck(),
                     HanabiState.makeDeck(),
                     HanabiState.makeDeck()]
            knowledge << [[new OneOperandFormula('A' as char, LogicOperator.NEC, new AtomicFormula('A' as char, 1, Color.BLUE, 0))],
                          [new OneOperandFormula('A' as char, LogicOperator.NEC, new AtomicFormula('A' as char, 2, Color.BLUE, 1)),
                           new OneOperandFormula('A' as char, LogicOperator.NEC, new AtomicFormula('A' as char, 1, null, 1))],
                          [new OneOperandFormula('B' as char, LogicOperator.NEC, new AtomicFormula('B' as char, 1, Color.RED, 2)),
                           new OneOperandFormula('B' as char, LogicOperator.NEC, new AtomicFormula('B' as char, 3, Color.RED, 2))]]
            cardsAtOtherPlayer << [[Card.YELLOW_ONE, Card.RED_FIVE, Card.BLUE_ONE, Card.GREEN_TWO, Card.GREEN_TWO],
                                   [Card.YELLOW_ONE, Card.RED_FIVE, Card.BLUE_ONE, Card.GREEN_TWO, Card.GREEN_TWO],
                                   []]
            firework << [[(Color.RED): [Card.RED_ONE, Card.RED_TWO], (Color.BLUE): [Card.BLUE_ONE]],
                         [(Color.RED): [Card.RED_ONE, Card.RED_TWO], (Color.BLUE): [Card.BLUE_ONE]],
                         [(Color.RED): [Card.RED_ONE, Card.RED_TWO], (Color.BLUE): [Card.BLUE_ONE]]]
            discardPile << [[Card.YELLOW_ONE],
                            [Card.GREEN_ONE],
                            []]
            cardToDiscard << [Card.BLUE_ONE,
                              Card.GREEN_ONE,
                              Card.RED_FOUR]
            discardCardIdx << [1,
                               1,
                               1]
            cardToDraw << [Card.YELLOW_ONE,
                           Card.GREEN_TWO,
                           Card.RED_TWO]
            expected << [0.025,
                         0.0,
                         0.0]
    }
}
