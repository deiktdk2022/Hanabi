package hu.unideb.inf.cooperative.games.logic

import hu.unideb.inf.cooperative.games.hanabi.Color
import spock.lang.Specification
import spock.lang.Unroll


class FormulaGroovyTest extends Specification {

    @Unroll
    def "Create one operand formula with operator #b = operator #expected"() {
        given:
            def actual

        when:
            actual = new OneOperandFormula(b, a).getOperator()

        then:
            expected == actual

        where:
            a                                                || b                 || expected
            new AtomicFormula('A' as char, 1, Color.BLUE, 4) || LogicOperator.NOT || LogicOperator.NOT
    }

    @Unroll
    def "Create two operand formula with operator #b = operator #expected"() {
        def actual

        when:
            actual = new TwoOperandFormula(b, a, c).getOperator()

        then:
            expected == actual

        where:
            a                                                || b                 || c                                                 || expected
            new AtomicFormula('A' as char, 1, Color.BLUE, 4) || LogicOperator.AND || new AtomicFormula('A' as char, 2, Color.BLUE, 3)  || LogicOperator.AND
            new AtomicFormula('A' as char, 1, Color.BLUE, 4) || LogicOperator.OR  || new AtomicFormula('A' as char, 2, Color.GREEN, 3) || LogicOperator.OR
            new AtomicFormula('A' as char, 1, Color.BLUE, 4) || LogicOperator.IMP || new AtomicFormula('A' as char, 3, Color.BLUE, 3)  || LogicOperator.IMP
    }
}
