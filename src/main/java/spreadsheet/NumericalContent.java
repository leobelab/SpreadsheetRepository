package spreadsheet;

// A number. An empty cell is a numerical content without value.
public class NumericalContent extends Content {

    private double value;
    private boolean empty;

    // empty content
    public NumericalContent() {
        this.value = 0;
        this.empty = true;
    }

    public NumericalContent(double value) {
        this.value = value;
        this.empty = false;
    }

    public String getText() {
        return getString();
    }

    // the number as a string, or "" if the cell is empty
    public String getString() {
        if (empty) {
            return "";
        }
        return numberToString(value);
    }

    public double getNumber() {
        return value;
    }
}
