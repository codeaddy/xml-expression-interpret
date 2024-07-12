import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.xmlevaluator.dto.*;
import org.xmlevaluator.treeMaker.TreeProvider;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TreeProviderTest {
    TreeProvider treeProvider;

    @BeforeEach
    public void BeforeEach() {
        treeProvider = new TreeProvider();
    }

    @Test
    public void isVariableNameSymbolTest() {
        assertEquals(true, treeProvider.isVariableNameSymbol('e'));
        assertEquals(true, treeProvider.isVariableNameSymbol('a'));
        assertEquals(true, treeProvider.isVariableNameSymbol('1'));
        assertEquals(true, treeProvider.isVariableNameSymbol(':'));

        assertEquals(false, treeProvider.isVariableNameSymbol('?'));
        assertEquals(false, treeProvider.isVariableNameSymbol('*'));
        assertEquals(false, treeProvider.isVariableNameSymbol('/'));
    }

    @Test
    public void isNumberTest() {
        assertEquals(1, treeProvider.isNumber("123"));
        assertEquals(1, treeProvider.isNumber("00"));
        assertEquals(1, treeProvider.isNumber("-12300"));
        assertEquals(1, treeProvider.isNumber(""));
        assertEquals(2, treeProvider.isNumber("0.0"));
        assertEquals(2, treeProvider.isNumber("0.05"));
        assertEquals(2, treeProvider.isNumber("-12300.5"));

        assertEquals(0, treeProvider.isNumber("xxx"));
        assertEquals(0, treeProvider.isNumber("twenty"));
        assertEquals(0, treeProvider.isNumber("1!"));
    }

    @Test
    public void parseQuery_TwoTest() {
        Expression e = treeProvider.parseQuery("2");

        Expression need = new Constant("2", ExpressionType.INTEGER);

        assertTrue(need.equals(e));
    }

    @Test
    public void parseQuery_TwoPlusTwoTest() {
        Expression e = treeProvider.parseQuery("2+2");

        Expression need = new Expression();
        need.addPart(new Constant("2", ExpressionType.INTEGER));
        need.addPart(new Operator(Operator.Type.PLUS));
        need.addPart(new Constant("2", ExpressionType.INTEGER));

        assertEquals(need.getPartsCount(), e.getPartsCount());

        for (int i = 0; i < need.getPartsCount(); ++i) {
            assertTrue(need.at(i).equals(e.at(i)));
        }
    }

    @Test
    public void parseQuery_TwoPlusThreeTest() {
        Expression e = treeProvider.parseQuery("2+3");

        Expression need = new Expression();
        need.addPart(new Constant("2", ExpressionType.INTEGER));
        need.addPart(new Operator(Operator.Type.PLUS));
        need.addPart(new Constant("3", ExpressionType.INTEGER));

        assertEquals(need.getPartsCount(), e.getPartsCount());

        for (int i = 0; i < need.getPartsCount(); ++i) {
            assertTrue(need.at(i).equals(e.at(i)));
        }
    }

    @Test
    public void parseQuery_TwoPlusThreeEqualFiveTest() {
        Expression e = treeProvider.parseQuery("2+3=5");

        Expression need = new Expression();
        need.addPart(new Constant("2", ExpressionType.INTEGER));
        need.addPart(new Operator(Operator.Type.PLUS));
        need.addPart(new Constant("3", ExpressionType.INTEGER));
        need.addPart(new Operator(Operator.Type.EQUAL));
        need.addPart(new Constant("5", ExpressionType.INTEGER));

        assertEquals(need.getPartsCount(), e.getPartsCount());

        for (int i = 0; i < need.getPartsCount(); ++i) {
            assertTrue(need.at(i).equals(e.at(i)));
        }
    }

    @Test
    public void parseQuery_TwoMinusThreeTest() {
        Expression e = treeProvider.parseQuery("2 - 3");

        Expression need = new Expression();
        need.addPart(new Constant("2", ExpressionType.INTEGER));
        need.addPart(new Operator(Operator.Type.MINUS));
        need.addPart(new Constant("3", ExpressionType.INTEGER));

        assertEquals(need.getPartsCount(), e.getPartsCount());

        for (int i = 0; i < need.getPartsCount(); ++i) {
            assertTrue(need.at(i).equals(e.at(i)));
        }
    }

    @Test
    public void parseQuery_TwoPlusSumOfTwoAndThreeTest() {
        Expression e = treeProvider.parseQuery("2+(2+3)");

        Expression need = new Expression();
        need.addPart(new Constant("2", ExpressionType.INTEGER));
        need.addPart(new Operator(Operator.Type.PLUS));
        need.addPart(new Expression());
        need.at(2).addPart(new Constant("2", ExpressionType.INTEGER));
        need.at(2).addPart(new Operator(Operator.Type.PLUS));
        need.at(2).addPart(new Constant("3", ExpressionType.INTEGER));

        assertEquals(need.getPartsCount(), e.getPartsCount());

        for (int i = 0; i < need.getPartsCount(); ++i) {
            assertTrue(need.at(i).equals(e.at(i)));
        }
    }

    @Test
    public void parseQuery_TwoMultiplyBySumOfTwoAndThreeTest() {
        Expression e = treeProvider.parseQuery("2*(2+3)");

        Expression need = new Expression();
        need.addPart(new Constant("2", ExpressionType.INTEGER));
        need.addPart(new Operator(Operator.Type.MULTIPLY));
        need.addPart(new Expression());
        need.at(2).addPart(new Constant("2", ExpressionType.INTEGER));
        need.at(2).addPart(new Operator(Operator.Type.PLUS));
        need.at(2).addPart(new Constant("3", ExpressionType.INTEGER));

        assertEquals(need.getPartsCount(), e.getPartsCount());

        for (int i = 0; i < need.getPartsCount(); ++i) {
            assertTrue(need.at(i).equals(e.at(i)));
        }
    }

    @Test
    public void parseQuery_XsdDateOfVariable() {
        Expression e = treeProvider.parseQuery("xsd:date($par:var)");

        Expression need = new Expression();
        need.addPart(new Operator(Operator.Type.XSD_DATE));
        need.addPart(new Variable("par:var"));

        assertEquals(need.getPartsCount(), e.getPartsCount());

        for (int i = 0; i < need.getPartsCount(); ++i) {
            assertTrue(need.at(i).equals(e.at(i)));
        }
    }

    @Test
    public void parseQuery_MaxOfTwoConstants() {
        Expression e = treeProvider.parseQuery("max(2,3)");

        Expression need = new Expression();
        need.addPart(new Operator(Operator.Type.MAX));
        need.addPart(new Expression());
        need.at(1).addPart(new Constant("2", ExpressionType.INTEGER));
        need.at(1).addPart(new Constant("3", ExpressionType.INTEGER));

        assertEquals(need.getPartsCount(), e.getPartsCount());

        for (int i = 0; i < need.getPartsCount(); ++i) {
            assertTrue(need.at(i).equals(e.at(i)));
        }
    }

    @Test
    public void parseQuery_MaxOfConstantAndVariable() {
        Expression e = treeProvider.parseQuery("max(2,$par:rap)");

        Expression need = new Expression();
        need.addPart(new Operator(Operator.Type.MAX));
        need.addPart(new Expression());
        need.at(1).addPart(new Constant("2", ExpressionType.INTEGER));
        need.at(1).addPart(new Variable("par:rap"));

        assertEquals(need.getPartsCount(), e.getPartsCount());

        for (int i = 0; i < need.getPartsCount(); ++i) {
            assertTrue(need.at(i).equals(e.at(i)));
        }
    }

    @Test
    public void parseQuery_ConcatStringOfDate() {
        Expression e = treeProvider.parseQuery("fn:concat('-12-31',xsd:string(xsd:date('2022-12-12')))");

        Expression need = new Expression();
        need.addPart(new Operator(Operator.Type.FN_CONCAT));
        need.addPart(new Expression());
        need.at(1).addPart(new Constant("-12-31", ExpressionType.STRING));
        need.at(1).addPart(new Operator(Operator.Type.XSD_STRING));
        need.at(1).addPart(new Expression());
        need.at(1).at(2).addPart(new Operator(Operator.Type.XSD_DATE));
        need.at(1).at(2).addPart(new Constant("2022-12-12", ExpressionType.STRING));

        assertEquals(need.getPartsCount(), e.getPartsCount());

        for (int i = 0; i < need.getPartsCount(); ++i) {
            assertTrue(need.at(i).equals(e.at(i)));
        }
    }

    @Test
    public void parseQuery_ifDateEq() {
        Expression e = treeProvider.parseQuery("if(xsd:date($par:par) eq xsd:date('2022-12-12')) then 'YES' else 'NO'");

        Expression if_e = new Expression();
        if_e.addPart(new Operator(Operator.Type.XSD_DATE));
        if_e.addPart(new Variable("par:par"));
        if_e.addPart(new Operator(Operator.Type.EQ));
        if_e.addPart(new Operator(Operator.Type.XSD_DATE));
        if_e.addPart(new Constant("2022-12-12", ExpressionType.DATE));

        Expression then_e = new Constant("YES", ExpressionType.STRING);

        Expression else_e = new Constant("NO", ExpressionType.STRING);

        Expression need = new Condition(if_e, then_e, else_e);
        assertEquals(need.getPartsCount(), e.getPartsCount());

        for (int i = 0; i < need.getPartsCount(); ++i) {
            assertTrue(need.at(i).equals(e.at(i)));
        }
    }

    @Test
    public void parseQuery_SingleXPath() {
        Expression e = treeProvider.parseQuery("//xbrli:period/xbrli:startDate");

        XPathElement need = new XPathElement("//xbrli:period/xbrli:startDate");

        assertEquals(need, e);
        assertEquals(need.getPartsCount(), e.getPartsCount());

        for (int i = 0; i < need.getPartsCount(); ++i) {
            assertTrue(need.at(i).equals(e.at(i)));
        }
    }

    @Test
    public void parseQuery_SelectExample1() {
        Expression e = treeProvider.parseQuery("if (exists(//xbrli:period/xbrli:startDate)) then max(//xbrli:period/xbrli:startDate) else xsd:date('2016-01-01')");

        Expression if_e = new Expression();
        if_e.addPart(new Operator(Operator.Type.EXISTS));
        if_e.addPart(new XPathElement("//xbrli:period/xbrli:startDate"));

        Expression then_e = new Expression();
        then_e.addPart(new Operator(Operator.Type.MAX));
        then_e.addPart(new XPathElement("//xbrli:period/xbrli:startDate"));

        Expression else_e = new Expression();
        else_e.addPart(new Operator(Operator.Type.XSD_DATE));
        else_e.addPart(new Constant("2016-01-01", ExpressionType.STRING));

        Condition need = new Condition(if_e, then_e, else_e);

        assertEquals(need, e);
        assertEquals(need.getPartsCount(), e.getPartsCount());

        for (int i = 0; i < need.getPartsCount(); ++i) {
            assertTrue(need.at(i).equals(e.at(i)));
        }
    }

    @Test
    public void parseQuery_SelectExample2() {
        Expression e = treeProvider.parseQuery("if (exists(max(//xbrli:period/(xbrli:instant | xbrli:endDate)))) then max(//xbrli:period/(xbrli:instant | xbrli:endDate)) else xsd:date('2016-12-31')");

        Expression if_e = new Expression();
        if_e.addPart(new Operator(Operator.Type.EXISTS));
        if_e.addPart(new Expression());
        if_e.at(1).addPart(new Operator(Operator.Type.MAX));
        if_e.at(1).addPart(new XPathElement("//xbrli:period/(xbrli:instant | xbrli:endDate)"));

        Expression then_e = new Expression();
        then_e.addPart(new Operator(Operator.Type.MAX));
        then_e.addPart(new XPathElement("//xbrli:period/(xbrli:instant | xbrli:endDate)"));

        Expression else_e = new Expression();
        else_e.addPart(new Operator(Operator.Type.XSD_DATE));
        else_e.addPart(new Constant("2016-12-31", ExpressionType.STRING));

        Condition need = new Condition(if_e, then_e, else_e);

        assertEquals(need, e);
        assertEquals(need.getPartsCount(), e.getPartsCount());

        for (int i = 0; i < need.getPartsCount(); ++i) {
            assertTrue(need.at(i).equals(e.at(i)));
        }
    }

    @Test
    public void parseQuery_SelectExample3() {
        Expression e = treeProvider.parseQuery("if ((fn:day-from-date($par:refPeriodEnd)=30) and (fn:month-from-date($par:refPeriodEnd)=6)) then ($par:refPeriodEnd - xsd:yearMonthDuration('P3M') + xsd:dayTimeDuration('P1D')) else ($par:refPeriodEnd - xsd:yearMonthDuration('P3M'))");

        Expression if_e = new Expression();
        if_e.addPart(new Expression());
        if_e.at(0).addPart(new Operator(Operator.Type.FN_DAY_FROM_DATE));
        if_e.at(0).addPart(new Variable("par:refPeriodEnd"));
        if_e.at(0).addPart(new Operator(Operator.Type.EQUAL));
        if_e.at(0).addPart(new Constant("30", ExpressionType.INTEGER));
        if_e.addPart(new Operator(Operator.Type.AND));
        if_e.addPart(new Expression());
        if_e.at(2).addPart(new Operator(Operator.Type.FN_MONTH_FROM_DATE));
        if_e.at(2).addPart(new Variable("par:refPeriodEnd"));
        if_e.at(2).addPart(new Operator(Operator.Type.EQUAL));
        if_e.at(2).addPart(new Constant("6", ExpressionType.INTEGER));

        Expression then_e = new Expression();
        then_e.addPart(new Variable("par:refPeriodEnd"));
        then_e.addPart(new Operator(Operator.Type.MINUS));
        then_e.addPart(new Operator(Operator.Type.XSD_YEAR_MONTH_DURATION));
        then_e.addPart(new Constant("P3M", ExpressionType.STRING));
        then_e.addPart(new Operator(Operator.Type.PLUS));
        then_e.addPart(new Operator(Operator.Type.XSD_DAY_TIME_DURATION));
        then_e.addPart(new Constant("P1D", ExpressionType.STRING));

        Expression else_e = new Expression();
        else_e.addPart(new Variable("par:refPeriodEnd"));
        else_e.addPart(new Operator(Operator.Type.MINUS));
        else_e.addPart(new Operator(Operator.Type.XSD_YEAR_MONTH_DURATION));
        else_e.addPart(new Constant("P3M", ExpressionType.STRING));

        Condition need = new Condition(if_e, then_e, else_e);

        assertEquals(need, e);
        assertEquals(need.getPartsCount(), e.getPartsCount());

        for (int i = 0; i < need.getPartsCount(); ++i) {
            assertTrue(need.at(i).equals(e.at(i)));
        }
    }

    @Test
    public void parseQuery_SelectExample4() {
        Expression e = treeProvider.parseQuery("if ((fn:month-from-date($par:refPeriodEnd)=3) or (fn:month-from-date($par:refPeriodEnd)=12) or (fn:month-from-date($par:refPeriodEnd)=9)) then ($par:refPeriodEnd - xsd:yearMonthDuration('P3M')) else ($par:refPeriodEnd - xsd:yearMonthDuration('P3M') + xsd:dayTimeDuration('P1D'))");

        Expression if_e = new Expression();
        if_e.addPart(new Expression());
        if_e.at(0).addPart(new Operator(Operator.Type.FN_MONTH_FROM_DATE));
        if_e.at(0).addPart(new Variable("par:refPeriodEnd"));
        if_e.at(0).addPart(new Operator(Operator.Type.EQUAL));
        if_e.at(0).addPart(new Constant("3", ExpressionType.INTEGER));
        if_e.addPart(new Operator(Operator.Type.OR));
        if_e.addPart(new Expression());
        if_e.at(2).addPart(new Operator(Operator.Type.FN_MONTH_FROM_DATE));
        if_e.at(2).addPart(new Variable("par:refPeriodEnd"));
        if_e.at(2).addPart(new Operator(Operator.Type.EQUAL));
        if_e.at(2).addPart(new Constant("12", ExpressionType.INTEGER));
        if_e.addPart(new Operator(Operator.Type.OR));
        if_e.addPart(new Expression());
        if_e.at(4).addPart(new Operator(Operator.Type.FN_MONTH_FROM_DATE));
        if_e.at(4).addPart(new Variable("par:refPeriodEnd"));
        if_e.at(4).addPart(new Operator(Operator.Type.EQUAL));
        if_e.at(4).addPart(new Constant("9", ExpressionType.INTEGER));

        Expression then_e = new Expression();
        then_e.addPart(new Variable("par:refPeriodEnd"));
        then_e.addPart(new Operator(Operator.Type.MINUS));
        then_e.addPart(new Operator(Operator.Type.XSD_YEAR_MONTH_DURATION));
        then_e.addPart(new Constant("P3M", ExpressionType.STRING));

        Expression else_e = new Expression();
        else_e.addPart(new Variable("par:refPeriodEnd"));
        else_e.addPart(new Operator(Operator.Type.MINUS));
        else_e.addPart(new Operator(Operator.Type.XSD_YEAR_MONTH_DURATION));
        else_e.addPart(new Constant("P3M", ExpressionType.STRING));
        else_e.addPart(new Operator(Operator.Type.PLUS));
        else_e.addPart(new Operator(Operator.Type.XSD_DAY_TIME_DURATION));
        else_e.addPart(new Constant("P1D", ExpressionType.STRING));

        Condition need = new Condition(if_e, then_e, else_e);

        assertEquals(need, e);
        assertEquals(need.getPartsCount(), e.getPartsCount());

        for (int i = 0; i < need.getPartsCount(); ++i) {
            assertTrue(need.at(i).equals(e.at(i)));
        }
    }

    @Test
    public void parseQuery_SelectExample5() {
        Expression e = treeProvider.parseQuery("xsd:date(fn:concat(xsd:string(fn:year-from-date($par:refPeriodEnd) - 1),'-12-31'))");

        Expression need = new Expression();
        need.addPart(new Operator(Operator.Type.XSD_DATE));
        need.addPart(new Expression());
        need.at(1).addPart(new Operator(Operator.Type.FN_CONCAT));
        need.at(1).addPart(new Expression());
        need.at(1).at(1).addPart(new Operator(Operator.Type.XSD_STRING));
        need.at(1).at(1).addPart(new Expression());
        need.at(1).at(1).at(1).addPart(new Operator(Operator.Type.FN_YEAR_FROM_DATE));
        need.at(1).at(1).at(1).addPart(new Variable("par:refPeriodEnd"));
        need.at(1).at(1).at(1).addPart(new Operator(Operator.Type.MINUS));
        need.at(1).at(1).at(1).addPart(new Constant("1", ExpressionType.INTEGER));

        need.at(1).at(1).addPart(new Constant("-12-31", ExpressionType.STRING));


        assertEquals(need, e);
        assertEquals(need.getPartsCount(), e.getPartsCount());

        for (int i = 0; i < need.getPartsCount(); ++i) {
            assertTrue(need.at(i).equals(e.at(i)));
        }
    }

    @Test
    public void parseQuery_SelectExample6() {
        Expression e = treeProvider.parseQuery("xsd:date($par:CurrentPeriodStart) - xsd:yearMonthDuration('P1Y')");

        Expression need = new Expression();
        need.addPart(new Operator(Operator.Type.XSD_DATE));
        need.addPart(new Variable("par:CurrentPeriodStart"));
        need.addPart(new Operator(Operator.Type.MINUS));
        need.addPart(new Operator(Operator.Type.XSD_YEAR_MONTH_DURATION));
        need.addPart(new Constant("P1Y", ExpressionType.STRING));


        assertEquals(need, e);
        assertEquals(need.getPartsCount(), e.getPartsCount());

        for (int i = 0; i < need.getPartsCount(); ++i) {
            assertTrue(need.at(i).equals(e.at(i)));
        }
    }

    @Test
    public void parseQuery_SelectExample7() {
        Expression e = treeProvider.parseQuery("xsd:date($par:CurrentPeriodStart) - xsd:yearMonthDuration(if($par:ReportingPeriodScope eq 'I') then 'P1Y' else 'P2Y')");

        Expression need = new Expression();
        need.addPart(new Operator(Operator.Type.XSD_DATE));
        need.addPart(new Variable("par:CurrentPeriodStart"));
        need.addPart(new Operator(Operator.Type.MINUS));
        need.addPart(new Operator(Operator.Type.XSD_YEAR_MONTH_DURATION));

        Expression if_e = new Expression();
        if_e.addPart(new Variable("par:ReportingPeriodScope"));
        if_e.addPart(new Operator(Operator.Type.EQ));
        if_e.addPart(new Constant("I", ExpressionType.STRING));

        Expression then_e = new Constant("P1Y", ExpressionType.STRING);

        Expression else_e = new Constant("P2Y", ExpressionType.STRING);

        need.addPart(new Condition(if_e, then_e, else_e));


        assertEquals(need, e);
        assertEquals(need.getPartsCount(), e.getPartsCount());

        for (int i = 0; i < need.getPartsCount(); ++i) {
            assertTrue(need.at(i).equals(e.at(i)));
        }
    }

    @Test
    public void parseQuery_SelectExample8() {
        Expression e = treeProvider.parseQuery("xsd:date($par:CurrentPeriodEnd) - xsd:yearMonthDuration(if($par:ReportingPeriodScope eq 'I') then 'P1Y' else 'P2Y')");

        Expression need = new Expression();
        need.addPart(new Operator(Operator.Type.XSD_DATE));
        need.addPart(new Variable("par:CurrentPeriodEnd"));
        need.addPart(new Operator(Operator.Type.MINUS));
        need.addPart(new Operator(Operator.Type.XSD_YEAR_MONTH_DURATION));

        Expression if_e = new Expression();
        if_e.addPart(new Variable("par:ReportingPeriodScope"));
        if_e.addPart(new Operator(Operator.Type.EQ));
        if_e.addPart(new Constant("I", ExpressionType.STRING));

        Expression then_e = new Constant("P1Y", ExpressionType.STRING);

        Expression else_e = new Constant("P2Y", ExpressionType.STRING);

        need.addPart(new Condition(if_e, then_e, else_e));


        assertEquals(need, e);
        assertEquals(need.getPartsCount(), e.getPartsCount());

        for (int i = 0; i < need.getPartsCount(); ++i) {
            assertTrue(need.at(i).equals(e.at(i)));
        }
    }

    @Test
    public void parseQuery_SelectExample9() {
        Expression e = treeProvider.parseQuery("xsd:string('A')");

        Expression need = new Expression();
        need.addPart(new Operator(Operator.Type.XSD_STRING));
        need.addPart(new Constant("A", ExpressionType.STRING));

        assertEquals(need, e);
        assertEquals(need.getPartsCount(), e.getPartsCount());

        for (int i = 0; i < need.getPartsCount(); ++i) {
            assertTrue(need.at(i).equals(e.at(i)));
        }
    }

    @Test
    public void parseQuery_SelectExample10() {
        Expression e = treeProvider.parseQuery("xsd:date(fn:concat(xsd:string(fn:year-from-date($par:refPeriodEnd) - 2),'-12-31'))");

        Expression need = new Expression();
        need.addPart(new Operator(Operator.Type.XSD_DATE));
        need.addPart(new Expression());

        need.at(1).addPart(new Operator(Operator.Type.FN_CONCAT));
        need.at(1).addPart(new Expression());

        need.at(1).at(1).addPart(new Operator(Operator.Type.XSD_STRING));
        need.at(1).at(1).addPart(new Expression());

        need.at(1).at(1).at(1).addPart(new Operator(Operator.Type.FN_YEAR_FROM_DATE));
        need.at(1).at(1).at(1).addPart(new Variable("par:refPeriodEnd"));
        need.at(1).at(1).at(1).addPart(new Operator(Operator.Type.MINUS));
        need.at(1).at(1).at(1).addPart(new Constant("2", ExpressionType.INTEGER));

        need.at(1).at(1).addPart(new Constant("-12-31", ExpressionType.STRING));

        assertEquals(need, e);
        assertEquals(need.getPartsCount(), e.getPartsCount());

        for (int i = 0; i < need.getPartsCount(); ++i) {
            assertTrue(need.at(i).equals(e.at(i)));
        }
    }

    @Test
    public void parseQuery_SelectExample11() {
        Expression e = treeProvider.parseQuery("xsd:date(fn:concat(xsd:string(fn:year-from-date($par:refPeriodEnd) - 2),'-12-31')) + xsd:dayTimeDuration('P1D')");

        Expression need = new Expression();
        need.addPart(new Operator(Operator.Type.XSD_DATE));
        need.addPart(new Expression());

        need.at(1).addPart(new Operator(Operator.Type.FN_CONCAT));
        need.at(1).addPart(new Expression());

        need.at(1).at(1).addPart(new Operator(Operator.Type.XSD_STRING));
        need.at(1).at(1).addPart(new Expression());

        need.at(1).at(1).at(1).addPart(new Operator(Operator.Type.FN_YEAR_FROM_DATE));
        need.at(1).at(1).at(1).addPart(new Variable("par:refPeriodEnd"));
        need.at(1).at(1).at(1).addPart(new Operator(Operator.Type.MINUS));
        need.at(1).at(1).at(1).addPart(new Constant("2", ExpressionType.INTEGER));

        need.at(1).at(1).addPart(new Constant("-12-31", ExpressionType.STRING));

        need.addPart(new Operator(Operator.Type.PLUS));
        need.addPart(new Operator(Operator.Type.XSD_DAY_TIME_DURATION));
        need.addPart(new Constant("P1D", ExpressionType.STRING));

        assertEquals(need, e);
        assertEquals(need.getPartsCount(), e.getPartsCount());

        for (int i = 0; i < need.getPartsCount(); ++i) {
            assertTrue(need.at(i).equals(e.at(i)));
        }
    }

    @Test
    public void parseQuery_SelectExample12() {
        Expression e = treeProvider.parseQuery("xsd:date(fn:concat(xsd:string(fn:year-from-date($par:refPeriodEnd) - 1),'-12-31'))");

        Expression need = new Expression();
        need.addPart(new Operator(Operator.Type.XSD_DATE));
        need.addPart(new Expression());

        need.at(1).addPart(new Operator(Operator.Type.FN_CONCAT));
        need.at(1).addPart(new Expression());

        need.at(1).at(1).addPart(new Operator(Operator.Type.XSD_STRING));
        need.at(1).at(1).addPart(new Expression());

        need.at(1).at(1).at(1).addPart(new Operator(Operator.Type.FN_YEAR_FROM_DATE));
        need.at(1).at(1).at(1).addPart(new Variable("par:refPeriodEnd"));
        need.at(1).at(1).at(1).addPart(new Operator(Operator.Type.MINUS));
        need.at(1).at(1).at(1).addPart(new Constant("1", ExpressionType.INTEGER));

        need.at(1).at(1).addPart(new Constant("-12-31", ExpressionType.STRING));

        assertEquals(need, e);
        assertEquals(need.getPartsCount(), e.getPartsCount());

        for (int i = 0; i < need.getPartsCount(); ++i) {
            assertTrue(need.at(i).equals(e.at(i)));
        }
    }

    @Test
    public void parseQuery_SelectExample13() {
        Expression e = treeProvider.parseQuery("if ((fn:month-from-date($par:refPeriodEnd)=3) or (fn:month-from-date($par:refPeriodEnd)=12) or (fn:month-from-date($par:refPeriodEnd)=9)) then ($par:refPeriodEnd - xsd:yearMonthDuration('P3M')) else ($par:refPeriodEnd - xsd:yearMonthDuration('P3M') + xsd:dayTimeDuration('P1D'))");

        Expression if_e = new Expression();
        if_e.addPart(new Expression());
        if_e.at(0).addPart(new Operator(Operator.Type.FN_MONTH_FROM_DATE));
        if_e.at(0).addPart(new Variable("par:refPeriodEnd"));
        if_e.at(0).addPart(new Operator(Operator.Type.EQUAL));
        if_e.at(0).addPart(new Constant("3", ExpressionType.INTEGER));
        if_e.addPart(new Operator(Operator.Type.OR));
        if_e.addPart(new Expression());
        if_e.at(2).addPart(new Operator(Operator.Type.FN_MONTH_FROM_DATE));
        if_e.at(2).addPart(new Variable("par:refPeriodEnd"));
        if_e.at(2).addPart(new Operator(Operator.Type.EQUAL));
        if_e.at(2).addPart(new Constant("12", ExpressionType.INTEGER));
        if_e.addPart(new Operator(Operator.Type.OR));
        if_e.addPart(new Expression());
        if_e.at(4).addPart(new Operator(Operator.Type.FN_MONTH_FROM_DATE));
        if_e.at(4).addPart(new Variable("par:refPeriodEnd"));
        if_e.at(4).addPart(new Operator(Operator.Type.EQUAL));
        if_e.at(4).addPart(new Constant("9", ExpressionType.INTEGER));

        Expression then_e = new Expression();
        then_e.addPart(new Variable("par:refPeriodEnd"));
        then_e.addPart(new Operator(Operator.Type.MINUS));
        then_e.addPart(new Operator(Operator.Type.XSD_YEAR_MONTH_DURATION));
        then_e.addPart(new Constant("P3M", ExpressionType.STRING));

        Expression else_e = new Expression();
        else_e.addPart(new Variable("par:refPeriodEnd"));
        else_e.addPart(new Operator(Operator.Type.MINUS));
        else_e.addPart(new Operator(Operator.Type.XSD_YEAR_MONTH_DURATION));
        else_e.addPart(new Constant("P3M", ExpressionType.STRING));
        else_e.addPart(new Operator(Operator.Type.PLUS));
        else_e.addPart(new Operator(Operator.Type.XSD_DAY_TIME_DURATION));
        else_e.addPart(new Constant("P1D", ExpressionType.STRING));

        Condition need = new Condition(if_e, then_e, else_e);

        assertEquals(need, e);
        assertEquals(need.getPartsCount(), e.getPartsCount());

        for (int i = 0; i < need.getPartsCount(); ++i) {
            assertTrue(need.at(i).equals(e.at(i)));
        }
    }

    @Test
    public void parseQuery_SelectExample14() {
        Expression e = treeProvider.parseQuery("xsd:date($par:RegStartInsQuart) + xsd:dayTimeDuration('P1D')");

        Expression need = new Expression();
        need.addPart(new Operator(Operator.Type.XSD_DATE));
        need.addPart(new Variable("par:RegStartInsQuart"));
        need.addPart(new Operator(Operator.Type.PLUS));
        need.addPart(new Operator(Operator.Type.XSD_DAY_TIME_DURATION));
        need.addPart(new Constant("P1D", ExpressionType.STRING));

        assertEquals(need, e);
        assertEquals(need.getPartsCount(), e.getPartsCount());

        for (int i = 0; i < need.getPartsCount(); ++i) {
            assertTrue(need.at(i).equals(e.at(i)));
        }
    }
}
