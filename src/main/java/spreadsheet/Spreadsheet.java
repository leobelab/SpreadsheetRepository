package spreadsheet;

// A spreadsheet. The cells are stored in a simple 2D array: cells[row][column].
// Positions in the array start in 0, so A1 is cells[0][0] and B3 is cells[2][1].
// The array grows when we write in a cell that is outside of it. An empty cell is null.
public class Spreadsheet {

    private static final int MAX_ROWS = 1000;
    private static final int MAX_COLUMNS = 1000;

    private String name;
    private Content[][] cells = new Content[10][10];

    public Spreadsheet(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    // set and get cells

    // Sets the content of a cell using its coordinate as "B3", from the text written by the user
    public void setContent(String coordinate, String text) {
        int[] position = readCoordinate(coordinate);
        Content content = Content.create(text);
        if (content.getText().equals("")) {
            content = null;
        }
        setContent(position[0], position[1], content);
    }

    // Sets the content of a cell using its position in the array (null empties the cell)
    public void setContent(int row, int column, Content content) {
        if (row >= MAX_ROWS || column >= MAX_COLUMNS) {
            throw new IllegalArgumentException("The cell is too far (maximum " + MAX_ROWS + " rows and "
                    + MAX_COLUMNS + " columns)");
        }
        if (content == null && (row >= cells.length || column >= cells[0].length)) {
            return;
        }
        makeSpace(row, column);
        cells[row][column] = content;
    }

    // Returns the content of a cell. If the cell is empty it returns an empty numerical content.
    public Content getContent(String coordinate) {
        int[] position = readCoordinate(coordinate);
        return getContent(position[0], position[1]);
    }

    public Content getContent(int row, int column) {
        if (row >= cells.length || column >= cells[0].length || cells[row][column] == null) {
            return new NumericalContent();
        }
        return cells[row][column];
    }

    public boolean isEmpty(int row, int column) {
        return row >= cells.length || column >= cells[0].length || cells[row][column] == null;
    }

    // Number of rows until the last one that has something
    public int getNumberOfRows() {
        int result = 0;
        for (int row = 0; row < cells.length; row++) {
            for (int column = 0; column < cells[0].length; column++) {
                if (cells[row][column] != null) {
                    result = row + 1;
                }
            }
        }
        return result;
    }

    // Number of columns until the last one that has something
    public int getNumberOfColumns() {
        int result = 0;
        for (int row = 0; row < cells.length; row++) {
            for (int column = 0; column < cells[0].length; column++) {
                if (cells[row][column] != null) {
                    result = Math.max(result, column + 1);
                }
            }
        }
        return result;
    }

    // Makes the array bigger (copying the old cells) if the position is outside of it
    private void makeSpace(int row, int column) {
        if (row < cells.length && column < cells[0].length) {
            return;
        }
        int newRows = Math.max(row + 1, cells.length);
        int newColumns = Math.max(column + 1, cells[0].length);
        Content[][] bigger = new Content[newRows][newColumns];
        for (int r = 0; r < cells.length; r++) {
            for (int c = 0; c < cells[0].length; c++) {
                bigger[r][c] = cells[r][c];
            }
        }
        cells = bigger;
    }

    // coordinates

    // "A" -> 0, "B" -> 1, "Z" -> 25, "AA" -> 26, "AB" -> 27 ...
    public static int columnToNumber(String letters) {
        int number = 0;
        for (int i = 0; i < letters.length(); i++) {
            number = number * 26 + (letters.charAt(i) - 'A' + 1);
        }
        return number - 1;
    }

    // 0 -> "A", 25 -> "Z", 26 -> "AA" ...
    public static String columnToLetters(int column) {
        String letters = "";
        int n = column + 1;
        while (n > 0) {
            n--;
            letters = (char) ('A' + n % 26) + letters;
            n = n / 26;
        }
        return letters;
    }

    // Converts a coordinate like "B3" into {row, column} = {2, 1}
    private int[] readCoordinate(String coordinate) {
        String text = coordinate.trim().toUpperCase();
        if (!text.matches("[A-Z]{1,3}[0-9]{1,6}")) {
            throw new IllegalArgumentException("Wrong coordinate '" + coordinate.trim()
                    + "'. It must be column letters and a row number, for example A1");
        }
        int i = 0;
        while (Character.isLetter(text.charAt(i))) {
            i++;
        }
        int column = columnToNumber(text.substring(0, i));
        int row = Integer.parseInt(text.substring(i)) - 1;
        if (row < 0) {
            throw new IllegalArgumentException("Wrong coordinate '" + coordinate.trim() + "'. Rows start in 1");
        }
        return new int[] {row, column};
    }
}
