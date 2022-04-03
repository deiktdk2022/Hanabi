package hu.unideb.inf.cooperative.games.hanabi;

import hu.unideb.inf.cooperative.games.knowledge.Knowledge;
import hu.unideb.inf.cooperative.games.logic.AtomicFormula;
import hu.unideb.inf.cooperative.games.logic.Formula;
import hu.unideb.inf.cooperative.games.logic.LogicOperator;
import hu.unideb.inf.cooperative.games.logic.OneOperandFormula;
import hu.unideb.inf.cooperative.games.statespace.Operator;
import hu.unideb.inf.cooperative.games.statespace.State;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

public class HanabiState extends State {

    private static final Logger logger = LoggerFactory.getLogger(HanabiState.class);

    private static final Map<Character, List<Formula>> possibleNewKnowledgeForPlayers = new HashMap<>();

    public static final int MAX_VALUE_OF_FIREWORK = 5;
    public static final int NUMBER_OF_CARDS_IN_HAND = 5;
    public static final int NUMBER_OF_FIREWORKS = 5;
    public static final int MAX_VALUE_OF_NOTE_TOKENS = 8;

    static {
        for (char player : List.of('A', 'B')) {
            List<Formula> newStatements = new ArrayList<>();
            for (int idx = 1; idx <= NUMBER_OF_CARDS_IN_HAND; idx++) {
                for (Color color : Color.values()) {
                    for (int value = 1; value <= MAX_VALUE_OF_FIREWORK; value++) {
                        newStatements.add(new OneOperandFormula(player, LogicOperator.NEC, new AtomicFormula(player, idx, color, value)));
                    }
                    newStatements.add(new OneOperandFormula(player, LogicOperator.NEC, new AtomicFormula(player, idx, color, 0)));
                }
                for (int value = 1; value <= MAX_VALUE_OF_FIREWORK; value++) {
                    newStatements.add(new OneOperandFormula(player, LogicOperator.NEC, new AtomicFormula(player, idx, null, value)));
                }
            }
            possibleNewKnowledgeForPlayers.put(player, newStatements);
        }
        for (int cardIdx = 1; cardIdx <= NUMBER_OF_CARDS_IN_HAND; cardIdx++) {
            operators.add(new DiscardCard(cardIdx));
            operators.add(new PutCard(cardIdx));
        }
    }


    private final List<Card> deck;
    private final List<Card> cardsInPlayerAHand;
    private final List<Card> cardsInPlayerBHand;
    private List<Formula> commonKnowledge;
    private final Map<Color, List<Card>> fireworkState;
    private final List<Card> discardPile;
    private int noteTokens;
    private int stormTokens;
    private int plusSteps;

    public static class HanabiStateBuilder {

        private char player;
        private List<Card> deck = new ArrayList<>();
        private List<Card> cardsInPlayerAHand = new ArrayList<>();
        private List<Card> cardsInPlayerBHand = new ArrayList<>();
        private List<Formula> commonKnowledge = new ArrayList<>();
        private Map<Color, List<Card>> fireworkState = new EnumMap<>(Color.class);
        private List<Card> discardPile = new ArrayList<>();
        private int noteTokens;
        private int stormTokens;
        private int plusSteps;

        public HanabiStateBuilder withPlayer(char player) {
            this.player = player;
            return this;
        }

        public HanabiStateBuilder withDeck(List<Card> deck) {
            this.deck = new ArrayList<>(deck);
            return this;
        }

        public HanabiStateBuilder withCardsInPlayerAHand(List<Card> cardsInPlayerAHand) {
            this.cardsInPlayerAHand = new ArrayList<>(cardsInPlayerAHand);
            return this;
        }

        public HanabiStateBuilder withCardsInPlayerBHand(List<Card> cardsInPlayerBHand) {
            this.cardsInPlayerBHand = new ArrayList<>(cardsInPlayerBHand);
            return this;
        }

        public HanabiStateBuilder withCommonKnowledge(List<Formula> commonKnowledge) {
            this.commonKnowledge = new ArrayList<>(commonKnowledge);
            return this;
        }

        public HanabiStateBuilder withFireworkState(Map<Color, List<Card>> fireworkState) {
            this.fireworkState = new EnumMap<>(fireworkState);
            return this;
        }

