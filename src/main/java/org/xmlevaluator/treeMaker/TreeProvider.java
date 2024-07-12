package org.xmlevaluator.treeMaker;

import org.xmlevaluator.dto.*;

public class TreeProvider {

    private static final String SPECIAL_SYMBOLS = " ,+*/()=";
    private static final String ARITHMETIC_SYMBOLS = "+-*/=";

    public boolean isVariableNameSymbol(char c) {
        return (Character.isLetter(c) || Character.isDigit(c) || c == ':');
    }

    // 0 - not a number
    // 1 - integer
    // 2 - double
    public Integer isNumber(String s) {
        boolean isDouble = false, isNegative = false;
        for (int i = 0; i < s.length(); ++i) {
            if (!Character.isDigit(s.charAt(i))) {
                if (s.charAt(i) == '.') {
                    if (isDouble) {
                        return 0;
                    }
                    isDouble = true;
                } else if (s.charAt(i) == '-') {
                    if (isNegative) {
                        return 0;
                    }
                    isNegative = true;
                } else {
                    return 0;
                }
            }
        }
        if (isDouble) {
            return 2;
        } else {
            return 1;
        }
    }

    private int findPositionAfterNextClosingBracket(String query, int startIndex) {
        int balance = 1;
        int endIndex = -1;
        for (int i = startIndex + 1; i < query.length(); ++i) {
            if (query.charAt(i) == '(') {
                ++balance;
            } else if (query.charAt(i) == ')') {
                --balance;
            }
            if (balance == 0) {
                endIndex = i + 1;
                break;
            }
        }
        if (endIndex == -1) {
            throw new UnsupportedOperationException("Found unmatched opening bracket.");
        }
        if (endIndex - startIndex <= 2) {
            throw new UnsupportedOperationException("Found empty brackets.");
        }
        return endIndex;
    }

    public Condition evaluateCondition(String query, int startIndex) {
        int thenIndex = query.indexOf("then", startIndex);
        if (thenIndex == -1) {
            throw new UnsupportedOperationException("'then' operator is not present after 'if'.");
        }
        int elseIndex = query.indexOf("else", thenIndex + 1);
        if (elseIndex == -1) {
            throw new UnsupportedOperationException("'else' operator is not present after 'if'.");
        }
        String ifQuery = query.substring(startIndex + 2, thenIndex);
        String thenQuery = query.substring(thenIndex + 4, elseIndex);
        String elseQuery = query.substring(elseIndex + 4);

        Expression ifExpression = parseQuery(ifQuery);
        Expression thenExpression = parseQuery(thenQuery);
        Expression elseExpression = parseQuery(elseQuery);

        return new Condition(ifExpression, thenExpression, elseExpression);
    }

    public Expression parseQuery(String query) {
        Expression expression = new Expression();
        for (int startIndex = 0; startIndex < query.length(); ) {
            if (query.charAt(startIndex) == ' ' || query.charAt(startIndex) == ',') {
                ++startIndex;
                continue;
            }
            int endIndex = startIndex + 1; // substring is [firstIndex; endIndex)
            if (query.charAt(startIndex) == '(') {
                endIndex = findPositionAfterNextClosingBracket(query, startIndex);
                Expression currentExpression = parseQuery(query.substring(startIndex + 1, endIndex - 1));
                expression.addPart(currentExpression);
            } else if (query.charAt(startIndex) == '$') {
                while (endIndex < query.length() && isVariableNameSymbol(query.charAt(endIndex))) {
                    ++endIndex;
                }
                if (endIndex - startIndex <= 1) {
                    throw new UnsupportedOperationException("Found mistake in the variable name.");
                }
                expression.addPart(new Variable(query.substring(startIndex + 1, endIndex)));
            } else if (query.charAt(startIndex) == '\'') {
                endIndex = query.indexOf('\'', startIndex + 1);
                if (endIndex == -1 || endIndex == startIndex) {
                    throw new IllegalArgumentException("Incorrect constant provided.");
                }
                String constant = query.substring(startIndex + 1, endIndex);
                expression.addPart(new Constant(constant, ExpressionType.STRING));
                ++endIndex;
            } else if (startIndex + 1 < query.length() && query.substring(startIndex, startIndex + 2).equals("//")) {
                expression.addPart(new XPathElement(query.substring(startIndex)));
                endIndex = query.length();
            } else if (ARITHMETIC_SYMBOLS.indexOf(query.charAt(startIndex)) != -1) {
                String operatorName = query.substring(startIndex, endIndex);
                int operatorTypeNumber;
                if ((operatorTypeNumber = Operator.Type.getAllStringTypes().indexOf(operatorName)) == -1) {
                    throw new UnsupportedOperationException("Found unknown operator " + operatorName + ".");
                }
                expression.addPart(new Operator(Operator.Type.values()[operatorTypeNumber]));
            } else if (startIndex + 1 < query.length() && query.substring(startIndex, startIndex + 2).equals("if")) {
                expression.addPart(evaluateCondition(query, startIndex));
                endIndex = query.length();
            } else {
                for (; endIndex < query.length() && SPECIAL_SYMBOLS.indexOf(query.charAt(endIndex)) == -1; ++endIndex) {

                }
                String queryPart = query.substring(startIndex, endIndex);
                int numberType = isNumber(queryPart);
                if (numberType != 0) {
                    if (numberType == 1) {
                        expression.addPart(new Constant(queryPart, ExpressionType.INTEGER));
                    } else {
                        expression.addPart(new Constant(queryPart, ExpressionType.DOUBLE));
                    }
                } else {
                    String operatorName = queryPart;
                    int operatorTypeNumber;
                    if ((operatorTypeNumber = Operator.Type.getAllStringTypes().indexOf(operatorName)) == -1) {
                        throw new UnsupportedOperationException("Found unknown operator " + operatorName + ".");
                    }
                    expression.addPart(new Operator(Operator.Type.values()[operatorTypeNumber]));
                }

            }
            startIndex = endIndex;
        }
        if (expression.getPartsCount() == 1) {
            return expression.at(0);
        }
        return expression;
    }

    public Tree buildTree(String query) {
        return new Tree(parseQuery(query));
    }
}
