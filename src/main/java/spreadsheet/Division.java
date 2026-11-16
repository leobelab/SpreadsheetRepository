package spreadsheet;

// Composite following the Composite tree pattern that you said in class
public class Division extends Expression {

    private Expression left;
    private Expression right;

    public Division(Expression left, Expression right) {
        this.left = left;
        this.right = right;
    }

    public double evaluate() {
        double divisor = right.evaluate();
        if (divisor == 0) {
            throw new ArithmeticException("Division by zero");
        }
        return left.evaluate() / divisor;
    }

    public String toString() {
        return "(" + left + " / " + right + ")";
    }
}