        public HanabiStateBuilder withThrowingDeck(List<Card> discardPile) {
            this.discardPile = new ArrayList<>(discardPile);
            return this;
        }

        public HanabiStateBuilder withNoteTokens(int noteTokens) {
            this.noteTokens = noteTokens;
            return this;
        }

        public HanabiStateBuilder withStormTokens(int stormTokens) {
            this.stormTokens = stormTokens;
            return this;
        }

        public HanabiStateBuilder withPlusSteps(int plusSteps) {
            this.plusSteps = plusSteps;
            return this;
        }

        public HanabiState build() {
            return new HanabiState(this);
        }
    }

    private HanabiState(HanabiStateBuilder hanabiStateBuilder) {
        this.player = hanabiStateBuilder.player;
        this.cardsInPlayerAHand = hanabiStateBuilder.cardsInPlayerAHand;
        this.cardsInPlayerBHand = hanabiStateBuilder.cardsInPlayerBHand;
        this.commonKnowledge = hanabiStateBuilder.commonKnowledge;
        this.fireworkState = hanabiStateBuilder.fireworkState;
        this.noteTokens = hanabiStateBuilder.noteTokens;
        this.stormTokens = hanabiStateBuilder.stormTokens;
        this.deck = hanabiStateBuilder.deck;
        this.discardPile = hanabiStateBuilder.discardPile;
        this.plusSteps = hanabiStateBuilder.plusSteps;
    }

    public HanabiState copyHanabiState() {
        Map<Color, List<Card>> newFireworkState = new EnumMap<>(Color.class);
        for (Map.Entry<Color, List<Card>> entry : this.fireworkState.entrySet()) {
            newFireworkState.put(entry.getKey(), new ArrayList<>(entry.getValue()));
        }
        return new HanabiState.HanabiStateBuilder()
                .withPlayer(this.getPlayer())
                .withCommonKnowledge(new ArrayList<>(this.getCommonKnowledge()))
                .withCardsInPlayerAHand(new ArrayList<>(this.getCardsInPlayerAHand()))
                .withCardsInPlayerBHand(new ArrayList<>(this.getCardsInPlayerBHand()))
                .withFireworkState(newFireworkState)
                .withDeck(new ArrayList<>(this.getDeck()))
                .withNoteTokens(this.getNoteTokens())
                .withStormTokens(this.getStormTokens())
                .withThrowingDeck(new ArrayList<>(this.getDiscardPile()))
                .withPlusSteps(this.getPlusSteps())
                .build();
    }

