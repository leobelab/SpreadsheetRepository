package spreadsheet;

/** Common abstract class/interface of all the nodes of the expressions tree (Composite pattern).
 Leaf: NumberExpression
 Composites: Addition, Subtraction, Multiplication, Division
 */
public abstract class Expression {

    double evaluate() {
        return 0;
    }
}
