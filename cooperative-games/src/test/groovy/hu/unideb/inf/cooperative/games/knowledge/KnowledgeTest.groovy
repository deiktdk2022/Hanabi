package hu.unideb.inf.cooperative.games.knowledge

import hu.unideb.inf.cooperative.games.hanabi.Card
import hu.unideb.inf.cooperative.games.hanabi.Color
import hu.unideb.inf.cooperative.games.logic.AtomicFormula
import hu.unideb.inf.cooperative.games.logic.LogicOperator
import hu.unideb.inf.cooperative.games.logic.OneOperandFormula
import spock.lang.Specification
import spock.lang.Unroll

import static org.assertj.core.api.Assertions.assertThat

class KnowledgeTest extends Specification {

    @Unroll
    def "Calculate the knowledge from cards in other player hand"() {
        given:
            def actual

        when:
            actual = Knowledge.knowledgeFromViewForOwnCards(currentPlayer, cardsInOtherPlayerHand, throwingDeck, fireworkState)

        then:
            assertThat(actual).isEqualTo(expectedList)

        where:
            currentPlayer << [
                    'A' as char
            ]
            cardsInOtherPlayerHand << [
                    [Card.BLUE_FIVE, Card.BLUE_FOUR, Card.GREEN_FOUR, Card.RED_ONE, Card.GREEN_TWO]
            ]
            throwingDeck << [
                    [Card.BLUE_ONE]
            ]
            fireworkState << [
                    [(Color.RED)   : [],
                     (Color.WHITE) : [],
                     (Color.BLUE)  : [],
                     (Color.GREEN) : [],
                     (Color.YELLOW): []]
            ]
            expectedList << [
                    [new OneOperandFormula(LogicOperator.NEC, new OneOperandFormula(LogicOperator.NOT, new AtomicFormula('A' as char, 1, Color.BLUE, 5))),
                     new OneOperandFormula(LogicOperator.NEC, new OneOperandFormula(LogicOperator.NOT, new AtomicFormula('A' as char, 2, Color.BLUE, 5))),
                     new OneOperandFormula(LogicOperator.NEC, new OneOperandFormula(LogicOperator.NOT, new AtomicFormula('A' as char, 3, Color.BLUE, 5))),
                     new OneOperandFormula(LogicOperator.NEC, new OneOperandFormula(LogicOperator.NOT, new AtomicFormula('A' as char, 4, Color.BLUE, 5))),
                     new OneOperandFormula(LogicOperator.NEC, new OneOperandFormula(LogicOperator.NOT, new AtomicFormula('A' as char, 5, Color.BLUE, 5)))]
            ]
    }

    @Unroll
    def "Calculate the knowledge from cards in the throwing deck"() {
        given:
            def actual

        when:
            actual = Knowledge.knowledgeFromViewForOwnCards(currentPlayer, cardsInOtherPlayerHand, throwingDeck, fireworkState)

        then:
            assertThat(actual).isEqualTo(expectedList)

        where:
            currentPlayer << [
                    'A' as char
            ]
            cardsInOtherPlayerHand << [
                    []
            ]
            throwingDeck << [
                    [Card.BLUE_FIVE]
            ]
            fireworkState << [
                    [(Color.RED)   : [],
                     (Color.WHITE) : [],
                     (Color.BLUE)  : [],
                     (Color.GREEN) : [],
                     (Color.YELLOW): []]
            ]
            expectedList << [
                    [new OneOperandFormula(LogicOperator.NEC, new OneOperandFormula(LogicOperator.NOT, new AtomicFormula('A' as char, 1, Color.BLUE, 5))),
                     new OneOperandFormula(LogicOperator.NEC, new OneOperandFormula(LogicOperator.NOT, new AtomicFormula('A' as char, 2, Color.BLUE, 5))),
                     new OneOperandFormula(LogicOperator.NEC, new OneOperandFormula(LogicOperator.NOT, new AtomicFormula('A' as char, 3, Color.BLUE, 5))),
                     new OneOperandFormula(LogicOperator.NEC, new OneOperandFormula(LogicOperator.NOT, new AtomicFormula('A' as char, 4, Color.BLUE, 5))),
                     new OneOperandFormula(LogicOperator.NEC, new OneOperandFormula(LogicOperator.NOT, new AtomicFormula('A' as char, 5, Color.BLUE, 5)))]
            ]
    }

