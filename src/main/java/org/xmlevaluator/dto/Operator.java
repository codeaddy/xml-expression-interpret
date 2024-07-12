package org.xmlevaluator.dto;

import java.util.ArrayList;

public class Operator extends Expression {
    public enum Type {
        PLUS("+", 2, true),
        MINUS("-", 2, true),
        MULTIPLY("*", 1, true),
        DIVIDE("/", 1, true),
        EXISTS("exists", 0, false),
        AND("and", 4, true),
        OR("or", 5, true),
        EQ("eq", 3, true),
        EQUAL("=", 3, true),
        NOTEQUAL("!=", 3, true),
        MAX("max", 0, false),
        MIN("min", 0, false),
        FN_DAY_FROM_DATE("fn:day-from-date", 0, false),
        FN_MONTH_FROM_DATE("fn:month-from-date", 0, false),
        FN_YEAR_FROM_DATE("fn:year-from-date", 0, false),
        FN_CONCAT("fn:concat", 0, false),
        XSD_DATE("xsd:date", 0, false),
        XSD_DAY_TIME_DURATION("xsd:dayTimeDuration", 0, false),
        XSD_YEAR_MONTH_DURATION("xsd:yearMonthDuration", 0, false),
        XSD_STRING("xsd:string", 0, false),
        ;

        private String stringType;
        private Integer priority;
        private Boolean isBinary;

        private Type(String stringType, Integer priority, Boolean isBinary) {
            this.stringType = stringType;
            this.priority = priority;
            this.isBinary = isBinary;
        }

        public String getStringType() {
            return stringType;
        }

        public Integer getPriority() {
            return priority;
        }

        public Boolean getIsBinary() {
            return isBinary;
        }

        public static ArrayList<String> getAllStringTypes() {
            ArrayList<String> result = new ArrayList<>();
            int index = 0;
            for (Operator.Type type : Operator.Type.values()) {
                result.add(type.getStringType());
            }
            return result;
        }

    }

    ;

    private Type type;
//    private static final Map<Type, Action>

    public Operator(Type type) {
        this.type = type;
    }

    public Type getOperatorType() {
        return type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Operator that = (Operator) o;
        return type.equals(that.type);
    }
}

// +, -, *, /, exists, fn:day-from-date, max, xsd:date, xsd:yearMonthDuration, xsd:dayTimeDuration,
// fn:concat, xsd:string, eq, or, =(==),