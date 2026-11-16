package spreadsheet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class SpreadsheetTest {

    private static final double DELTA = 0.000001;

    @Test
    public void emptyCell() {
        Spreadsheet sheet = new Spreadsheet("test");
        assertEquals("", sheet.getContent("A1").getString());
        assertEquals(0, sheet.getContent("A1").getNumber(), DELTA);
        assertEquals(0, sheet.getNumberOfRows());
        assertEquals(0, sheet.getNumberOfColumns());
    }

    @Test
    public void storeNumbers() {
        Spreadsheet sheet = new Spreadsheet("test");
        sheet.setContent("A1", "4");
        sheet.setContent("B1", "1.2");
        sheet.setContent("C1", "-7");
        assertEquals("4", sheet.getContent("A1").getString());
        assertEquals(4, sheet.getContent("A1").getNumber(), DELTA);
        assertEquals("1.2", sheet.getContent("B1").getString());
        assertEquals(1.2, sheet.getContent("B1").getNumber(), DELTA);
        assertEquals(-7, sheet.getContent("C1").getNumber(), DELTA);
        assertTrue(sheet.getContent("A1") instanceof NumericalContent);
    }

    @Test
    public void storeText() {
        Spreadsheet sheet = new Spreadsheet("test");
        sheet.setContent("B3", "TOTAL");
        assertEquals("TOTAL", sheet.getContent("B3").getString());
        assertTrue(sheet.getContent("B3") instanceof TextContent);
    }

    @Test
    public void textThatIsNotANumberGivesAnErrorAsNumber() {
        Spreadsheet sheet = new Spreadsheet("test");
        sheet.setContent("B3", "TOTAL");
        assertThrows(IllegalArgumentException.class, () -> sheet.getContent("B3").getNumber());
    }

    @Test
    public void textContentNumbers() {
        // a text with a number inside gives the number, an empty text gives 0
        assertEquals(1, new TextContent("1").getNumber(), DELTA);
        assertEquals(1.2, new TextContent("1.2").getNumber(), DELTA);
        assertEquals(0, new TextContent("").getNumber(), DELTA);
        assertThrows(IllegalArgumentException.class, () -> new TextContent("1,2").getNumber());
    }

    @Test
    public void storeFormulaWithoutCalculating() {
        Spreadsheet sheet = new Spreadsheet("test");
        sheet.setContent("A1", "=C1+C2");
        assertTrue(sheet.getContent("A1") instanceof FormulaContent);
        assertEquals("=C1+C2", sheet.getContent("A1").getText());
        assertThrows(UnsupportedOperationException.class, () -> sheet.getContent("A1").getNumber());
    }

    @Test
    public void overwriteAndClearACell() {
        Spreadsheet sheet = new Spreadsheet("test");
        sheet.setContent("A1", "5");
        sheet.setContent("A1", "hello");
        assertEquals("hello", sheet.getContent("A1").getString());
        sheet.setContent("A1", "");
        assertEquals("", sheet.getContent("A1").getString());
        assertEquals(0, sheet.getNumberOfRows());
    }

    @Test
    public void coordinatesAreNotCaseSensitive() {
        Spreadsheet sheet = new Spreadsheet("test");
        sheet.setContent("b2", "9");
        assertEquals("9", sheet.getContent("B2").getString());
    }

    @Test
    public void columnLetters() {
        assertEquals(0, Spreadsheet.columnToNumber("A"));
        assertEquals(25, Spreadsheet.columnToNumber("Z"));
        assertEquals(26, Spreadsheet.columnToNumber("AA"));
        assertEquals(27, Spreadsheet.columnToNumber("AB"));
        assertEquals(52, Spreadsheet.columnToNumber("BA"));
        assertEquals("A", Spreadsheet.columnToLetters(0));
        assertEquals("Z", Spreadsheet.columnToLetters(25));
        assertEquals("AA", Spreadsheet.columnToLetters(26));
        assertEquals("AB", Spreadsheet.columnToLetters(27));
        assertEquals("BA", Spreadsheet.columnToLetters(52));
    }

    @Test
    public void spreadsheetGrowsWhenNeeded() {
        Spreadsheet sheet = new Spreadsheet("test");
        sheet.setContent("A1", "first");
        sheet.setContent("AB50", "far away");
        assertEquals("first", sheet.getContent("A1").getString());
        assertEquals("far away", sheet.getContent("AB50").getString());
        assertEquals(50, sheet.getNumberOfRows());
        assertEquals(28, sheet.getNumberOfColumns());
        // reading outside of the array is fine, the cell is empty
        assertEquals("", sheet.getContent("Z99").getString());
    }

    @Test
    public void wrongCoordinates() {
        Spreadsheet sheet = new Spreadsheet("test");
        assertThrows(IllegalArgumentException.class, () -> sheet.setContent("A0", "1"));
        assertThrows(IllegalArgumentException.class, () -> sheet.setContent("1A", "1"));
        assertThrows(IllegalArgumentException.class, () -> sheet.setContent("A", "1"));
        assertThrows(IllegalArgumentException.class, () -> sheet.setContent("", "1"));
        assertThrows(IllegalArgumentException.class, () -> sheet.setContent("A-1", "1"));
    }

    @Test
    public void textWithSemicolonIsRejected() {
        Spreadsheet sheet = new Spreadsheet("test");
        assertThrows(IllegalArgumentException.class, () -> sheet.setContent("A1", "a;b"));
    }

    @Test
    public void numberOfRowsAndColumns() {
        Spreadsheet sheet = new Spreadsheet("test");
        sheet.setContent("C1", "1");
        sheet.setContent("A3", "x");
        assertEquals(3, sheet.getNumberOfRows());
        assertEquals(3, sheet.getNumberOfColumns());
    }
}
