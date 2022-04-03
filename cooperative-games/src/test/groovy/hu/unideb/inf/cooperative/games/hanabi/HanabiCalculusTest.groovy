package hu.unideb.inf.cooperative.games.hanabi

import hu.unideb.inf.cooperative.games.logic.AtomicFormula
import hu.unideb.inf.cooperative.games.logic.LogicOperator
import hu.unideb.inf.cooperative.games.logic.OneOperandFormula
import spock.lang.Specification
import spock.lang.Unroll


class HanabiCalculusTest extends Specification {

    @Unroll
    @SuppressWarnings("GroovyAssignabilityCheck")
    def "Check if list of knowledge is correct with one level knowledge"() {
        given:
            def actual

        when:
            actual = new HanabiCalculus(knowledge).isCorrectKnowledge()

        then:
            actual == expected

        where:
            knowledge << [
                    [new OneOperandFormula(LogicOperator.NEC, new OneOperandFormula(LogicOperator.NOT, new AtomicFormula('A' as char, 1, Color.BLUE, 5))),
                     new OneOperandFormula(LogicOperator.POS, new AtomicFormula('A' as char, 2, Color.BLUE, 5))],
                    [new OneOperandFormula(LogicOperator.NEC, new OneOperandFormula(LogicOperator.NOT, new AtomicFormula('A' as char, 1, Color.BLUE, 5))),
                     new OneOperandFormula(LogicOperator.POS, new AtomicFormula('A' as char, 1, Color.BLUE, 5))],
                    [new OneOperandFormula(LogicOperator.POS, new OneOperandFormula(LogicOperator.NOT, new AtomicFormula('A' as char, 1, Color.BLUE, 5))),
                     new OneOperandFormula(LogicOperator.POS, new AtomicFormula('A' as char, 1, Color.BLUE, 5))],
                    [new OneOperandFormula(LogicOperator.POS, new OneOperandFormula(LogicOperator.NOT, new AtomicFormula('A' as char, 1, Color.BLUE, 5))),
                     new OneOperandFormula(LogicOperator.POS, new AtomicFormula('A' as char, 1, Color.GREEN, 5))],
                    [new OneOperandFormula(LogicOperator.NEC, new OneOperandFormula(LogicOperator.NOT, new AtomicFormula('A' as char, 1, Color.BLUE, 5))),
                     new OneOperandFormula(LogicOperator.NEC, new AtomicFormula('A' as char, 1, Color.BLUE, 5))]

            ]
            expected << [
                    true,
                    false,
                    true,
                    true,
                    false
            ]
    }

    @Unroll
    def "Check if list of knowledge is correct with more level knowledge"() {
        given:
            def actual

        when:
            actual = new HanabiCalculus(knowledge).isCorrectKnowledge()

        then:
            actual == expected

        where:
            knowledge << [
                    [new OneOperandFormula(LogicOperator.NEC, new OneOperandFormula(LogicOperator.NOT, new AtomicFormula('A' as char, 1, Color.BLUE, 0))),
                     new OneOperandFormula(LogicOperator.POS, new AtomicFormula('A' as char, 1, Color.BLUE, 0)),
                     new OneOperandFormula('A' as char, LogicOperator.POS, new OneOperandFormula('B' as char, LogicOperator.POS, new AtomicFormula('A' as char, 2, Color.BLUE, 5)))],
                    [new OneOperandFormula(LogicOperator.NEC, new OneOperandFormula(LogicOperator.NEC, new OneOperandFormula(LogicOperator.NOT, new AtomicFormula('A' as char, 1, Color.BLUE, 0)))),
                     new OneOperandFormula(LogicOperator.POS, new AtomicFormula('A' as char, 1, Color.BLUE, 0)),
                     new OneOperandFormula('A' as char, LogicOperator.POS, new OneOperandFormula('B' as char, LogicOperator.POS, new AtomicFormula('A' as char, 2, Color.BLUE, 5)))],
                    [new OneOperandFormula(LogicOperator.NEC, new OneOperandFormula(LogicOperator.NOT, new AtomicFormula('A' as char, 1, Color.BLUE, 0))),
                     new OneOperandFormula(LogicOperator.NEC, new OneOperandFormula(LogicOperator.NEC, new AtomicFormula('A' as char, 1, Color.BLUE, 0)))],
                    [new OneOperandFormula(LogicOperator.POS, new OneOperandFormula(LogicOperator.POS, new OneOperandFormula(LogicOperator.NOT, new AtomicFormula('A' as char, 1, Color.BLUE, 0)))),
                     new OneOperandFormula(LogicOperator.POS, new OneOperandFormula(LogicOperator.NEC, new OneOperandFormula(LogicOperator.NOT, new AtomicFormula('A' as char, 1, Color.BLUE, 0)))),
                     new OneOperandFormula(LogicOperator.NEC, new OneOperandFormula(LogicOperator.NEC, new AtomicFormula('A' as char, 1, Color.BLUE, 0)))]
            ]
            expected << [
                    false,
                    false,
                    false,
                    false
            ]
    }

