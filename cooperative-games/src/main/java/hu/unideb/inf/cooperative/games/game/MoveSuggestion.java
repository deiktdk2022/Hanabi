package hu.unideb.inf.cooperative.games.game;

import hu.unideb.inf.cooperative.games.statespace.Operator;
import hu.unideb.inf.cooperative.games.statespace.State;

import java.util.Arrays;

public abstract class MoveSuggestion {

    protected State state;

    protected int depth;

    protected Operator suggestedOp;

    protected double[] utility = new double[2];

    protected int numberOfStates;

    public State getState() {
        return state;
    }

    public int getDepth() {
        return depth;
    }

    public Operator getSuggestedOp() {
        return suggestedOp;
    }

    public double[] getUtility() {
        return utility;
    }

    public int getNumberOfStates() {
        return numberOfStates;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Current state: ").append(state).append(System.lineSeparator());
        sb.append("Depth: ").append(depth).append(System.lineSeparator());
        sb.append("Suggested operator: ").append(suggestedOp).append(System.lineSeparator());
        sb.append("Utility: ").append(Arrays.toString(utility)).append(System.lineSeparator());
        sb.append("Number of states: ").append(numberOfStates).append(System.lineSeparator());
        return sb.toString();
    }
}
