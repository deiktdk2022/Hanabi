package hu.unideb.inf.cooperative.games.hanabi;

import hu.unideb.inf.cooperative.games.game.Game;
import hu.unideb.inf.cooperative.games.game.MoveSuggestion;
import hu.unideb.inf.cooperative.games.logic.AtomicFormula;
import hu.unideb.inf.cooperative.games.logic.Formula;
import hu.unideb.inf.cooperative.games.logic.LogicOperator;
import hu.unideb.inf.cooperative.games.logic.OneOperandFormula;
import hu.unideb.inf.cooperative.games.statespace.Operator;
import hu.unideb.inf.cooperative.games.statespace.State;
import org.apache.commons.math3.analysis.function.Log;

import java.util.*;

import static hu.unideb.inf.cooperative.games.hanabi.Card.*;

public class HanabiGame extends Game {


    public static class HanabiGameBuilder extends Game.GameBuilder<HanabiGameBuilder> {

        public HanabiGameBuilder(HanabiState state) {
            super(state);
        }

        public HanabiGameBuilder withEndState() {
            return this;
        }

        @Override
        public HanabiGame build() {
            return new HanabiGame(this);
        }
    }

    @Override
    public void play() {
        State state = start;
        MoveSuggestion move = null;
        int count = 0;
        while (true) {
            System.out.println("Current state: " + System.lineSeparator() + state);
            if (state.endState()) {
                break;
            }
            System.out.printf("Move #%s%n", count + 1);
            count++;
            Operator op = null;
            if (!isPlayingAgainstHuman && (isMachineStart && state.getPlayer() == 'A' || !isMachineStart && state.getPlayer() == 'B')) {
                move = new HanabiMoveSuggestion(state, depthForMachine);
                op = move.getSuggestedOp();
                if (op instanceof HelpStatementSimulator) {
                    op = new HelpStatement(((HelpStatementSimulator) op).getStatement());
                }
                System.out.printf("Machine's move: %s%n", op);
            } else {
                if (isThereMoveSuggestion) {
                    move = new HanabiMoveSuggestion(state, depthForHuman);
                    op = move.getSuggestedOp();
                    if (op instanceof HelpStatementSimulator) {
                        op = new HelpStatement(((HelpStatementSimulator) op).getStatement());
                    }
                    System.out.printf("The suggestion is: %s%n", op);
                }
                op = state.readOperator();
            }
            state = state.apply(op);
            state.changePlayer();
        }
        System.out.println("Game over!");
        System.out.println("Score achieved: " + state.scoreAchieved());
        System.out.println(state.successfulEnd());
    }

    public HanabiGame(HanabiGameBuilder hanabiGameBuilder) {
        super(hanabiGameBuilder);
    }

    public static void main(String[] args) {
        Map<Color, List<Card>> fireworkState = new EnumMap<>(Color.class);
        fireworkState.put(Color.RED, new ArrayList<>());
        fireworkState.put(Color.WHITE, new ArrayList<>());
        fireworkState.put(Color.BLUE, new ArrayList<>());
        fireworkState.put(Color.GREEN, new ArrayList<>());
        fireworkState.put(Color.YELLOW, new ArrayList<>());
        List<Card> deck = HanabiState.makeDeck();
        List<Card> cardsAtPlayerA = HanabiState.handingOutCards(deck);
        List<Card> cardsAtPlayerB = HanabiState.handingOutCards(deck);
        System.out.println("deck = " + deck);
        System.out.println("cardsAtPlayerA = " + cardsAtPlayerA);
        System.out.println("cardsAtPlayerB = " + cardsAtPlayerB);
        HanabiGame hanabiGame = new HanabiGame.HanabiGameBuilder(new HanabiState.HanabiStateBuilder()
                .withCardsInPlayerAHand(cardsAtPlayerA)
                .withCardsInPlayerBHand(cardsAtPlayerB)
                .withDeck(deck).withNoteTokens(8)
                .withPlayer('A')
                .withFireworkState(fireworkState)
                .build())
                .setDepthForHuman(1)
                .setDepthForMachine(1)
                .withMoveSuggestion()
                .build();
        System.out.println(hanabiGame);
        hanabiGame.play();
    }
}
