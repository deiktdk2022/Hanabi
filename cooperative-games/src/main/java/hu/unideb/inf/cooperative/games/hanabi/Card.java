package hu.unideb.inf.cooperative.games.hanabi;

import java.util.Arrays;

public enum Card {

    RED_ONE(Color.RED, 1),
    RED_TWO(Color.RED, 2),
    RED_THREE(Color.RED, 3),
    RED_FOUR(Color.RED, 4),
    RED_FIVE(Color.RED, 5),
    BLUE_ONE(Color.BLUE, 1),
    BLUE_TWO(Color.BLUE, 2),
    BLUE_THREE(Color.BLUE, 3),
    BLUE_FOUR(Color.BLUE, 4),
    BLUE_FIVE(Color.BLUE, 5),
    YELLOW_ONE(Color.YELLOW, 1),
    YELLOW_TWO(Color.YELLOW, 2),
    YELLOW_THREE(Color.YELLOW, 3),
    YELLOW_FOUR(Color.YELLOW, 4),
    YELLOW_FIVE(Color.YELLOW, 5),
    GREEN_ONE(Color.GREEN, 1),
    GREEN_TWO(Color.GREEN, 2),
    GREEN_THREE(Color.GREEN, 3),
    GREEN_FOUR(Color.GREEN, 4),
    GREEN_FIVE(Color.GREEN, 5),
    WHITE_ONE(Color.WHITE, 1),
    WHITE_TWO(Color.WHITE, 2),
    WHITE_THREE(Color.WHITE, 3),
    WHITE_FOUR(Color.WHITE, 4),
    WHITE_FIVE(Color.WHITE, 5);

    private final Color color;
    private final int value;

    Card(final Color color, final int value) {
        this.color = color;
        this.value = value;
    }

    public Color getColor() {
        return color;
    }

    public int getValue() {
        return value;
    }
}
