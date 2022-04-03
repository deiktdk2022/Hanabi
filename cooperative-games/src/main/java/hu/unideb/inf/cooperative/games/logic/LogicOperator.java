package hu.unideb.inf.cooperative.games.logic;

public enum LogicOperator {

    NOT(1),
    AND(2),
    OR(3),
    IMP(4),
    EQU(5),
    POS(6),
    NEC(7);

    private final int id;

    LogicOperator(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}