    @Unroll
    def "Calculate the knowledge from firework state"() {
        given:
            def actual

        when:
            actual = Knowledge.knowledgeFromViewForOwnCards(currentPlayer, cardsInOtherPlayerHand, throwingDeck, fireworkState)

        then:
            assertThat(actual).isEqualTo(expectedList)

        where:
            currentPlayer << [
                    'A' as char
            ]
            cardsInOtherPlayerHand << [
                    []
            ]
            throwingDeck << [
                    []
            ]
            fireworkState << [
                    [(Color.RED)   : [],
                     (Color.WHITE) : [],
                     (Color.BLUE)  : [Card.BLUE_ONE, Card.BLUE_TWO, Card.BLUE_THREE, Card.BLUE_FOUR, Card.BLUE_FIVE],
                     (Color.GREEN) : [],
                     (Color.YELLOW): []]
            ]
            expectedList << [
                    [new OneOperandFormula(LogicOperator.NEC, new OneOperandFormula(LogicOperator.NOT, new AtomicFormula('A' as char, 1, Color.BLUE, 5))),
                     new OneOperandFormula(LogicOperator.NEC, new OneOperandFormula(LogicOperator.NOT, new AtomicFormula('A' as char, 2, Color.BLUE, 5))),
                     new OneOperandFormula(LogicOperator.NEC, new OneOperandFormula(LogicOperator.NOT, new AtomicFormula('A' as char, 3, Color.BLUE, 5))),
                     new OneOperandFormula(LogicOperator.NEC, new OneOperandFormula(LogicOperator.NOT, new AtomicFormula('A' as char, 4, Color.BLUE, 5))),
                     new OneOperandFormula(LogicOperator.NEC, new OneOperandFormula(LogicOperator.NOT, new AtomicFormula('A' as char, 5, Color.BLUE, 5)))]
            ]
    }

