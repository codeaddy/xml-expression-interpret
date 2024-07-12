package org.xmlevaluator.dto;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public class Result<T> {
    private final T value;
    private final String stringValue;
    private ExpressionType type;

    public Result(T value, String stringValue, ExpressionType type) {
        this.value = value;
        this.stringValue = stringValue;
        this.type = type;
    }

    public T getValue() {
        return value;
    }

    public ExpressionType getType() {
        return type;
    }

    public void setType(ExpressionType type) {
        this.type = type;
    }

    public String getStringValue() {
        return stringValue;
    }

    @Override
    public String toString() {
        return stringValue;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Result that = (Result) o;
        return value.equals(that.value) && type.equals(that.type) && stringValue.equals(that.stringValue);
    }
}
