package spreadsheet;

// Composite following the Composite tree pattern that you said in class
public class Multiplication extends Expression {

    private Expression left;
    private Expression right;

    public Multiplication(Expression left, Expression right) {
        this.left = left;
        this.right = right;
    }

    public double evaluate() {
        return left.evaluate() * right.evaluate();
    }

    public String toString() {
        return "(" + left + " * " + right + ")";
    }
}
