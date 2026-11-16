package spreadsheet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

/**
 * Tests of the expression tree. The trees are built by hand, so the operator precedence and the
 * parentheses are decided by the shape of the tree: what is lower in the tree is calculated first.
 */
public class ExpressionTest {

    private static final double DELTA = 0.000001;

    // small helper so the trees are easier to read
    private Expression num(double value) {
        return new NumberExpression(value);
    }

    // numbers

    @Test
    public void numberReturnsItsValue() {
        assertEquals(5, num(5).evaluate(), DELTA);
    }

    @Test
    public void negativeAndDecimalNumbers() {
        assertEquals(-3, num(-3).evaluate(), DELTA);
        assertEquals(2.5, num(2.5).evaluate(), DELTA);
        assertEquals(0, num(0).evaluate(), DELTA);
    }

    // one operation

    @Test
    public void addition() {
        // 1 + 2
        Expression e = new Addition(num(1), num(2));
        assertEquals(3, e.evaluate(), DELTA);
    }

    @Test
    public void subtraction() {
        // 10 - 4
        Expression e = new Subtraction(num(10), num(4));
        assertEquals(6, e.evaluate(), DELTA);
    }

    @Test
    public void subtractionCanBeNegative() {
        // 2 - 5
        Expression e = new Subtraction(num(2), num(5));
        assertEquals(-3, e.evaluate(), DELTA);
    }

    @Test
    public void multiplication() {
        // 3 * 5
        Expression e = new Multiplication(num(3), num(5));
        assertEquals(15, e.evaluate(), DELTA);
    }

    @Test
    public void division() {
        // 10 / 4
        Expression e = new Division(num(10), num(4));
        assertEquals(2.5, e.evaluate(), DELTA);
    }

    @Test
    public void decimals() {
        // 1.5 + 2.5
        Expression e = new Addition(num(1.5), num(2.5));
        assertEquals(4, e.evaluate(), DELTA);
    }

    // precedence

    @Test
    public void multiplicationBeforeAddition() {
        // 3 * 5 + 4 = 19
        Expression e = new Addition(new Multiplication(num(3), num(5)), num(4));
        assertEquals(19, e.evaluate(), DELTA);
    }

    @Test
    public void multiplicationBeforeAdditionOnTheRight() {
        // 4 + 3 * 5 = 19
        Expression e = new Addition(num(4), new Multiplication(num(3), num(5)));
        assertEquals(19, e.evaluate(), DELTA);
    }

    @Test
    public void onePlusTwoTimesThree() {
        // 1 + 2 * 3 = 7
        Expression e = new Addition(num(1), new Multiplication(num(2), num(3)));
        assertEquals(7, e.evaluate(), DELTA);
    }

    @Test
    public void divisionBeforeSubtraction() {
        // 10 - 8 / 2 = 6
        Expression e = new Subtraction(num(10), new Division(num(8), num(2)));
        assertEquals(6, e.evaluate(), DELTA);
    }

    // parentheses

    @Test
    public void parenthesesChangeTheResult() {
        // (1 + 2) * 3 = 9   (without parentheses it would be 7)
        Expression e = new Multiplication(new Addition(num(1), num(2)), num(3));
        assertEquals(9, e.evaluate(), DELTA);
    }

    @Test
    public void parenthesesInBothSides() {
        // (1 + 2) * (3 + 4) = 21
        Expression e = new Multiplication(
                new Addition(num(1), num(2)),
                new Addition(num(3), num(4)));
        assertEquals(21, e.evaluate(), DELTA);
    }

    @Test
    public void subtractionOrderLeftToRight() {
        // 10 - 3 - 2 = (10 - 3) - 2 = 5
        Expression e = new Subtraction(new Subtraction(num(10), num(3)), num(2));
        assertEquals(5, e.evaluate(), DELTA);
    }

    @Test
    public void subtractionWithParentheses() {
        // 10 - (3 - 2) = 9
        Expression e = new Subtraction(num(10), new Subtraction(num(3), num(2)));
        assertEquals(9, e.evaluate(), DELTA);
    }

    @Test
    public void divisionOrderLeftToRight() {
        // 8 / 4 / 2 = (8 / 4) / 2 = 1
        Expression e = new Division(new Division(num(8), num(4)), num(2));
        assertEquals(1, e.evaluate(), DELTA);
    }

    @Test
    public void divisionWithParentheses() {
        // 8 / (4 / 2) = 4
        Expression e = new Division(num(8), new Division(num(4), num(2)));
        assertEquals(4, e.evaluate(), DELTA);
    }

    // nested expressions

    @Test
    public void nestedExpression() {
        // ((2 + 3) * (4 - 1)) / 5 = 3
        Expression e = new Division(
                new Multiplication(
                        new Addition(num(2), num(3)),
                        new Subtraction(num(4), num(1))),
                num(5));
        assertEquals(3, e.evaluate(), DELTA);
    }

    @Test
    public void threeFactors() {
        // 2 * (3 + 4) * (5 - 1) = 56
        Expression e = new Multiplication(
                new Multiplication(num(2), new Addition(num(3), num(4))),
                new Subtraction(num(5), num(1)));
        assertEquals(56, e.evaluate(), DELTA);
    }

    @Test
    public void deepExpression() {
        // 1 + (2 + (3 + (4 + 5))) = 15
        Expression e = new Addition(num(1),
                new Addition(num(2),
                        new Addition(num(3),
                                new Addition(num(4), num(5)))));
        assertEquals(15, e.evaluate(), DELTA);
    }

    @Test
    public void allTheOperationsTogether() {
        // 2 + 3 * 4 - 10 / 5 = 12
        Expression e = new Subtraction(
                new Addition(num(2), new Multiplication(num(3), num(4))),
                new Division(num(10), num(5)));
        assertEquals(12, e.evaluate(), DELTA);
    }

    @Test
    public void sameTreeCanBeEvaluatedTwice() {
        Expression e = new Addition(num(1), num(2));
        assertEquals(3, e.evaluate(), DELTA);
        assertEquals(3, e.evaluate(), DELTA);
    }

    // division by zero

    @Test
    public void divisionByZero() {
        Expression e = new Division(num(1), num(0));
        assertThrows(ArithmeticException.class, () -> e.evaluate());
    }

    @Test
    public void divisionByZeroInsideAnotherExpression() {
        // 5 + 1 / (2 - 2)
        Expression e = new Addition(num(5), new Division(num(1), new Subtraction(num(2), num(2))));
        assertThrows(ArithmeticException.class, () -> e.evaluate());
    }

    @Test
    public void zeroDividedByNumberIsZero() {
        Expression e = new Division(num(0), num(5));
        assertEquals(0, e.evaluate(), DELTA);
    }

    // toString

    @Test
    public void toStringShowsTheTree() {
        Expression e = new Multiplication(new Addition(num(1), num(2)), num(3));
        assertEquals("((1 + 2) * 3)", e.toString());
    }
}