    public static List<Card> makeDeck() {
        List<Card> result = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            result.add(Card.WHITE_ONE);
            result.add(Card.RED_ONE);
            result.add(Card.BLUE_ONE);
            result.add(Card.GREEN_ONE);
            result.add(Card.YELLOW_ONE);
        }
        for (int i = 0; i < 2; i++) {
            result.add(Card.WHITE_TWO);
            result.add(Card.RED_TWO);
            result.add(Card.BLUE_TWO);
            result.add(Card.GREEN_TWO);
            result.add(Card.YELLOW_TWO);
            result.add(Card.WHITE_THREE);
            result.add(Card.RED_THREE);
            result.add(Card.BLUE_THREE);
            result.add(Card.GREEN_THREE);
            result.add(Card.YELLOW_THREE);
            result.add(Card.WHITE_FOUR);
            result.add(Card.RED_FOUR);
            result.add(Card.BLUE_FOUR);
            result.add(Card.GREEN_FOUR);
            result.add(Card.YELLOW_FOUR);
        }
        result.add(Card.WHITE_FIVE);
        result.add(Card.RED_FIVE);
        result.add(Card.BLUE_FIVE);
        result.add(Card.GREEN_FIVE);
        result.add(Card.YELLOW_FIVE);
        Collections.shuffle(result);
        return result;
    }

    public static List<Card> handingOutCards(List<Card> deck) {
        List<Card> hand = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            hand.add(deck.remove(0));
        }
        return hand;
    }

    @Override
    public boolean endState() {
        int finishedFireworks = 0;
        for (Map.Entry<Color, List<Card>> entry : fireworkState.entrySet()) {
            for (Card card : entry.getValue()) {
                if (card.getValue() == 5) {
                    finishedFireworks++;
                }
            }
        }
        if (deck.isEmpty() && plusSteps < 2) {
            plusSteps++;
            return false;
        }
        return stormTokens == 3 || (deck.isEmpty() && plusSteps == 2) || (player == 'A' ? cardsInPlayerAHand.stream().filter(Objects::isNull).count() == 5 : cardsInPlayerBHand.stream().filter(Objects::isNull).count() == 5) || finishedFireworks == 5;
    }

    @Override
    public int scoreCanBeAchieved(){
        int maxScore = 25;
        for (Color color : Color.values()){
            if (discardPile.stream().filter(c -> c.getColor() == color && c.getValue() == 1).count() == 3){
                maxScore -= 5;
            } else if (discardPile.stream().filter(c -> c.getColor() == color && c.getValue() == 2).count() == 2){
                maxScore -= 4;
            } else if (discardPile.stream().filter(c -> c.getColor() == color && c.getValue() == 3).count() == 2){
                maxScore -= 3;
            } else if (discardPile.stream().filter(c -> c.getColor() == color && c.getValue() == 4).count() == 2){
                maxScore -= 2;
            } else if (discardPile.stream().filter(c -> c.getColor() == color && c.getValue() == 5).count() == 1){
                maxScore -= 1;
            }
        }
        return maxScore;
    }

    @Override
    public int scoreAchieved() {
        return fireworkState.values().stream().filter(f -> !f.isEmpty()).map(f -> f.get(f.size() - 1)).mapToInt(Card::getValue).sum();
    }

    @Override
    public String successfulEnd() {
        if (stormTokens == 3) {
            return "The players lost.";
        }
        int point = scoreAchieved();
        String res = "Legendary! The audience will never forget this show!";
        if (point <= 5) {
            res = "Oh dear! The crowd booed.";
        } else if (point <= 10) {
            res = "Poor! Hardly applaused.";
        } else if (point <= 15) {
            res = "OK! The viewers have seen better.";
        } else if (point <= 20) {
            res = "Good! The audience is pleased.";
        } else if (point <= 24) {
            res = "Very good! The audience is enthusiastic!";
        }
        return res;
    }

    @Override
    public int miniMaxUtility() {
        return utilityBasedOnFireworksAndKnowledge();
    }

    public int utilityBasedOnCurrentStatusOfFireworks() {
        return fireworkState.values().stream().filter(f -> !f.isEmpty()).map(f -> f.get(f.size() - 1)).mapToInt(Card::getValue).sum() * 10000 - (stormTokens == 3 ? 1000000 : 0);
    }

    public int utilityBasedOnCurrentAndPossibleFireworks() {
        int utility = this.utilityBasedOnCurrentStatusOfFireworks();
        if (20 < utility || discardPile.size() < 15) {
            utility += 500;
        } else if (discardPile.size() < 25) {
            utility += 200;
        }
        List<Card> currentFirework = fireworkState.values().stream().flatMap(List::stream).collect(Collectors.toList());
        for (Card card : discardPile) {
            if (card.getValue() == 1 && discardPile.stream().filter(c -> c.equals(card)).count() == 3 ||
                    card.getValue() == 2 && discardPile.stream().filter(c -> c.equals(card)).count() == 2 ||
                    card.getValue() == 3 && discardPile.stream().filter(c -> c.equals(card)).count() == 2 ||
                    card.getValue() == 4 && discardPile.stream().filter(c -> c.equals(card)).count() == 2 ||
                    card.getValue() == 5) {
                utility -= 250;
            }
            if (currentFirework.contains(card)) {
                utility += 250;
            }
        }
        return utility;
    }

    public int utilityBasedOnFireworksAndKnowledge() {
        int utility = this.utilityBasedOnCurrentAndPossibleFireworks();
        List<Card> currentPlayerHand = player == 'A' ? cardsInPlayerAHand : cardsInPlayerBHand;
        int cardIdx = 1;
        for (Card card : currentPlayerHand) {
            if (card == null) {
                continue;
            }
            if (card.getValue() == fireworkState.get(card.getColor()).size() + 1) {
                if (commonKnowledge.contains(new OneOperandFormula(player, LogicOperator.NEC, new AtomicFormula(player, cardIdx, card.getColor(), card.getValue())))) {
                    utility += 1000;
                }
                if (commonKnowledge.contains(new OneOperandFormula(player, LogicOperator.NEC, new AtomicFormula(player, cardIdx, card.getColor(), 0)))) {
                    utility += 1000;
                }
                if (commonKnowledge.contains(new OneOperandFormula(player, LogicOperator.NEC, new AtomicFormula(player, cardIdx, null, card.getValue())))) {
                    utility += 1000;
                }
            }
            cardIdx++;
        }
        return utility + (0 < noteTokens ? 50 : 0);
    }

    @Override
    public Operator readOperator() {
        Scanner sc = new Scanner(System.in);
        Operator operator = null;
        while (true) {
            System.out.print("Type your move, please: ");
            String[] input = sc.nextLine().split(" ");
            System.out.println();
            if (input.length == 1 && input[0].equals("")) {
                System.out.println("This operator is not applicable.");
                continue;
            } else if (input.length == 2 && input[0].equals("DiscardCard")) {
                operator = new DiscardCard(Integer.parseInt(input[1]));
            } else if (input.length == 2 && input[0].equals("PutCard")) {
                operator = new PutCard(Integer.parseInt(input[1]));
            } else if (input.length == 1 && input[0].equals("HelpStatement")) {
                List<List<Formula>> statements = null;
                if (player == 'A') {
                    statements = Knowledge.possibleHelpStatement(cardsInPlayerBHand, 'B', commonKnowledge);
                } else {
                    statements = Knowledge.possibleHelpStatement(cardsInPlayerAHand, 'A', commonKnowledge);
                }
                System.out.println("Possible help statements: ");
                statements.stream().forEach(System.out::println);
                System.out.print("Please choose one or type 0 to select another move: ");
                int idxOfHelp = Integer.parseInt(sc.nextLine());
                if (0 < idxOfHelp && idxOfHelp <= statements.size()) {
                    operator = new HelpStatement(statements.get(idxOfHelp - 1));
                }
            }
            if (operator != null && operator.applicable(this)) {
                break;
            } else {
                System.out.println("This operator is not applicable." + System.lineSeparator());
            }
        }
        return operator;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Score can be achieved: ").append(scoreCanBeAchieved()).append(System.lineSeparator());
        sb.append("Fireworks state: ").append(fireworkState.toString()).append(System.lineSeparator());
        sb.append("Cards in Player A hands: ").append(cardsInPlayerAHand.toString()).append(System.lineSeparator());
        sb.append("Cards in Player B hands: ").append(cardsInPlayerBHand.toString()).append(System.lineSeparator());
        sb.append("Common knowledge: ").append(System.lineSeparator()).append(String.join(System.lineSeparator(), commonKnowledge.stream().map(f -> " " + f.toString()).collect(Collectors.toList()))).append(System.lineSeparator());
        sb.append("Throwing deck: ").append(discardPile.toString()).append(System.lineSeparator());
        sb.append("Note tokens: ").append(noteTokens).append(System.lineSeparator());
        sb.append("Storm tokens: ").append(stormTokens).append(System.lineSeparator());
        sb.append("Next player: ").append(player).append(System.lineSeparator());
        return sb.toString();
    }

    public static Map<Character, List<Formula>> getPossibleNewKnowledgeForPlayers() {
        return possibleNewKnowledgeForPlayers;
    }

    public List<Card> getCardsInPlayerAHand() {
        return cardsInPlayerAHand;
    }

    public List<Card> getCardsInPlayerBHand() {
        return cardsInPlayerBHand;
    }

    public List<Card> getDeck() {
        return deck;
    }

    public void setCommonKnowledge(List<Formula> commonKnowledge) {
        this.commonKnowledge = commonKnowledge;
    }

    public List<Formula> getCommonKnowledge() {
        return commonKnowledge;
    }

    public Map<Color, List<Card>> getFireworkState() {
        return fireworkState;
    }

    public List<Card> getDiscardPile() {
        return discardPile;
    }

    public int getNoteTokens() {
        return noteTokens;
    }

    public int getStormTokens() {
        return stormTokens;
    }

    public void increaseStormTokens() {
        this.stormTokens++;
    }

    public void decreaseNoteTokens() {
        this.noteTokens--;
    }

    public void increaseNoteTokens() {
        this.noteTokens++;
    }

    public int getPlusSteps() {
        return plusSteps;
    }
}