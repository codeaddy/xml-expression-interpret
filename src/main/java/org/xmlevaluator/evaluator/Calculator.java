package org.xmlevaluator.evaluator;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xmlevaluator.OperatorsRealizationLibrary;
import org.xmlevaluator.dto.*;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Calculator {
    OperatorsRealizationLibrary library;
    VariableStorage variableStorage;
    Document xmlDocument;

    public Calculator() {
        library = new OperatorsRealizationLibrary();
        variableStorage = new VariableStorage();
        xmlDocument = null;
    }

    public Calculator(VariableStorage vs) {
        library = new OperatorsRealizationLibrary();
        variableStorage = vs;
        xmlDocument = null;
    }

    public Calculator(Document doc) {
        library = new OperatorsRealizationLibrary();
        variableStorage = new VariableStorage();
        xmlDocument = doc;
    }

    public Calculator(VariableStorage vs, Document doc) {
        library = new OperatorsRealizationLibrary();
        variableStorage = vs;
        xmlDocument = doc;
    }

    public Integer getLastOperationIndex(Expression expression) {
        if (expression == null || expression.getPartsCount() == 0) {
            return -1;
        }
        int max_index = -1, max_priority = -100;
        for (int i = 0; i < expression.getPartsCount(); ++i) {
            if (expression.at(i) instanceof Operator) {
                Operator operator = (Operator) expression.at(i);
                int current_priority;
                if ((current_priority = operator.getOperatorType().getPriority()) >= max_priority) {
                    max_index = i;
                    max_priority = current_priority;
                }
            }
        }
        return max_index;
    }

    public Integer getNextOperationIndex(Expression expression) {
        if (expression == null || expression.getPartsCount() == 0) {
            return -1;
        }
        int max_index = -1, max_priority = 100;
        for (int i = 0; i < expression.getPartsCount(); ++i) {
            if (expression.at(i) instanceof Operator) {
                Operator operator = (Operator) expression.at(i);
                int current_priority;
                if ((current_priority = operator.getOperatorType().getPriority()) < max_priority) {
                    max_index = i;
                    max_priority = current_priority;
                }
            }
        }
        return max_index;
    }

    public Result evaluate(Tree tree) {
        Expression expression = evaluate(tree.getRoot());
        if (expression.getPartsCount() > 0 || !(expression instanceof Constant)) {
            throw new IllegalArgumentException("Incorrect expression provided.");
        }
        Constant constant = (Constant) expression;
        if (constant.getType() == ExpressionType.DOUBLE) {
            return new Result<Double>(Double.parseDouble(constant.getValue()), constant.getValue(), ExpressionType.DOUBLE);
        } else if (constant.getType() == ExpressionType.INTEGER) {
            return new Result<Integer>(Integer.parseInt(constant.getValue()), constant.getValue(), ExpressionType.INTEGER);
        } else if (constant.getType() == ExpressionType.STRING) {
            return new Result<String>(constant.getValue(), constant.getValue(), ExpressionType.STRING);
        } else if (constant.getType() == ExpressionType.PERIOD) {
            return new Result<String>(constant.getValue(), constant.getValue(), ExpressionType.PERIOD);
        } else {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            try {
                Date date = formatter.parse(constant.getValue());
                return new Result<Date>(date, constant.getValue(), ExpressionType.DATE);
            } catch (ParseException pe) {
                throw new IllegalArgumentException("Couldn't parse date from " + constant.getValue());
            }
        }
    }

    public Constant processXPath(Expression expression) {
        if (xmlDocument == null) {
            throw new IllegalArgumentException("XML Document was not provided.");
        }
        XPathFactory xPathFactory = XPathFactory.newInstance();
        XPath xpath = xPathFactory.newXPath();

        XPathElement elementPath = (XPathElement) expression;

        try {
            Node node = (Node) xpath.evaluate(elementPath.getPath(), xmlDocument, XPathConstants.NODE);
            String value = node.getTextContent();
            return new Constant(value, ExpressionType.STRING);
        } catch (XPathExpressionException e) {
            throw new IllegalArgumentException("Can't evaluate XPath");
        }
    }

    public Expression processCondition(Expression expression) {
        Condition condition = (Condition) expression;
        if (condition.getIfExpression() == null || condition.getElseExpression() == null || condition.getThenExpression() == null) {
            throw new IllegalArgumentException("Condition statement is not full.");
        }
        Expression if_exp = evaluate(condition.getIfExpression());
        if (!(if_exp instanceof Constant) || !(if_exp.getType() == ExpressionType.INTEGER)) {
            throw new IllegalArgumentException("Condition statement is broken.");
        }
        if (Integer.parseInt(((Constant) if_exp).getValue()) == 1) {
            return evaluate(condition.getThenExpression());
        } else {
            return evaluate(condition.getElseExpression());
        }
    }

    public Expression evaluateInnerExpressions(Expression expression) {
        Expression processedExpression = new Expression();
        for (int i = 0; i < expression.getPartsCount(); ++i) {
            Expression current = expression.at(i);
            if (expression.at(i).getPartsCount() > 0) {
                current = evaluate(current);
            }
            processedExpression.addPart(current);
        }
        return processedExpression;
    }

    public Expression evaluateExpression(Expression expression) {
        expression = evaluateInnerExpressions(expression);
        while (true) {
            int nextOperationIndex = getNextOperationIndex(expression);
            if (nextOperationIndex == -1) {
                break;
            }
            Expression left = null;
            Expression right = null;
            Operator.Type operatorType = ((Operator) expression.at(nextOperationIndex)).getOperatorType();
            if (operatorType.getIsBinary() && nextOperationIndex - 1 >= 0) {
                left = evaluate(expression.at(nextOperationIndex - 1));
            }
            if (nextOperationIndex + 1 < expression.getPartsCount()) {
                right = evaluate(expression.at(nextOperationIndex + 1));
            }
            Constant currentResult = library.getRealization(operatorType).apply(left, right);

            Expression newExpression = new Expression();
            for (int i = 0; i < nextOperationIndex - 1; ++i) {
                newExpression.addPart(expression.at(i));
            }
            if (!operatorType.getIsBinary() && nextOperationIndex - 1 >= 0) {
                newExpression.addPart(expression.at(nextOperationIndex - 1));
            }
            newExpression.addPart(currentResult);
            for (int i = nextOperationIndex + 2; i < expression.getPartsCount(); ++i) {
                newExpression.addPart(expression.at(i));
            }
            expression = newExpression;
        }
        return expression;
    }

    public Expression evaluate(Expression expression) {
        if (expression == null) {
            return new Constant("0", ExpressionType.INTEGER);
        }
        if (expression.getPartsCount() == 1) {
            expression = expression.at(0);
        }
        if (expression instanceof XPathElement) {
            return processXPath(expression);
        }
        if (expression instanceof Variable) {
            Variable variable = (Variable) expression;
            expression = new Constant(variableStorage.getValue(variable.getName()), variableStorage.getType(variable.getName()));
        }
        if (expression instanceof Condition) {
            return processCondition(expression);
        }
        if (expression instanceof Constant) {
            return expression;
        } else if (expression.getPartsCount() > 0) {
            expression = evaluateExpression(expression);
            if (expression.getPartsCount() == 1) {
                expression = expression.at(0);
            }
            return expression;
        }
        throw new IllegalArgumentException("Couldn't evaluate expression.");
    }
}