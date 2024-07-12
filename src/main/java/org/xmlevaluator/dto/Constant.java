package org.xmlevaluator.dto;

public class Constant extends Expression {
    private final String value;

    public Constant(String value, ExpressionType expressionType) {
        if (expressionType == ExpressionType.DATE && value.length() == 10) {
            value += " 00:00:00";
        }
        this.value = value;
        this.setType(expressionType);
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Constant that = (Constant) o;
        return value.equals(that.value);
    }
}