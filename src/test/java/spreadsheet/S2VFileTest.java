package spreadsheet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class S2VFileTest {

    // the folder is created by JUnit and deleted at the end
    @TempDir
    Path folder;

    // The spreadsheet of the project document:
    //    A        B      C
    // 1  =C1+C2   4      1
    // 2                  2
    // 3           TOTAL  =A1+B1
    private Spreadsheet exampleSheet() {
        Spreadsheet sheet = new Spreadsheet("example");
        sheet.setContent("A1", "=C1+C2");
        sheet.setContent("B1", "4");
        sheet.setContent("C1", "1");
        sheet.setContent("C2", "2");
        sheet.setContent("B3", "TOTAL");
        sheet.setContent("C3", "=A1+B1");
        return sheet;
    }

    @Test
    public void saveTheExampleOfTheDocument() throws IOException {
        String path = folder.resolve("example.s2v").toString();
        S2VFile.save(exampleSheet(), path);
        String expected = "=C1+C2;4;1\n;;2\n;TOTAL;=A1+B1\n";
        assertEquals(expected, Files.readString(Path.of(path)));
    }

    @Test
    public void loadTheExampleOfTheDocument() throws IOException {
        Path file = folder.resolve("example.s2v");
        Files.writeString(file, "=C1+C2;4;1\n;;2\n;TOTAL;=A1+B1\n");

        Spreadsheet sheet = S2VFile.load(file.toString(), "example");
        assertEquals("=C1+C2", sheet.getContent("A1").getText());
        assertEquals("4", sheet.getContent("B1").getString());
        assertEquals("1", sheet.getContent("C1").getString());
        assertEquals("", sheet.getContent("A2").getString());
        assertEquals("", sheet.getContent("B2").getString());
        assertEquals("2", sheet.getContent("C2").getString());
        assertEquals("", sheet.getContent("A3").getString());
        assertEquals("TOTAL", sheet.getContent("B3").getString());
        assertEquals("=A1+B1", sheet.getContent("C3").getText());
        assertEquals(3, sheet.getNumberOfRows());
        assertEquals(3, sheet.getNumberOfColumns());
    }

    @Test
    public void saveAndLoadGiveTheSameFile() throws IOException {
        String first = folder.resolve("first.s2v").toString();
        String second = folder.resolve("second.s2v").toString();
        S2VFile.save(exampleSheet(), first);
        Spreadsheet loaded = S2VFile.load(first, "first");
        S2VFile.save(loaded, second);
        assertEquals(Files.readString(Path.of(first)), Files.readString(Path.of(second)));
    }

    @Test
    public void semicolonsOfFunctionsAreCommasInTheFile() throws IOException {
        Spreadsheet sheet = new Spreadsheet("functions");
        sheet.setContent("A1", "1");
        sheet.setContent("B1", "2");
        sheet.setContent("C1", "=SUMA(A1;B1;MAX(3;4))");
        String path = folder.resolve("functions.s2v").toString();
        S2VFile.save(sheet, path);
        assertEquals("1;2;=SUMA(A1,B1,MAX(3,4))\n", Files.readString(Path.of(path)));

        // and when loading they come back to ';'
        Spreadsheet loaded = S2VFile.load(path, "functions");
        assertEquals("=SUMA(A1;B1;MAX(3;4))", loaded.getContent("C1").getText());
    }

    @Test
    public void commasInTextAreNotChanged() throws IOException {
        Spreadsheet sheet = new Spreadsheet("text");
        sheet.setContent("A1", "Hello, world");
        String path = folder.resolve("text.s2v").toString();
        S2VFile.save(sheet, path);
        assertEquals("Hello, world\n", Files.readString(Path.of(path)));
        assertEquals("Hello, world", S2VFile.load(path, "text").getContent("A1").getString());
    }

    @Test
    public void emptyRowsAndCellsAreKept() throws IOException {
        Spreadsheet sheet = new Spreadsheet("gaps");
        sheet.setContent("C1", "1");
        sheet.setContent("A3", "x");
        String path = folder.resolve("gaps.s2v").toString();
        S2VFile.save(sheet, path);
        assertEquals(";;1\n\nx\n", Files.readString(Path.of(path)));

        Spreadsheet loaded = S2VFile.load(path, "gaps");
        assertEquals("1", loaded.getContent("C1").getString());
        assertEquals("x", loaded.getContent("A3").getString());
        assertEquals(3, loaded.getNumberOfRows());
    }

    @Test
    public void emptySpreadsheet() throws IOException {
        String path = folder.resolve("empty.s2v").toString();
        S2VFile.save(new Spreadsheet("empty"), path);
        assertEquals("", Files.readString(Path.of(path)));
        assertEquals(0, S2VFile.load(path, "empty").getNumberOfRows());
    }

    @Test
    public void fileThatDoesNotExist() {
        String path = folder.resolve("nothing.s2v").toString();
        assertThrows(IOException.class, () -> S2VFile.load(path, "nothing"));
    }

    @Test
    public void saveInAFolderThatDoesNotExist() {
        String path = folder.resolve("no-such-folder/sheet.s2v").toString();
        assertThrows(IOException.class, () -> S2VFile.save(exampleSheet(), path));
    }
}
