package hu.unideb.inf.cooperative.games.logic;

import java.util.Objects;

public class OneOperandFormula extends Formula {

    private char whoThink;

    private Formula operand;

    private LogicOperator operator;

    public OneOperandFormula(LogicOperator operator, Formula firstOperand) {
        this('\0', operator, firstOperand);
    }

    public OneOperandFormula(char whoThink, LogicOperator operator, Formula firstOperand) {
        this.whoThink = whoThink;
        this.operand = firstOperand;
        this.operator = operator;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (whoThink == 'A' || whoThink == 'B') {
            sb.append(whoThink).append(" knows that ");
        }
        sb.append(operator).append(" (").append(operand).append(")");
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OneOperandFormula that = (OneOperandFormula) o;
        return Objects.equals(operand, that.operand) && operator == that.operator;
    }

    @Override
    public int hashCode() {
        return Objects.hash(operand, operator);
    }

    public Formula getOperand() {
        return operand;
    }

    public LogicOperator getOperator() {
        return operator;
    }

    public char getWhoThink() {
        return whoThink;
    }
}