    @Unroll
    def "Delete knowledge because of new card"() {
        given:
            def actual

        when:
            actual = Knowledge.deleteKnowledgeBecauseOfNewCard(playerKnowledge, 1, 'A' as char)

        then:
            assertThat(actual).isEqualTo(expectedList)

        where:
            playerKnowledge << [
                    [new OneOperandFormula(LogicOperator.NEC, new AtomicFormula('A' as char, 1, Color.BLUE, 2)),
                     new OneOperandFormula(LogicOperator.NEC, new AtomicFormula('A' as char, 2, Color.BLUE, 1)),
                     new OneOperandFormula(LogicOperator.NEC, new AtomicFormula('A' as char, 3, Color.BLUE, 1)),
                     new OneOperandFormula(LogicOperator.NEC, new AtomicFormula('A' as char, 4, Color.BLUE, 3)),
                     new OneOperandFormula(LogicOperator.NEC, new AtomicFormula('A' as char, 5, Color.BLUE, 2))],
                    [new OneOperandFormula(LogicOperator.NEC, new OneOperandFormula(LogicOperator.NOT, new AtomicFormula('A' as char, 1, Color.BLUE, 5))),
                     new OneOperandFormula(LogicOperator.NEC, new OneOperandFormula(LogicOperator.NOT, new AtomicFormula('A' as char, 2, Color.BLUE, 5))),
                     new OneOperandFormula(LogicOperator.NEC, new OneOperandFormula(LogicOperator.NOT, new AtomicFormula('A' as char, 3, Color.BLUE, 5))),
                     new OneOperandFormula(LogicOperator.NEC, new OneOperandFormula(LogicOperator.NOT, new AtomicFormula('A' as char, 4, Color.BLUE, 5))),
                     new OneOperandFormula(LogicOperator.NEC, new OneOperandFormula(LogicOperator.NOT, new AtomicFormula('A' as char, 5, Color.BLUE, 5)))],
                    [new OneOperandFormula(LogicOperator.NEC, new OneOperandFormula(LogicOperator.NEC, new OneOperandFormula(LogicOperator.NOT, new AtomicFormula('A' as char, 1, Color.BLUE, 5)))),
                     new OneOperandFormula(LogicOperator.NEC, new OneOperandFormula(LogicOperator.NEC, new OneOperandFormula(LogicOperator.NOT, new AtomicFormula('A' as char, 2, Color.BLUE, 5)))),
                     new OneOperandFormula(LogicOperator.NEC, new OneOperandFormula(LogicOperator.NEC, new OneOperandFormula(LogicOperator.NOT, new AtomicFormula('A' as char, 3, Color.BLUE, 5)))),
                     new OneOperandFormula(LogicOperator.NEC, new OneOperandFormula(LogicOperator.NEC, new OneOperandFormula(LogicOperator.NOT, new AtomicFormula('A' as char, 4, Color.BLUE, 5)))),
                     new OneOperandFormula(LogicOperator.NEC, new OneOperandFormula(LogicOperator.NEC, new OneOperandFormula(LogicOperator.NOT, new AtomicFormula('A' as char, 5, Color.BLUE, 5))))]
            ]
            expectedList << [
                    [new OneOperandFormula(LogicOperator.NEC, new AtomicFormula('A' as char, 2, Color.BLUE, 1)),
                     new OneOperandFormula(LogicOperator.NEC, new AtomicFormula('A' as char, 3, Color.BLUE, 1)),
                     new OneOperandFormula(LogicOperator.NEC, new AtomicFormula('A' as char, 4, Color.BLUE, 3)),
                     new OneOperandFormula(LogicOperator.NEC, new AtomicFormula('A' as char, 5, Color.BLUE, 2))],
                    [new OneOperandFormula(LogicOperator.NEC, new OneOperandFormula(LogicOperator.NOT, new AtomicFormula('A' as char, 2, Color.BLUE, 5))),
                     new OneOperandFormula(LogicOperator.NEC, new OneOperandFormula(LogicOperator.NOT, new AtomicFormula('A' as char, 3, Color.BLUE, 5))),
                     new OneOperandFormula(LogicOperator.NEC, new OneOperandFormula(LogicOperator.NOT, new AtomicFormula('A' as char, 4, Color.BLUE, 5))),
                     new OneOperandFormula(LogicOperator.NEC, new OneOperandFormula(LogicOperator.NOT, new AtomicFormula('A' as char, 5, Color.BLUE, 5)))],
                    [new OneOperandFormula(LogicOperator.NEC, new OneOperandFormula(LogicOperator.NEC, new OneOperandFormula(LogicOperator.NOT, new AtomicFormula('A' as char, 2, Color.BLUE, 5)))),
                     new OneOperandFormula(LogicOperator.NEC, new OneOperandFormula(LogicOperator.NEC, new OneOperandFormula(LogicOperator.NOT, new AtomicFormula('A' as char, 3, Color.BLUE, 5)))),
                     new OneOperandFormula(LogicOperator.NEC, new OneOperandFormula(LogicOperator.NEC, new OneOperandFormula(LogicOperator.NOT, new AtomicFormula('A' as char, 4, Color.BLUE, 5)))),
                     new OneOperandFormula(LogicOperator.NEC, new OneOperandFormula(LogicOperator.NEC, new OneOperandFormula(LogicOperator.NOT, new AtomicFormula('A' as char, 5, Color.BLUE, 5))))]
            ]
    }

