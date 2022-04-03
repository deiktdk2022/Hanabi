package hu.unideb.inf.cooperative.games.hanabi;

public enum Color {

    RED(1),
    WHITE(2),
    BLUE(3),
    GREEN(4),
    YELLOW(5);

    private final int id;

    Color(final int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}
