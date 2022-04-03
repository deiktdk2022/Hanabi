package hu.unideb.inf.cooperative.games.logic;

import hu.unideb.inf.cooperative.games.hanabi.Color;

import java.util.Objects;

public class AtomicFormula extends Formula {

    private char whose;

    private int idx;

    private Color color;

    private int value;

    public AtomicFormula(char whose, int idx, Color color, int value) {
        this.whose = whose;
        this.idx = idx;
        this.color = color;
        this.value = value;
    }

    @Override
    public String toString() {
        return whose + "'s card #" + idx + ": " + color + " " + value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AtomicFormula that = (AtomicFormula) o;
        return whose == that.whose && idx == that.idx && value == that.value && color == that.color;
    }

    @Override
    public int hashCode() {
        return Objects.hash(whose, idx, color, value);
    }

    public char getWhose() {
        return whose;
    }

    public int getIdx() {
        return idx;
    }

    public Color getColor() {
        return color;
    }

    public int getValue() {
        return value;
    }
}
