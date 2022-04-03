package hu.unideb.inf.cooperative.games.statespace;

import java.util.*;

public abstract class State {

    protected char player;

    protected static Set<Operator> operators = new HashSet<>();

    public abstract boolean endState();

    public abstract int scoreCanBeAchieved();

    public abstract int scoreAchieved();

    public abstract String successfulEnd();

    public abstract int miniMaxUtility();

//    public abstract int negaMaxUtility();

    public void changePlayer() {
        if (player == 'A') {
            player = 'B';
        } else {
            player = 'A';
        }
    }

    public abstract Operator readOperator();

    public boolean applicable(Operator op) {
        return op.applicable(this);
    }

    public State apply(Operator op) {
        return op.apply(this);
    }

    public static Set<Operator> getOperators() {
        return operators;
    }

    public char getPlayer() {
        return player;
    }
}
