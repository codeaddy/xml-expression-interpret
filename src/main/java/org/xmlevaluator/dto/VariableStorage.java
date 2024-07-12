package org.xmlevaluator.dto;

import java.util.HashMap;
import java.util.Map;

public class VariableStorage {
    private Map<String, String> value;
    private Map<String, ExpressionType> type;

    public VariableStorage() {
        value = new HashMap<>();
        type = new HashMap<>();
    }

    public void pushVariable(String name, String value, ExpressionType type) {
        if (type == ExpressionType.DATE) {
            if (value.length() == 10) {
                value += " 00:00:00";
            }
        }
        this.value.put(name, value);
        this.type.put(name, type);
    }

    public String getValue(String name) {
        return this.value.get(name);
    }

    public ExpressionType getType(String name) {
        return this.type.get(name);
    }
}