    @Unroll
    def "Delete knowledge because of new statement"() {
        given:
            def actual

        when:
            actual = Knowledge.deleteKnowledgeBecauseOfNewStatement(playerKnowledge, newStatement)

        then:
            assertThat(actual).isEqualTo(expectedList)

        where:
            playerKnowledge << [
                    [new OneOperandFormula(LogicOperator.POS, new AtomicFormula('A' as char, 1, Color.BLUE, 0)),
                     new OneOperandFormula(LogicOperator.POS, new AtomicFormula('A' as char, 1, Color.GREEN, 0)),
                     new OneOperandFormula(LogicOperator.POS, new AtomicFormula('A' as char, 1, Color.BLUE, 3)),
                     new OneOperandFormula(LogicOperator.POS, new AtomicFormula('A' as char, 1, Color.RED, 2)),],
                    [new OneOperandFormula(LogicOperator.POS, new AtomicFormula('B' as char, 1, Color.BLUE, 0)),
                     new OneOperandFormula(LogicOperator.POS, new AtomicFormula('A' as char, 1, Color.GREEN, 0)),
                     new OneOperandFormula(LogicOperator.POS, new AtomicFormula('A' as char, 1, Color.BLUE, 3)),
                     new OneOperandFormula(LogicOperator.POS, new AtomicFormula('A' as char, 1, Color.RED, 2)),],
                    [new OneOperandFormula(LogicOperator.POS, new AtomicFormula('A' as char, 1, Color.BLUE, 0)),
                     new OneOperandFormula(LogicOperator.POS, new AtomicFormula('A' as char, 1, Color.GREEN, 0)),
                     new OneOperandFormula(LogicOperator.POS, new AtomicFormula('A' as char, 1, null, 1)),
                     new OneOperandFormula(LogicOperator.POS, new AtomicFormula('A' as char, 1, Color.BLUE, 3)),
                     new OneOperandFormula(LogicOperator.POS, new AtomicFormula('A' as char, 1, Color.RED, 2))],
                    [new OneOperandFormula(LogicOperator.POS, new AtomicFormula('A' as char, 1, Color.BLUE, 0)),
                     new OneOperandFormula(LogicOperator.POS, new AtomicFormula('A' as char, 1, Color.GREEN, 0)),
                     new OneOperandFormula(LogicOperator.POS, new AtomicFormula('A' as char, 1, Color.BLUE, 3)),
                     new OneOperandFormula(LogicOperator.POS, new AtomicFormula('A' as char, 1, Color.RED, 2)),
                     new OneOperandFormula(LogicOperator.POS, new AtomicFormula('A' as char, 2, Color.RED, 2)),
                     new OneOperandFormula(LogicOperator.NEC, new AtomicFormula('A' as char, 3, Color.RED, 1))],
                    [new OneOperandFormula(LogicOperator.POS, new AtomicFormula('A' as char, 1, null, 1)),
                     new OneOperandFormula(LogicOperator.POS, new AtomicFormula('A' as char, 1, Color.BLUE, 0)),
                     new OneOperandFormula(LogicOperator.POS, new AtomicFormula('A' as char, 1, Color.GREEN, 2)),
                     new OneOperandFormula(LogicOperator.POS, new AtomicFormula('A' as char, 1, Color.GREEN, 1))]
            ]
            newStatement << [
                    new OneOperandFormula(LogicOperator.NEC, new AtomicFormula('A' as char, 1, Color.BLUE, 0)),
                    new OneOperandFormula(LogicOperator.NEC, new AtomicFormula('A' as char, 1, Color.BLUE, 0)),
                    new OneOperandFormula(LogicOperator.NEC, new AtomicFormula('A' as char, 1, Color.BLUE, 0)),
                    new OneOperandFormula(LogicOperator.NEC, new AtomicFormula('A' as char, 1, Color.BLUE, 0)),
                    new OneOperandFormula(LogicOperator.NEC, new AtomicFormula('A' as char, 1, null, 1))
            ]
            expectedList << [
                    [new OneOperandFormula(LogicOperator.POS, new AtomicFormula('A' as char, 1, Color.BLUE, 3))],
                    [new OneOperandFormula(LogicOperator.POS, new AtomicFormula('B' as char, 1, Color.BLUE, 0)),
                     new OneOperandFormula(LogicOperator.POS, new AtomicFormula('A' as char, 1, Color.BLUE, 3))],
                    [new OneOperandFormula(LogicOperator.POS, new AtomicFormula('A' as char, 1, null, 1)),
                     new OneOperandFormula(LogicOperator.POS, new AtomicFormula('A' as char, 1, Color.BLUE, 3))],
                    [new OneOperandFormula(LogicOperator.POS, new AtomicFormula('A' as char, 1, Color.BLUE, 3)),
                     new OneOperandFormula(LogicOperator.POS, new AtomicFormula('A' as char, 2, Color.RED, 2)),
                     new OneOperandFormula(LogicOperator.NEC, new AtomicFormula('A' as char, 3, Color.RED, 1))],
                    [new OneOperandFormula(LogicOperator.POS, new AtomicFormula('A' as char, 1, Color.BLUE, 0)),
                     new OneOperandFormula(LogicOperator.POS, new AtomicFormula('A' as char, 1, Color.GREEN, 1))]
            ]
    }

