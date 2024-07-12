package org.xmlevaluator.dto;

public class Variable extends Expression {
    private final String name;

    public Variable(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Variable that = (Variable) o;
        return name.equals(that.name);
    }
}