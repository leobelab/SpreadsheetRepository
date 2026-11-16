package spreadsheet;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Scanner;

public class Main {

    private static final String FOLDER = "spreadsheets";
    private static final String EXTENSION = ".s2v";

    private static Scanner keyboard = new Scanner(System.in);
    private static Spreadsheet current = null;
    private static boolean modified = false;

    public static void main(String[] args) {
        new File(FOLDER).mkdirs();

        boolean exit = false;
        while (!exit) {
            printMenu();
            String option = ask("Choose an option: ");
            try {
                switch (option) {
                    case "1" -> createSpreadsheet();
                    case "2" -> openSpreadsheet();
                    case "3" -> closeSpreadsheet();
                    case "4" -> deleteSpreadsheet();
                    case "5" -> saveSpreadsheet();
                    case "6" -> showSpreadsheet();
                    case "7" -> editCell();
                    case "8" -> showCell();
                    case "9" -> expressionExamples();
                    case "0" -> exit = exitProgram();
                    default -> System.out.println("That is not a valid option.");
                }
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
        System.out.println("Bye!");
    }

    // menu
    private static void printMenu() {
        System.out.println();
        System.out.println("=====================================================");
        System.out.println("  SPREADSHEET");
        if (current == null) {
            System.out.println("  Open spreadsheet: none");
        } else if (modified) {
            System.out.println("  Open spreadsheet: " + current.getName() + " (not saved)");
        } else {
            System.out.println("  Open spreadsheet: " + current.getName());
        }
        System.out.println("=====================================================");
        System.out.println("  1. Create a new spreadsheet");
        System.out.println("  2. Open a spreadsheet");
        System.out.println("  3. Close the open spreadsheet");
        System.out.println("  4. Delete a spreadsheet");
        System.out.println("  5. Save the open spreadsheet");
        System.out.println("  6. Show the open spreadsheet");
        System.out.println("  7. Write in a cell (number, text or formula)");
        System.out.println("  8. Show a cell");
        System.out.println("  9. Evaluate example expressions (expression tree)");
        System.out.println("  0. Exit");
    }

    // spreadsheets
    private static void createSpreadsheet() throws IOException {
        if (current != null) {
            System.out.println("There is a spreadsheet open. Close it first (option 3).");
            return;
        }
        String name = askName();
        if (new File(pathOf(name)).exists()) {
            System.out.println("There is already a spreadsheet called " + name + ".");
            return;
        }
        current = new Spreadsheet(name);
        S2VFile.save(current, pathOf(name));          // so the file exists from the beginning
        modified = false;
        System.out.println("Spreadsheet " + name + " created.");
    }

    private static void openSpreadsheet() throws IOException {
        if (current != null) {
            System.out.println("There is a spreadsheet open. Close it first (option 3).");
            return;
        }
        if (!printSpreadsheets()) {
            return;
        }
        String name = askName();
        if (!new File(pathOf(name)).exists()) {
            System.out.println("There is no spreadsheet called " + name + ".");
            return;
        }
        current = S2VFile.load(pathOf(name), name);
        modified = false;
        System.out.println("Spreadsheet " + name + " opened.");
    }

    private static void closeSpreadsheet() throws IOException {
        if (current == null) {
            System.out.println("There is no spreadsheet open.");
            return;
        }
        if (modified && askYesNo("There are changes that are not saved. Save them? (y/n): ")) {
            S2VFile.save(current, pathOf(current.getName()));
            System.out.println("Saved.");
        }
        System.out.println("Spreadsheet " + current.getName() + " closed.");
        current = null;
        modified = false;
    }

    private static void deleteSpreadsheet() {
        if (!printSpreadsheets()) {
            return;
        }
        String name = askName();
        File file = new File(pathOf(name));
        if (!file.exists()) {
            System.out.println("There is no spreadsheet called " + name + ".");
            return;
        }
        if (!askYesNo("Are you sure you want to delete " + name + "? (y/n): ")) {
            System.out.println("Not deleted.");
            return;
        }
        file.delete();
        if (current != null && current.getName().equals(name)) {
            current = null;                           // it was the open one
            modified = false;
        }
        System.out.println("Spreadsheet " + name + " deleted.");
    }

    private static void saveSpreadsheet() throws IOException {
        if (current == null) {
            System.out.println("There is no spreadsheet open.");
            return;
        }
        S2VFile.save(current, pathOf(current.getName()));
        modified = false;
        System.out.println("Spreadsheet " + current.getName() + " saved.");
    }

    // Asks to save the changes (if there are) and returns true to finish the program
    private static boolean exitProgram() throws IOException {
        if (current != null && modified && askYesNo("There are changes that are not saved. Save them? (y/n): ")) {
            S2VFile.save(current, pathOf(current.getName()));
            System.out.println("Saved.");
        }
        return true;
    }

    // cells

    private static void editCell() {
        if (current == null) {
            System.out.println("There is no spreadsheet open.");
            return;
        }
        String coordinate = ask("Cell (for example A1): ");
        String text = ask("Content (a number, a text or a formula starting with '='. Empty to clear): ");
        current.setContent(coordinate, text);
        modified = true;
        System.out.println("Cell " + coordinate.trim().toUpperCase() + " updated.");
    }

    private static void showSpreadsheet() {
        if (current == null) {
            System.out.println("There is no spreadsheet open.");
            return;
        }
        int rows = current.getNumberOfRows();
        int columns = current.getNumberOfColumns();
        if (rows == 0) {
            System.out.println("The spreadsheet is empty.");
            return;
        }

        String header = String.format("%5s", "");
        for (int column = 0; column < columns; column++) {
            header = header + String.format(" | %-14s", Spreadsheet.columnToLetters(column));
        }
        System.out.println(header);

        for (int row = 0; row < rows; row++) {
            String line = String.format("%5d", row + 1);
            for (int column = 0; column < columns; column++) {
                String value = current.getContent(row, column).getString();
                if (value.length() > 14) {
                    value = value.substring(0, 12) + "..";
                }
                line = line + String.format(" | %-14s", value);
            }
            System.out.println(line);
        }
    }

    private static void showCell() {
        if (current == null) {
            System.out.println("There is no spreadsheet open.");
            return;
        }
        String coordinate = ask("Cell (for example A1): ");
        Content content = current.getContent(coordinate);
        System.out.println("Content   : " + content.getText());
        System.out.println("As string : \"" + content.getString() + "\"");
        try {
            System.out.println("As number : " + Content.numberToString(content.getNumber()));
        } catch (Exception e) {
            System.out.println("As number : not available (" + e.getMessage() + ")");
        }
    }

    // expression tree

    // The trees are built by hand, there is no parser yet
    private static void expressionExamples() {
        Expression[] examples = new Expression[5];

        // 1 + 2
        examples[0] = new Addition(new NumberExpression(1), new NumberExpression(2));

        // 3 * 5 + 4
        examples[1] = new Addition(
                new Multiplication(new NumberExpression(3), new NumberExpression(5)),
                new NumberExpression(4));

        // 1 + 2 * 3  (the multiplication is lower in the tree, so it is done first)
        examples[2] = new Addition(
                new NumberExpression(1),
                new Multiplication(new NumberExpression(2), new NumberExpression(3)));

        // (1 + 2) * 3  (the parentheses are the addition being lower in the tree)
        examples[3] = new Multiplication(
                new Addition(new NumberExpression(1), new NumberExpression(2)),
                new NumberExpression(3));

        // (10 - 4) / (1 + 2)
        examples[4] = new Division(
                new Subtraction(new NumberExpression(10), new NumberExpression(4)),
                new Addition(new NumberExpression(1), new NumberExpression(2)));

        for (int i = 0; i < examples.length; i++) {
            System.out.println(examples[i] + " = " + Content.numberToString(examples[i].evaluate()));
        }
    }

    // helpers

    private static String pathOf(String name) {
        return FOLDER + "/" + name + EXTENSION;
    }

    // Prints the names of the spreadsheets in the folder. Returns false if there are none.
    private static boolean printSpreadsheets() {
        String[] files = new File(FOLDER).list();
        if (files == null) {
            files = new String[0];
        }
        Arrays.sort(files);
        boolean any = false;
        for (int i = 0; i < files.length; i++) {
            if (files[i].endsWith(EXTENSION)) {
                if (!any) {
                    System.out.println("Spreadsheets in the folder '" + FOLDER + "':");
                }
                System.out.println("  - " + files[i].substring(0, files[i].length() - EXTENSION.length()));
                any = true;
            }
        }
        if (!any) {
            System.out.println("There are no spreadsheets in the folder '" + FOLDER + "'.");
        }
        return any;
    }

    private static String askName() {
        String name = ask("Name of the spreadsheet: ").trim();
        if (!name.matches("[\\w-]+")) {
            throw new IllegalArgumentException("The name can only have letters, numbers, '-' and '_'");
        }
        return name;
    }

    private static boolean askYesNo(String question) {
        String answer = ask(question).trim().toLowerCase();
        return answer.equals("y") || answer.equals("yes") || answer.equals("s") || answer.equals("si");
    }

    private static String ask(String question) {
        System.out.print(question);
        if (!keyboard.hasNextLine()) {
            System.out.println();
            System.out.println("Bye!");
            System.exit(0);
        }
        return keyboard.nextLine();
    }
}
