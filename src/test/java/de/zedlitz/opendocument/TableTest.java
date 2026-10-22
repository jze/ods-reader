package de.zedlitz.opendocument;

import org.junit.jupiter.api.Test;

import javax.xml.stream.XMLStreamException;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author jzedlitz
 */
public class TableTest extends AbstractBaseTest {
    private static final String CONTENT_EMPTY_TABLE =
            "<table:table xmlns:table='urn:oasis:names:tc:opendocument:xmlns:table:1.0'" +
                    " table:name=\"Tabelle1\" table:style-name=\"ta1\" table:print=\"false\">" +
                    "</table:table>";
    private static final String CONTENT_TWO_EMPTY_ROWS =
            "<table:table xmlns:table='urn:oasis:names:tc:opendocument:xmlns:table:1.0'" +
                    " table:name=\"Tabelle1\" table:style-name=\"ta1\" table:print=\"false\">" +
                    "<table:table-row/><table:table-row/>" + "</table:table>";

    private static final String CONTENT_TWO_ROWS_WITH_TEXT =
            "<table:table xmlns:table='urn:oasis:names:tc:opendocument:xmlns:table:1.0'" +
                    " xmlns:text='urn:oasis:names:tc:opendocument:xmlns:text:1.0'>" +
                    "<table:table-row><table:table-cell><text:p>r1</text:p></table:table-cell></table:table-row>" +
                    "<table:table-row><table:table-cell><text:p>r2</text:p></table:table-cell></table:table-row>" +
                    "</table:table>";

    /**
     * A repeated row with content, two repeated empty rows ("gap") and a final row. The table ends with
     * repeated empty rows like LibreOffice writes them for formatted rows.
     */
    private static final String CONTENT_REPEATED_ROWS =
            "<table:table xmlns:table='urn:oasis:names:tc:opendocument:xmlns:table:1.0'" +
                    " xmlns:text='urn:oasis:names:tc:opendocument:xmlns:text:1.0'>" +
                    "<table:table-row table:number-rows-repeated='3'>" +
                    "<table:table-cell table:number-columns-repeated='2'><text:p>same</text:p></table:table-cell>" +
                    "</table:table-row>" +
                    "<table:table-row table:number-rows-repeated='2'><table:table-cell/></table:table-row>" +
                    "<table:table-row><table:table-cell><text:p>last</text:p></table:table-cell></table:table-row>" +
                    "<table:table-row table:number-rows-repeated='1048570'>" +
                    "<table:table-cell table:number-columns-repeated='1024'/></table:table-row>" +
                    "</table:table>";

    private static final String BROKEN_XML_CONTENT =
            "<table:table xmlns:table='urn:oasis:names:tc:opendocument:xmlns:table:1.0'" +
                    " table:name=\"Tabelle1\" table:style-name=\"ta1\" table:print=\"false\">";
    
    @Test
    public void testEmptyTable() throws Exception {
        final Table table = new Table(advanceToStartTag(createParser(CONTENT_EMPTY_TABLE)));
        assertEquals("Tabelle1", table.getName(), "table has a name");
        assertNull(table.nextRow(), "table has no row");
        assertNull(table.nextRow(), "second call ok");
    }


    @Test
    public void testTwoEmptyRows() throws Exception {
        final Table table = new Table(advanceToStartTag(createParser(CONTENT_TWO_EMPTY_ROWS)));
        assertNotNull(table.nextRow(), "1st row ok");
        assertNotNull(table.nextRow(), "2nd row ok");
        assertNull(table.nextRow(), "no 3rd row");
    }

    @Test
    public void testTwoEmptyRowsCheckCells() throws Exception {
        final Table table =
                new Table(advanceToStartTag(createParser(CONTENT_TWO_EMPTY_ROWS)));

        final Row row1 = table.nextRow();
        assertNotNull(row1, "1st row ok");
        assertNull(row1.nextCell(), "no cell in 1st row");
        assertNull(row1.nextCell(), "second call ok");

        final Row row2 = table.nextRow();
        assertNotNull(row2, "2nd row ok");
        assertNull(row2.nextCell(), "no cell in 2nd row");
        assertNull(row2.nextCell(), "second call ok");

        assertNull(table.nextRow(), "no 3rd row");
    }

    @Test
    public void brokenXmlContent() throws Exception {
        PrintStream originalErr = System.err;
        ByteArrayOutputStream errContent = new ByteArrayOutputStream();
        System.setErr(new PrintStream(errContent));

        final Table table = new Table(advanceToStartTag(createParser(BROKEN_XML_CONTENT)));

        Row row = table.nextRow();

        System.setErr(originalErr);

        assertNull(row);
        assertTrue(errContent.toString().contains("XMLStreamException"));
    }

    @Test
    void iterator() throws XMLStreamException {
        final Table table = new Table(advanceToStartTag(createParser(CONTENT_EMPTY_TABLE)));

        Iterator<Row> it = table.iterator();
        assertFalse(it.hasNext());
        assertThrows(NoSuchElementException.class, it::next);
    }

    @Test
    void iteratorReadsCellsOfCurrentRow() throws XMLStreamException {
        final Table table = new Table(advanceToStartTag(createParser(CONTENT_TWO_ROWS_WITH_TEXT)));

        List<String> contents = new ArrayList<>();
        for (Row row : table) {
            for (Cell cell : row) {
                contents.add(row.getRowNum() + ":" + cell.getContent());
            }
        }
        assertEquals(Arrays.asList("1:r1", "2:r2"), contents);
    }

    @Test
    void openStreamReadsCellsOfCurrentRow() throws XMLStreamException {
        final Table table = new Table(advanceToStartTag(createParser(CONTENT_TWO_ROWS_WITH_TEXT)));

        List<String> contents = table.openStream()
                .map(row -> row.nextCell().getContent())
                .collect(Collectors.toList());
        assertEquals(Arrays.asList("r1", "r2"), contents);
    }

    @Test
    void repeatedRows() throws XMLStreamException {
        final Table table = new Table(advanceToStartTag(createParser(CONTENT_REPEATED_ROWS)));

        List<String> addresses = new ArrayList<>();
        table.eachRow(row -> row.eachCell(cell -> addresses.add(cell.getAddress() + "=" + cell.getContent())));

        assertEquals(Arrays.asList(
                "A1=same", "B1=same",
                "A2=same", "B2=same",
                "A3=same", "B3=same",
                "A4=", "A5=",
                "A6=last"), addresses.subList(0, 9));
        // the trailing empty rows are not expanded - only one row with the repeated cells remains
        assertEquals(9 + 1024, addresses.size());
        assertEquals("A7=", addresses.get(9));
    }

    @Test
    void openStream() throws XMLStreamException {
        final Table table = new Table(advanceToStartTag(createParser(CONTENT_TWO_EMPTY_ROWS)));
        Stream<Row> stream = table.openStream();
        List<Row> rows = stream.collect(Collectors.toList());
        assertEquals(2, rows.size());
    }
}
