package hu.unideb.inf.cooperative.games.hanabi

import spock.lang.Specification
import spock.lang.Unroll

class HanabiGameTest extends Specification {

    @Unroll
    def "Test DropCard operator"() {
        given:
            def actual

        when:
            actual = new DiscardCard(idx).apply(state)

        then:
            actual.discardPile == expected

        where:
            idx << [
                    1,
                    1,
                    3
            ]

            state << [
                    new HanabiState.HanabiStateBuilder()
                            .withCardsInPlayerAHand(Arrays.asList(Card.BLUE_FIVE, Card.GREEN_FOUR, Card.BLUE_FOUR, Card.RED_ONE, Card.GREEN_TWO))
                            .withCardsInPlayerBHand(Arrays.asList(Card.BLUE_ONE, Card.BLUE_FOUR, Card.GREEN_FOUR, Card.RED_ONE, Card.GREEN_TWO))
                            .withDeck(HanabiState.makeDeck())
                            .withNoteTokens(9)
                            .withPlayer('A' as char)
                            .build(),
                    new HanabiState.HanabiStateBuilder()
                            .withCardsInPlayerAHand(Arrays.asList(Card.BLUE_FIVE, Card.GREEN_FOUR, Card.BLUE_FOUR, Card.RED_ONE, Card.GREEN_TWO))
                            .withCardsInPlayerBHand(Arrays.asList(Card.BLUE_ONE, Card.BLUE_FOUR, Card.GREEN_FOUR, Card.RED_ONE, Card.GREEN_TWO))
                            .withNoteTokens(9)
                            .withPlayer('A' as char)
                            .build(),
                    new HanabiState.HanabiStateBuilder()
                            .withCardsInPlayerAHand(Arrays.asList(Card.RED_THREE, Card.RED_FOUR, Card.GREEN_TWO, Card.WHITE_TWO, Card.RED_ONE))
                            .withCardsInPlayerBHand(Arrays.asList(Card.GREEN_FOUR, Card.BLUE_FOUR, Card.YELLOW_FIVE, Card.BLUE_FIVE, Card.BLUE_ONE))
                            .withDeck(HanabiState.makeDeck())
                            .withThrowingDeck(Arrays.asList(Card.GREEN_FIVE))
                            .withNoteTokens(9)
                            .withStormTokens(1)
                            .withPlayer('B' as char)
                            .build()
            ]
            expected << [
                    [Card.BLUE_FIVE],
                    [Card.BLUE_FIVE],
                    [Card.GREEN_FIVE, Card.YELLOW_FIVE]
            ]
    }
}

