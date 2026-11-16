package spreadsheet;

// A text (headers, notes...)
public class TextContent extends Content {

    private String text;

    public TextContent(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public String getString() {
        return text;
    }

    // An empty string is 0, a string like "1.2" is 1.2, anything else is an error
    public double getNumber() {
        String trimmed = text.trim();
        if (trimmed.equals("")) {
            return 0;
        }
        if (!isNumber(trimmed)) {
            throw new IllegalArgumentException("'" + text + "' is not a number");
        }
        return Double.parseDouble(trimmed);
    }
}
