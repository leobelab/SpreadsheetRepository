package spreadsheet;

// Leaf of the composite pattern tree
public class NumberExpression extends Expression {

    private double value;

    public NumberExpression(double value) {
        this.value = value;
    }

    public double evaluate() {
        return value;
    }

    public String toString() {
        return Content.numberToString(value);
    }
}
