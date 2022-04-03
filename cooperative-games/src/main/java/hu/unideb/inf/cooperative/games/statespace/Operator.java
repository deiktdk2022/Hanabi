package hu.unideb.inf.cooperative.games.statespace;

public abstract class Operator {

    public abstract boolean applicable(State state);

    public abstract State apply(State state);
}