    @Unroll
    def "Check if list of knowledge is correct"() {
        given:
            def actual

        when:
            actual = new HanabiCalculus(knowledge).canAddTheNewStatement(newStatement)

        then:
            actual == expected

        where:
            knowledge << [
                    [new OneOperandFormula('A' as char, LogicOperator.NEC, new AtomicFormula('A' as char, 1, Color.RED, 0))],
                    [new OneOperandFormula('A' as char, LogicOperator.NEC, new AtomicFormula('A' as char, 1, Color.BLUE, 0)),
                     new OneOperandFormula('B' as char, LogicOperator.NEC, new AtomicFormula('B' as char, 1, Color.YELLOW, 0)),
                     new OneOperandFormula('B' as char, LogicOperator.POS, new OneOperandFormula(LogicOperator.NOT, new AtomicFormula('B' as char, 1, Color.BLUE, 0))),
                     new OneOperandFormula(LogicOperator.NOT, new AtomicFormula('A' as char, 1, Color.YELLOW, 0))],
                    [new OneOperandFormula('A' as char, LogicOperator.NEC, new AtomicFormula('A' as char, 1, Color.BLUE, 0)),
                     new OneOperandFormula('B' as char, LogicOperator.NEC, new AtomicFormula('B' as char, 1, Color.YELLOW, 0)),
                     new OneOperandFormula('B' as char, LogicOperator.POS, new OneOperandFormula(LogicOperator.NOT, new AtomicFormula('B' as char, 1, Color.BLUE, 0))),
                     new OneOperandFormula(LogicOperator.NOT, new AtomicFormula('A' as char, 1, Color.YELLOW, 0))],
                    [new OneOperandFormula('A' as char, LogicOperator.NEC, new OneOperandFormula(LogicOperator.NOT, new AtomicFormula('A' as char, 1, Color.RED, 0))),
                     new OneOperandFormula('A' as char, LogicOperator.NEC, new OneOperandFormula(LogicOperator.NOT, new AtomicFormula('A' as char, 1, Color.YELLOW, 0))),
                     new OneOperandFormula('A' as char, LogicOperator.NEC, new OneOperandFormula(LogicOperator.NOT, new AtomicFormula('A' as char, 1, Color.BLUE, 0))),
                     new OneOperandFormula('A' as char, LogicOperator.NEC, new OneOperandFormula(LogicOperator.NOT, new AtomicFormula('A' as char, 1, Color.GREEN, 0))),],
                    [new OneOperandFormula('A' as char, LogicOperator.NEC, new OneOperandFormula(LogicOperator.NOT, new AtomicFormula('A' as char, 1, Color.YELLOW, 0))),
                     new OneOperandFormula('A' as char, LogicOperator.NEC, new OneOperandFormula(LogicOperator.NOT, new AtomicFormula('A' as char, 1, Color.BLUE, 0))),
                     new OneOperandFormula('A' as char, LogicOperator.NEC, new OneOperandFormula(LogicOperator.NOT, new AtomicFormula('A' as char, 1, Color.GREEN, 0)))],
                    [new OneOperandFormula('A' as char, LogicOperator.NEC, new AtomicFormula('A' as char, 1, null, 3)),
                     new OneOperandFormula('A' as char, LogicOperator.NEC, new AtomicFormula('A' as char, 2, null, 4))],
                    [new OneOperandFormula('A' as char, LogicOperator.NEC, new AtomicFormula('A' as char, 2, null, 3)),
                     new OneOperandFormula('A' as char, LogicOperator.NEC, new AtomicFormula('A' as char, 2, Color.GREEN, 0))],
                    [new OneOperandFormula('A' as char, LogicOperator.NEC, new AtomicFormula('A' as char, 1, null, 3)),
                     new OneOperandFormula('A' as char, LogicOperator.NEC, new AtomicFormula('A' as char, 1, Color.GREEN, 0))],
                    [new OneOperandFormula('A' as char, LogicOperator.NEC, new OneOperandFormula(LogicOperator.NOT, new AtomicFormula('A' as char, 1, Color.RED, 0))),
                     new OneOperandFormula('A' as char, LogicOperator.NEC, new OneOperandFormula(LogicOperator.NOT, new AtomicFormula('A' as char, 1, Color.YELLOW, 0))),
                     new OneOperandFormula('A' as char, LogicOperator.NEC, new OneOperandFormula(LogicOperator.NOT, new AtomicFormula('A' as char, 1, Color.BLUE, 0))),
                     new OneOperandFormula('A' as char, LogicOperator.NEC, new OneOperandFormula(LogicOperator.NOT, new AtomicFormula('A' as char, 1, Color.GREEN, 0)))],
                    [new OneOperandFormula('A' as char, LogicOperator.NEC, new OneOperandFormula(LogicOperator.NOT, new AtomicFormula('A' as char, 1, Color.RED, 0))),
                     new OneOperandFormula('A' as char, LogicOperator.NEC, new OneOperandFormula(LogicOperator.NOT, new AtomicFormula('A' as char, 1, Color.YELLOW, 0))),
                     new OneOperandFormula('A' as char, LogicOperator.NEC, new OneOperandFormula(LogicOperator.NOT, new AtomicFormula('A' as char, 1, Color.BLUE, 0))),
                     new OneOperandFormula('A' as char, LogicOperator.NEC, new OneOperandFormula(LogicOperator.NOT, new AtomicFormula('A' as char, 1, Color.GREEN, 0))),
                     new OneOperandFormula('A' as char, LogicOperator.NEC, new OneOperandFormula(LogicOperator.NOT, new AtomicFormula('A' as char, 1, Color.RED, 5)))],
                    [new OneOperandFormula('A' as char, LogicOperator.NEC, new OneOperandFormula(LogicOperator.NOT, new AtomicFormula('A' as char, 1, Color.RED, 0))),
                     new OneOperandFormula('A' as char, LogicOperator.NEC, new OneOperandFormula(LogicOperator.NOT, new AtomicFormula('A' as char, 1, Color.YELLOW, 0))),
                     new OneOperandFormula('A' as char, LogicOperator.NEC, new OneOperandFormula(LogicOperator.NOT, new AtomicFormula('A' as char, 1, Color.BLUE, 0))),
                     new OneOperandFormula('A' as char, LogicOperator.NEC, new OneOperandFormula(LogicOperator.NOT, new AtomicFormula('A' as char, 1, Color.GREEN, 0))),
                     new OneOperandFormula('A' as char, LogicOperator.NEC, new AtomicFormula('A' as char, 1, null, 5))]
            ]

            newStatement << [
                    new OneOperandFormula('A' as char, LogicOperator.NEC, new OneOperandFormula(LogicOperator.NOT, new AtomicFormula('A' as char, 1, Color.RED, 0))),
                    new OneOperandFormula('A' as char, LogicOperator.POS, new OneOperandFormula(LogicOperator.NOT, new AtomicFormula('A' as char, 1, Color.GREEN, 0))),
                    new OneOperandFormula('A' as char, LogicOperator.POS, new OneOperandFormula(LogicOperator.NOT, new AtomicFormula('A' as char, 1, Color.BLUE, 0))),
                    new OneOperandFormula('A' as char, LogicOperator.NEC, new AtomicFormula('A' as char, 1, Color.WHITE, 0)),
                    new OneOperandFormula('A' as char, LogicOperator.NEC, new AtomicFormula('A' as char, 1, Color.WHITE, 0)),
                    new OneOperandFormula('A' as char, LogicOperator.NEC, new AtomicFormula('A' as char, 1, null, 3)),
                    new OneOperandFormula('A' as char, LogicOperator.NEC, new AtomicFormula('A' as char, 2, Color.GREEN, 3)),
                    new OneOperandFormula('A' as char, LogicOperator.NEC, new OneOperandFormula(LogicOperator.NOT, new AtomicFormula('A' as char, 1, Color.GREEN, 3))),
                    new OneOperandFormula('A' as char, LogicOperator.NEC, new AtomicFormula('A' as char, 1, Color.RED, 5)),
                    new OneOperandFormula('A' as char, LogicOperator.NEC, new AtomicFormula('A' as char, 1, Color.RED, 0)),
                    new OneOperandFormula('A' as char, LogicOperator.NEC, new AtomicFormula('A' as char, 1, Color.GREEN, 5)),
            ]

            expected << [
                    false,
                    true,
                    false,
                    true,
                    false,
                    true,
                    true,
                    false,
                    false,
                    false,
                    false
            ]
    }
}
