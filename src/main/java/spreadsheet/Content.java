package spreadsheet;

//What a cell contains. There are three kinds: numerical, text and formula.
public abstract class Content {

    // The text as the user writes it (and as it is saved in the file)
    public abstract String getText();

    // The value as a string for composites formula and numerical
    public abstract String getString();

    // The value as a number. If there is no number it throws an exception.
    public abstract double getNumber();

    // If it starts with '=' -> formula
    // If it looks like a number -> numerical
    //empty -> empty numerical content
    // anything else -> text
    public static Content create(String text) {
        String trimmed = text.trim();
        if (trimmed.equals("")) {
            return new NumericalContent();
        }
        if (trimmed.startsWith("=")) {
            return new FormulaContent(trimmed);
        }
        if (isNumber(trimmed)) {
            return new NumericalContent(Double.parseDouble(trimmed));
        }
        if (text.contains(";")) {
            // ';' is the separator of the S2V files, so a text cannot have it
            throw new IllegalArgumentException("A text cannot contain the character ';'");
        }
        return new TextContent(text);
    }

    // True if the text is a number like 1, -5, 1.2 or +3.75
    public static boolean isNumber(String text) {
        return text.matches("[+-]?[0-9]+(\\.[0-9]+)?");
    }

    // 3.0 -> "3" and 1.2 -> "1.2"
    public static String numberToString(double number) {
        if (number == Math.rint(number) && Math.abs(number) < 1e15) {
            return String.valueOf((long) number);
        }
        return String.valueOf(number);
    }
}
