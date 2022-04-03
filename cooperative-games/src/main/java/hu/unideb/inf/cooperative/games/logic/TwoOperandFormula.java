package hu.unideb.inf.cooperative.games.logic;

import java.util.Objects;

public class TwoOperandFormula extends Formula {

    private Formula firstOperand;

    private Formula secondOperand;

    private LogicOperator operator;

    public TwoOperandFormula(LogicOperator operator, Formula firstOperand, Formula secondOperand) {
        this.firstOperand = firstOperand;
        this.secondOperand = secondOperand;
        this.operator = operator;
    }

    @Override
    public String toString() {
        return "(" + firstOperand + ") " + operator + " (" + secondOperand + ")";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TwoOperandFormula that = (TwoOperandFormula) o;
        return Objects.equals(firstOperand, that.firstOperand) && Objects.equals(secondOperand, that.secondOperand) && operator == that.operator;
    }

    @Override
    public int hashCode() {
        return Objects.hash(firstOperand, secondOperand, operator);
    }

    public Formula getFirstOperand() {
        return firstOperand;
    }

    public Formula getSecondOperand() {
        return secondOperand;
    }

    public LogicOperator getOperator() {
        return operator;
    }
}
