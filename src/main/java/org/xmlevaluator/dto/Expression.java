package org.xmlevaluator.dto;


import java.util.ArrayList;

public class Expression {

    private ArrayList<Expression> parts;
    private ExpressionType expressionType;

    public Expression() {
        parts = new ArrayList<>();
        expressionType = ExpressionType.INTEGER;
    }

    public Expression(ExpressionType expressionType) {
        parts = new ArrayList<>();
        this.expressionType = expressionType;
    }

    public Integer getPartsCount() {
        return parts.size();
    }

    public Expression at(int index) {
        if (index < 0 || index >= parts.size()) {
            throw new IndexOutOfBoundsException("Index is out of [0.." + (parts.size() - 1) + "] range.");
        }
        return parts.get(index);
    }

    public ExpressionType getType() {
        return expressionType;
    }

    public void addPart(Expression expression) {
        parts.add(expression);
    }

    public void setType(ExpressionType expressionType) {
        this.expressionType = expressionType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Expression that = (Expression) o;
        return parts.equals(that.parts) &&
                expressionType.equals(that.expressionType);
    }
}
