package spreadsheet;

// A formula, for example "=C1+C2". For now it is only stored as text, it is not calculated.
public class FormulaContent extends Content {

    private String formula;

    public FormulaContent(String formula) {
        this.formula = formula;
    }

    public String getText() {
        return formula;
    }

    // There is no calculated value yet, so we return the formula itself
    public String getString() {
        return formula;
    }

    public double getNumber() {
        throw new UnsupportedOperationException("Formulas are not calculated yet");
    }
}
