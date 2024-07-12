package org.xmlevaluator.dto;

public class Condition extends Expression {
    private final Expression ifExpression;
    private final Expression thenExpression;
    private final Expression elseExpression;

    public Condition(Expression ifExp, Expression thenExp, Expression elseExp) {
        this.ifExpression = ifExp;
        this.thenExpression = thenExp;
        this.elseExpression = elseExp;
    }

    public Expression getIfExpression() {
        return ifExpression;
    }

    public Expression getThenExpression() {
        return thenExpression;
    }

    public Expression getElseExpression() {
        return elseExpression;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Condition that = (Condition) o;
        return ifExpression.equals(that.ifExpression)
                && thenExpression.equals(that.thenExpression)
                && elseExpression.equals(that.elseExpression);
    }
}