    @Unroll
    def "Chek knowledge simplification"() {
        given:
            def actual

        when:
            Knowledge.simplifyAtomicFormulasWithNot(currentKnowledge, 'A' as char)
            actual = currentKnowledge

        then:
            assertThat(actual).isEqualTo(expectedList)

        where:
            currentKnowledge << [
                    [new OneOperandFormula('A' as char, LogicOperator.NEC, new OneOperandFormula(LogicOperator.NOT, new AtomicFormula('A' as char, 3, Color.RED, 1))),
                     new OneOperandFormula('A' as char, LogicOperator.NEC, new OneOperandFormula(LogicOperator.NOT, new AtomicFormula('A' as char, 3, Color.RED, 2))),
                     new OneOperandFormula('A' as char, LogicOperator.NEC, new OneOperandFormula(LogicOperator.NOT, new AtomicFormula('A' as char, 3, Color.RED, 3))),
                     new OneOperandFormula('A' as char, LogicOperator.NEC, new OneOperandFormula(LogicOperator.NOT, new AtomicFormula('A' as char, 3, Color.RED, 4))),
                     new OneOperandFormula('A' as char, LogicOperator.NEC, new OneOperandFormula(LogicOperator.NOT, new AtomicFormula('A' as char, 3, Color.RED, 5))),],
                    [new OneOperandFormula('A' as char, LogicOperator.NEC, new OneOperandFormula(LogicOperator.NOT, new AtomicFormula('A' as char, 2, Color.RED, 2))),
                     new OneOperandFormula('A' as char, LogicOperator.NEC, new OneOperandFormula(LogicOperator.NOT, new AtomicFormula('A' as char, 2, Color.YELLOW, 2))),
                     new OneOperandFormula('A' as char, LogicOperator.NEC, new OneOperandFormula(LogicOperator.NOT, new AtomicFormula('A' as char, 2, Color.BLUE, 2))),
                     new OneOperandFormula('A' as char, LogicOperator.NEC, new OneOperandFormula(LogicOperator.NOT, new AtomicFormula('A' as char, 2, Color.GREEN, 2))),
                     new OneOperandFormula('A' as char, LogicOperator.NEC, new OneOperandFormula(LogicOperator.NOT, new AtomicFormula('A' as char, 2, Color.WHITE, 2))),]
            ]

            expectedList << [
                    [new OneOperandFormula('A' as char, LogicOperator.NEC, new OneOperandFormula(LogicOperator.NOT, new AtomicFormula('A' as char, 3, Color.RED, 0)))],
                    [new OneOperandFormula('A' as char, LogicOperator.NEC, new OneOperandFormula(LogicOperator.NOT, new AtomicFormula('A' as char, 2, null, 2)))]
            ]
    }
}
