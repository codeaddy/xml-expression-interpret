package org.xmlevaluator;

import org.xmlevaluator.dto.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Period;
import java.util.*;
import java.util.function.BiFunction;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class OperatorsRealizationLibrary {

    private static Map<Operator.Type, BiFunction<Expression, Expression, Constant>> realizations;
    private static final SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public OperatorsRealizationLibrary() {
        realizations = new HashMap<>();
        realizations.put(Operator.Type.PLUS, this::plusOperation);
        realizations.put(Operator.Type.MINUS, this::minusOperation);
        realizations.put(Operator.Type.MULTIPLY, this::multiplyOperation);
        realizations.put(Operator.Type.DIVIDE, this::divideOperation);
        realizations.put(Operator.Type.AND, this::andOperation);
        realizations.put(Operator.Type.OR, this::orOperation);
        realizations.put(Operator.Type.EQ, this::eqOperation);
        realizations.put(Operator.Type.EQUAL, this::eqOperation);
        realizations.put(Operator.Type.NOTEQUAL, this::notEqOperation);
        realizations.put(Operator.Type.MAX, this::maxOperation);
        realizations.put(Operator.Type.MIN, this::minOperation);
        realizations.put(Operator.Type.FN_DAY_FROM_DATE, this::fnDayFromDateOperation);
        realizations.put(Operator.Type.FN_MONTH_FROM_DATE, this::fnMonthFromDateOperation);
        realizations.put(Operator.Type.FN_YEAR_FROM_DATE, this::fnYearFromDateOperation);
        realizations.put(Operator.Type.FN_CONCAT, this::fnConcatOperation);
        realizations.put(Operator.Type.XSD_DATE, this::xsdDateOperation);
        realizations.put(Operator.Type.XSD_STRING, this::xsdStringOperation);
        realizations.put(Operator.Type.XSD_DAY_TIME_DURATION, this::xsdDayTimeDurationOperation);
        realizations.put(Operator.Type.XSD_YEAR_MONTH_DURATION, this::xsdYearMonthDurationOperation);
    }

    public BiFunction<Expression, Expression, Constant> getRealization(Operator.Type type) {
        return realizations.get(type);
    }

    public Date getDateFromString(String s) {
        try {
            Date date = dateFormatter.parse(s);
            return date;
        } catch (ParseException pe) {
            throw new IllegalArgumentException("Can't parse date from string ' + " + s + "'.");
        }
    }

    public ExpressionType getCommonType(Expression first, Expression second) {
        if (first.getType() == ExpressionType.INTEGER) {
            if (second.getType() == ExpressionType.INTEGER) {
                return ExpressionType.INTEGER;
            } else if (second.getType() == ExpressionType.DOUBLE) {
                return ExpressionType.DOUBLE;
            } else {
                throw new IllegalArgumentException("Can't operate with values of types " + first.getType() + " and "
                        + second.getType());
            }
        } else if (first.getType() == ExpressionType.DOUBLE) {
            if (second.getType() == ExpressionType.INTEGER) {
                return ExpressionType.DOUBLE;
            } else if (second.getType() == ExpressionType.DOUBLE) {
                return ExpressionType.DOUBLE;
            } else {
                throw new IllegalArgumentException("Can't operate with values of types " + first.getType() + " and "
                        + second.getType());
            }
        } else if (first.getType() == ExpressionType.STRING) {
            if (second.getType() == ExpressionType.STRING) {
                return ExpressionType.STRING;
            } else {
                throw new IllegalArgumentException("Can't operate with values of types " + first.getType() + " and "
                        + second.getType());
            }
        } else if (first.getType() == ExpressionType.DATE) {
            if (second.getType() == ExpressionType.DATE) {
                return ExpressionType.DATE;
            } else if (second.getType() == ExpressionType.PERIOD) {
                return ExpressionType.DATE;
            } else {
                throw new IllegalArgumentException("Can't operate with values of types " + first.getType() + " and "
                        + second.getType());
            }
        } else if (first.getType() == ExpressionType.PERIOD) {
            if (second.getType() == ExpressionType.DATE) {
                return ExpressionType.DATE;
            } else {
                throw new IllegalArgumentException("Can't operate with values of types " + first.getType() + " and "
                        + second.getType());
            }
        } else {
            throw new IllegalArgumentException("Can't operate with values of types " + first.getType() + " and "
                    + second.getType());
        }
    }

    private Constant sumDateAndPeriod(Constant left, Constant right) {
        Date left_date = getDateFromString(left.getValue());
        String period = right.getValue();
        String[] period_parts_strs = period.split(":");
        int[] period_parts = Arrays.stream(period_parts_strs).mapToInt(Integer::parseInt).toArray();

        Calendar c = Calendar.getInstance();
        c.setTime(left_date);
        c.add(Calendar.YEAR, period_parts[0]);
        c.add(Calendar.MONTH, period_parts[1]);
        c.add(Calendar.DAY_OF_MONTH, period_parts[2]);
        c.add(Calendar.HOUR, period_parts[3]);
        c.add(Calendar.MINUTE, period_parts[4]);
        c.add(Calendar.SECOND, period_parts[5]);

        return new Constant(dateFormatter.format(c.getTime()), ExpressionType.DATE);
    }

    public Constant plusOperation(Expression left, Expression right) {
        if (!(left instanceof Constant) || !(right instanceof Constant)) {
            throw new IllegalArgumentException("Can't sum values of types " + left.getType() + " and "
                    + right.getType());
        }
        Constant left_const = (Constant) left;
        Constant right_const = (Constant) right;

        if (left.getType() == ExpressionType.PERIOD && right.getType() == ExpressionType.DATE) {
            Constant tmp = right_const;
            right_const = left_const;
            left_const = tmp;
        }

        ExpressionType commonType = getCommonType(left, right);
        if (commonType == ExpressionType.INTEGER) {
            Integer sum = Integer.parseInt(left_const.getValue()) + Integer.parseInt(right_const.getValue());
            return new Constant(String.valueOf(sum), ExpressionType.INTEGER);
        } else if (commonType == ExpressionType.DOUBLE) {
            Double sum = Double.parseDouble(left_const.getValue()) + Double.parseDouble(right_const.getValue());
            return new Constant(String.valueOf(sum), ExpressionType.DOUBLE);
        } else if (left_const.getType() == ExpressionType.DATE && right_const.getType() == ExpressionType.PERIOD) {
            return sumDateAndPeriod(left_const, right_const);
        } else {
            throw new IllegalArgumentException("Can't do '+' operation with values of types " + left.getType() + " and "
                    + right.getType());
        }
    }

    private Constant diffDateAndPeriod(Constant left, Constant right) {
        Date left_date = getDateFromString(left.getValue());
        String period = right.getValue();
        String[] period_parts_strs = period.split(":");
        int[] period_parts = Arrays.stream(period_parts_strs).mapToInt(Integer::parseInt).toArray();

        Calendar c = Calendar.getInstance();
        c.setTime(left_date);
        c.add(Calendar.YEAR, -period_parts[0]);
        c.add(Calendar.MONTH, -period_parts[1]);
        c.add(Calendar.DAY_OF_MONTH, -period_parts[2]);
        c.add(Calendar.HOUR, -period_parts[3]);
        c.add(Calendar.MINUTE, -period_parts[4]);
        c.add(Calendar.SECOND, -period_parts[5]);

        return new Constant(dateFormatter.format(c.getTime()), ExpressionType.DATE);
    }

    public Constant minusOperation(Expression left, Expression right) {
        if (!(left instanceof Constant) || !(right instanceof Constant)) {
            throw new IllegalArgumentException("Can't do '-' operation with types " + left.getType() + " and "
                    + right.getType());
        }
        Constant left_const = (Constant) left;
        Constant right_const = (Constant) right;

        if (left.getType() == ExpressionType.PERIOD && right.getType() == ExpressionType.DATE) {
            Constant tmp = right_const;
            right_const = left_const;
            left_const = tmp;
        }

        ExpressionType commonType = getCommonType(left, right);
        if (commonType == ExpressionType.INTEGER) {
            Integer sum = Integer.parseInt(left_const.getValue()) - Integer.parseInt(right_const.getValue());
            return new Constant(String.valueOf(sum), ExpressionType.INTEGER);
        } else if (commonType == ExpressionType.DOUBLE) {
            Double sum = Double.parseDouble(left_const.getValue()) - Double.parseDouble(right_const.getValue());
            return new Constant(String.valueOf(sum), ExpressionType.DOUBLE);
        } else if (left_const.getType() == ExpressionType.DATE && right_const.getType() == ExpressionType.PERIOD) {
            return diffDateAndPeriod(left_const, right_const);
        } else {
            throw new IllegalArgumentException("Can't do '-' operation with values of types " + left.getType() + " and "
                    + right.getType());
        }
    }

    public Constant multiplyOperation(Expression left, Expression right) {
        if (!(left instanceof Constant) || !(right instanceof Constant)) {
            throw new IllegalArgumentException("Can't sum values of types " + left.getType() + " and "
                    + right.getType());
        }
        Constant left_const = (Constant) left;
        Constant right_const = (Constant) right;
        ExpressionType commonType = getCommonType(left, right);
        if (commonType == ExpressionType.INTEGER) {
            Integer sum = Integer.parseInt(left_const.getValue()) * Integer.parseInt(right_const.getValue());
            return new Constant(String.valueOf(sum), ExpressionType.INTEGER);
        } else if (commonType == ExpressionType.DOUBLE) {
            Double sum = Double.parseDouble(left_const.getValue()) * Double.parseDouble(right_const.getValue());
            return new Constant(String.valueOf(sum), ExpressionType.DOUBLE);
        } else {
            throw new IllegalArgumentException("Can't do '*' operation with values of types " + left.getType() + " and "
                    + right.getType());
        }
    }

    public Constant divideOperation(Expression left, Expression right) {
        if (!(left instanceof Constant) || !(right instanceof Constant)) {
            throw new IllegalArgumentException("Can't sum values of types " + left.getType() + " and "
                    + right.getType());
        }
        Constant left_const = (Constant) left;
        Constant right_const = (Constant) right;
        ExpressionType commonType = getCommonType(left, right);
        if (commonType == ExpressionType.INTEGER) {
            Integer sum = Integer.parseInt(left_const.getValue()) / Integer.parseInt(right_const.getValue());
            return new Constant(String.valueOf(sum), ExpressionType.INTEGER);
        } else if (commonType == ExpressionType.DOUBLE) {
            Double sum = Double.parseDouble(left_const.getValue()) / Double.parseDouble(right_const.getValue());
            return new Constant(String.valueOf(sum), ExpressionType.DOUBLE);
        } else {
            throw new IllegalArgumentException("Can't do '/' operation with values of types " + left.getType() + " and "
                    + right.getType());
        }
    }


    public Constant andOperation(Expression left, Expression right) {
        if (!(left instanceof Constant) || !(right instanceof Constant)) {
            throw new IllegalArgumentException("Can't sum values of types " + left.getType() + " and "
                    + right.getType());
        }
        Constant left_const = (Constant) left;
        Constant right_const = (Constant) right;
        ExpressionType commonType = getCommonType(left, right);
        if (commonType == ExpressionType.INTEGER) {
            if (Integer.parseInt(left_const.getValue()) > 0 && Integer.parseInt(right_const.getValue()) > 0) {
                return new Constant("1", ExpressionType.INTEGER);
            } else {
                return new Constant("0", ExpressionType.INTEGER);
            }
        } else {
            throw new IllegalArgumentException("Can't do 'and' operation with values of types " + left.getType() + " and "
                    + right.getType());
        }
    }

    public Constant orOperation(Expression left, Expression right) {
        if (!(left instanceof Constant) || !(right instanceof Constant)) {
            throw new IllegalArgumentException("Can't sum values of types " + left.getType() + " and "
                    + right.getType());
        }
        Constant left_const = (Constant) left;
        Constant right_const = (Constant) right;
        ExpressionType commonType = getCommonType(left, right);
        if (commonType == ExpressionType.INTEGER) {
            if (Integer.parseInt(left_const.getValue()) > 0 || Integer.parseInt(right_const.getValue()) > 0) {
                return new Constant("1", ExpressionType.INTEGER);
            } else {
                return new Constant("0", ExpressionType.INTEGER);
            }
        } else {
            throw new IllegalArgumentException("Can't do 'or' operation with values of types " + left.getType() + " and "
                    + right.getType());
        }
    }

    public Constant eqOperation(Expression left, Expression right) {
        if (!(left instanceof Constant) || !(right instanceof Constant)) {
            throw new IllegalArgumentException("Can't sum values of types " + left.getType() + " and "
                    + right.getType());
        }
        Constant left_const = (Constant) left;
        Constant right_const = (Constant) right;
        if (left_const.getValue().equals(right_const.getValue())) {
            return new Constant("1", ExpressionType.INTEGER);
        } else {
            return new Constant("0", ExpressionType.INTEGER);
        }
    }

    public Constant notEqOperation(Expression left, Expression right) {
        if (!(left instanceof Constant) || !(right instanceof Constant)) {
            throw new IllegalArgumentException("Can't sum values of types " + left.getType() + " and "
                    + right.getType());
        }
        Constant left_const = (Constant) left;
        Constant right_const = (Constant) right;
        if (!left_const.getValue().equals(right_const.getValue())) {
            return new Constant("1", ExpressionType.INTEGER);
        } else {
            return new Constant("0", ExpressionType.INTEGER);
        }
    }

    public static Date getMaxDate(Date d1, Date d2) {
        return d1.compareTo(d2) >= 0 ? d1 : d2;
    }

    public Constant maxOperation(Expression left, Expression right) {
        if (right == null) {
            throw new IllegalArgumentException("0 operands provided to max operator.");
        }
        if (right.getPartsCount() == 0) {
            throw new IllegalArgumentException("Invalid operands provided to max operator.");
        }
        Constant mx_const = (Constant) right.at(0);
        for (int i = 1; i < right.getPartsCount(); ++i) {
            if (!(right.at(i) instanceof Constant)) {
                throw new IllegalArgumentException("Can't do max operation with values of types " + mx_const.getType() + " and "
                        + right.at(i).getType());
            }
            Constant left_const = mx_const;
            Constant right_const = (Constant) right.at(i);
            ExpressionType commonType = getCommonType(left_const, right_const);
            if (commonType == ExpressionType.INTEGER) {
                Integer mx = Integer.max(Integer.parseInt(left_const.getValue()), Integer.parseInt(right_const.getValue()));
                mx_const = new Constant(String.valueOf(mx), ExpressionType.INTEGER);
            } else if (commonType == ExpressionType.DOUBLE) {
                Double mx = Double.max(Double.parseDouble(left_const.getValue()), Double.parseDouble(right_const.getValue()));
                mx_const = new Constant(String.valueOf(mx), ExpressionType.DOUBLE);
            } else if (commonType == ExpressionType.DATE) {
                Date mx = getMaxDate((getDateFromString(left_const.getValue())), getDateFromString(right_const.getValue()));
                mx_const = new Constant(dateFormatter.format(mx), ExpressionType.DATE);
            } else {
                throw new IllegalArgumentException("Can't do max operation with values of types " + left_const.getType() + " and "
                        + right_const.at(i).getType());
            }
        }
        return mx_const;
    }

    public static Date getMinDate(Date d1, Date d2) {
        return d1.compareTo(d2) <= 0 ? d1 : d2;
    }

    public Constant minOperation(Expression left, Expression right) {
        if (right == null) {
            throw new IllegalArgumentException("0 operands provided to min operator.");
        }
        if (right.getPartsCount() == 0) {
            throw new IllegalArgumentException("Invalid operands provided to min operator.");
        }
        Constant mi_const = (Constant) right.at(0);
        for (int i = 1; i < right.getPartsCount(); ++i) {
            if (!(right.at(i) instanceof Constant)) {
                throw new IllegalArgumentException("Can't do min operation with values of types " + mi_const.getType() + " and "
                        + right.at(i).getType());
            }
            Constant left_const = mi_const;
            Constant right_const = (Constant) right.at(i);
            ExpressionType commonType = getCommonType(left_const, right_const);
            if (commonType == ExpressionType.INTEGER) {
                Integer mi = Integer.min(Integer.parseInt(left_const.getValue()), Integer.parseInt(right_const.getValue()));
                mi_const = new Constant(String.valueOf(mi), ExpressionType.INTEGER);
            } else if (commonType == ExpressionType.DOUBLE) {
                Double mi = Double.min(Double.parseDouble(left_const.getValue()), Double.parseDouble(right_const.getValue()));
                mi_const = new Constant(String.valueOf(mi), ExpressionType.DOUBLE);
            } else if (commonType == ExpressionType.DATE) {
                Date mi = getMinDate((getDateFromString(left_const.getValue())), getDateFromString(right_const.getValue()));
                mi_const = new Constant(String.valueOf(mi), ExpressionType.DATE);
            } else {
                throw new IllegalArgumentException("Can't do min operation with values of types " + left_const.getType() + " and "
                        + right_const.at(i).getType());
            }
        }
        return mi_const;
    }

    public Constant fnDayFromDateOperation(Expression left, Expression right) {
        if (right == null) {
            throw new IllegalArgumentException("0 operands provided to fn:day-from-date operator.");
        }
        if (!(right instanceof Constant) || right.getType() != ExpressionType.DATE) {
            throw new IllegalArgumentException("Invalid operands provided to fn:day-from-date operator.");
        }
        Date date = getDateFromString(((Constant) right).getValue());
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return new Constant(String.valueOf(calendar.get(Calendar.DAY_OF_MONTH)), ExpressionType.INTEGER);
    }

    public Constant fnMonthFromDateOperation(Expression left, Expression right) {
        if (right == null) {
            throw new IllegalArgumentException("0 operands provided to fn:month-from-date operator.");
        }
        if (!(right instanceof Constant) || right.getType() != ExpressionType.DATE) {
            throw new IllegalArgumentException("Invalid operands provided to fn:month-from-date operator.");
        }
        Date date = getDateFromString(((Constant) right).getValue());
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return new Constant(String.valueOf(calendar.get(Calendar.MONTH) + 1), ExpressionType.INTEGER);
    }

    public Constant fnYearFromDateOperation(Expression left, Expression right) {
        if (right == null) {
            throw new IllegalArgumentException("0 operands provided to fn:year-from-date operator.");
        }
        if (!(right instanceof Constant) || right.getType() != ExpressionType.DATE) {
            throw new IllegalArgumentException("Invalid operands provided to fn:year-from-date operator.");
        }
        Date date = getDateFromString(((Constant) right).getValue());
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return new Constant(String.valueOf(calendar.get(Calendar.YEAR)), ExpressionType.INTEGER);
    }

    public Constant fnConcatOperation(Expression left, Expression right) {
        if (right == null || right.getPartsCount() == 0) {
            throw new IllegalArgumentException("Invalid operands provided to fn:concat operator.");
        }
        Constant result = (Constant) right.at(0);
        for (int i = 1; i < right.getPartsCount(); ++i) {
            if (!(right.at(i) instanceof Constant)) {
                throw new IllegalArgumentException("Can't do min operation with values of types " + result.getType() + " and "
                        + right.at(i).getType());
            }
            Constant left_const = result;
            Constant right_const = (Constant) right.at(i);
            result = new Constant(left_const.getValue() + right_const.getValue(), ExpressionType.STRING);
        }
        return result;
    }

    public Constant xsdDateOperation(Expression left, Expression right) {
        if (right == null) {
            throw new IllegalArgumentException("Invalid operand provided to xsd:date operator.");
        }
        if (!(right instanceof Constant)) {
            throw new IllegalArgumentException("Invalid operand provided to xsd:date operator.");
        }
        if (right.getType() != ExpressionType.STRING && right.getType() != ExpressionType.DATE) {
            throw new IllegalArgumentException("Invalid operand provided to xsd:date operator.");
        }
        return new Constant(((Constant) right).getValue(), ExpressionType.DATE);
    }

    public Constant xsdStringOperation(Expression left, Expression right) {
        if (right == null) {
            throw new IllegalArgumentException("Invalid operand provided to xsd:string operator.");
        }
        if (!(right instanceof Constant)) {
            throw new IllegalArgumentException("Invalid operand provided to xsd:string operator.");
        }
        return new Constant(((Constant) right).getValue(), ExpressionType.STRING);
    }

    public String parseDayTimeDuration(String duration) {
        String regex = "P(?:(\\d+)D)?(?:T(?:(\\d+)H)?(?:(\\d+)M)?(?:(\\d+)S)?)?";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(duration);

        int days = 0, hours = 0, minutes = 0, seconds = 0;

        if (matcher.matches()) {
            String daysStr = matcher.group(1);
            String hoursStr = matcher.group(2);
            String minutesStr = matcher.group(3);
            String secondsStr = matcher.group(4);

            days = (daysStr != null) ? Integer.parseInt(daysStr) : 0;
            hours = (hoursStr != null) ? Integer.parseInt(hoursStr) : 0;
            minutes = (minutesStr != null) ? Integer.parseInt(minutesStr) : 0;
            seconds = (secondsStr != null) ? Integer.parseInt(secondsStr) : 0;
        }

        return String.format("0:0:%d:%d:%d:%d", days, hours, minutes, seconds);
    }

    public Constant xsdDayTimeDurationOperation(Expression left, Expression right) {
        if (right == null) {
            throw new IllegalArgumentException("Invalid operand provided to xsd:dayTimeDuration operator.");
        }
        if (!(right instanceof Constant) || !(right.getType() == ExpressionType.STRING)) {
            throw new IllegalArgumentException("Invalid operand provided to xsd:dayTimeDuration operator.");
        }
        String period = parseDayTimeDuration(((Constant) right).getValue());
        return new Constant(period, ExpressionType.PERIOD);
    }

    public String parseYearMonthDuration(String duration) {
        String regex = "P(?:(\\d+)Y)?(?:(\\d+)M)?";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(duration);

        int years = 0, months = 0;

        if (matcher.matches()) {
            String yearsStr = matcher.group(1);
            String monthsStr = matcher.group(2);

            years = (yearsStr != null) ? Integer.parseInt(yearsStr) : 0;
            months = (monthsStr != null) ? Integer.parseInt(monthsStr) : 0;
        }

        return String.format("%d:%d:0:0:0:0", years, months);
    }

    public Constant xsdYearMonthDurationOperation(Expression left, Expression right) {
        if (right == null) {
            throw new IllegalArgumentException("Invalid operand provided to xsd:dayTimeDuration operator.");
        }
        if (!(right instanceof Constant) || !(right.getType() == ExpressionType.STRING)) {
            throw new IllegalArgumentException("Invalid operand provided to xsd:dayTimeDuration operator.");
        }
        String period = parseYearMonthDuration(((Constant) right).getValue());
        return new Constant(period, ExpressionType.PERIOD);
    }
}
