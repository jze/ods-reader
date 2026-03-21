package de.zedlitz.opendocument;

import org.junit.jupiter.api.Test;

import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class RealworldTest {

    static String table2csv(Table table) {
        StringBuilder sb = new StringBuilder();
        Row row = table.nextRow();
        while (row != null) {
            Cell cell = row.nextCell();
            while (cell != null) {
                sb.append(cell.getLanguageIndependentContent());
                cell = row.nextCell();
                if (cell != null) {
                    sb.append('\t');
                }
            }

            sb.append('\n');
            row = table.nextRow();
        }
        return sb.toString();
    }

    /**
     * Tests the use of the Consumers for the various classes.
     */
    @Test
    public void consumer() throws Exception {
        final Document doc = new Document(getClass().getResourceAsStream("/test01.ods"));

        AtomicInteger numberOfTables = new AtomicInteger(0);
        AtomicInteger numberOfRows = new AtomicInteger(0);
        AtomicInteger numberOfCells = new AtomicInteger(0);

        doc.eachTable(table -> {
            numberOfTables.incrementAndGet();

            table.eachRow(row -> {
                numberOfRows.incrementAndGet();

                row.eachCell(cell -> numberOfCells.incrementAndGet());

            });
        });

        assertEquals(3, numberOfTables.get());
        assertEquals(6, numberOfRows.get());
        assertEquals(26, numberOfCells.get());
    }

    @Test
    public void testSpaces() throws Exception {
        final Document doc = new Document(getClass().getResourceAsStream("/testSpaces.ods"));

        Table table = doc.nextTable();
        Row row = table.nextRow();

        // first cell has content "A  A" (with double-spaces)
        Cell cell = row.nextCell();
        assertSpacesAndNewLines(cell, 2, 0);

        // second cell has content "A   A" (3 spaces)
        cell = row.nextCell();
        assertSpacesAndNewLines(cell, 3, 0);

        // third cell has content "A\nA\nA" (2 newlines)
        cell = row.nextCell();
        assertSpacesAndNewLines(cell, 0,  2);

        // third cell has content "A\n\nA" (2 newlines)
        cell = row.nextCell();
        assertSpacesAndNewLines(cell, 0,  2);
    }

    private int countOccurrences(String string, String search) {
        int count = 0;
        for (int i = 0; i < string.length(); i++) {
            if (hasStringAt(string, search, i)) count++;
        }

        return count;
    }

    private void assertSpacesAndNewLines(Cell cell, int expectedSpaces, int expectedNewLines) {
        String content = cell.getContent();
        int spacesInContent = countOccurrences(content, " ");
        int newLinesInContent = countOccurrences(content, System.lineSeparator());

        String message = "Cell " + cell.getColumnIndex() + " - content read: [" + content + "]";
        assertEquals(expectedSpaces, spacesInContent, message);
        assertEquals(expectedNewLines, newLinesInContent, message);
    }

    private boolean hasStringAt(String string, String search, int index) {
        if (index < 0 || index >= string.length()) {
            return false;
        }

        for (int i = 0; i < search.length() && (index + i) < string.length() ; i++) {
            if (string.charAt(index + i) !=  search.charAt(i)) return false;
        }

        return true;
    }

    /**
     * Test the access to a cell by its number
     */
    @Test
    public void directCellAccess() throws XMLStreamException, IOException {
        final Document doc = new Document(getClass().getResourceAsStream("/test01.ods"));
        Table table = doc.nextTable();
        Row row1 = table.nextRow();

        Cell cellC1 = row1.getAt(2);
        Cell cellB1 = row1.getAt(1);
        Cell cellA1 = row1.getAt(0);
        Cell cellE1 = row1.getAt(4);

        assertEquals("A1", cellA1.getContent());
        assertEquals("b1", cellB1.getContent());
        assertEquals("c1", cellC1.getContent());
        assertEquals("e1", cellE1.getContent());

        // The second line is only sparsely filled.
        Row row2 = table.nextRow();
        Cell cellC2 = row2.getAt(2);
        Cell cellB2 = row2.getAt(1);
        Cell cellA2 = row2.getAt(0);
        Cell cellE2 = row2.getAt(4);

        assertEquals("2", cellA2.getContent());
        assertEquals("b2", cellB2.getContent());
        assertEquals("", cellC2.getContent());
        assertEquals("e2", cellE2.getContent());
    }

    @Test
    public void noOdsFile() throws XMLStreamException, IOException {
        final Document doc = new Document(getClass().getResourceAsStream("/no-ods.zip"));
        assertNull(doc.nextTable());
    }

    @Test
    public void iterators() throws XMLStreamException, IOException {
        AtomicInteger numberOfRows = new AtomicInteger(0);
        AtomicInteger numberOfCells = new AtomicInteger(0);

        final Document doc = new Document(getClass().getResourceAsStream("/test01.ods"));
        Table table = doc.nextTable();
        for (Row row : table) {
            numberOfRows.incrementAndGet();
            for (Cell cell : row) {
                numberOfCells.incrementAndGet();
            }
        }

        assertEquals(4, numberOfRows.get());
        assertEquals(19, numberOfCells.get());
    }

    @Test
    public void differentFormats() throws Exception {
        List<String> expectedRow1 = Arrays.asList("text", "number", "thousand point", "date", "percentage", "currency", "time", "floating point number");
        List<String> expectedRow2 = Arrays.asList("abc", "7392", "5.039", "2024-07-01", "5,63 %", "78,34 €", "08:35", "54,143662");

        final Document doc = new Document(getClass().getResourceAsStream("/formats.ods"));
        Table table = doc.nextTable();

        List<String> row1 = table.nextRow().openStream().map(Cell::getContent).collect(Collectors.toList());
        List<String> row2 = table.nextRow().openStream().map(Cell::getContent).collect(Collectors.toList());
        assertEquals(expectedRow1, row1);
        assertEquals(expectedRow2, row2);

        assertNull(table.nextRow());
        assertNull(doc.nextTable());
    }

    @Test
    public void languageDependentFrench() throws Exception {
        final Document doc = new Document(getClass().getResourceAsStream("/formats_french.ods"));
        Table table = doc.nextTable();

        List<String> values = new ArrayList<>();
        Row row = table.nextRow();
        while (row != null) {
            values.add(row.nextCell().getContent());
            row = table.nextRow();
        }

        assertEquals("31 déc. 99", values.get(13));
        assertEquals("ven. 31 déc. 99", values.get(17));
        assertEquals("4e trimestre 99", values.get(19));
        assertEquals("123  4/10", values.get(32));
        assertEquals("VRAI", values.get(33));
        assertEquals("FAUX", values.get(34));
    }

    @Test
    public void languageDependentGerman() throws Exception {
        final Document doc = new Document(getClass().getResourceAsStream("/formats_german.ods"));
        Table table = doc.nextTable();

        List<String> values = new ArrayList<>();
        Row row = table.nextRow();
        while (row != null) {
            values.add(row.nextCell().getContent());
            row = table.nextRow();
        }

        assertEquals("31. Dez 99", values.get(13));
        assertEquals("Fr, 31. Dez 99", values.get(17));
        assertEquals("4. Quartal 99", values.get(19));
        assertEquals("123  4/10", values.get(32));
        assertEquals("WAHR", values.get(33));
        assertEquals("FALSCH", values.get(34));
    }

    @Test
    public void languageDependentEnglish() throws Exception {
        final Document doc = new Document(getClass().getResourceAsStream("/formats_english.ods"));
        Table table = doc.nextTable();

        List<String> values = new ArrayList<>();
        Row row = table.nextRow();
        while (row != null) {
            values.add(row.nextCell().getContent());
            row = table.nextRow();
        }

        assertEquals("Dec 31, 99", values.get(13));
        assertEquals("Fri, Dec 31, 99", values.get(17));
        assertEquals("4th quarter 99", values.get(19));
        assertEquals("123  4/10", values.get(32));
        assertEquals("TRUE", values.get(33));
        assertEquals("FALSE", values.get(34));
    }

    /**
     * This table contains a lot of repeated cells and "gaps" (empty cells).
     */
    @Test
    void ods2csv_gaps() throws XMLStreamException, IOException {
        String expectedText = "a\tb\tc\td\te\tf\tg\n" +
                "1\t1\t1\t2\t3\t4\t5\n" +
                "1\t\t\t\t2\t3\t4\n" +
                "1\t1\t\t\t2\t2\t\n";

        final Document doc = new Document(getClass().getResourceAsStream("/gaps.ods"));
        Table table = doc.nextTable();

        String data = table2csv(table);

        assertEquals(expectedText, data);

    }
}
