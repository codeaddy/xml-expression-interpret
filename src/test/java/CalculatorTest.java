import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.xmlevaluator.dto.*;
import org.xmlevaluator.evaluator.Calculator;
import org.xmlevaluator.treeMaker.TreeProvider;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CalculatorTest {
    TreeProvider treeProvider;
    Calculator calculator;
    Tree tree;

    @BeforeEach
    public void BeforeEach() {
        treeProvider = new TreeProvider();

        VariableStorage storage = new VariableStorage();
        storage.pushVariable("varDate1", "2022-12-12", ExpressionType.DATE);
        storage.pushVariable("varDate2", "2023-12-12", ExpressionType.DATE);
        storage.pushVariable("varNumThree", "3", ExpressionType.INTEGER);
        storage.pushVariable("varNumFour", "4", ExpressionType.INTEGER);
        storage.pushVariable("par:refPeriodEnd", "2000-06-30", ExpressionType.DATE);

        storage.pushVariable("test4", "2000-09-30", ExpressionType.DATE);

        storage.pushVariable("test5", "2000-09-30", ExpressionType.DATE);

        storage.pushVariable("test6", "1928-01-30", ExpressionType.DATE);

        storage.pushVariable("test71", "1928-01-30", ExpressionType.DATE);
        storage.pushVariable("test72", "I", ExpressionType.STRING);

        storage.pushVariable("test81", "1928-01-30", ExpressionType.DATE);
        storage.pushVariable("test82", "Q", ExpressionType.STRING);

        storage.pushVariable("test10", "1928-01-30", ExpressionType.DATE);

        storage.pushVariable("test11", "1928-01-30", ExpressionType.DATE);

        storage.pushVariable("test12", "1928-01-30", ExpressionType.DATE);

        storage.pushVariable("test13", "1928-01-30", ExpressionType.DATE);

        storage.pushVariable("test14", "1921-12-31", ExpressionType.DATE);

        calculator = new Calculator(storage);
    }

    @Test
    public void SingleIntTest() {
        tree = treeProvider.buildTree("2");

        Result result = calculator.evaluate(tree);

        assertEquals(ExpressionType.INTEGER, result.getType());
        assertEquals(2, result.getValue());
    }

    @Test
    public void SingleDoubleTest() {
        tree = treeProvider.buildTree("2.3");

        Result result = calculator.evaluate(tree);

        assertEquals(ExpressionType.DOUBLE, result.getType());
        assertEquals(2.3, result.getValue());
    }

    @Test
    public void SingleDateTest() throws ParseException {
        tree = treeProvider.buildTree("'2024-12-15'");

        Result result = calculator.evaluate(tree);

        assertEquals(ExpressionType.STRING, result.getType());
        assertEquals("2024-12-15", result.getValue());
    }

    @Test
    public void SingleConstTest() {
        tree = treeProvider.buildTree("'consta'");

        Result result = calculator.evaluate(tree);

        assertEquals(ExpressionType.STRING, result.getType());
        assertEquals("consta", result.getValue());
    }

    @Test
    public void NextOperationTests_TwoPlusTwo() {
        Expression e = new Expression();
        e.addPart(new Constant("2", ExpressionType.INTEGER));
        e.addPart(new Operator(Operator.Type.PLUS));
        e.addPart(new Constant("2", ExpressionType.INTEGER));

        Integer index = calculator.getLastOperationIndex(e);

        assertEquals(1, index);
    }

    @Test
    public void LastOperationTests_TwoPlusTwo_DivideByThree() {
        Expression e = new Expression();
        e.addPart(new Constant("2.0", ExpressionType.INTEGER));
        e.addPart(new Operator(Operator.Type.PLUS));
        e.addPart(new Constant("2.0", ExpressionType.INTEGER));
        e.addPart(new Operator(Operator.Type.DIVIDE));
        e.addPart(new Constant("3.0", ExpressionType.INTEGER));

        Integer index = calculator.getLastOperationIndex(e);

        assertEquals(1, index);
    }

    @Test
    public void TwoPlusTwoTest() {
        tree = treeProvider.buildTree("2 + 2");

        Result result = calculator.evaluate(tree);

        assertEquals(ExpressionType.INTEGER, result.getType());
        assertEquals(4, result.getValue());
    }

    @Test
    public void TwoPointSevenPlusTwoPointEight() {
        tree = treeProvider.buildTree("2.7 + 2.8");

        Result result = calculator.evaluate(tree);

        assertEquals(ExpressionType.DOUBLE, result.getType());
        assertEquals(5.5, (Double) result.getValue(), 0.001);
    }

    @Test
    public void TwoPlusTwoPointEight() {
        tree = treeProvider.buildTree("2 + 2.8");

        Result result = calculator.evaluate(tree);

        assertEquals(ExpressionType.DOUBLE, result.getType());
        assertEquals(4.8, (Double) result.getValue(), 0.001);
    }

    @Test
    public void TwoMinusTwoPointEight() {
        tree = treeProvider.buildTree("2 - 2.8");

        Result result = calculator.evaluate(tree);

        assertEquals(ExpressionType.DOUBLE, result.getType());
        assertEquals(-0.8, (Double) result.getValue(), 0.001);
    }

    @Test
    public void TwoMultiplyByTwoPointEight() {
        tree = treeProvider.buildTree("2 * 2.8");

        Result result = calculator.evaluate(tree);

        assertEquals(ExpressionType.DOUBLE, result.getType());
        assertEquals(5.6, (Double) result.getValue(), 0.001);
    }

    @Test
    public void SevenDivideByTwoPointEight() {
        tree = treeProvider.buildTree("7 / 2.8");

        Result result = calculator.evaluate(tree);

        assertEquals(ExpressionType.DOUBLE, result.getType());
        assertEquals(2.5, (Double) result.getValue(), 0.001);
    }

    @Test
    public void OnePlusSevenDividedByTwoPointEight() {
        tree = treeProvider.buildTree("1 + 7 / 2.8");

        Result result = calculator.evaluate(tree);

        assertEquals(ExpressionType.DOUBLE, result.getType());
        assertEquals(3.5, (Double) result.getValue(), 0.001);
    }

    @Test
    public void SumOfOneAndSevenDividedByTwoPointEight() {
        tree = treeProvider.buildTree("(1 + 7) / 2.8");

        Result result = calculator.evaluate(tree);

        assertEquals(ExpressionType.DOUBLE, result.getType());
        assertEquals(2.86, (Double) result.getValue(), 0.01);
    }

    @Test
    public void OneEqualsOne() {
        tree = treeProvider.buildTree("1 = 1");

        Result result = calculator.evaluate(tree);

        assertEquals(ExpressionType.INTEGER, result.getType());
        assertEquals(1, result.getValue());
    }

    @Test
    public void OneEqualsTwo() {
        tree = treeProvider.buildTree("1 = 2");

        Result result = calculator.evaluate(tree);

        assertEquals(ExpressionType.INTEGER, result.getType());
        assertEquals(0, result.getValue());
    }

    @Test
    public void StrEqualsStr() {
        tree = treeProvider.buildTree("'str' eq 'str'");

        Result result = calculator.evaluate(tree);

        assertEquals(ExpressionType.INTEGER, result.getType());
        assertEquals(1, result.getValue());
    }

    @Test
    public void MaxOfOneAndSevenDividedByTwoPointEight() {
        tree = treeProvider.buildTree("max(1,  7 / 2.8)");

        Result result = calculator.evaluate(tree);

        assertEquals(ExpressionType.DOUBLE, result.getType());
        assertEquals(2.5, (Double) result.getValue(), 0.01);
    }

    public Date getDateFromString(String s) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date date = formatter.parse(s);
            return date;
        } catch (ParseException pe) {
            throw new IllegalArgumentException("Can't parse date from string ' + " + s + "'.");
        }
    }

    @Test
    public void XsdDateCheck() {
        tree = treeProvider.buildTree("xsd:date('2022-12-02')");

        Result result = calculator.evaluate(tree);

        assertEquals(ExpressionType.DATE, result.getType());

        Date need = getDateFromString("2022-12-02 00:00:00");
        assertEquals(need, (Date) result.getValue());
    }

    @Test
    public void DayTimeDurationCheck() {
        tree = treeProvider.buildTree("xsd:dayTimeDuration('PT20M')");

        Result result = calculator.evaluate(tree);

        assertEquals(ExpressionType.PERIOD, result.getType());

        assertEquals("0:0:0:0:20:0", result.getValue());
    }

    @Test
    public void SumOfDateAndDayTimeDurationCheck() {
        tree = treeProvider.buildTree("xsd:date('2022-12-12') + xsd:dayTimeDuration('P1DT2H')");

        Result result = calculator.evaluate(tree);

        assertEquals(ExpressionType.DATE, result.getType());

        Date need = getDateFromString("2022-12-13 02:00:00");
        assertEquals(need, (Date) result.getValue());
    }

    @Test
    public void SumOfDayTimeDurationAndDateCheck() {
        tree = treeProvider.buildTree("xsd:dayTimeDuration('P1DT2H') + xsd:date('2022-12-12')");

        Result result = calculator.evaluate(tree);

        assertEquals(ExpressionType.DATE, result.getType());

        Date need = getDateFromString("2022-12-13 02:00:00");
        assertEquals(need, (Date) result.getValue());
    }

    @Test
    public void AndCheck() {
        tree = treeProvider.buildTree("if max(xsd:date('2022-12-12'),xsd:date('2023-12-12'))=xsd:date('2023-12-12') then 'YES' else 'NO'");

        Result result = calculator.evaluate(tree);

        assertEquals(ExpressionType.STRING, result.getType());

        assertEquals("YES", (String) result.getValue());
    }

    @Test
    public void OrCheck() {
        tree = treeProvider.buildTree("if (max(xsd:date('2022-12-12'),xsd:date('2023-12-12'))=xsd:date('2022-12-12') or 2+3=5) then 'YES' else 'NO'");

        Result result = calculator.evaluate(tree);

        assertEquals(ExpressionType.STRING, result.getType());

        assertEquals("YES", (String) result.getValue());
    }

    @Test
    public void OrFalseCheck() {
        tree = treeProvider.buildTree("if (max(xsd:date('2022-12-12'),xsd:date('2023-12-12'))=xsd:date('2022-12-12') or 2+3=4) then 'YES' else 'NO'");

        Result result = calculator.evaluate(tree);

        assertEquals(ExpressionType.STRING, result.getType());

        assertEquals("NO", (String) result.getValue());
    }

    @Test
    public void OrVariableCheck() {
        tree = treeProvider.buildTree("if (max(xsd:date($varDate1),xsd:date($varDate2))=xsd:date($varDate2) or 2+3=4) then 'YES' else 'NO'");

        Result result = calculator.evaluate(tree);

        assertEquals(ExpressionType.STRING, result.getType());

        assertEquals("YES", (String) result.getValue());
    }

    @Test
    public void NumericVariableSumCheck() {
        tree = treeProvider.buildTree("$varNumThree + $varNumFour");

        Result result = calculator.evaluate(tree);

        assertEquals(ExpressionType.INTEGER, result.getType());

        assertEquals(7, (Integer) result.getValue());
    }

    @Test
    public void calculator_SelectExample3() {
        tree = treeProvider.buildTree("if ((fn:day-from-date($par:refPeriodEnd)=30) and (fn:month-from-date($par:refPeriodEnd)=6)) then ($par:refPeriodEnd - xsd:yearMonthDuration('P3M') + xsd:dayTimeDuration('P1D')) else ($par:refPeriodEnd - xsd:yearMonthDuration('P3M'))");

        Result result = calculator.evaluate(tree);

        assertEquals(ExpressionType.DATE, result.getType());

        Date need = getDateFromString("2000-03-31 00:00:00");
        assertEquals(need, (Date) result.getValue());
    }

    @Test
    public void calculator_SelectExample4() {
        tree = treeProvider.buildTree("if ((fn:month-from-date($test4)=3) or (fn:month-from-date($test4)=12) or (fn:month-from-date($test4)=9)) then ($test4 - xsd:yearMonthDuration('P3M')) else ($test4 - xsd:yearMonthDuration('P3M') + xsd:dayTimeDuration('P1D'))");

        Result result = calculator.evaluate(tree);

        assertEquals(ExpressionType.DATE, result.getType());

        Date need = getDateFromString("2000-06-30 00:00:00");
        assertEquals(need, (Date) result.getValue());
    }

    @Test
    public void calculator_SelectExample5() {
        tree = treeProvider.buildTree("xsd:date(fn:concat(xsd:string(fn:year-from-date($test5) - 1),'-12-31'))");

        Result result = calculator.evaluate(tree);

        assertEquals(ExpressionType.DATE, result.getType());

        Date need = getDateFromString("1999-12-31 00:00:00");
        assertEquals(need, (Date) result.getValue());
    }

    @Test
    public void calculator_SelectExample6() {
        tree = treeProvider.buildTree("xsd:date($test6) - xsd:yearMonthDuration('P1Y')");

        Result result = calculator.evaluate(tree);

        assertEquals(ExpressionType.DATE, result.getType());

        Date need = getDateFromString("1927-01-30 00:00:00");
        assertEquals(need, (Date) result.getValue());
    }

    @Test
    public void calculator_SelectExample7() {
        tree = treeProvider.buildTree("xsd:date($test71) - xsd:yearMonthDuration(if($test72 eq 'I') then 'P1Y' else 'P2Y')");

        Result result = calculator.evaluate(tree);

        assertEquals(ExpressionType.DATE, result.getType());

        Date need = getDateFromString("1927-01-30 00:00:00");
        assertEquals(need, (Date) result.getValue());
    }

    @Test
    public void calculator_SelectExample8() {
        tree = treeProvider.buildTree("xsd:date($test81) - xsd:yearMonthDuration(if($test82 eq 'I') then 'P1Y' else 'P2Y')");

        Result result = calculator.evaluate(tree);

        assertEquals(ExpressionType.DATE, result.getType());

        Date need = getDateFromString("1926-01-30 00:00:00");
        assertEquals(need, (Date) result.getValue());
    }

    @Test
    public void calculator_SelectExample9() {
        tree = treeProvider.buildTree("xsd:string('A')");

        Result result = calculator.evaluate(tree);

        assertEquals(ExpressionType.STRING, result.getType());

        assertEquals("A", result.getValue());
    }

    @Test
    public void calculator_SelectExample10() {
        tree = treeProvider.buildTree("xsd:date(fn:concat(xsd:string(fn:year-from-date($test10) - 2),'-12-31'))");

        Result result = calculator.evaluate(tree);

        assertEquals(ExpressionType.DATE, result.getType());

        Date need = getDateFromString("1926-12-31 00:00:00");
        assertEquals(need, (Date) result.getValue());
    }

    @Test
    public void calculator_SelectExample11() {
        tree = treeProvider.buildTree("xsd:date(fn:concat(xsd:string(fn:year-from-date($test11) - 2),'-12-31')) + xsd:dayTimeDuration('P1D')");

        Result result = calculator.evaluate(tree);

        assertEquals(ExpressionType.DATE, result.getType());

        Date need = getDateFromString("1927-01-01 00:00:00");
        assertEquals(need, (Date) result.getValue());
    }

    @Test
    public void calculator_SelectExample12() {
        tree = treeProvider.buildTree("xsd:date(fn:concat(xsd:string(fn:year-from-date($test12) - 1),'-12-31'))");

        Result result = calculator.evaluate(tree);

        assertEquals(ExpressionType.DATE, result.getType());

        Date need = getDateFromString("1927-12-31 00:00:00");
        assertEquals(need, (Date) result.getValue());
    }

    @Test
    public void calculator_SelectExample13() {
        tree = treeProvider.buildTree("if ((fn:month-from-date($test12)=3) or (fn:month-from-date($test12)=12) or (fn:month-from-date($test12)=9)) then ($test12 - xsd:yearMonthDuration('P3M')) else ($test12 - xsd:yearMonthDuration('P3M') + xsd:dayTimeDuration('P1D'))");

        Result result = calculator.evaluate(tree);

        assertEquals(ExpressionType.DATE, result.getType());

        Date need = getDateFromString("1927-10-31 00:00:00");
        assertEquals(need, (Date) result.getValue());
    }

    @Test
    public void calculator_SelectExample14() {
        tree = treeProvider.buildTree("xsd:date($test14) + xsd:dayTimeDuration('P1D')");

        Result result = calculator.evaluate(tree);

        assertEquals(ExpressionType.DATE, result.getType());

        Date need = getDateFromString("1922-01-01 00:00:00");
        assertEquals(need, (Date) result.getValue());
    }

    @Test
    public void calculator_FnDayFromDateTest() {
        tree = treeProvider.buildTree("fn:day-from-date($par:refPeriodEnd)");

        Result result = calculator.evaluate(tree);

        assertEquals(ExpressionType.INTEGER, result.getType());

        assertEquals(30, (Integer) result.getValue());
    }

    @Test
    public void calculator_FnMonthFromDateTest() {
        tree = treeProvider.buildTree("fn:month-from-date($par:refPeriodEnd)");

        Result result = calculator.evaluate(tree);

        assertEquals(ExpressionType.INTEGER, result.getType());

        assertEquals(6, (Integer) result.getValue());
    }

    @Test
    public void calculator_XsdYearMonthDurationTest() {
        tree = treeProvider.buildTree("xsd:yearMonthDuration('P3M')");

        Result result = calculator.evaluate(tree);

        assertEquals(ExpressionType.PERIOD, result.getType());

        assertEquals("0:3:0:0:0:0", result.getValue());
    }

    @Test
    public void calculator_XsdYearMonthDurationTest2Years() {
        tree = treeProvider.buildTree("xsd:yearMonthDuration('P2Y3M')");

        Result result = calculator.evaluate(tree);

        assertEquals(ExpressionType.PERIOD, result.getType());

        assertEquals("2:3:0:0:0:0", result.getValue());
    }
}
