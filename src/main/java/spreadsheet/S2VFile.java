package spreadsheet;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

// Load and store of spreadsheets in S2V format, one line per row, the contents of the cells
// of a row are separated by ';', a row ends in its last cell that has something, in a formula
// the ';' between the arguments of a function are written as ',' in the file
public class S2VFile {

    public static void save(Spreadsheet sheet, String path) throws IOException {
        String text = "";
        int rows = sheet.getNumberOfRows();
        int columns = sheet.getNumberOfColumns();

        for (int row = 0; row < rows; row++) {
            // last cell of this row that has something
            int last = -1;
            for (int column = 0; column < columns; column++) {
                if (!sheet.isEmpty(row, column)) {
                    last = column;
                }
            }
            String line = "";
            for (int column = 0; column <= last; column++) {
                Content content = sheet.getContent(row, column);
                String cellText = content.getText();
                if (content instanceof FormulaContent) {
                    cellText = cellText.replace(';', ',');
                }
                if (column > 0) {
                    line = line + ";";
                }
                line = line + cellText;
            }
            text = text + line + "\n";
        }
        Files.writeString(Path.of(path), text, StandardCharsets.UTF_8);
    }

    public static Spreadsheet load(String path, String name) throws IOException {
        List<String> lines = Files.readAllLines(Path.of(path), StandardCharsets.UTF_8);
        Spreadsheet sheet = new Spreadsheet(name);

        for (int row = 0; row < lines.size(); row++) {
            String[] fields = lines.get(row).split(";", -1);   // -1 keeps the empty fields at the end
            for (int column = 0; column < fields.length; column++) {
                String field = fields[column];
                if (field.trim().equals("")) {
                    continue;                                  // empty cell
                }
                if (field.trim().startsWith("=")) {
                    field = field.replace(',', ';');           // back to ';' between arguments
                }
                sheet.setContent(row, column, Content.create(field));
            }
        }
        return sheet;
    }
}
