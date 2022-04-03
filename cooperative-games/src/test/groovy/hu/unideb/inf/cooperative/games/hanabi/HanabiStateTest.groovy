package hu.unideb.inf.cooperative.games.hanabi

import hu.unideb.inf.cooperative.games.knowledge.Knowledge
import hu.unideb.inf.cooperative.games.logic.AtomicFormula
import hu.unideb.inf.cooperative.games.logic.OneOperandFormula

import static hu.unideb.inf.cooperative.games.hanabi.Card.*
import static hu.unideb.inf.cooperative.games.hanabi.Color.*
import static hu.unideb.inf.cooperative.games.logic.LogicOperator.*
import static org.assertj.core.api.Assertions.*
import spock.lang.Specification
import spock.lang.Unroll

class HanabiStateTest extends Specification {

    @Unroll
    def "Create starting state with exactly one card of 5"() {
        given:
            def actual

        when:
            actual = new HanabiState.HanabiStateBuilder()
                    .withCardsInPlayerAHand(list)
                    .build()

        then:
            assertThat(actual.cardsInPlayerAHand).isEqualTo(expectedList)

        where:
            list                                                   | expectedList
            [BLUE_FIVE, BLUE_FOUR, GREEN_FOUR, RED_ONE, GREEN_TWO] | [BLUE_FIVE, BLUE_FOUR, GREEN_FOUR, RED_ONE, GREEN_TWO]
    }

    @Unroll
    def "Create starting state with exactly one card of 5 with ..."() {
        given:
            def actual

        when:
            actual = new HanabiState.HanabiStateBuilder()
                    .withCardsInPlayerAHand(list)
                    .build()

        then:
            assertThat(actual.cardsInPlayerAHand).isNotEqualTo(expectedList)

        where:
            list                                                   | expectedList
            [BLUE_FIVE, GREEN_FOUR, BLUE_FOUR, RED_ONE, GREEN_TWO] | [BLUE_FIVE, BLUE_FOUR, GREEN_FOUR, RED_ONE, GREEN_TWO]
    }
}
