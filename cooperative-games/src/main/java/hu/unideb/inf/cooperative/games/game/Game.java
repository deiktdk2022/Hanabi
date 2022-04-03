package hu.unideb.inf.cooperative.games.game;

import hu.unideb.inf.cooperative.games.statespace.Operator;
import hu.unideb.inf.cooperative.games.statespace.State;

public class Game {

    protected final State start;
    protected final boolean isPlayingAgainstHuman;
    protected final boolean isMachineStart;
    protected final boolean isWorkingWithMinimax;
    protected final boolean isWorkingWithAlphabeta;
    protected final boolean isThereMoveSuggestion;
    protected final int depthForMachine;
    protected final int depthForHuman;

    public static class GameBuilder<T extends GameBuilder<T>> {

        private final State start;
        private boolean isPlayingAgainstHuman;
        private boolean isMachineStart;
        private boolean isWorkingWithMinimax;
        private boolean isWorkingWithAlphabeta;
        private boolean isThereMoveSuggestion;
        private int depthForMachine;
        private int depthForHuman;

        public GameBuilder(State start) {
            this.start = start;
        }

        public T withPlayingAgainstHuman() {
            this.isPlayingAgainstHuman = true;
            return (T) this;
        }

        public T withMachineStart() {
            this.isMachineStart = true;
            return (T) this;
        }

        public T withMinimax() {
            this.isWorkingWithMinimax = true;
            return (T) this;
        }

        public T withAlphabeta() {
            this.isWorkingWithAlphabeta = true;
            return (T) this;
        }

        public T withMoveSuggestion() {
            this.isThereMoveSuggestion = true;
            return (T) this;
        }

        public T setDepthForMachine(int depthForMachine) {
            this.depthForMachine = depthForMachine;
            return (T) this;
        }

        public T setDepthForHuman(int depthForHuman) {
            this.depthForHuman = depthForHuman;
            return (T) this;
        }

        public Game build() {
            return new Game(this);
        }
    }

    protected Game(GameBuilder<?> gameBuilder) {
        this.start = gameBuilder.start;
        this.isPlayingAgainstHuman = gameBuilder.isPlayingAgainstHuman;
        this.isMachineStart = gameBuilder.isMachineStart;
        this.isWorkingWithMinimax = gameBuilder.isWorkingWithMinimax;
        this.isWorkingWithAlphabeta = gameBuilder.isWorkingWithAlphabeta;
        this.isThereMoveSuggestion = gameBuilder.isThereMoveSuggestion;
        this.depthForMachine = gameBuilder.depthForMachine;
        this.depthForHuman = gameBuilder.depthForHuman;
    }

    public void play() {
        State state = start;
        MoveSuggestion move = null;
        while (true) {
            System.out.println("Current state: " + System.lineSeparator() + state);
            if (state.endState()) {
                break;
            }
            Operator op = null;
            if (!isPlayingAgainstHuman && (isMachineStart && state.getPlayer() == 'A' || !isMachineStart && state.getPlayer() == 'B')) {
                if (isWorkingWithMinimax && !isWorkingWithAlphabeta) {

                }
                if (isWorkingWithAlphabeta && !isWorkingWithMinimax) {

                }
                op = move.suggestedOp;
                System.out.printf("Machine's move: %s%n", op);
            } else {
                if (isThereMoveSuggestion) {
                    if (isWorkingWithMinimax && !isWorkingWithAlphabeta) {

                    }
                    if (isWorkingWithAlphabeta && !isWorkingWithMinimax) {

                    }
                    op = move.suggestedOp;
                    System.out.printf("The suggestion is: %s%n", op);
                }
                op = state.readOperator();
            }
            state = state.apply(op);
            state.changePlayer();
        }
        System.out.println("Game over!");
        System.out.println(state.successfulEnd());
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("isPlayingAgainstHuman: ").append(isPlayingAgainstHuman).append(System.lineSeparator());
        sb.append("isMachineStart: ").append(isMachineStart).append(System.lineSeparator());
        sb.append("isWorkingWithMinimax: ").append(isWorkingWithMinimax).append(System.lineSeparator());
        sb.append("isWorkingWithAlphabeta: ").append(isWorkingWithAlphabeta).append(System.lineSeparator());
        sb.append("isThereMoveSuggestion: ").append(isThereMoveSuggestion).append(System.lineSeparator());
        return sb.toString();
    }
}
